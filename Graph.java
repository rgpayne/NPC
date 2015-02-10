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
    private int[][] G; //the graph itself
    private int mCliqueSize; //size of the max clique
    private Set<Integer> mClique; //the max clique itself
    private long maxCliqueSpeed; //how long it took to find the max clique

    Graph() {}

    /*
    Creates a graph from an adjacency matrix
     */
    //untested
    Graph(int[][] matrix) {
        this.G = matrix;
        V = matrix.length;

        int e = 0;

        //counting number of edges. could be improved to only need to scan half the graph
        for (int i = 0; i < matrix.length; i ++) {
            for (int j = 0; j < matrix.length; j++) {
                if (G[i][j] == 1) e++;
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
                //c.printGraph();
                //System.out.println();
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
    Gives the info in the form as requested in the instructions (for the most part)
     */
    @Override
    public String toString() {
        String mc = mClique.toString().replace('[','{').replace(']','}');
        return "("+V+","+E+") "+ mc +" (size="+mCliqueSize+", "+maxCliqueSpeed+" ms)";
    }

    /*
    Reads the graph from file and makes a 2d array. also tracks number of vertices and number of edges
     */
    public void readGraph(Scanner s) {
        int e = 0;
        int r;
        int g[][];
        int d = s.nextInt();
        if (d == 0) return;
        g = new int[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                if ((r = s.nextInt()) == 1) e++;
                g[i][j] = r;
            }
        }
        this.G = g;
        this.E = (e-d)/2;
        this.V = d;
    }

    /* Returns a set that represents the largest clique in G and times how long it took to find.
    *  The idea is that for graph G of size |V| we find the power set of |V|. We then
    *  go through every entry in the power set and check if each is a clique,
    *  keeping track of the largest as we go.
    * */
    void findLargestClique() {
        Set<Integer> R = new HashSet();
        Set<Integer> P = new HashSet();
        Set<Integer> X = new HashSet();

        mClique = new HashSet<Integer>();
        mClique.add(0);
        for (int i = 0; i < G.length; i++) {
            P.add(i);
        }
        long start = System.currentTimeMillis();
        bronKerbosch(R,P,X);
        maxCliqueSpeed = System.currentTimeMillis()-start;
    }

    /*
    An implementation of the vanilla Bron-Kerbosch algorithm
     */
    void bronKerbosch(Set<Integer> r, Set<Integer> p, Set<Integer> x) {
        if (p.isEmpty() && x.isEmpty()) {
            if (mClique.size() < r.size()) {
                mClique = r;
                mCliqueSize = r.size();
            }
        }
        Iterator<Integer> iter = p.iterator();
        while (iter.hasNext()){
            Integer vertex = iter.next();
            Set<Integer> ruv = new HashSet(r);
            ruv.add(vertex);

            Set<Integer> n = findNeighbors(vertex);

            Set<Integer> pIn = new HashSet(p);
            pIn.retainAll(n);

            Set<Integer> xIn = new HashSet(x);
            xIn.retainAll(n);

            bronKerbosch(ruv, pIn, xIn);

            iter.remove();
            x.add(vertex);
        }

    }

    /*
    Returns the set of vertexes connected to vertex
     */
    Set<Integer> findNeighbors(int vertex) {
        Set <Integer> r = new HashSet<Integer>();
        for (int i = 0; i < G.length; i++ ) {
            if (G[vertex][i] == 1 && vertex != i) {
                r.add(i);
            }
        }
        return r;
    }
}
