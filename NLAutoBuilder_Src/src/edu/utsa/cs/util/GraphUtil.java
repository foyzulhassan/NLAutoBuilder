package edu.utsa.cs.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class GraphUtil {
    public static boolean hasCycle(Hashtable<IGraphNode, Set<IGraphNode>> graph){
	Queue<IGraphNode> nodes = new LinkedBlockingQueue<IGraphNode>();
	Set<IGraphNode> visited = new HashSet<IGraphNode>();
	nodes.addAll(getEntries(graph));
	while(!nodes.isEmpty()){
	    IGraphNode from = nodes.poll();
	    if(!visited.contains(from)){
		visited.add(from);
	    }else{
		return true;
	    }
	    for(IGraphNode dest : graph.get(from)){
		nodes.add(dest);
	    }
	}
	return false;
    }
    
    public static <T extends IGraphNode> Set<T> getEntries(Hashtable<T, Set<T>> graph){
	HashSet<T> candidates = new HashSet<T>();
	candidates.addAll(graph.keySet());
	for(T from: graph.keySet()){
	    for(T dest : graph.get(from)){
		if (candidates.contains(dest)){
		    candidates.remove(dest);
		}
	    }
	}
	return candidates;
    }
    public static <T extends IGraphNode> Set<T> getExits(Hashtable<T, Set<T>> graph){
	HashSet<T> candidates = new HashSet<T>();
	for(T from: graph.keySet()){
	    if(graph.get(from).size()==0){
		candidates.add(from);
	    }
	}
	return candidates;
    }
    public static <T extends IGraphNode> List<T> getOrderedEntries(Hashtable<T, Set<T>> graph){
	Hashtable<T, Integer> nodeValTable = new Hashtable<T, Integer>();
	Set<T> entries = getEntries(graph);
	for(T entry : entries){
	    getValue(entry, nodeValTable, graph);
	}
	
	List<T> orderedEntries = sortEntries(nodeValTable, entries);
	return orderedEntries;
    }
    private static <T extends IGraphNode> List<T> sortEntries(
	    Hashtable<T, Integer> nodeValTable, Set<T> entries) {
	List<Map.Entry<T, Integer>> list = new ArrayList<Map.Entry<T, Integer>>(nodeValTable.entrySet());
	Collections.sort(list, new Comparator<Map.Entry<T, Integer>>(){
	    public int compare(Map.Entry<T, Integer> o1, Map.Entry<T, Integer> o2) {
	            return o1.getValue().compareTo(o2.getValue());
	        }});
	List<T> orderedEntries = new ArrayList<T>();
	for(Map.Entry<T, Integer> e : list){
	    if(entries.contains(e.getKey())){
		orderedEntries.add(e.getKey());
	    }
	}
	return orderedEntries;
    }
    private static <T extends IGraphNode> int getValue(T node,
	    Hashtable<T, Integer> nodeValTable, Hashtable<T, Set<T>> graph) {
	int val = node.getValue();
	for(T dest : graph.get(node)){
	    if(nodeValTable.get(dest)!=null){
		val = val + nodeValTable.get(dest);
	    }else{
		val = val + getValue(dest, nodeValTable, graph);
	    }
	}
	nodeValTable.put(node, val);
	return val;
    }
    public static Hashtable<IGraphNode, Set<IGraphNode>> revert(Hashtable<IGraphNode, Set<IGraphNode>> graph){
	Hashtable<IGraphNode, Set<IGraphNode>> reverted = new Hashtable<IGraphNode, Set<IGraphNode>>();
	for(IGraphNode key: graph.keySet()){
	    reverted.put(key, new HashSet<IGraphNode>());
	}
	for(IGraphNode key: graph.keySet()){
	    for(IGraphNode value : graph.get(key)){
		reverted.get(value).add(key);
	    }
	}
	return graph;
    }
}
