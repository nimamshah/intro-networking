public class Node {
  private int id;
  private int[] links;
  private static final int inf = 100;

  // Helper class to model Nodes in a network
  public Node(int id, String[] links) {
    this.id = id;
    this.links = new int[links.length];
    for (int i = 0; i < links.length; i++) {
      if (links[i].equals("N")) links[i] = Integer.toString(inf);
      this.links[i] = Integer.parseInt(links[i]);
    }
  }

  // Returns the ID of the current node
  public int getId() {
    return id;
  }

  // Returns the weight of a specified link
  public int getLinkWeight(int i) {
    return links[i];
  }

  // Returns string representation of node
  //  this looks like the single line representation
  public String toString() {
    String s = "";
    for (int i = 0; i < links.length; i++) {
      if (i == links.length-1) {
        if (links[i] == inf) s += "N";
        else s += links[i];
      }
      else {
        if (links[i] == inf) s+= "N,";
        else s += links[i] + ",";
      }
    }
    return s;
  }
}
