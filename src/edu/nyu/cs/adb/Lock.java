package edu.nyu.cs.adb;

public class Lock {
	

	
	private String variableID; 
	private String lockType; 
	
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

	
	
}
