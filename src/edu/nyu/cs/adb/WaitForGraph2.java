package edu.nyu.cs.adb;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;



public class WaitForGraph2 {
	
	private HashMap <String, ArrayList <String>> waitForGraph = new HashMap <String ,ArrayList <String>> ();
	//private ArrayList <Node> graph = new ArrayList <Node>();
	private ArrayList <Transaction> transactions = new ArrayList <Transaction>();
	
	void addNode(Transaction transaction){

		transactions.add(transaction);
		
		//Build node
		String id = transaction.getTransactionID(); 
		ArrayList <String> edges = new ArrayList <String>();
		
		//check dependencies
		if (transaction.getStatus() == Transaction.Status.WAIT){
			ArrayList <String> variables = new ArrayList <String>();
			ArrayList <Lock> waitLock = transaction.getLocksWait();
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
		//graph.add(node);
		
	}
	
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
	
	public ArrayList <String> removeDeadlock(){
		ArrayList <String> cycle = new  ArrayList <String>(); 
		ArrayList <String> nodeRemoved = new ArrayList <String>();
		while (cycle != null){
			System.out.println("iter");
			cycle = checkCyle();
			if (cycle == null)
				break;
		
			//find older node
			String olderNode = "T0"; 
			for (String node: cycle){
				if (node.compareTo(olderNode)<0 ){
					olderNode = node;
				}
			}
			removeNode(olderNode);
			nodeRemoved.add(olderNode);
		}
		return nodeRemoved;
	}
	
	public static void main (String [] args){
		
//		WaitForGraph2 g = new WaitForGraph2();
//		
//		//Node test: 
//		Node node1 = new Node(1, new ArrayList<Node>());
//		Node node2 = new Node(2, new ArrayList<Node>());
//		Node node3 = new Node(3, new ArrayList<Node>());
//		Node node4 = new Node(4, new ArrayList<Node>());
//		Node node5 = new Node(5, new ArrayList<Node>());
//		Node node6 = new Node(6, new ArrayList<Node>());
//
//		
//		ArrayList<Node> edges = new ArrayList<Node>();
//		edges.add(node2);		
//		node1.setEdges(edges);
//		
//		ArrayList<Node> edges2 = new ArrayList<Node>();
//		edges2.add(node3);
//		node2.setEdges(edges2);		
//		
//		ArrayList<Node> edges3 = new ArrayList<Node>();
//		edges3.add(node4);
//		node3.setEdges(edges3);	
//
//		ArrayList<Node> edges4 = new ArrayList<Node>();
//		edges4.add(node1);
//		node4.setEdges(edges4);	
//
//		
//		g.addNode(node1);
//		g.addNode(node2);
//		g.addNode(node3);
//		g.addNode(node4);
//		g.addNode(node5);
//		g.addNode(node6);
//
//		
//		System.out.println(g.removeDeadlock());
		
	}
}

