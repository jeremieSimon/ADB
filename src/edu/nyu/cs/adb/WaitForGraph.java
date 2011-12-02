package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A data structure used to detect deadlocks.
 * <br>This class is a singleton. 
 * <br>When an instance is created, it creates the structure.
 * @author dandelarosa
 */
public final class WaitForGraph {

	private ArrayList <Transaction> transactions = new ArrayList <Transaction>();
	private HashMap <Transaction, HashSet <String>> waitForGraph = new HashMap <Transaction, HashSet <String>>();

	
	/**
	 * This function is called when a transaction is waiting. 
	 * @param transaction Transaction
	 */
	void addTransaction (Transaction transaction) {
		
		//If transaction is already here, update the transaction
		if (transactions.contains(transaction.getTransactionID())){
			updateTransaction(transaction);
		}
		else{
			transactions.add(transaction);
		
			if (waitForGraph.isEmpty()){
				waitForGraph.put(transaction, null);
			}
			else{
				//waitForGraph.put(transaction, null);
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
		HashSet <String> edges = new HashSet <String>();
		for (Lock lock : transaction.getLocksWait()){
			if (lock.getLockType() == "WRITE")
				variables.add(lock.getVariableID());
		}
		for (Transaction T: waitForGraph.keySet()){
			for (Lock lock:  T.getLocksHold()){
				if (variables.contains(lock.getVariableID())){
					edges.add(T.getTransactionID());
				}
			}
		}
		waitForGraph.put(transaction, edges);
	}
	
	/**
	 * Called when a transaction go from WAIT to ACTIVE or ABORTED 
	 * @param transaction transaction
	 */
	void removeNode (Transaction transaction) {
		waitForGraph.remove(transaction);
		
		//Need to update all graph
		for (Transaction t: waitForGraph.keySet()){
			updateTransaction(t);
		}
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
	
	/**
	 * This function is called if a deadlock is detected in the graph
	 * It tells the TM which Transaction to abort
	 * @return Transaction object
	 */
	private Transaction abortTransaction(){
		return null;
	}
	
	//GETTER: 
	public HashMap <Transaction, HashSet <String>> getWaitForGraph(){ 
		return waitForGraph; 
	}
	
	public static void main(String[]args){
		
		WaitForGraph graph = new WaitForGraph(); 
		Transaction t1 = new Transaction("T1");
		t1.addLocksHold(new Lock("x1", "WRITE"));
		t1.addLocksHold(new Lock("x2", "WRITE"));
		t1.addLocksWait(new Lock("x3", "WRITE"));
		
		Transaction t2 = new Transaction("T2");
		t2.addLocksHold(new Lock("x4", "WRITE"));
		t2.addLocksWait(new Lock("x1", "WRITE"));
		
		Transaction t3 = new Transaction("T2");
		t3.addLocksHold(new Lock("x5", "WRITE"));
		t3.addLocksWait(new Lock("x4", "WRITE"));
		
		graph.addTransaction(t1);
		graph.addTransaction(t2);
		graph.addTransaction(t3);

		
		for (HashSet<String> set : graph.getWaitForGraph().values()){
			System.out.println("Transaction:");
				System.out.println("s "+set);
		}
		
		
		
	}
}
