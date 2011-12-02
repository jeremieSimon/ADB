package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.HashMap;
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
	private ArrayList <Transaction> transactions = new ArrayList <Transaction>();
	private HashMap <Transaction, HashSet <String>> graph = new HashMap <Transaction, HashSet <String>>();
	
	void addTransaction (Transaction transaction) {
		
		//If transaction is already here, update the transaction
		if (transactions.contains(transaction.getTransactionID())){
			updateTransaction(transaction);
		}
		else{
			transactions.add(transaction);
		
			if (graph.isEmpty())
				graph.put(transaction, null);
			
			else{
				graph.put(transaction, null);
				updateTransaction(transaction);
			}

		}
	}
	
	/**
	 * A transaction might acquire new locks while
	 * other are waiting. We wanna keep updating the transaction
	 * @param transaction
	 */
	private void updateTransaction(Transaction transaction){
		ArrayList <String> variables = new ArrayList <String>();
		for (Lock lock : transaction.getLocks()){
			if (lock.getLockType() == "WRITE")
				variables.add(lock.getVariableID());
		}
		for (Transaction T: graph.keySet()){
			for (Lock lock:  T.getLocks()){
				if (variables.contains(lock.getVariableID())){
					graph.get(transaction).add(T.getTransactionID());
				}
			}
		}
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
