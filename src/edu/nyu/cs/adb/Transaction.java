	package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import edu.nyu.cs.adb.Operation.Opcode;

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
	
	
	public enum Status{
		ACTIVE, 
		WAIT,
		ABORTED, 
		END
	}
	
	
	private ArrayList <Operation> operations; 
	private ArrayList <Integer> sites; 
	private HashMap <String, ArrayList <Integer>> variableMap; 
	private String transactionID; 
	private boolean isDumpSuccessful = false; 
	private int operationIndex;  
	private Status status; 
	
	

	
	Transaction (HashMap <String, ArrayList <Integer>> variableMap, String transactionID) {
		// TODO
		this.variableMap = variableMap; 
		this.transactionID = transactionID; 
		operationIndex = 0; 
		status = Status.ACTIVE; 
		operations = new ArrayList <Operation> (); 
		sites = new ArrayList <Integer> ();
	}
	
	/**
	 *1. Each time an operation is performed by Transaction, it is being added. 
	 *We want to keep track of the operation of a transaction in case Transaction is being aborted, so it can restart later
	 *2. The sites on which the operation is active is being added to the list of sites used so far
	 *3. If the operation is on a site that is not on the list of sites that have been up so far, transaction is a failure
	 *4. Dump
	 * @param operation Operation
	 */
	void addOperations (Operation operation) {
	
		//Prevent adding operation to a Transaction that ended
		if (status != Status.END && status != Status.ABORTED){
			//0. Clear operations		
			if (isDumpSuccessful)
				operations.clear();
	
			if (operation.getOperationID() == Opcode.FINISH)
				status = Status.END; 
		
			//1. Add operation
			operations.add(operation);
	
			//2.
			String var = operation.getVariableID();
			ArrayList <Integer> site = variableMap.get(var);
			sites.addAll(site);
		}
	}
	/**
	 * 1. If response is success, increment the counter
	 * 2. If response is lock, wait, keep the operation for the next cycle 
	 * 3. If response is failure, set status to aborted, no more operation from that transaction can be sent
	 * @param response
	 */
	void updateTransaction(Response response){
		if (response.getStatus() == Response.Status.SUCCESS){
			operationIndex++; 
		}
		else if (response.getStatus() == Response.Status.LOCKED){
			status = Status.WAIT; 
		}
		else if (response.getStatus() == Response.Status.FAILURE){
			status = Status.ABORTED;
		}
	}
	
	/**
	 * 1. If the site that failed hold a unique variable with Write Lock
	 * Then the transaction becomes incorrect
	 * @param siteID
	 */
	void siteFailure (int siteID) {
		
		//Check if site was used
		for (Operation operation: operations){
			if (operation.getSiteID().contains(siteID)){
				String var = operation.getVariableID(); 
				if (variableMap.get(var).size() == 1 && operation.getOperationID() == Opcode.WRITE){
					status = Status.ABORTED; 
				}
				
			}
		}
	}
	
	/**
	 * Function called by TM at the end of each cycle. 
	 * Return the operation to be executed next
	 * @return operation   
	 */
	Operation getnextOperation() {
		return operations.get(operationIndex);
	}

	/**
	 * 1. A transaction cannot commit if a site that stores a 
	 * unique variable failed during execution.
	 * @return True or False
	 */
	boolean isTransactionCorrect () {
		if (status == Status.ABORTED)
			return false;
		else
			return true; 
	}
	
	//GETTER: 
	public String getTransactionID(){
		return transactionID; 
	}

	public ArrayList<Integer> getSites() {
		return sites;
	}

	
	
}
