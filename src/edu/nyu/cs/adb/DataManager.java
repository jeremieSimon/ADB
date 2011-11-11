package edu.nyu.cs.adb;

/**
 * The representation of a database site: contains a map for stored variable 
 * values as well as  lock data for its own site to see which transactions 
 * have a lock on each variable
 * @author dandelarosa
 */
public final class DataManager {
	/**
	 * Sends a message to the Data Manager
	 * @param message The message object
	 */
	void sendMessage (Message message) {
		// TODO
	}
	
	/**
	 * Processes the message in the message buffer. To be called at every 
	 * timestep.
	 */
	void update () {
		// TODO
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
		// TODO
		return "";
	}
	
	/**
	 * Returns the value for variable xj at this site.
	 * @param xj The variable index
	 * @return A string containing the value of the variable
	 */
	String dump (int xj) {
		// TODO
		return "";
	}
}
