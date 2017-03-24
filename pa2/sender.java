import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class sender {

  enum STATE {
    WAIT_0CALL, WAIT_ACK0,
    WAIT_1CALL, WAIT_ACK1
  }

  private BufferedReader in;
  private PrintWriter out;
  private ArrayList<Message> packets = new ArrayList<Message>();
  private STATE state;
  private int totalSent;
  private int currentPacket;

  private static final String delims = "[ ]+";

  // Constructor to allow calling member functions
  public sender(String serverURL, int port) throws IOException {
    // Setup networking
    Socket socket = new Socket(serverURL, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    // Initialize state
    state = STATE.WAIT_0CALL;
    totalSent = 0;
    currentPacket = 0;

    String greeting = in.readLine();
    log("GREETING: " + greeting);
  }

  // Wrapper function for RDT 3.0 Sender protocol
  private void sendPackets() {

    while (true) {
      if (currentPacket == packets.size()) {
        out.println("-1");
        break;
      }
      Message sndpkt = packets.get(currentPacket);
      rdt_send(sndpkt);

      try {
        rdt_rcv(in.readLine(), sndpkt);
      } catch (IOException ex) {
        log("Error: " + ex);
      }
    }
  }

  // Wrapper function for rdt_send of RDT 3.0 Sender protocol
  private void rdt_send(Message sndpkt) {
    switch (state) {
      case WAIT_0CALL:
        state = STATE.WAIT_ACK0;
        break;
      case WAIT_1CALL:
        state = STATE.WAIT_ACK1;
        break;
    }
    // log("\tsending: " + Message.toString(sndpkt));
    totalSent++;
    udt_send(sndpkt);
  }

  // Wrapper function for rdt_rcv of RDT 3.0 Sender protocol
  private void rdt_rcv(String rcvstr, Message sndpkt) {
    Message rcvpkt = Message.extract(rcvstr);

    switch (state) {
      case WAIT_ACK0:
        if (notcorrupt(rcvpkt) && isACK(rcvpkt, (byte)0)) {
          currentPacket++;
          state = STATE.WAIT_1CALL;
        }
        log("Waiting ACK0, " + Integer.toString(totalSent) + " " + (timeout(rcvpkt) ? "DROP" : rcvpkt.getPacket()) +
          (rcvpkt.isACK() ? Byte.toString(rcvpkt.getSeq()) : "") + " " +
          ((!isACK(rcvpkt, (byte)0) || !notcorrupt(rcvpkt)) ? "resend Packet0"
            : done() ? "no more packets to send"
            : "send Packet1"));
        break;
      case WAIT_ACK1:
        if (notcorrupt(rcvpkt) && isACK(rcvpkt, (byte)1)) {
          currentPacket++;
          state = STATE.WAIT_0CALL;
        }
        log("Waiting ACK1, " + Integer.toString(totalSent) + " " + (timeout(rcvpkt) ? "DROP" : rcvpkt.getPacket()) +
          (rcvpkt.isACK() ? Byte.toString(rcvpkt.getSeq()) : "") + " " +
          ((!isACK(rcvpkt, (byte)1) || !notcorrupt(rcvpkt)) ? "resend Packet1"
            : done() ? "no more packets to send"
            : "send Packet0"));
        break;
    }
  }

  // Checks if packet has been dropped
  private boolean timeout(Message rcvpkt) {
    return rcvpkt.getSeq() == (byte)2;
  }

  // Verifies checksum of ACK packet is 0
  private boolean notcorrupt(Message rcvpkt) {
    // String s = rcvpkt.getPacket();
    // s = s.replaceAll("\\s+","");
    // char[] chars = s.toCharArray();
    // int checksum = 0;
    // for (int i = 0; i < chars.length; i++) {
    //   checksum += (int)chars[i];
    // }
    // if (checksum == rcvpkt.getChecksum()) return true;
    // else return false;
    if (rcvpkt.getChecksum() == 0) return true;
    else return false;
  }

  // Checks sequence number of ACK
  private boolean isACK(Message rcvpkt, byte seq) {
    return rcvpkt.getSeq() == seq;
  }

  // Passes packet to the network
  private void udt_send(Message data) {
    out.println(Message.toString(data));
  }

  // Checks if final packet has been sent
  private boolean done() {
    return currentPacket >= packets.size();
  }

  // Convenience function for printing to console
  private static void log(String s) {
    System.out.println(s);
  }

  // Helper function to determine checksum
  private static int calculateChecksum(String s) {
    s = s.replaceAll("\\s+","");
    char[] chars = s.toCharArray();
    int checksum = 0;
    for (int i = 0; i < chars.length; i++) {
      checksum += (int)chars[i];
    }
    return checksum;
  }

  // Helper function to parse message.txt and create packets for transmission
  private void createPackets(String filename) {
    byte seq = 1;

    // JDK7+ try-with-resources
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String currentLine;
      currentLine = br.readLine();
      String[] words = currentLine.split(delims);

      for (int i = 0; i < words.length; i++) {
        if (seq == 0) seq = 1;
        else seq = 0;
        packets.add(new Message(seq, (byte)(i+1), calculateChecksum(words[i]), words[i]));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Debugging function to see what packets are created
  private void printPackets() {
    for (int i = 0; i < packets.size(); i++) {
      log(Message.toString(packets.get(i)));
    }
  }

  public static void main(String[] args) throws Exception {
    String serverURL = args[0];
    int port = Integer.parseInt(args[1]);
    String messageFileName = args[2];

    sender s = new sender(serverURL, port);
    s.createPackets(messageFileName);

    // DEBUGGING
    // s.printPackets();
    // END DEBUGGING

    s.sendPackets();
  }
}
