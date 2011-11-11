package edu.nyu.cs.adb;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * This is the object that has the main function
 * @author dandelarosa
 */
public final class RepCRecApp {
	/**
	 * This function will create an instance of the TransactionManager and run 
	 * it.
	 * @param args will be an array of strings. args[0] will be the input 
	 * filename, args[1] will be the output filename.
	 */
	public static void main (String args[]) {
		TransactionManager sim = null;
		if (args.length == 0) {
			// If there are no arguments, use standard in and standard out
			sim = new TransactionManager(System.in, System.out);
		}
		else if (args.length == 1) {
			OutputStream out;
			try {
				out = new FileOutputStream(args[0]);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("Could not open file" 
						+ args[0]);
			}
			// If one argument, use standard in and the filename is output
			sim = new TransactionManager(System.in, out);
		}
		else {
			// If two or more arguments, use filename in and filename out
			sim = new TransactionManager(args[0], args[1]);
		}
		
		if (sim != null) sim.run();
	}
}

