package edu.nyu.cs.adb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The representation of a database site: contains a map for stored variable 
 * values as well as  lock data for its own site to see which transactions 
 * have a lock on each variable
 * @author dandelarosa
 */
public final class DataManager {
	private final int siteID;
	private int currentTime = 0;
	private boolean isActive = true;
	
	private Message currentMessage = null;
	
	private Map<String, List<CommittedValue>> stableStorage 
		= new HashMap<String, List<CommittedValue>>();
	private Map<String, Integer> unstableStorage 
		= new HashMap<String, Integer>();
	
	/**
	 * Data structure for a read-only transaction
	 */
	private static final class ReadOnlyTransaction {
		private final String transactionID;
		private final int startTime;
		
		/**
		 * Constructor
		 * @param transactionID
		 * @param startTime
		 */
		private ReadOnlyTransaction (String transactionID, int startTime) {
			this.transactionID = transactionID;
			this.startTime = startTime;
		}
		
		/**
		 * Get the transaction ID
		 * @return
		 */
		private String getTransactionID () {
			return transactionID;
		}
		
		/**
		 * Get the time the transaction started
		 * @return
		 */
		private int getStartTime () {
			return startTime;
		}
	}
	private List<ReadOnlyTransaction> readOnlyTransactions 
		= new LinkedList<ReadOnlyTransaction>();
	
	private List<String> readWriteTransactions = new LinkedList<String>();
	
	/**
	 * Lock information
	 * @author dandelarosa
	 */
	private static final class Lock {
		private final String transactionID;
		private final String variableID;
		
		/**
		 * Constructor
		 * @param transactionID
		 * @param variableID
		 */
		private Lock (String transactionID, String variableID) {
			this.transactionID = transactionID;
			this.variableID = variableID;
		}
		
		/**
		 * Get the transaction ID
		 * @return
		 */
		private String getTransactionID () {
			return transactionID;
		}
		
		/**
		 * Get the variable ID
		 * @return
		 */
		private String getVariableID () {
			return variableID;
		}
	}
	private List<Lock> readLocks = new LinkedList<Lock>();
	private List<Lock> writeLocks = new LinkedList<Lock>();

	private Map<String, Map<String, Integer>> beforeImages 
		= new HashMap<String, Map<String, Integer>>();	
	/**
	 * Constructor
	 * Ideally we'd actually do initialization of variables via a builder
	 * object, but we're doing it in here instead for the purposes of the
	 * project.
	 * @param siteID
	 */
	DataManager (int siteID) {
		this.siteID = siteID;
		
		// Even indexed variables are at all sites
		for (int i = 2; i <= 20; i += 2) {
			stableStorage.put(
					"x" + i, new LinkedList<CommittedValue>());
			stableStorage.get("x" + i)
				.add(new CommittedValue(10 * i, currentTime));
		}
		
		// Odd indexed variables are at one site each (1 + index mod 10)
		for (int i = 1; i < 20; i +=2) {
			if (siteID == 1 + (i % 10)) {
				stableStorage.put(
					"x" + i, new LinkedList<CommittedValue>());
				stableStorage.get("x" + i)
					.add(new CommittedValue(10 * i, currentTime));
			}
		}
	}
	
	/**
	 * Committed value/timestamp pair
	 * @author dandelarosa
	 */
	private static final class CommittedValue {
		private final int value;
		private final int timestamp;
		
		/**
		 * Constructor
		 * @param value
		 * @param timestamp
		 */
		private CommittedValue (int value, int timestamp) {
			this.value = value;
			this.timestamp = timestamp;
		}

		/**
		 * Gets the committed value
		 * @return
		 */
		private int getValue () {
			return value;
		}

		/**
		 * Gets the time when the value was committed
		 * @return
		 */
		private int getTimestamp () {
			return timestamp;
		}
	}
	
	/**
	 * Sends a message to the Data Manager
	 * @param message The message object
	 */
	void sendMessage (Message message) {
		currentMessage = message;
	}
	
	/**
	 * Processes the message in the message buffer. To be called at every 
	 * timestep.
	 */
	void update () {
		// Don't process any messages if the site has failed
		if (!isActive) return;
		
		// Don't process any messages if there isn't any
		if (currentMessage != null) return;
		
		for (Operation operation : currentMessage) {
			switch (operation.getOperationID()) {
				case ABORT:
				{
					// TODO
					break;
				}
				case BEGIN:
				{
					String transactionID = operation.getTransactionID();
					
					// Throw an exception if the transaction already started
					if (readWriteTransactions.contains(transactionID)) {
						throw new IllegalStateException("Transaction " 
								+ transactionID + " already started");
					}
					if (readOnlyTransactions.contains(transactionID)) {
						throw new IllegalStateException("Transaction " 
								+ transactionID + " already started");
					}
					
					// Create before image
					Map<String, Integer> beforeImage 
						= new HashMap<String, Integer>();
					Set<String> variableIDs = unstableStorage.keySet();
					for (String variableID : variableIDs) {
						int value = unstableStorage.get(variableID);
						beforeImage.put(variableID, value);
					}
					beforeImages.put(transactionID, beforeImage);
					
					// Safely add transaction
					readWriteTransactions.add(transactionID);
					break;
				}
				case BEGIN_READONLY:
				{
					String transactionID = operation.getTransactionID();
					
					// Throw an exception if the transaction already started
					if (readWriteTransactions.contains(transactionID)) {
						throw new IllegalStateException("Transaction " 
								+ transactionID + " already started");
					}
					if (readOnlyTransactions.contains(transactionID)) {
						throw new IllegalStateException("Transaction " 
								+ transactionID + " already started");
					}
					
					// Safely add transaction
					readOnlyTransactions.add(
						new ReadOnlyTransaction(transactionID, currentTime));
					break;
				}
				case FINISH:
				{
					// TODO
					// Commit value you had a lock on
					// Remove the lock
					break;
				}
				case READ:
				{
					// TODO
					// IF RO read from stable storage history
					// IF RW ask for read lock
					break;
				}
				case WRITE:
				{
					// TODO
					// Ask for write lock
					break;
				}
			}
		}
		currentTime++;
	}
	
	/**
	 * Tells the site to fail. The site loses all volatile data and lock 
	 * information. The transaction manager won’t be able to send messages to 
	 * this site until this site recovers.
	 */
	void fail () {
		// TODO
		isActive = false;
	}
	
	/**
	 * Tells the site to recover
	 */
	void recover () {
		// TODO
		isActive = true;
	}
	
	/**
	 * Returns the entire database state for this site.
	 * @return A string listing the variables and their value
	 */
	String dump () {
		StringBuilder sb = new StringBuilder();
		sb.append("Contents of site ");
		sb.append(siteID);
		sb.append(":\n");
		for (int i = 1; i <= 20; i++) {
			String variableID = "x" + i;
			if (stableStorage.containsKey(variableID)) {
				sb.append(variableID);
				sb.append("=");
				sb.append(stableStorage.get(variableID).get(0).getValue());
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns the value for variable xj at this site.
	 * @param xj The variable index
	 * @return A string containing the value of the variable
	 */
	String dump (String variableID) {
		if (stableStorage.containsKey(variableID)) {
			StringBuilder sb = new StringBuilder();
			sb.append("Site ");
			sb.append(siteID);
			sb.append(": ");
			sb.append(stableStorage.get(variableID).get(0).getValue());
			return sb.toString();
		}
		else return "";
	}
}
