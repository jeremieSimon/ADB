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
	 * @param transaction
	 */
	void addNode(Transaction transaction){
		transactions.add(transaction);	
		ageMap.put(transaction.getTransactionID(), transaction.getAge());
	}
	
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
	 * 
	 * @param nodeID
	 */
	void removeNode(String nodeID){
		System.out.println("Remove "+nodeID);
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
					System.out.println("Visit "+nodePath);
					cycle.add(nodePath);
					ArrayList <String> neighbors = waitForGraph.get(nodePath);
					if (neighbors.size() > 0)
						nodeMap.put(nodePath, 1);
					
					for (String neighbor : neighbors){
						//neighbor is not read, add to queue
						if (nodeMap.get(neighbor) == 0)
							pathQueue.add(neighbor);
						else if (nodeMap.get(neighbor) == 1){
							System.out.println("Cycle detected "+neighbor);
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
			System.out.println("iter");
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
		
		Transaction T1 =  new Transaction("T1", 1);
		Transaction T2 =  new Transaction("T2", 3);
		Transaction T3 =  new Transaction("T3", 2);
		
		T1.addLocksHold(new Lock("x1", "WRITE"));
		T1.addLocksWait(new Lock("x3", "WRITE"));
		T1.setStatus(Transaction.Status.WAIT);
		
		T2.addLocksHold(new Lock("x2", "WRITE"));
		T2.addLocksWait(new Lock("x1", "WRITE"));
		T2.setStatus(Transaction.Status.WAIT);

		T3.addLocksHold(new Lock("x3", "WRITE"));
		T3.addLocksWait(new Lock("x2", "WRITE"));
		T3.setStatus(Transaction.Status.WAIT);


		WaitForGraph g = new WaitForGraph();
		g.addNode(T1);
		g.addNode(T2);
		g.addNode(T3);
		
		g.init();
		
		System.out.println(g.removeDeadlock());
		

		
		
	}
}