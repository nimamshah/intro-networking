import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;


public class linkstate {
  Node[] nodes;
  String nprime = "";
  String nprimeIndices = "";
  Node currentNode;
  int step = 0;
  boolean header = false;
  int size = -1;
  private static final String dash1 = "--------";
  private static final String dash2 = dash1 + dash1;
  String fittedDashes = "";
  String tab = "";

  // Parallel arrays used for tracking cost of least-cost path
  //  and previous node along current least-cost path
  int[] Dv;
  String[] pv;

  private static final int inf = 100;


  // Helper function for shorter console logs
  private static void log(String s) {
    System.out.println(s);
  }

  // Attempts to set number of tabs to align columns as N' grows in length
  // Does not work perfectly under all cases, but usually within one tab
  private void setTab() {
    tab = "";
    String s = "";
    int i = -1;
    for (i = 1; i < size+1; i++) {
      s += Integer.toString(i) + ",";
    }

    double q = (s.length() - nprime.length())/8.0;

    if (fittedDashes.equals("")) {
      for (int j = 0; j < size-1; j++) fittedDashes += dash2;
      for (int j = 0; j < Math.ceil(q); j++) {
        fittedDashes += dash1;
      }
    }

    for (i = 0; i < (Math.ceil(q)); i++) {
      tab += "\t";
    }
    if (q > 0.5 && q < 1.0) {
      tab += "\t";
    }
  }

  // Reads file to form table of node data
  private void initialize(String filename) {
    // JDK7 try-with-resources
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String currentLine;

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
      }

      // Uncomment to check that nodes are loaded properly
      // log("------ Nodes ------");
      // for (i = 0; i < nodes.length; i++) {
      //   log(nodes[i].toString());
      // }

      // Set N' = {1}
      currentNode = nodes[0];
      nprime = Integer.toString(nodes[0].getId());

      for (i = 0; i < nodes.length-1; i++) {
        Dv[i] = currentNode.getLinkWeight(i+1);
        pv[i] = Integer.toString(currentNode.getId());
      }
      printProgress();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Helper function to determine when LS algo has completed
  private boolean done() {
    String[] nprimeNodes = nprime.split(",");
    if (nprimeNodes.length == size) return true;
    else return false;
  }

  // Runs Link-State algorithm
  private void compute() {
    String[] skipIndices = new String[0];
    boolean firstIndex = true;

    while (!done()) {
      // Find w not in N' such that D(w) is a minimum
      int iMin = -1;
      int min = inf;
      boolean skip = false;
      for (int i = 0; i < Dv.length; i++) {
        for (int j = 0; j < skipIndices.length; j++) {
          if (i == Integer.parseInt(skipIndices[j])) {
            skip = true;
            continue;
          }
        }
        if (skip) {
          skip = false;
        } else if (Dv[i] < min) {
          min = Dv[i];
          iMin = i;
        }
      }

      // Add w to N'
      nprime += "," + Integer.toString(nodes[iMin+1].getId());
      if (firstIndex) {
        nprimeIndices += Integer.toString(nodes[iMin+1].getId()-2);
        firstIndex = false;
      }
      else nprimeIndices += "," + Integer.toString(nodes[iMin+1].getId()-2);
      skipIndices = nprimeIndices.split(",");

      currentNode = nodes[iMin+1];

      // Update D(v) for each neighbor v of w and not in N':
      //  D(v) = min( D(v), D(w) + c(w,v) )
      for (int i = 0; i < Dv.length; i++) {
        if (Dv[i] <= Math.abs(Dv[iMin] + nodes[iMin+1].getLinkWeight(i+1))); // do nothing
        else {
          Dv[i] = Math.abs(Dv[iMin] + nodes[iMin+1].getLinkWeight(i+1));
          pv[i] = Integer.toString(nodes[iMin+1].getId());
        }
      }
      printProgress();
    }
  }

  // Logs the current step
  private void printProgress() {
    setTab();
    int i = 0;
    if (!header) {
      log(fittedDashes);
      String head = "Step\tN'" + tab;
      for (i = 0; i < Dv.length; i++) {
        head += "D(" + (i+2) + "),p(" + (i+2) + ")\t";
      }
      log(head);
      log(fittedDashes);
    }

    String s = Integer.toString(step) + "\t" + nprime + tab;
    for (i = 0; i < Dv.length; i++) {
      if (Dv[i] == inf) s += "N";
      else s += Integer.toString(Dv[i]) + "," + pv[i];
      s += "\t\t";
    }
    header = true;
    step++;
    log(s);
    log(fittedDashes);
  }

  // Initializes data structure of nodes and begins LS algo
  public static void main(String[] args) {
    linkstate ls = new linkstate();
    ls.initialize(args[0]);
    ls.compute();
  }
}
