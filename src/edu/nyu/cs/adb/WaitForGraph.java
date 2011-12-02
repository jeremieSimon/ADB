package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A data structure used to detect deadlocks.
 * <br>This class is a singleton. 
 * <br>When an instance is created, it creates the structure.
 * @author dandelarosa
 */
public final class WaitForGraph {
	/**
	 * This function is called when a transaction is waiting. 
	 * @param transaction Transaction
	 */
	
	private ArrayList <HashSet <Transaction>> transactions = new ArrayList<HashSet <Transaction>>();
	
	void addNode (Transaction transaction) {
		
	}
	
	/**
	 * Called when a transaction go from WAIT to ACTIVE or ABORTED 
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
