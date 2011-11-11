package edu.nyu.cs.adb;

/**
 * Describes an operation to be done by the Data Manager
 * @author dandelarosa
 */
public final class Operation {
	private final int transactionID;
	private final int opcode;
	private final int variableID;
	private final int writeValue;
	
	/**
	 * Prevent Instantiation!
	 */
	private Operation () {
		throw new AssertionError();
	}

	/**
	 * Gets the ID of the transaction that requests the operation
	 * @return The transaction ID
	 */
	public int getTransactionID () { return transactionID; }
	
	/**
	 * Gets the opcode to check whether this operation is a begin, 
	 * beginReadOnly, read, write, abort, or finish
	 * @return The opcode
	 */
	public int getOperationID () { return opcode; } // TODO this should be an enum instead
	
	/**
	 * If this operation is a read or write, get the ID of the variable the 
	 * operation is asked to act on
	 * @return The variable ID
	 */
	public int getVariableID () { return variableID; }
	
	/**
	 * If the operation is a write operation, get the value it is supposed to 
	 * write
	 * @return The write value
	 */
	public int getWriteValue () { return writeValue; }
}
