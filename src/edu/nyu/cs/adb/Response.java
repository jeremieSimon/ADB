package edu.nyu.cs.adb;

/**
 * A response object sent from the Data Manager to the Transaction Manager 
 * @author dandelarosa
 */
public final class Response {
	private final int siteID;
	private final int transactionID;
	private final int status;
	private final int readValue;
	
	/**
	 * Prevent instantiation!
	 */
	private Response () {
		throw new AssertionError();
	}
	
	/**
	 * Reads which site the response is from
	 * @return The site ID
	 */
	public int getSiteID () { return siteID; }
	
	/**
	 * Reads which transaction the site is referring to
	 * @return The transaction ID
	 */
	public int getTransactionID () { return transactionID; }
	
	/**
	 * Reads the status of the transaction
	 * @return The status ID (later this can be elaborated upon to describe 
	 * the different status codes: fail, locked, etc.)
	 */
	public int getStatus () { return status; } // TODO use status enum instead
	
	/**
	 * Reads the value, usually this in response to a read request
	 * @return The value returned from the a read operation
	 */
	public int getReadValue () { return readValue; }
}
