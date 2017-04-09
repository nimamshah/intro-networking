public class Node {
  private int id;
  private String[] links;

  public Node(int id, String[] links) {
    this.id = id;
    this.links = links;
  }

  public int getId() {
    return id;
  }

  public String getLinkWeight(int i) {
    return links[i];
  }

  public String toString() {
    String s = "";
    for (int i = 0; i < links.length; i++) {
      s += links[i] + ", ";
    }
    return s;
  }
}
