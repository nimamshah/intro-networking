public class Node {
  private int id;
  private int[] links;
  private static final int inf = 100;

  public Node(int id, String[] links) {
    this.id = id;
    this.links = new int[links.length];
    for (int i = 0; i < links.length; i++) {
      if (links[i].equals("N")) links[i] = Integer.toString(inf);
      this.links[i] = Integer.parseInt(links[i]);
    }
  }

  public int getId() {
    return id;
  }

  public int getLinkWeight(int i) {
    return links[i];
  }

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
