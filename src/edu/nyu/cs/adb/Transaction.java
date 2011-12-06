package edu.nyu.cs.adb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.nyu.cs.adb.Operation.Opcode;

/**
 * A data structure defining a transaction. 
 * <br>Instance of Transaction will be created by the Transaction Manager 
 * <br>Each time the Transaction Manager will read from the Script �Begin Ti�, 
 * <br>it will create an instance of the transactions.
 * @author dandelarosa
 */
public final class Transaction {
	
	/**
	 * A transaction is IDLE if: 
	 * The transaction started but no operation READ or WRITE were added
	 * A transaction is ACTIVE if: 
	 * Default status
	 * A transaction is WAIT if: 
	 * Transaction is blocked because of a lock or because it cannot commit
	 * A transaction is ABORTED if: 
	 * because of Deadlocks, site failures or timeout
	 * A Transaction is END if: 
	 * All commit were successful
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
	
	//Timeout variables: 
	private int timeout = 0; 
	final int TIMEOUT_DELAY = 30;

	//Variables use to keep track of the sites (up/down) 
	private ArrayList <Integer> sitesUp; 
	private ArrayList <Integer> originalSitesUp; 
	private ArrayList <Integer> sitesConcerned = new ArrayList <Integer>(); 
	
	//Variables use to know what lock the transaction is holding 
	//and waiting for 
	private Set <Lock> locksHold = new HashSet <Lock>();
	private Set <Lock> locksWait = new HashSet <Lock>();
	
	//Variables used in sendResponse
	private boolean isFailed = false;
	private boolean isLocked = false;
	private boolean isEndedSuccessfully = false;
	private boolean isResponse = false;
	
	//variables used when needs to abort a Transaction: 
	boolean isNotAborted = true; 
	
	private boolean isTransactionOver = false;
	
	/**
	 * @param variableMap
	 * @param transactionID
	 * @param sitesUp can only send a message to the sites that were up when the 
	 * Transaction instance was created
	 */
	Transaction (HashMap <String, ArrayList <Integer>> variableMap, String transactionID, ArrayList <Integer> sitesUp, int age) {

		this.variableMap = variableMap; 
		this.transactionID = transactionID; 
		this.sitesUp = (ArrayList<Integer>) sitesUp.clone(); 
		originalSitesUp = (ArrayList<Integer>) sitesUp.clone(); 
		System.out.println("Sites up when created "+this.originalSitesUp);
		operationIndex = -1; 
		status = Status.IDLE; 
		this.age = age;
	}

	
	/**
	 * This function is called by the Transaction Manager
	 * The operation that can be added are: 
	 * READ, WRITE, END, ABORTED
	 * @param operation Operation
	 */
	void addOperations (Operation operation) {
	
		//Operation types can be W, R, End
		//Prevent adding operation to a Transaction that ended
		//Status is ACTIVE or IDLE
		if (status != Status.END && status != Status.ABORTED){
			
			operations.add(operation);
			
			//if this is the first operation added: 
			//Init the pointer operationIndex
			if (operations.size() == 1)
				operationIndex = 0; 
						
			//ON R or W: 
			if (operation.getOperationID() == Operation.Opcode.READ || operation.getOperationID() == Operation.Opcode.WRITE){
				status = Status.ACTIVE;
			}
			
			//Nothing on FINISH operation
		}
		//On abort: 
		else if (status == Status.ABORTED && isNotAborted){
			operations.clear();
			Operation.Builder builder = new Operation.Builder(Opcode.ABORT);
			builder.setTransactionID(transactionID);
			Operation abort = builder.build();
			operations.add(abort);
			operationIndex = 0;
			
			//release all locks: 
			locksHold.clear();
			locksWait.clear();
		}
		
		//On end, no operation can be added
	}
	
	/**
	 * If any site respond with a lock, Transaction will be locked
	 * If any site respond with a fail, Transaction will fail
	 * If all sites answer successfully and the status is END. Transaction is over
	 * If all sites answer successfully and the status is ABORTED. Transaction is over
	 * If all sites answer successfully, message was successfull
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
		
		else if (response.getStatus() == Response.Status.SUCCESS && status == Status.END){
			isEndedSuccessfully = true;
		}
		
		else if (response.getStatus() == Response.Status.SUCCESS && status == Status.ABORTED){
			isEndedSuccessfully = true;
		}
	}
	
	/**
	 * This function is called by the Transaction Manager at the end of each cycle. 
	 * It re-init some variables
	 * Update status
	 * Parses responses send before
	 */
	void reinit(){
				
		//Operation ended succesfully: 
		 if (isEndedSuccessfully && !isLocked && !isFailed && isResponse){
			isTransactionOver = true;	
		}
		
		//Operation was a success: 
		 else if (!isFailed && !isLocked && isResponse && !isEndedSuccessfully){
			//re-init the timeout
			 timeout = 0;
			//acquire locks
			if (operationIndex > 0 && (operations.get(operationIndex).getOperationID() == Operation.Opcode.READ ||
					operations.get(operationIndex).getOperationID() == Operation.Opcode.WRITE)){
				 String lockType = operations.get(operationIndex).getOperationID().toString();
				 String variableID = operations.get(operationIndex).getVariableID();
				 locksHold.add(new Lock(variableID, lockType));
			}
			operationIndex++; 
			locksWait.clear();
			if (status == Status.WAIT)
				status = Status.ACTIVE;
		}
		
		//Operation could not succeed because of a lock
		else if (isLocked && !isFailed && isResponse){
			timeout++;
			
			status = Status.WAIT; 
			
			//Add a lock to locksWait
			boolean isLock = false; 
			String variableID = operations.get(operationIndex).getVariableID();
			String lockType = operations.get(operationIndex).getOperationID().toString();
			Lock lock = new Lock (variableID, lockType); 
			for (Lock l: locksWait){
				if (l.equals(lock)){
					isLock = true;
				}
			}
			if (!isLock)
				locksWait.add(new Lock (variableID, lockType));
		}
		
		//Operation failed: 
		//Release all locks
		else if (isFailed && isResponse){
			System.out.println("operation failed flag");
			status = Status.ABORTED;
			locksWait.clear();
			locksHold.clear();
			timeout = 0;
		}
		
		//No operation was sent and operation is not over: 
		else if (!isFailed && !isLocked && !isResponse && !isEndedSuccessfully){
			timeout++;
			//if the status is idle, init the operation index
			if (timeout>TIMEOUT_DELAY && status == Status.IDLE)
				operationIndex = 0;
		}
		
		
		//re-init the variables: 
		isFailed = false; 
		isLocked = false; 
		isEndedSuccessfully = false;
		isResponse = false;		
	}
	
	/**
	 * This function is called by the Transaction Manager. 
	 * This function says whether should abort or not. 
	 * The transaction abort if: 
	 * The site that failed hold a variable with Write Lock
	 * This function also make sure to update the lock. 
	 * If the site that failed hold a read lock on non-replicated variable 
	 * Then the should be removed
	 * @param siteID
	 */
	void siteFailure (int siteID) {
		
		//Only check for operations that were already executed
		//So all operations before operationIndex
		
		// if T is Active or Wait: 
		if (status == Status.ACTIVE || status == Status.WAIT){
			for (int i = 0; i<operationIndex; i++){
				//Check if site that failed was used previously 
				if (operations.get(i).getOperationID() != Operation.Opcode.BEGIN && operations.get(i).getSiteID().contains(siteID)){
					//If var was updated on any kind of site
					if (operations.get(i).getOperationID() == Opcode.WRITE){
						status = Status.ABORTED; 
					}
					String variableID = operations.get(i).getVariableID(); 
					int var = Integer.parseInt(variableID.substring(1));
					//If var was read or write on non-replicated site
					if (var %2 == 1){ 
						//if var was just read, then lock disapear
						if (operations.get(i).getOperationID() == Opcode.READ){
							Lock lock = new Lock (variableID, "READ");
							for (Lock l: locksHold){
								if (lock.equals(l))
									locksHold.remove(l);				
							}
						}
					}
				}
			}			
		}
		
		//2. Update the sitesUP: 
		sitesUp.remove(sitesUp.indexOf(siteID));
	}
	
	/**
	 * Function called by the Transaction Manager
	 * The transaction can recover if these 2 holds: 
	 * None of the operation executed before was a WRITE
	 * The site that recovers was originally up when Transaction was created 
	 * @param siteID
	 */
	void siteRecover(int siteID){
		
		boolean isWriteOperations = false; 
		boolean cantRecover = false; 

		System.out.println("Sites up when created "+this.originalSitesUp);

		//iter on all operations before the operation index: 
		for (int i=0; i<operationIndex; i++){
			//Check if there exist a WRITE operation: 
			System.out.println("debug "+operations.get(i).getOperationID()+" t "+transactionID);
			if (operations.get(i).getOperationID() == Operation.Opcode.WRITE){
				isWriteOperations = true;
			}
			if (!originalSitesUp.contains(siteID)){
				cantRecover = true;
			}
		}
		//then add the site to the list of the sites up: 
		if (!isWriteOperations && !cantRecover){
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
		
		if (operations.get(operationIndex).getOperationID() == Operation.Opcode.FINISH){
			status = Status.END;
			//release all locks: 
			locksHold.clear();
			locksWait.clear();
		}
		return operations.get(operationIndex);
	}

	
	//GETTER 
	
	/**
	 * This function is called at every cycle by the Transaction Manager. 
	 * It ensures that no message is sent to the wrong sites 
	 * and that no message is sent if the site is not concerned
	 * @return lists of the sites concerned by the operation
	 */
	public ArrayList <Integer> getSitesConcerned(){
		//if no operation was added, return null
		//No sites are concerned by the operation
		//Then no site will ask for the next operation
		if (operationIndex >= operations.size()){
			return new ArrayList <Integer>();
		}
		
		//All sites are concerned
		else if (status == Status.IDLE){
			return sitesUp;
		}
		
		//Aborted status: 
		else if (status == Status.ABORTED && isNotAborted){
			isNotAborted = false;
			return sitesUp;
		}
		else if  (status == Status.END){
			return sitesUp;
		}
		
		//Transaction is currently Active: 
		else{
			if (operations.get(operationIndex).getOperationID() == Operation.Opcode.FINISH){
				return sitesUp;
			}
			if (operations.get(operationIndex).getOperationID() == Operation.Opcode.ABORT){
				return new ArrayList <Integer>();
				}
			//See if variable is replicated or not
			Integer variableID = Integer.parseInt(operations.get(operationIndex).getVariableID().substring(1));
			if (variableID %2 != 0){
				//make sure that the sitesConcerned are up: 
				sitesConcerned = (variableMap.get(operations.get(operationIndex).getVariableID()));
				int siteID = sitesConcerned.get(0);
				if (sitesUp.contains(siteID))
					return sitesConcerned;
				//Site for commit is down
				else{
					status = Status.WAIT;
					return new ArrayList <Integer>();
				}
			}

			else{
				return sitesUp;
			}
		}
	}
	
	/**
	 * @return transactionID
	 */
	public String getTransactionID(){
		return transactionID; 
	}

	/**
	 * @return list of the sitesUp
	 */
	public ArrayList <Integer> getSitesUp(){
		return sitesUp; 
	}
	
	/**
	 * @return operationIndex
	 */
	public int getOperationIndex(){
		return operationIndex; 
	}
	
	/**
	 * @return status of the transaction
	 */
	public Status getStatus(){
		return status;
	}
	
	/**
	 * @return age of the transaction
	 */
	public int getAge(){
		return age;
	}

	/**
	 * @return set of locks
	 */
	public Set <Lock> getLocksHold(){
		return locksHold;
	}
	
	/**
	 * @return set of locks
	 */
	public Set <Lock> getLocksWait(){
		return locksWait;
	}
	
	/**
	 * This function is called by the transaction Manager
	 * in case the status has to be changed
	 * @param status
	 */
	public void setStatus(Status status){
		this.status = status;
	}
	
	/**
	 * @return timeout
	 */
	public int getTimeout(){
		return timeout;
	}
	
	/**
	 * @return true if the transaction has ended or is aborted
	 * false otherwise
	 */
	public boolean getIsTransactionOver(){
		return isTransactionOver;
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
		s+="\nSites Up: "+sitesUp+"\n";
		return s;
	}

	
	
}
