	package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
	
	/**
	 * A transaction is said IDLE, when it starts but has no operation on going
	 * After a transaction is asked to read or write, a transaction becomes active
	 */
	public enum Status{
		IDLE,
		ACTIVE, 
		WAIT,
		ABORTED, 
		END
	}
	
	
	private ArrayList <Operation> operations = new ArrayList <Operation> ();
	private HashMap <String, ArrayList <Integer>> variableMap; 
	private String transactionID; 
	private int operationIndex;  
	private int age; 
	private Status status; 
	private ArrayList <Integer> sitesUp; 
	private ArrayList <Integer> sitesConcerned = new ArrayList <Integer>(); 
	
	//Variables use to know what lock the transaction is holding 
	//and waiting for 
	private Set <Lock> locksHold = new HashSet <Lock>();
	private Set <Lock> locksWait = new HashSet <Lock>();
	
	//Variables used in sendResponse
	private boolean isFailed = false;
	private boolean isLocked = false;
	private boolean isResponse = false;
	
	//variables used when needs to abort a Transaction: 
	boolean isAborted = true; 
	
	/**
	 * 
	 * @param variableMap
	 * @param transactionID
	 * @param sitesUp can only send a message to the sites that were up when the 
	 * Transaction instance was created
	 */
	Transaction (HashMap <String, ArrayList <Integer>> variableMap, String transactionID, ArrayList <Integer> sitesUp, int age) {

		this.variableMap = variableMap; 
		this.transactionID = transactionID; 
		this.sitesUp = (ArrayList<Integer>) sitesUp.clone(); 
		//sitesConcerned = (ArrayList<Integer>) this.sitesUp.clone();
		operationIndex = -1; 
		status = Status.IDLE; 
		this.age = age;
	}
	
	Transaction(String transactionID){
		this.transactionID = transactionID; 
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
	
		//Operation types can be W, R, End
		
		//Prevent adding operation to a Transaction that ended
		if (status != Status.END && status != Status.ABORTED){
			
			//On End: 
			if (operation.getOperationID() == Operation.Opcode.FINISH){
				operations.add(operation);
				//if the operation is end, then status of the transaction to end: 
				if (operations.get(operationIndex).getOperationID() == Operation.Opcode.FINISH){
					status = Status.END;
					
					//release all locks: 
					locksHold.clear();
					locksWait.clear();
				}
			}
			//On WRITE, READ: 
			else{
				operations.add(operation);
			
				//if this is the first operation added: 
				//Init the pointer operationIndex
				if (operations.size() == 1)
					operationIndex = 0; 
							
				//ON R or W: 
				if (operation.getOperationID() == Operation.Opcode.READ || operation.getOperationID() == Operation.Opcode.WRITE){
					status = Status.ACTIVE;
				}
			}
		}
		//On abort: 
		else if (status == Status.ABORTED && isAborted){
			Operation.Builder builder = 
					new Operation.Builder(Opcode.ABORT);
			builder.setTransactionID(transactionID);
			Operation abort = builder.build();
			operations.clear();
			operations.add(abort);
			operationIndex = 0;
			
			//release all locks: 
			locksHold.clear();
			locksWait.clear();
		}
	}
	
	/**
	 * 1. If response is success, increment the counter
	 * 2. If response is lock, wait, keep the operation for the next cycle 
	 * 3. If response is failure, set status to aborted, no more operation from that transaction can be sent
	 * @param response
	 */
	void sendResponse(Response response){
		
		isResponse = true;
		
		if (response.getStatus() == Response.Status.LOCKED){
			isLocked = true; 
		}
		
		else if (response.getStatus() == Response.Status.FAILURE){
			isFailed = true; 
		}
		
	}
	
	/**
	 * This function is called by the TM at the end of each cycle. 
	 * It re-init some variables
	 */
	void reinit(){
		
		//Operation was a success: 
		if (!isFailed && !isLocked && isResponse){
			if (operationIndex > 0 && (operations.get(operationIndex).getOperationID() == Operation.Opcode.READ ||
					operations.get(operationIndex).getOperationID() == Operation.Opcode.WRITE)){
				 String lockType = operations.get(operationIndex).getOperationID().toString();
				 String variableID = operations.get(operationIndex).getVariableID();
				 locksHold.add(new Lock(variableID, lockType));
			}
			operationIndex++; 
			locksWait.clear();
		}
		
		//Operation could not succeed because of a lock
		else if (isLocked && !isFailed && isResponse){
			status = Status.WAIT; 
			
			//Add a lock to locksWait
			String variableID = operations.get(operationIndex).getVariableID();
			String lockType = operations.get(operationIndex).getOperationID().toString();
			locksWait.add(new Lock (variableID, lockType));
		}
		
		//Operation failed: 
		//Release all locks
		else if (isFailed && isResponse){
			isFailed = true; 
			status = Status.ABORTED;
			locksWait.clear();
			locksHold.clear();
		}
		
		//re-init the variables: 
		isFailed = false; 
		isLocked = false; 
		isResponse = false;		
	}
	
	/**
	 * 1. If the site that failed hold a variable with Write Lock
	 * Then the transaction becomes incorrect
	 * 2. If the site that failed hold a unique variable used by the Transaction
	 * @param siteID
	 */
	void siteFailure (int siteID) {
		
		// if T is Active or Wait: 
		if (status == Status.ACTIVE || status == Status.WAIT){
			for (Operation operation: operations){
				//Check if site that failed was used previously 
				if (operation.getOperationID() != Operation.Opcode.BEGIN && 
						operation.getSiteID().contains(siteID)){
					//If var was updated and var is on replicated site
					if (operation.getOperationID() == Opcode.WRITE){
						status = Status.ABORTED; 
					}
					String variableID = operation.getVariableID(); 
					//If var was read or write on non-replicated site
					if (variableMap.get(variableID).size() == 1){
						//if var was updated
						if (operation.getOperationID() == Opcode.WRITE)
							status = Status.ABORTED; 
						//if var was just read, then lock disapear
						else if (operation.getOperationID() == Opcode.READ){
							String lockType = "READ";
							Lock lock = new Lock (variableID, lockType);
							locksWait.remove(lock);
						}
					}
				}
			}			
		}
		
		//2. Update the sitesUP: 
		sitesUp.remove(sitesUp.indexOf(siteID));
	}
	
	/**
	 * Case 1. If no operations ever wrote, then site is added to the list
	 * @param siteID
	 */
	void siteRecover(int siteID){
		
		boolean isWriteOperations = false; 
		//iter on all operations: 
		for (Operation operation: operations){
			//Check if there exist WRITE operation: 
			if (operation.getOperationID() == Operation.Opcode.WRITE){
				isWriteOperations = true;
			}
		}
		
		//then add the site to the list of the sites up: 
		if (!isWriteOperations){
			sitesUp.add(siteID);
			Collections.sort(sitesUp);
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

	
	//GETTER: 
	public ArrayList <Integer> getSitesConcerned(){
		//if no operation was added, return null
		//No sites are concerned by the operation
		//Then no site will ask for the next operation
		if (operationIndex >= operations.size())
			return new ArrayList <Integer>();
		
		//All sites are concerned
		else if (status == Status.IDLE)
			return sitesUp;
		
		//No site is concerned: 
		else if (status == Status.ABORTED && isAborted){
			isAborted = false;
			return sitesUp;
		}
		else if  (status == Status.END){
			return sitesUp;
		}
		
		//Transaction is Active: 
		else{
			//See if variable is replicated or not
			Integer variableID = Integer.parseInt(operations.get(operationIndex).getVariableID().substring(1));
			if (variableID %2 != 0){
				sitesConcerned = (variableMap.get(operations.get(operationIndex).getVariableID()));
				return sitesConcerned;
			}

			else{
				return sitesUp;
			}
		}
	}
	
	public String getTransactionID(){
		return transactionID; 
	}

	public ArrayList <Integer> getSitesUp(){
		return sitesUp; 
	}
	
	public int getOperationIndex(){
		return operationIndex; 
	}
	
	public Status getStatus(){
		return status;
	}

	public Set <Lock> getLocksHold(){
		return locksHold;
	}
	
	public Set <Lock> getLocksWait(){
		return locksWait;
	}
	
	//TO BE REMOVED
	public void addLocksHold(Lock lock){
		locksHold.add(lock);
	}

	//TO BE REMOVED
	public void addLocksWait(Lock lock){
		locksWait.add(lock);
	}
	
	
	@Override
	public String toString()
	{
		
		String s =  "Transaction ID: "+transactionID+"\nStatus "+status+"\nFollowing Operations:\n";
		for (Operation operation: operations){
			s+=operation+"\n";
		}
		s+="lock hold "+locksHold;
		s+="\nlock wait "+locksWait;
		s+="\nSites Up: "+sitesUp;
		return s;
	}

	
	
}
