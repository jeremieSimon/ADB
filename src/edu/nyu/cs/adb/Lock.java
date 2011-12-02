package edu.nyu.cs.adb;

public class Lock {
	
	public enum LockType{
		READ, 
		WRITE
	}
	
	private String variableID; 
	private LockType lockType; 
	
	public Lock(String variableID, LockType lockType){
		
		this.variableID = variableID; 
		this.lockType = lockType; 
	}

	public String getVariableID() {
		return variableID;
	}

	public LockType getLockType() {
		return lockType;
	}

	
	
}
