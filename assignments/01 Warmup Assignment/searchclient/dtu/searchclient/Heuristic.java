package dtu.searchclient;

import java.util.Comparator;

public abstract class Heuristic implements Comparator< Node > {

	public Node initialState;
	public Heuristic(Node initialState) {
		this.initialState = initialState;
	}

	public int compare( Node n1, Node n2 ) {
		return f( n1 ) - f( n2 );
	}

	public int h( Node n ) {
		return 0;
	}

	public abstract int f( Node n );

	public static class AStar extends Heuristic {
		public AStar(Node initialState) {
			super( initialState );
		}

		public int f( Node n ) {
			return n.g() + h( n );
		}

		public String toString() {
			return "A* evaluation";
		}
	}

	public static class WeightedAStar extends Heuristic {
		private int W;

		public WeightedAStar(Node initialState) {
			super( initialState );
			W = 5; // You're welcome to test this out with different values, but for the reporting part you must at least indicate benchmarks for W = 5
		}

		public int f( Node n ) {
			return n.g() + W * h( n );
		}

		public String toString() {
			return String.format( "WA*(%d) evaluation", W );
		}
	}

	public static class Greedy extends Heuristic {

		public Greedy(Node initialState) {
			super( initialState );
		}

		public int f( Node n ) {
			return h( n );
		}

		public String toString() {
			return "Greedy evaluation";
		}
	}
}
