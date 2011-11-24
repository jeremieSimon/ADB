package edu.nyu.cs.adb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The representation of a database site: contains a map for stored variable 
 * values as well as  lock data for its own site to see which transactions 
 * have a lock on each variable
 * @author dandelarosa
 */
public final class DataManager {
	private final int siteID;
	int currentTime = 0;
	private Message currentMessage = null;
	Map<String, List<CommittedValue>> stableStorage 
		= new HashMap<String, List<CommittedValue>>();
	
	/**
	 * Constructor
	 * Ideally we'd actually do initialization of variables via a builder
	 * object, but we're doing it in here instead for the purposes of the
	 * project.
	 * @param siteID
	 */
	DataManager(int siteID) {
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
		private CommittedValue(int value, int timestamp) {
			this.value = value;
			this.timestamp = timestamp;
		}

		/**
		 * Gets the committed value
		 * @return
		 */
		private int getValue() {
			return value;
		}

		/**
		 * Gets the time when the value was committed
		 * @return
		 */
		private int getTimestamp() {
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
		if (currentMessage != null) return;
		
		for (Operation operation : currentMessage) {
			switch (operation.getOperationID()) {
				case ABORT:
					// TODO
					break;
				case BEGIN:
					// TODO
					break;
				case BEGIN_READONLY:
					// TODO
					break;
				case FINISH:
					// TODO
					break;
				case READ:
					// TODO
					break;
				case WRITE:
					// TODO
					break;
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
	}
	
	/**
	 * Tells the site to recover
	 */
	void recover () {
		// TODO
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
