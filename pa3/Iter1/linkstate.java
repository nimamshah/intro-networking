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

  // Helper function for shorter console logs
  private static void log(String s) {
    System.out.println(s);
  }

  // Reads file to form table of node data
  private void initialize(String filename) {
    // JDK7 try-with-resources
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String currentLine;

      // Iterate through lines
      // int row = 0;
      int size = -1;
      int i = 0;

      while (!(currentLine = br.readLine()).equals("EOF.")) {
        // Do something with each line
        log(currentLine);

        // Remove period from end of line
        currentLine = currentLine.substring(0, currentLine.length() - 1);
        String[] tokens = currentLine.split(",");

        if (nodes == null) {
          size = tokens.length;
          nodes = new Node[size];
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

      // for (i = 0; i < table.length; i++) {
      //   for (int j = 0; j < table[i].length; j++) {
      //     // log(table[i][j]);
      //     System.out.print(table[i][j]);
      //   }
      //   System.out.println();
      // }

      for (i = 0; i < nodes.length; i++) {
        log(nodes[i].toString());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    linkstate ls = new linkstate();
    ls.initialize(args[0]);
  }
}
