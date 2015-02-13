import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Ross Payne
 * 2-7-2015
 *
 * Reads in a graph from a file. Stores into a 2D array and finds the max clique for that graph using
 * the Bron-Kerbosch algorithm.
 */
public class Graph {
    private int E; //number of edges
    private int V; //number of vertices
    private boolean[][] G; //the graph itself
    private int mCliqueSize; //size of the max clique
    private Set<Integer> mClique; //the max clique itself
    private long mCliqueSpeed; //how long it took to find the max clique

    Graph() {}

    /*
    Creates a graph from an adjacency matrix. Assumes the passed in adjacency matrix is valid but counts edges, vertices, etc.
     */


    //UNTESTED
    Graph(boolean[][] matrix) {
        this.G = matrix;
        V = matrix.length;

        int e = 0;

        //counting number of edges. could be improved to only need to scan half the graph
        for (int i = 0; i < matrix.length; i ++) {
            for (int j = 0; j < matrix.length; j++) {
                if (G[i][j]) e++;
            }
        }
        this.E = (e-V)/2; //no edges to onesself and each edge is counted twice
    }

    public static final void main(String[] args ) {
        File graphs = new File("graphsDense.txt");
        ArrayList<Graph> col = new ArrayList<Graph>(); //for holding the graphs after we make them
        try {
            Scanner s = new Scanner(graphs);
            Graph c;
            while (s.hasNext()) {
                c = new Graph();
                c.readGraph(s); //read from file to a 2d array
                //discard a graph with empty vertex set. i.e. when end of file is reached and the last line is 0 which causes readGraphs to abort
                if (c.V == 0) continue;
                col.add(c); //add to the collection of graphs
            }
        } catch (FileNotFoundException e) {
            System.out.println("Scanner failed to open file");
            return;
        }

        //finding the cliques and printing
        int c = 1;
        for (Graph gr: col) {
            gr.findLargestClique();
            System.out.println("G"+(c++)+" "+gr.toString());
        }
    }

    //just prints a graph in the form found in the given files
    public void printGraph() {
        for (int i = 0; i < G.length; i++) {
            System.out.println();
            for (int j = 0; j < G.length; j++) {
                System.out.print(G[i][j]);
            }
        }
        System.out.println();
    }

    /*
    Gives the info in the form as requested in the instructions
     */
    @Override
    public String toString() {
        String mc = mClique.toString().replace('[','{').replace(']','}');
        return "("+V+","+E+") "+ mc +" (size="+mCliqueSize+", "+mCliqueSpeed+" ms)";
    }

    /*
    Reads the graph from file and makes a 2d array. also tracks number of vertices and number of edges
     */
    public void readGraph(Scanner s) {
        int e = 0;
        boolean r;
        boolean g[][];
        int d = s.nextInt();
        if (d == 0) return;
        g = new boolean[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                if ((r = (s.nextInt()) == 1)) e++;
                g[i][j] = r;
            }
        }
        this.G = g;
        this.E = (e-d)/2;
        this.V = d;
    }

    /* kicks off and times the recursion for the overloaded findLargestClique method */
    void findLargestClique() {
        Set<Integer> empty = new HashSet();
        Set<Integer> allNodes = new HashSet();

        mClique = new HashSet<Integer>();
        mClique.add(0);
        for (int i = 0; i < G.length; i++) {
            allNodes.add(i);
        }
        long start = System.currentTimeMillis();
        /* start off the recursion with current = empty set and candidates = all nodes in G */
        findLargestClique(empty,allNodes);
        mCliqueSpeed = System.currentTimeMillis()-start;
    }

    /*
    Finds maximum clique using recursive backtracking
    current = the current clique we are building
    candidates = the set of candidate vertices that may end up in r (initially the entire vertex set of G)
     */
    void findLargestClique(Set<Integer> current, Set<Integer> candidates) {
        /* if we've run out of candidate notes */
        if (candidates.isEmpty()) {
            /* keep track of largest clique found */
            if (mClique.size() < current.size()) {
                mClique = current;
                mCliqueSize = current.size();
            }
        }
        Iterator<Integer> iter = candidates.iterator();
        /* iterate through entire vertex set of G */
        while (iter.hasNext()){
            Integer vertex = iter.next();

            /* add vertex to current set */
            Set<Integer> newCurrent = new HashSet(current);
            newCurrent.add(vertex);

            /* the neighbors of the vertex */
            Set<Integer> n = findNeighbors(vertex);

            /* Intersect candidate nodes and neighbors of vertex */
            Set<Integer> candidateNeighbors = new HashSet(candidates);
            candidateNeighbors.retainAll(n);

            /* Call recursively */
            findLargestClique(newCurrent, candidateNeighbors);

            iter.remove();
        }

    }

    /*
    Returns the set of vertices connected to vertex
     */
    Set<Integer> findNeighbors(int vertex) {
        Set <Integer> r = new HashSet<Integer>();
        for (int i = 0; i < G.length; i++ ) {
            if (G[vertex][i] && vertex != i) {
                r.add(i);
            }
        }
        return r;
    }
}
