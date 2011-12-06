package edu.nyu.cs.adb;

public class Lock {

	private String variableID; 
	private String lockType; 
	
	/**
	 * Lock  object built in the Transaction function. 
	 * @param variableID
	 * @param lockType
	 */
	public Lock(String variableID, String lockType){
		
		this.variableID = variableID; 
		this.lockType = lockType; 
	}

	public String getVariableID() {
		return variableID;
	}

	public String getLockType() {
		return lockType;
	}
	
	/**
	 * Compare locks between each other 
	 * @param lock
	 * @return true if both the type and the variableID are the same
	 */
	public boolean equals(Lock lock){
		if (lock.getLockType().compareTo(this.lockType) == 0 &&
				lock.getVariableID().compareTo(this.variableID) == 0){
			return true;
		}
		return false; 
	}
	
	@Override
	public String toString(){
		String s = "";
		s+="Var: "+variableID;
		s+=" Type: "+lockType;
		return s;
	}

	
	
}
