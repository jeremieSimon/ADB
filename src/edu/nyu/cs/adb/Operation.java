package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Describes an operation to be done by the Data Manager
 * <p>Immutable</p>
 * @author dandelarosa
 */
public final class Operation {
	private final String transactionID;
	private final Opcode opcode;
	private final String variableID;
	private final int writeValue;
	private HashMap<String, ArrayList <Integer>> variableMap =createVariableMap();
	ArrayList <Integer> siteID; 
	
	/**
	 * Operation code values
	 * @author dandelarosa
	 */
	public static enum Opcode {
		BEGIN, 
		BEGIN_READONLY, 
		READ, 
		WRITE,
		ABORT, 
		FINISH
	}
	
	/**
	 * Prevent Instantiation!
	 */
	private Operation () {
		throw new AssertionError();
	}
	
	/**
	 * Constructs a new Operation from a builder object
	 * @param builder
	 */
	private Operation (Builder builder) {  
		this.opcode = builder.opcode;
		this.transactionID = builder.transactionID;
		this.variableID = builder.variableID;
		this.writeValue = builder.writeValue;
		
		siteID = variableMap.get(variableID);
		
	}
	
	/**
	 * Builder class for Operation
	 * @author dandelarosa
	 */
	public static class Builder {
		private String transactionID;
		private final Opcode opcode;
		private String variableID;
		private int writeValue;
		
		/**
		 * Create a builder for a given operation
		 * @param opcode
		 */
		public Builder (Opcode opcode) {
			this.opcode = opcode;
		}
		
		/**
		 * Set the transaction ID
		 * @param transactionID
		 * @return self
		 */
		public Builder setTransactionID (String transactionID) {
			this.transactionID = transactionID;
			return this;
		}
		
		/**
		 * Set the variableID
		 * @param variableID
		 * @return self
		 */
		public Builder setVariableID (String variableID) {
			if (this.opcode != Opcode.READ && this.opcode != Opcode.WRITE) {
				throw new UnsupportedOperationException("Not a read or " +
						"write operation");
			}
			this.variableID = variableID;
			return this;
		}
		
		/**
		 * Set the write value
		 * @param writeValue
		 * @return self
		 * @throws UnsupportedOperationException if this not a write operation
		 */
		public Builder setWriteValue (int writeValue) {
			if (this.opcode != Opcode.WRITE) {
				throw new UnsupportedOperationException("Write value " +
						"should be defined only for write operations.");
			}
			this.writeValue = writeValue;
			return this;
		}
		
		/**
		 * Generate a new Operation from the builder
		 * @return a new instance of Operation
		 */
		public Operation build () {
			return new Operation(this);
		}
	}

	/**
	 * Gets the ID of the transaction that requests the operation
	 * @return The transaction ID
	 */
	public String getTransactionID () { return transactionID; }
	
	/**
	 * Gets the opcode to check whether this operation is a begin, 
	 * beginReadOnly, read, write, abort, or finish
	 * @return The opcode
	 */
	public Opcode getOperationID () { return opcode; }
	
	public ArrayList <Integer> getSiteID () {return siteID; }
	
	/**
	 * If this operation is a read or write, get the ID of the variable the 
	 * operation is asked to act on
	 * @return The variable ID
	 */
	public String getVariableID () {
		if (this.opcode != Opcode.READ && this.opcode != Opcode.WRITE) {
			throw new UnsupportedOperationException("Not a read or " +
					"write operation");
		}
		return variableID;
	}
	
	/**
	 * If the operation is a write operation, get the value it is supposed to 
	 * write
	 * @return The write value
	 * @throws UnsupportedOperationException if this is not a write operation
	 */
	public int getWriteValue () {
		if (this.opcode != Opcode.WRITE) {
			throw new UnsupportedOperationException("Not a write operation");
		}
		return writeValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opcode == null) ? 0 : opcode.hashCode());
		result = prime * result
				+ ((transactionID == null) ? 0 : transactionID.hashCode());
		result = prime * result
				+ ((variableID == null) ? 0 : variableID.hashCode());
		result = prime * result + writeValue;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Operation other = (Operation) obj;
		if (opcode != other.opcode)
			return false;
		if (transactionID == null) {
			if (other.transactionID != null)
				return false;
		} else if (!transactionID.equals(other.transactionID))
			return false;
		if (variableID == null) {
			if (other.variableID != null)
				return false;
		} else if (!variableID.equals(other.variableID))
			return false;
		if (writeValue != other.writeValue)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Operation [transactionID=" + transactionID + ", opcode="
				+ opcode + ", variableID=" + variableID + ", writeValue="
				+ writeValue + "]";
	}
	
	private HashMap <String, ArrayList<Integer>> createVariableMap(){
		
		int NUMBER_OF_SITES = 10; 
		HashMap<String, ArrayList <Integer>> variableMap = new HashMap<String, ArrayList <Integer>>();

		ArrayList <Integer> sites = new ArrayList<Integer> (); 
		for (int i=0; i<NUMBER_OF_SITES; i++){
			sites.add(i);
		}
		
		for (int i=1; i<20; i++){
			if (i%2 == 0){
			variableMap.put("x"+i, sites);
			}
			else{
				ArrayList <Integer> A = new ArrayList<Integer>(); 
				A.add(((1+i)%10)); 
				variableMap.put("x"+i, A);
				}
		}
		return variableMap; 
	}
}
