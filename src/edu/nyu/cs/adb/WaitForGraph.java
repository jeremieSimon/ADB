package edu.nyu.cs.adb;

/**
 * A data structure used to detect deadlocks.
 * <br>This class is a singleton. 
 * <br>When an instance is created, it creates the structure.
 * @author dandelarosa
 */
public final class WaitForGraph {
	/**
	 * This function is called by the Transaction Manager. The Transaction 
	 * Manager will decide to call this function if it thinks that the 
	 * transaction should wait. 
	 * <br>The waiting Transaction it is being added to the graph
	 * @param transaction Transaction
	 */
	void addNode (Transaction transaction) {
		// TODO
	}
	
	/**
	 * When a transaction can get the lock, it is removed from the graph  
	 * @param transaction transaction
	 */
	void removeNode (Transaction transaction) {
		// TODO
	}
	
	/**
	 * This function will be called by the Transaction Manager.
	 * <br>This function will be called when transactions have been waiting 
	 * for too long.
	 * <br>It runs a breadth First Search to look for cycle.
	 * <br>If cycle is found, it kills Transaction such that the graph is 
	 * acyclic
	 * @return True or False
	 */
	boolean isDeadlock() {
		// TODO
		return false;
	}
}
