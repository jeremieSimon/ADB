package edu.nyu.cs.adb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * This class will read the input and perform operations with the DataManager 
 * objects which represent the site.
 * @author dandelarosa
 */
public final class TransactionManager {
	// TODO declare member variables here...
	private final BufferedReader input;
	private final BufferedWriter output;
	
	// Operation Generators
	private final Operation.Builder beginOperationBuilder = 
		new Operation.Builder(Operation.Opcode.BEGIN);
	private final Operation.Builder beginROOperationBuilder = 
		new Operation.Builder(Operation.Opcode.BEGIN_READONLY);
	private final Operation.Builder readOperationBuilder = 
		new Operation.Builder(Operation.Opcode.READ);
	private final Operation.Builder writeOperationBuilder = 
		new Operation.Builder(Operation.Opcode.WRITE);
	private final Operation.Builder finishOperationBuilder = 
		new Operation.Builder(Operation.Opcode.FINISH);
	private final Operation.Builder abortOperationBuilder = 
		new Operation.Builder(Operation.Opcode.ABORT);
	
	// Message Generator
	private final Message.Builder messageBuilder= new Message.Builder();
	
	/**
	 * This is the constructor, with additional functionality
	 * <ol>
	 * <li>Create all sites, a site is a DM object</li>
	 * <li>Assign each variable to the proper site</li>
	 * <li>Create a variable map, that is a map where each key is a variable 
	 * and the value would be a linked list where each node would be a site
	 * </li>
	 * <li>Create an instance of Wait-For-Graph</li>
	 * </ol>
	 * @param inname filename of the input
	 * @param outname filename of the output
	 * @throws IllegalArgumentException if there is a file the program cannot 
	 * read
	 */
	TransactionManager (String inname, String outname) {
		File inputFile = new File(inname);
		File outputFile = new File(outname);
		InputStream in;
		try {
			in = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not open file" + inname);
		}
		OutputStream out;
		try {
			out = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not open file" 
					+ outname);
		}
		
		// Set up reading and writing mechanisms
		InputStreamReader tempreader = new InputStreamReader(in);
		this.input = new BufferedReader(tempreader);
		OutputStreamWriter tempwriter = new OutputStreamWriter(out);
		this.output = new BufferedWriter(tempwriter);
		
		this.init();
	}
	
	/**
	 * This is the constructor, with additional functionality
	 * <ol>
	 * <li>Create all sites, a site is a DM object</li>
	 * <li>Assign each variable to the proper site</li>
	 * <li>Create a variable map, that is a map where each key is a variable 
	 * and the value would be a linked list where each node would be a site
	 * </li>
	 * <li>Create an instance of Wait-For-Graph</li>
	 * </ol>
	 * @param in the input stream
	 * @param out the output stream
	 */
	TransactionManager (InputStream in, OutputStream out) {
		// Set up reading and writing mechanisms
		InputStreamReader tempreader = new InputStreamReader(in);
		this.input = new BufferedReader(tempreader);
		OutputStreamWriter tempwriter = new OutputStreamWriter(out);
		this.output = new BufferedWriter(tempwriter);
		this.init();
	}
	
	/**
	 * Constructor's helper function
	 */
	private void init () {
		// TODO Make data managers and stuff
	}
	
	/**
	 * Runs the simulation, taking a line of input in each timestep.
	 * <ul>
	 * <li>On ‘begin Ti’, it creates a new instance of Transaction</li>
	 * <li>On ‘end Ti’, it deletes the instance i of Transaction</li>
	 * <li>On write, or read, it calls the addOperations(Operation) of the 
	 * transaction concerned</li>
	 * </ul>
	 */
	public void run () {
		try {
			// Read the first line
			String currentLine = input.readLine();
			while (currentLine != null) {
				// TODO implement the rest...
				
				// Sample Message generation code
				messageBuilder.clear();
				beginOperationBuilder.setTransactionID(1);
				Operation beginT1 = 
					beginOperationBuilder.build();
				messageBuilder.addOperation(beginT1);
				Message message = messageBuilder.build();
				
				
				output.write(""); // temporary code So the compiler doesn't throw an exception...
				
				// Read the next line
				currentLine = input.readLine();
			}
		}
		catch (IOException e) {
			throw new AssertionError("I/O failure");
		}
	}
	
	/**
	 * Send a response to the Transaction Manager. This intended to be called 
	 * after a Data Manager has processed the message it received earlier in 
	 * the timestep.
	 * @param response The response object
	 */
	public void sendResponse (Response response) {
		// TODO: implement
	}
}
