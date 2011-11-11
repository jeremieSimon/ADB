package edu.nyu.cs.adb;

import java.util.Iterator;

/**
 * A data structure that will be passed from the Transaction Manager to the 
 * Data Manager. Contains a set of operations, each from different 
 * transactions, for the Data Manager to process. Implements Iterable.
 * @author dandelarosa
 */
public final class Message implements Iterable<Operation> {
	/**
	 * Prevent Instantiation!
	 */
	private Message () {
		throw new AssertionError();
	}
	
	/**
	 * Gets an iterator object, in compliance with the Iterator 
	 * interface/design pattern.
	 * @return An iterator with next() and hasNext() functions; this is used 
	 * to look at the operations in the order that they were placed in the 
	 * operation.
	 */
	@Override
	public Iterator<Operation> iterator () {
		// TODO Auto-generated method stub
		return null;
	}
}
