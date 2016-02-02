package dtu.searchclient;

import java.util.ArrayDeque;
import java.util.HashSet;

import dtu.searchclient.SearchClient.Memory;

public abstract class Strategy {

	public HashSet< Node > explored;
	public long startTime = System.currentTimeMillis();

	public Strategy() {
		explored = new HashSet< Node >();
	}

	public void addToExplored( Node n ) {
		explored.add( n );
	}

	public boolean isExplored( Node n ) {
		return explored.contains( n );
	}

	public int countExplored() {
		return explored.size();
	}

	public String searchStatus() {
		return String.format( "#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), Memory.stringRep() );
	}
	
	public float timeSpent() {
		return ( System.currentTimeMillis() - startTime ) / 1000f;
	}

	public abstract Node getAndRemoveLeaf();

	public abstract void addToFrontier( Node n );

	public abstract boolean inFrontier( Node n );

	public abstract int countFrontier();

	public abstract boolean frontierIsEmpty();
	
	public abstract String toString();

	public static class StrategyBFS extends Strategy {

		private ArrayDeque< Node > frontier;

		public StrategyBFS() {
			super();
			frontier = new ArrayDeque< Node >();
		}

		public Node getAndRemoveLeaf() {
			return frontier.pollFirst();
		}

		public void addToFrontier( Node n ) {
			frontier.addLast( n );
		}

		public int countFrontier() {
			return frontier.size();
		}

		public boolean frontierIsEmpty() {
			return frontier.isEmpty();
		}

		public boolean inFrontier( Node n ) {
			return frontier.contains( n );
		}

		public String toString() {
			return "Breadth-first Search";
		}
	}

	public static class StrategyDFS extends Strategy {
		public StrategyDFS() {
			super();
			// Unimplemented			
		}

		public Node getAndRemoveLeaf() {
			// Unimplemented
			return null;
		}

		public void addToFrontier( Node n ) {
			// Unimplemented
		}

		public int countFrontier() {
			// Unimplemented
			return 0;
		}

		public boolean frontierIsEmpty() {
			// Unimplemented
			return true;
		}

		public boolean inFrontier( Node n ) {
			// Unimplemented
			return false;
		}

		public String toString() {
			return "Depth-first Search";
		}
	}

	// Ex 3: Best-first Search uses a priority queue (Java contains no implementation of a Heap data structure)
	public static class StrategyBestFirst extends Strategy {
		private Heuristic heuristic;
		public StrategyBestFirst( Heuristic h ) {
			super();
			heuristic = h;
			// Unimplemented
		}
		public Node getAndRemoveLeaf() {
			// Unimplemented
			return null;
		}

		public void addToFrontier( Node n ) {
			// Unimplemented
		}

		public int countFrontier() {
			// Unimplemented
			return 0;
		}

		public boolean frontierIsEmpty() {
			// Unimplemented
			return true;
		}

		public boolean inFrontier( Node n ) {
			// Unimplemented
			return false;
		}

		public String toString() {
			return "Best-first Search (PriorityQueue) using " + heuristic.toString();
		}
	}
}
