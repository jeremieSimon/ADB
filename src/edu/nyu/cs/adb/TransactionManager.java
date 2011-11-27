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
import java.util.ArrayList;
import java.util.List;

/**
 * This class will read the input and perform operations with the DataManager 
 * objects which represent the site.
 * @author dandelarosa
 */
public final class TransactionManager {
	// Our data managers
	private final List<DataManager> dataManagers = 
		new ArrayList<DataManager>();
	
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
		// Make data managers
		for (int siteID = 1; siteID <= 20; siteID++) {
			dataManagers.add(new DataManager(this, siteID));
		}
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
				// Ignore lines that start with "//"
				if (currentLine.startsWith("//")) {
					// Ignore this line and go to the next one
					currentLine = input.readLine();
					continue;
				}
				
				// If there are multiple operations per line, each operation is
				// separated by a semicolon and a space ("; ")
				String [] operations = currentLine.split("; ");
				for (String operation : operations) {
					// In each operation, the opcode is before the parentheses 
					// and the arguments are inside them
					int lparen = operation.indexOf('(');
					int rparen = operation.indexOf(')');
					// Bug out if parenthesis aren't found
					if (lparen == -1 || rparen == -1) {
						throw new AssertionError("Input line " + currentLine 
								+ " missing parenthesis on " + operation);
					}
					String opcode = operation.substring(0, lparen);
					String arglist = operation.substring(lparen+1, rparen);
					
					// If there are multiple arguments, each is separated by a 
					// comma
					String [] args = arglist.split(",");
					
					// TODO implement the rest...
					// if(opcode.equals())...
				}
				
				// TODO implement the rest...
				
				// Sample Message generation code
				messageBuilder.clear();
				beginOperationBuilder.setTransactionID("T1");
				Operation beginT1 = 
					beginOperationBuilder.build();
				messageBuilder.addOperation(beginT1);
				Message message = messageBuilder.build();
				
				
				output.write(""); // temporary code So the compiler doesn't throw an exception...
				
				// Update the data sites
				for (DataManager site : dataManagers) {
					site.update();
				}
				
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
