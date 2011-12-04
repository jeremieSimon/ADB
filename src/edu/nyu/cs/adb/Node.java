package edu.nyu.cs.adb;
import java.util.ArrayList;


public class Node {
	
	private String id; 
	private ArrayList <String> edges = new ArrayList <String>();
	
	public Node(String id, ArrayList <String> edges){
		
		this.id = id;
		this.edges = edges;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<String> edges) {
		this.edges = edges;
	}
	
	@Override 
	public String toString(){
		return "Node id: "+id+"\nEdges "+edges;
	}
	

}