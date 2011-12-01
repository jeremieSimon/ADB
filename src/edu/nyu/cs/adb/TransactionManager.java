package edu.nyu.cs.adb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.nyu.cs.adb.Operation.Opcode;

/**
 * This class will read the input and perform operations with the DataManager 
 * objects which represent the site.
 * @author dandelarosa
 */
public final class TransactionManager {
	// Our data managers
	private final List<DataManager> dataManagers = 
		new ArrayList<DataManager>();
	
	private final BufferedReader input;
	private final PrintStream output;
	private HashMap <String, ArrayList<Integer>> variableMap;
	private HashMap <String, Transaction> transactionMap; 
		
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
		// Set up reading mechanisms
		File inputFile = new File(inname);
		InputStream in;
		try {
			in = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not open file" + inname);
		}
		InputStreamReader tempreader = new InputStreamReader(in);
		this.input = new BufferedReader(tempreader);
		
		// Set up writing mechanisms
		File outputFile = new File(outname);
		try {
			this.output = new PrintStream(outputFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not open file" 
					+ outname);
		}
		
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
	TransactionManager (InputStream in, PrintStream out) {
		// Set up reading and writing mechanisms
		InputStreamReader tempreader = new InputStreamReader(in);
		this.input = new BufferedReader(tempreader);
		this.output = out;
		
		variableMap = createVariableMap(); 
		transactionMap =  new HashMap <String, Transaction>(); 
		this.init();
		
	}
	
	/**
	 * Constructor's helper function
	 */
	private void init () {
		// Make data managers
		for (int siteID = 1; siteID <= 10; siteID++) {
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
					
					// begin(T1) says that T1 begins
					if (opcode.equals("begin")) {

						String transactionID = args[0];
						//create a new instance of transaction: 
						transactionMap.put(transactionID, new Transaction(variableMap, transactionID));
						
						Operation.Builder builder = 
							new Operation.Builder(Opcode.BEGIN);
						builder.setTransactionID(transactionID);
						Operation begin = builder.build();
						// TODO implement the rest
					}
					
					// beginRO(T3) says that T3 is read-only
					if (opcode.equals("beginRO")) {
						String transactionID = args[0];
						
						//create a new instance of transaction: 
						transactionMap.put(transactionID, new Transaction(variableMap, transactionID));
						
						Operation.Builder builder = 
							new Operation.Builder(Opcode.BEGIN_READONLY);
						builder.setTransactionID(transactionID);
						Operation beginRO = builder.build();
						// TODO implement the rest
					}
					
					// R(T1,x4) says transaction 1 wishes to read x4 (provided 
					// it can get the locks or provided it doesn't need the 
					// locks (if T1 is a read-only transaction)). It should 
					// read any up (i.e. alive) copy and return the current 
					// value.
					if (opcode.equals("R")) {
						String transactionID = args[0];
						String variableID = args[1];
						Operation.Builder builder = 
							new Operation.Builder(Opcode.READ);
						builder.setTransactionID(transactionID);
						builder.setVariableID(variableID);
						Operation read = builder.build();
						// TODO implement the rest
					}
					
					// W(T1,x6,v) says transaction 1 wishes to write all 
					// available copies of x6 (provided it can get the locks) 
					// with the value v.
					if (opcode.equals("W")) {
						String transactionID = args[0];
						String variableID = args[1];
						int writeValue = Integer.parseInt(args[2]);
						Operation.Builder builder = 
							new Operation.Builder(Opcode.WRITE);
						builder.setTransactionID(transactionID);
						builder.setVariableID(variableID);
						builder.setWriteValue(writeValue);
						Operation write = builder.build();
						// TODO implement the rest
					}
					
					// See below for specific cases for dump
					if (opcode.equals("dump")) {
						String arg = args[0];
						// If there are no arguments, print out the committed 
						// values of all variables at all sites, sorted per
						// site.
						if (arg.length() == 0) {
							for (DataManager dm : dataManagers) {
								output.print(dm.dump());
							}
						}
						// dump(xj) gives the committed values of all copies of
						// variable xj at all sites
						else if (arg.startsWith("x")) {
							String variableID = arg;
							for (DataManager dm : dataManagers) {
								output.print(dm.dump(variableID));
							}
						}
						// dump(i) gives the committed values of all copies of 
						// all variables at site i
						else {
							int siteID = Integer.parseInt(arg);
							if (siteID <= 0 || siteID > dataManagers.size()) {
								throw new AssertionError("Site " + siteID 
										+ " does not exist");
							}
							// Remember that sites are zero-indexed
							DataManager dm = dataManagers.get(siteID - 1);
							output.print(dm.dump());
						}
					}
					
					// end(T1) causes your system to report whether T1 can 
					// commit
					if (opcode.equals("end")) {
						String transactionID = args[0];
						
						//Remove the transaction from transactions
						transactionMap.remove(transactionID);
						
						Operation.Builder builder = 
							new Operation.Builder(Opcode.FINISH);
						builder.setTransactionID(transactionID);
						Operation endOperation = builder.build();
						// TODO implement the rest
					}
					
					// fail(6) says site 6 fails. (This is not issued by a 
					// transaction, but is just an event that the tester will
					// will execute.)
					if (opcode.equals("fail")) {
						
						int siteID = Integer.parseInt(args[0]);
						
						//call siteFailure for transaction concerned
						for (Transaction t: transactionMap.values()){
							ArrayList <Integer> sites = t.getSites();
							for (int site: sites){
								if (site == siteID){
									t.siteFailure(siteID);
								}
							}
						}
						
						
						
						if (siteID <= 0 || siteID > dataManagers.size()) {
							throw new AssertionError("Site " + siteID 
									+ " does not exist");
						}
						// Remember that sites are zero-indexed
						DataManager dm = dataManagers.get(siteID - 1);
						dm.fail();
					}
					
					// recover(7) says site 7 recovers. (Again, a tester-caused
					// event)
					if (opcode.equals("recover")) {
						int siteID = Integer.parseInt(args[0]);
						if (siteID <= 0 || siteID > dataManagers.size()) {
							throw new AssertionError("Site " + siteID 
									+ " does not exist");
						}
						// Remember that sites are zero-indexed
						DataManager dm = dataManagers.get(siteID - 1);
						dm.recover();
					}
					
					//At the end of each cycle, update the TransactionMap
				}
				
				// TODO implement the rest...
				// Now that the input has been processed, send messages
				
				// Sample Message generation code
				Operation.Builder beginOperationBuilder = 
					new Operation.Builder(Opcode.BEGIN);
				beginOperationBuilder.setTransactionID("T1");
				Operation beginT1 = 
					beginOperationBuilder.build();
				
				Message.Builder messageBuilder = new Message.Builder();
				messageBuilder.addOperation(beginT1);
				Message message = messageBuilder.build();
				
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
	 * Create a variable map, that is a map where each key is a variable and 
	 * the value is an array list where each element is a site
	 * @return
	 */
	private HashMap <String, ArrayList<Integer>> createVariableMap(){
		
		int NUMBER_OF_SITES = 10; 
		HashMap<String, ArrayList <Integer>> variableMap = new HashMap<String, ArrayList <Integer>>();

		ArrayList <Integer> sites = new ArrayList<Integer> (); 
		for (int i=0; i<NUMBER_OF_SITES; i++){
			sites.add(i);
		}
		
		for (int i=1; i<20; i++){
			if (i%2 == 0){
			variableMap.put("x"+i, sites);
			}
			else{
				ArrayList <Integer> A = new ArrayList<Integer>(); 
				A.add(((1+i)%10)); 
				variableMap.put("x"+i, A);
				}
		}
		return variableMap; 
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
