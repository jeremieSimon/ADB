package edu.nyu.cs.adb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class WaitForGraph {

	/**
	 *An instance of Wait For Graph is created by the transaction manager
	 *If 2 or more transactions are waiting then waitForGraph is being called
	 *Build a new graph at each call 
	 */
	
	private HashMap <String, ArrayList <String>> waitForGraph = new HashMap <String ,ArrayList <String>> ();
	private HashMap <String, Integer> ageMap = new HashMap <String, Integer>();
	private ArrayList <Transaction> transactions = new ArrayList <Transaction>();
	
	/**
	 * Add transaction in the waitForGraph
	 * @param transaction
	 */
	void addNode(Transaction transaction){
		transactions.add(transaction);	
		ageMap.put(transaction.getTransactionID(), transaction.getAge());
	}
	
	/**
	 * After all transactions have been added, 
	 * this function checks the dependencies between each transaction
	 */
	void init(){
		for (Transaction transaction: transactions){
			buildDependencies(transaction);
		}
	}
	
	private void buildDependencies(Transaction transaction){
		
		//Build node
		String id = transaction.getTransactionID(); 
		ArrayList <String> edges = new ArrayList <String>();
		
		//check dependencies
		if (transaction.getStatus() == Transaction.Status.WAIT){
			ArrayList <String> variables = new ArrayList <String>();
			Set<Lock> waitLock = transaction.getLocksWait();
			for (Lock w: waitLock){
				variables.add(w.getVariableID());
			}
			for (Transaction t: transactions){
				for (Lock lock: t.getLocksHold()){
					if (lock.getLockType() == "WRITE" && variables.contains(lock.getVariableID())){
						edges.add(t.getTransactionID());
					}
				}
			}
		}
		waitForGraph.put(id, edges);
	}
	
	/**
	 * This function is called in case of deadlock.
	 * It removes the node causing the deadlock
	 * @param nodeID
	 */
	void removeNode(String nodeID){
		waitForGraph.remove(nodeID);
		for (ArrayList <String> n: waitForGraph.values()){
			if (n.contains(nodeID)){
				n.remove(n.indexOf(nodeID));
			}
		}
	}
	
	private ArrayList <String> checkCyle(){
		HashMap <String, Integer> nodeMap = new HashMap <String, Integer>();
		
		//Mark all nodes as unread
		for (String nodeID: waitForGraph.keySet()){
			nodeMap.put(nodeID, 0);
		}
		
		Queue <String> nodeQueue = new ArrayBlockingQueue <String>(waitForGraph.size());
		Queue <String> pathQueue = new ArrayBlockingQueue <String>(waitForGraph.size());
		ArrayList <String> cycle = new ArrayList <String>();
		boolean isCycle = false; 
		
		//init queue: 
		for (String nodeID: nodeMap.keySet()){
			nodeQueue.add(nodeID);
		}
		
		
		while (!nodeQueue.isEmpty()){
			String nodeID = nodeQueue.poll();
			
			//node not read: 
			if (nodeMap.get(nodeID) == 0){
				pathQueue.add(nodeID);
				
				//If no cycle in path, then all nodes are marked as safe
				if (!isCycle){
					for (String n: cycle){
						nodeMap.put(n, 2); 
					}
				}
				//reinit: 
				cycle.clear();
				isCycle = false; 

				//Discover all neighbors
				while (!pathQueue.isEmpty()){
					String nodePath = pathQueue.poll();
					cycle.add(nodePath);
					ArrayList <String> neighbors = waitForGraph.get(nodePath);
					if (neighbors.size() > 0)
						nodeMap.put(nodePath, 1);
					
					for (String neighbor : neighbors){
						//neighbor is not read, add to queue
						if (nodeMap.get(neighbor) == 0)
							pathQueue.add(neighbor);
						else if (nodeMap.get(neighbor) == 1){
							return cycle;
						}
					}
				}
			}
		}
		return null;
		
	}
	
	/**
	 * @return transactionIDs that were removed. List is empty if no transaction 
	 * were removed
	 */
	public ArrayList <String> removeDeadlock(){
		
		ArrayList <String> cycle = new  ArrayList <String>(); 
		ArrayList <String> nodeRemoved = new ArrayList <String>();
		while (cycle != null){
			cycle = checkCyle();
			if (cycle == null)
				break;
		
			//find the youngest node
			String olderNode = "T0"; 
			int oldest = -1;
			for (String node: cycle){
				if (ageMap.get(node) > oldest){
				//if (node.compareTo(olderNode)>0 ){
					olderNode = node;
					oldest = ageMap.get(node);
				}
			}
			removeNode(olderNode);
			nodeRemoved.add(olderNode);
		}
		return nodeRemoved;
	}
	
	public static void main (String[] args){		
		
	}
}