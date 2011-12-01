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
	private HashMap <String, ArrayList <Integer>> variableMap; 
	private String transactionID; 
	private int operationIndex;  
	private Status status; 
	private ArrayList <Integer> sitesUp; 
	private ArrayList <Integer> sitesConcerned; 
	
	
	/**
	 * 
	 * @param variableMap
	 * @param transactionID
	 * @param sitesUp can only send a message to the sites that were up when the 
	 * Transaction instance was created
	 */
	Transaction (HashMap <String, ArrayList <Integer>> variableMap, String transactionID, ArrayList <Integer> sitesUp) {

		this.variableMap = variableMap; 
		this.transactionID = transactionID; 
		this.sitesUp = sitesUp; 
		sitesConcerned = (ArrayList<Integer>) this.sitesUp.clone();
		operationIndex = -1; 
		status = Status.ACTIVE; 
		operations = new ArrayList <Operation> (); 
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
	
		//Operation types can be W, R, End or Dump
		
		//Prevent adding operation to a Transaction that ended
		if (status != Status.END && status != Status.ABORTED){
			
			//On Dump: 		
			if (operation.getOperationID() == Operation.Opcode.DUMP){
				//TODO...
			}
			//On End: 
			else if (operation.getOperationID() == Operation.Opcode.FINISH){
				operations.add(operation);
			}
			//On W or R: 
			operations.add(operation);
			if (operations.size() == 1){
				operationIndex = 0; 
			}
	

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
	 * 1. If the site that failed hold a variable with Write Lock
	 * Then the transaction becomes incorrect
	 * 2. If the site that failed hold a unique variable used by the Transaction
	 * @param siteID
	 */
	void siteFailure (int siteID) {
		
		//1. Check if site was used
		for (Operation operation: operations){
			if (operation.getSiteID().contains(siteID)){
				if (operation.getOperationID() == Opcode.WRITE)
					status = Status.ABORTED; 
				
				String var = operation.getVariableID(); 
				if (variableMap.get(var).size() == 1)
					status = Status.ABORTED; 
			}
		}
		
		//2. Update the sitesUP: 
		sitesUp.remove(siteID);
	}
	
	/**
	 * Function called by TM at the end of each cycle. 
	 * Return the operation to be executed next
	 * @return operation   
	 */
	Operation getnextOperation() {
		
		Integer var = Integer.parseInt(operations.get(operationIndex).getVariableID().substring(1));
		if (var %2 != 0)
			sitesConcerned = (variableMap.get(operations.get(operationIndex).getVariableID()));
		
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

	public ArrayList <Integer> getSitesUp(){
		return sitesUp; 
	}
	
	public ArrayList <Integer> getSitesConcerned(){
		if (sitesConcerned.size() == 1)
			return sitesConcerned;
		else
			return sitesUp;
	}
	
	public int getOperationIndex(){
		return operationIndex; 
	}
	
	public Status getStatus(){
		return status;
	}

	
	@Override
	public String toString()
	{
		
		String s =  "Transaction ID: "+transactionID+"\nStatus "+status+"\nFollowing Operations:\n";
		for (Operation operation: operations){
			s+=operation+"\n";
		}
		return s;
	}

	
	
}
