
import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Boolean.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The independent set, is the inverse of the k-clique problem, therefore we are
 * finding the inverse of the k-clique to solve the independent set.
 */
/**
 *
 * @author Kevin Prigge
 */
public class IndSet 
{
    /**
     * Takes a graph, and reverses its matrix.  1 <-> 0, 0<->1
     * @param g the graph we are converting
     */
    static void invertMatrix(Graph g)
    {
        for(int i = 0; i< g.G.length; i++)
        {
            for(int j = i; j <g.G.length; j++)
            {
                if(i==j) g.G[i][j] = TRUE;
                else
                    g.G[i][j]=g.G[j][i]= !g.G[i][j];
            }
        }
    }
    
    public static final void main(String[] args ) {
        File graphs = new File("graphs.txt");
        ArrayList<Graph> col = new ArrayList<Graph>();
        try 
        {
            Scanner s = new Scanner(graphs);
            Graph c;
            while (s.hasNext()) 
            {
                c = new Graph();
                c.readGraph(s);
                
                if (c.V == 0) continue;
                col.add(c);
            }
        } 
        catch (FileNotFoundException e) 
        {
            System.out.println("Scanner failed to open file");
            return;
        }

        int c = 1;
        System.out.println("* Max Independent Sets in graphs in graphs.txt\n");
        System.out.println("\n\t(|V|,|E|) Independent Set(size, ms used)\n");
        
        for (Graph gr: col) 
        {
            invertMatrix(gr);
            gr.findLargestClique();
            System.out.println("G"+(c++)+" "+gr.toString());
        }
    }
}

