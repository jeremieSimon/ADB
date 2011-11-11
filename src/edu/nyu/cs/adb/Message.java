package edu.nyu.cs.adb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A data structure that will be passed from the Transaction Manager to the 
 * Data Manager. Contains a set of operations, each from different 
 * transactions, for the Data Manager to process. Implements Iterable.
 * <p>Immutable</p>
 * @author dandelarosa
 */
public final class Message implements Iterable<Operation> {
	private final Collection<Operation> operations;
	
	/**
	 * Prevent Instantiation!
	 */
	private Message () {
		throw new AssertionError();
	}
	
	/**
	 * Construct a message from a builder object
	 * @param builder
	 */
	private Message (Builder builder) {
		// Use ArrayList for fast iteration
		List<Operation> operations = 
			new ArrayList<Operation>(builder.operations);
		this.operations = Collections.unmodifiableCollection(operations);
	}
	
	/**
	 * Builder class for Message
	 * @author dandelarosa
	 */
	public static class Builder {
		private final List<Operation> operations = new LinkedList<Operation>();
		
		/**
		 * Adds an operation to add to the message
		 * @param operation
		 * @return self
		 */
		public Builder addOperation (Operation operation) {
			this.operations.add(operation);
			return this;
		}
		
		/**
		 * Empties the builder
		 * @return self
		 */
		public Builder clear () {
			this.operations.clear();
			return this;
		}
		
		/**
		 * Creates a Message from the builder
		 * @return a new instance of Message
		 */
		public Message build () {
			return new Message(this);
		}
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
		return operations.iterator();
	}
}
