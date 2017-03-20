public class Message {

  private byte seq;
  private byte id;
  private int checksum;
  private String packet;

  private static final String delims = "[ ]+";

  // Constructor to a message for transmission
  public Message(byte seq, byte id, int checksum, String packet) {
    this.seq = seq;
    this.id = id;
    this.checksum = checksum;
    this.packet = packet;
  }

  // Converts object to a string for transmission
  public static String toString(Message m) {
    String s = Byte.toString(m.seq) + ' ' + Byte.toString(m.id)
      + ' ' + Integer.toString(m.checksum) + ' ' + m.packet;

    return s;
  }

  // Converts String representation of message to original
  public static Message toMessage(String s) {
    String[] sections = s.split(delims);
    return new Message((byte)Integer.parseInt(sections[0]),
      (byte)Integer.parseInt(sections[1]), Integer.parseInt(sections[2]),
      sections[3]);
  }
}
