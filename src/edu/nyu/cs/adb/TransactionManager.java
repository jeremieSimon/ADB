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
import java.util.Collections;
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
	
	//Variables used to create the variableMap: 
	private final int NUMBER_OF_SITES = 10; 
	private final int NUMBER_OF_VAR = 20;
	
	//Variable used to ensure that all transactions are over
	private int numberOfTransactions = 0;
	private int numberOfTransactionsOver = 0;
	private int numberOfWaitingTransactions = 0;

	
	private final BufferedReader input;
	private final PrintStream output;
	private HashMap <String, ArrayList<Integer>> variableMap;
	private HashMap <String, Transaction> transactionMap; 
	private ArrayList <Integer> sitesUp = new ArrayList <Integer>(); 
	private Message.Builder[] messageBuilders = new Message.Builder [NUMBER_OF_SITES]; 	
	private Message[] messages = new Message [NUMBER_OF_SITES];
	private ArrayList <Response>[] siteResponses = new ArrayList [NUMBER_OF_SITES];
	private int age = 0; 


	
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
		run();
	}
		
	/**
	 * Constructor's helper function
	 */
	private void init () {
		// Make data managers
		for (int siteID = 1; siteID <= NUMBER_OF_SITES; siteID++) {
			dataManagers.add(new DataManager(siteID));
			sitesUp.add(siteID);
		}
		
		//Create the maps
		variableMap = createVariableMap(); 
		transactionMap =  new HashMap <String, Transaction>(); 
		
		//init the messages: 
		for (int i=0; i<NUMBER_OF_SITES; i++){
			messageBuilders[i] = new Message.Builder(); 
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
				//Skip line if line is blank: 
				if (currentLine.length() == 0){
					currentLine = input.readLine();
					continue;
				}
				
				// If there are multiple operations per line, each operation is
				// separated by a semicolon and a space ("; ")
				String [] operations = currentLine.split("; ");
				for (String operation : operations) {
					age++;
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

						numberOfTransactions++;
						String transactionID = args[0];
						//create a new instance of transaction: 
						transactionMap.put(transactionID, new Transaction(variableMap, transactionID, sitesUp, age));
						Operation.Builder builder = 
							new Operation.Builder(Opcode.BEGIN);
						builder.setTransactionID(transactionID);
						Operation begin = builder.build();

						transactionMap.get(transactionID).addOperations(begin);

					}
					
					// beginRO(T3) says that T3 is read-only
					if (opcode.equals("beginRO")) {
						
						numberOfTransactions++;
						String transactionID = args[0];
						
						//create a new instance of transaction: 
						transactionMap.put(transactionID, new Transaction(variableMap, transactionID, sitesUp, age));
						
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
						
						// Append the operation to the correct transaction: 
						transactionMap.get(transactionID).addOperations(read);
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
						
						// Append the operation to the correct transaction: 
						transactionMap.get(transactionID).addOperations(write);
					}
					
					// See below for specific cases for dump
					if (opcode.equals("dump")) {
						String arg = args[0];
						// If there are no arguments, print out the committed 
						// values of all variables at all sites, sorted per
						// site.
												
						if (arg.length() == 0) {
							for (DataManager dm : dataManagers) {
								System.out.println(dm.dump());
							}
						}
						// dump(xj) gives the committed values of all copies of
						// variable xj at all sites
						else if (arg.startsWith("x")) {
							String variableID = arg;
							for (DataManager dm : dataManagers) {
								System.out.println(dm.dump(variableID));
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
							System.out.println(dm.dump());
						}
					}
					
					// end(T1) causes your system to report whether T1 can 
					// commit
					if (opcode.equals("end")) {
						String transactionID = args[0];
												
						Operation.Builder builder = 
							new Operation.Builder(Opcode.FINISH);
						builder.setTransactionID(transactionID);
						Operation endOperation = builder.build();
						//Add end operation
						transactionMap.get(transactionID).addOperations(endOperation);

					}
					
					// fail(6) says site 6 fails. (This is not issued by a 
					// transaction, but is just an event that the tester will
					// will execute.)
					if (opcode.equals("fail")) {
						
						int siteID = Integer.parseInt(args[0]);
						
						System.out.println("Site failure "+siteID);
						
						//0. remove site from sitesUp: 
						sitesUp.remove(sitesUp.indexOf(siteID));
						//1. site failure: 	
						for (Transaction transaction : transactionMap.values()){
							transaction.siteFailure(siteID);
							if (transaction.getStatus() == Transaction.Status.ABORTED){
								Operation.Builder builder = new Operation.Builder(Opcode.ABORT);
								builder.setTransactionID(transaction.getTransactionID());
								Operation abort = builder.build();
								transaction.addOperations(abort);
							}
						}
						
						if (siteID <= 0 || siteID > dataManagers.size()) {
							throw new AssertionError("Site " + siteID + " does not exist");
						}
						// Remember that sites are zero-indexed
						DataManager dm = dataManagers.get(siteID - 1);
						dm.fail();
					}
					
					// recover(7) says site 7 recovers. (Again, a tester-caused
					// event)
					if (opcode.equals("recover")) {
						int siteID = Integer.parseInt(args[0]);
						
						//add site to sitesUp: 
						//There is no site 0, then site must be added at index of siteID-1
						sitesUp.add(siteID);
						Collections.sort(sitesUp);
											
						//iterate over all transaction and notify that site is up: 
						for (Transaction transaction: transactionMap.values()){
							transaction.siteRecover(siteID);
						}
						
						
						if (siteID <= 0 || siteID > dataManagers.size()) {
							throw new AssertionError("Site " + siteID 
									+ " does not exist");
						}
						// Remember that sites are zero-indexed
						DataManager dm = dataManagers.get(siteID - 1);
						dm.recover();
					}
					
				}	
				transactionControler();
				// Read the next line
				currentLine = input.readLine();
			}
		}
		catch (IOException e) {
			throw new AssertionError("I/O failure");
		}
		


	
		//Process the remaining operations
     	while (numberOfTransactionsOver < numberOfTransactions){
        		transactionControler();
        	}
	}
	
	
	/**
	 * Create a variable map, that is a map where each key is a variable and 
	 * the value is an array list where each element is a site
	 * @return
	 */
	private HashMap <String, ArrayList<Integer>> createVariableMap(){
		
		HashMap<String, ArrayList <Integer>> variableMap = new HashMap<String, ArrayList <Integer>>();
		ArrayList <Integer> sites = new ArrayList<Integer> (); 
		
		for (int i=0; i<NUMBER_OF_SITES; i++)
			sites.add(i);
		
		for (int i=1; i<NUMBER_OF_VAR; i++){
			if (i%2 == 0){
			variableMap.put("x"+i, sites);
			}
			else{
				ArrayList <Integer> A = new ArrayList<Integer>();
				if ((i+1)% NUMBER_OF_SITES == 0){
					A.add(10); }
				else{
					A.add(((1+i)%NUMBER_OF_SITES)); }
				variableMap.put("x"+i, A);
				}
		}
		return variableMap; 
	}

	private ArrayList <String> graphManager(){
		
		WaitForGraph g = new WaitForGraph();
		for (Transaction transaction: transactionMap.values())
			g.addNode(transaction);
		
		g.init();
		return g.removeDeadlock();
	}
	
	private void transactionControler(){

		//create message builder: 
		for (Transaction transaction: transactionMap.values()){
			if (transaction.getOperationIndex() != -1 ){
				
				if (transaction.getTimeout() > transaction.TIMEOUT_DELAY && !transaction.getIsTransactionOver()){
					System.out.println("dead coz timeout"+transaction.getTransactionID());
					Operation.Builder builder = new Operation.Builder(Opcode.ABORT);
					builder.setTransactionID(transaction.getTransactionID());
					Operation abort = builder.build();
					transaction.setStatus(Transaction.Status.ABORTED);
					transaction.addOperations(abort);
				}
				for (Integer site: transaction.getSitesConcerned()){	
					messageBuilders[site-1].addOperation(transaction.getnextOperation());
				}
			}
		}
		
		//Create message, send messages and get responses
		for (int i=0; i<messageBuilders.length; i++){
				
			messages[i] = messageBuilders[i].build();	
			System.out.println("Site "+(i+1)+" "+messages[i]);
			dataManagers.get(i).sendMessage(messages[i]);
			siteResponses[i] = dataManagers.get(i).update();
			for (Response r: siteResponses[i]){
				transactionMap.get(r.getTransactionID()).sendResponse(r);
			}
			System.out.println("response message "+siteResponses[i]);
			messageBuilders[i].clear();
		}
												
		
		//re-init
		numberOfTransactionsOver = 0;
		numberOfWaitingTransactions = 0;
		for (Transaction transaction : transactionMap.values()){
			transaction.reinit();
			
			//A transaction is over is Status is FINISH or ABORT: 
			if (transaction.getIsTransactionOver()){	
				numberOfTransactionsOver++;
				transactionMap.remove(transaction);
			}
			
			//Transaction graph: 
			//if 2 transactions or more are waiting, called graphManager()
			if (transaction.getStatus() == Transaction.Status.WAIT){
				numberOfWaitingTransactions++;
			}
		}
		

		//Check deadlock in WaitForGraph: 
		if (numberOfWaitingTransactions >=2){
			ArrayList <String> removeList = graphManager();
			if (removeList.size() >0){
				for (Transaction transaction: transactionMap.values()){
					if (removeList.contains(transaction.getTransactionID())){
						Operation.Builder builder = new Operation.Builder(Opcode.ABORT);
						builder.setTransactionID(transaction.getTransactionID());
						Operation abort = builder.build();
						transaction.setStatus(Transaction.Status.ABORTED);
						transaction.addOperations(abort);
					}
				}
			}
		}		
	}
	
	public static void main (String[] args){
		if (args.length >= 2) {
			TransactionManager TM = new TransactionManager (args[0], args[1]);
		}
		else {
			TransactionManager TM = new TransactionManager ("testscripts/input/ADBPartIITest5.txt", "tt.txt");
		}	
	}
}