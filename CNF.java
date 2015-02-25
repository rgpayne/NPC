
import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/*
 * 3-CNF builds a matrix based on the values entered from cnfs.txt
 * this matrix is then tested to find which variables need to be T/F
 * to solve the problem.
 */

/**
 *
 * @author Kevin Prigge
 */
public class CNF 
{
    /**
     * The data is input.  The first number is the different number of variables
     * used in the equation, and then followed by all the vertices.
     * @param s is the line of data entered in
     * @return  a graph with the new matrix.
     */
    static Graph createGraph(String s)
    {
        Scanner data = new Scanner(s);
        int t, var;
        ArrayList<Integer> values = new ArrayList<Integer>();
        var = data.nextInt();
        if(var == 0) 
        {
            return new Graph();
        }
        while(data.hasNext())
        {
            t = data.nextInt();
            values.add(t);
        }
        
        boolean[][] myMatrix = new boolean[values.size()][values.size()];
        for(int i = 0; i < myMatrix.length; i++)
        {
            for(int j = i; j < myMatrix.length; j++)
            {
                if(i==j)
                {
                    myMatrix[i][j]=TRUE;
                }
                else if(i % 3 == 0 && j < i+3)
                {
                    myMatrix[i][j]=myMatrix[j][i] = FALSE;
                }
                else if(i % 3 == 1 && j < i+2)
                {
                    myMatrix[i][j]=myMatrix[j][i] = FALSE;
                }
                else if(values.get(i)+values.get(j) == 0)
                {
                     myMatrix[i][j]=myMatrix[j][i]=FALSE;   
                }
                else
                {
                     myMatrix[i][j]=myMatrix[j][i]=TRUE;
                }
            }
        }
        
        Graph temp = new Graph(myMatrix);
        temp.N = var; //Store variables
        temp.Values = values; //Store values in the graph
        return temp;
    }
    
    /**
     * A variable array tells whether a variable should be true or false.
     * 
     * @param gr The graph we are trying to solve.
     * @return A boolean array which indicates the variables status.
     */
    static boolean [] truthArray(Graph gr)
    {
        int value;
        boolean [] temp = new boolean[gr.N];
        for(int i =0; i<temp.length; i++)
        {
            temp[i] = FALSE;
        }
        
        Iterator<Integer> iter = gr.mClique.iterator();
        while (iter.hasNext()) 
        {
            value = iter.next();
            if(gr.Values.get(value) > 0)
            {
                temp[gr.Values.get(value)-1] = TRUE;
            }
        }
        return temp;
    }
    
    public static final void main(String[] args ) 
    {
        File graphs = new File("cnfs.txt");
        ArrayList<Graph> col = new ArrayList<Graph>();
        try 
        {
            String ln;
            Scanner s = new Scanner(graphs);
            Graph c;
            while (s.hasNextLine()) 
            {
                ln = s.nextLine();
                c = createGraph(ln);

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
        System.out.println("* 3-CNF in cnfs.txt\n");
        long mCliqueSpeed;
        for (Graph gr: col) 
        {
            // time
            long start = System.currentTimeMillis();
            gr.findLargestClique();
            mCliqueSpeed = System.currentTimeMillis()-start;
            // generate a boolean array to show which variables need to be true
            boolean [] table = truthArray(gr);

            System.out.println("3-CNF No."+(c++)+": [n="+gr.N+" k="+gr.V/3+"]");
            int i = 0;
            if(gr.mCliqueSize != gr.V/3)
            {
                // Problem is not solvable
                System.out.println("No "+gr.V/3+"-clique; no solution"+"("+mCliqueSpeed+"ms) mclique="+gr.mCliqueSize+"\n\n");// No solution record
            }
            else
            {
                // Problem is solvable, followed are the T/F assignments
                System.out.print("Assignments: [");// solutions
                while(i < gr.N)
                {
                    System.out.print("A"+(i+1)+"="+table[i]+" ");
                    i++;
                }
                    System.out.print("]"+"("+mCliqueSpeed+"ms) \n\n");
                }
        }
    }
}

