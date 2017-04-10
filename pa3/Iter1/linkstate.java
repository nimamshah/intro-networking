import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;


public class linkstate {
  // List<List<Character>> table = new ArrayList<List<Character>>();
  // String[][] table;
  Node[] nodes;
  String nprime;
  Node currentNode;
  int step = 0;
  boolean header = false;
  int size = -1;

  // Parallel arrays used for tracking cost of least-cost path
  //  and previous node along current least-cost path
  int[] Dv;
  String[] pv;

  private static final int inf = 100;


  // Helper function for shorter console logs
  private static void log(String s) {
    System.out.println(s);
  }

  // Reads file to form table of node data
  private void initialize(String filename) {
    // JDK7 try-with-resources
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String currentLine;

      // int row = 0;
      int i = 0;

      // Loop to EOF
      while (!(currentLine = br.readLine()).equals("EOF.")) {
        // Remove period from end of line
        currentLine = currentLine.substring(0, currentLine.length() - 1);
        String[] tokens = currentLine.split(",");

        // Lazy initialize nodes array
        if (nodes == null) {
          size = tokens.length;
          nodes = new Node[size];
          Dv = new int[size-1];
          pv = new String[size-1];
        }

        nodes[i] = new Node(i+1, tokens);
        i++;

        // if (table == null) {
        //   size = tokens.length;
        //   table = new String[size][size];
        // }
        //
        // for (int col = 0; col < size; col++) {
        //   table[row][col] = tokens[col];
        // }
        // row++;
      }

      log("------ Nodes ------");
      for (i = 0; i < nodes.length; i++) {
        log(nodes[i].toString());
      }

      // Set N' = {1}
      currentNode = nodes[0];
      nprime = Integer.toString(nodes[0].getId());

      for (i = 0; i < nodes.length-1; i++) {
        Dv[i] = currentNode.getLinkWeight(i+1);
        pv[i] = Integer.toString(currentNode.getId());
      }
      printProgress();

      // for (i = 0; i < table.length; i++) {
      //   for (int j = 0; j < table[i].length; j++) {
      //     // log(table[i][j]);
      //     System.out.print(table[i][j]);
      //   }
      //   System.out.println();
      // }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean done() {
    String[] nprimeNodes = nprime.split(",");
    if (nprimeNodes.length == size) return true;
    else return false;
  }

  private int min(int a, int b) {
    if (a < b) return a;
    else return b;
  }

  private void compute() {
    while (!done()) {
      // Find w not in N' such that D(w) is a minimum
      int iMin = -1;
      int min = inf;
      for (int i = 0; i < Dv.length; i++) {
        if (Dv[i] < min) {
          min = Dv[i];
          iMin = i;
        }
      }

      // Add w to N'
      nprime += "," + Integer.toString(nodes[iMin+1].getId());
      // log(nprime);
      currentNode = nodes[iMin+1];

      // Update D(v) for each neighbor v of w and not in N':
      //  D(v) = min( D(v), D(w) + c(w,v) )
      for (int i = 0; i < Dv.length; i++) {
        Dv[i] = min(Dv[i], Math.abs(Dv[iMin] + Dv[i]));
      }
      printProgress();
    }
  }

  private void printProgress() {
    int i = 0;
    if (!header) {
      String head = "Step\tN'\t";
      for (i = 0; i < Dv.length; i++) {
        head += "D(" + (i+2) + "),p(" + (i+2) + ")\t";
      }
      log(head);
    }

    String s = Integer.toString(step) + "\t" + nprime + "\t";
    for (i = 0; i < Dv.length; i++) {
      if (Dv[i] == inf) s += "N";
      else s += Integer.toString(Dv[i]) + "," + pv[i];
      s += "\t\t";
    }
    header = true;
    step++;
    log(s);
  }

  public static void main(String[] args) {
    linkstate ls = new linkstate();
    ls.initialize(args[0]);
    ls.compute();
  }
}
