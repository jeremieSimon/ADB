	package edu.nyu.cs.adb;

/**
 * A data structure defining a transaction. 
 * <br>Instance of Transaction will be created by the Transaction Manager 
 * <br>Each time the Transaction Manager will read from the Script ‘Begin Ti’, 
 * <br>it will create an instance of the transactions.
 * @author dandelarosa
 */
public final class Transaction {
	/**
	 * Set of sites = all sites 
	 * <br>status = run 
	 * <br>status can be changed to wait
	 */
	Transaction () {
		// TODO
	}
	
	/**
	 * Each time an operation is performed by Transaction, it is being added. 
	 * We want to keep track of the operation of a transaction in case 
	 * Transaction is being aborted, so it can restart later.
	 * @param operation Operation
	 */
	void addOperations (Operation operation) {
		// TODO
	}
	
	/**
	 * If a site fails, then it is removed from the set of sites. To make sure 
	 * that the transaction can still be run, is calls isTransactionCorrect()
	 * If it returns false, then the transaction is aborted
	 * @param siteID
	 */
	void siteFailure (int siteID) {
		// TODO
	}
	
	/**
	 * When this function is called, it sends the operation that was/were last 
	 * added to the set of sites
	 * @return send message to all sites concerned  
	 */
	void sendToSite () {
		// TODO
	}

	/**
	 * This function is called when the Transaction Manager reads ‘end Ti’ or 
	 * when a site fails. This function make sure that the Transaction can 
	 * commit. Typically, a transaction cannot commit if a site that stores a 
	 * unique variable failed during execution.
	 * @return True or False
	 */
	boolean isTransactionCorrect () {
		// TODO
		return false;
	}
}
