import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class receiver {

  enum STATE {
    WAIT0, WAIT1
  }

  private BufferedReader in;
  private PrintWriter out;
  private static final String delims = "[ ]+";
  private STATE state;
  private int totalPackets;

  // Constructor to allow calling member functions
  public receiver(String serverURL, int port) throws IOException {
    // Setup networking
    Socket socket = new Socket(serverURL, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    // Initialize state
    state = STATE.WAIT0;
    totalPackets = 0;

    String greeting = in.readLine();
    log("received: " + greeting);
  }

  // Wrapper function for RDT 3.0 Receiver protocol
  private void receivePackets() {

    while(true) {
      try {
        // Increments number of received packets
        //  then hands control to RDT2.2/3.0 FSM
        totalPackets++;
        rdt_rcv(in.readLine());

      } catch (IOException e) {
        log("Error: " + e);
      }
    }
  }

  // make_pkt for creating ACKs, accepts only a sequence number and the string
  private Message make_pkt(byte seq, String packet) {
    return new Message(seq, (byte)0, 0, packet + Byte.toString(seq));
  }

  // Wrapper for all logic of RDT2.2/3.0 Receiver
  private void rdt_rcv(String rcvstr) {
    Message rcvpkt = Message.extract(rcvstr);
    Message sndpkt = new Message();
    switch (state) {
      case WAIT0:
        // NOTE: Ternary inserts proper ACK to be transmitted
        log("Waiting 0, " + totalPackets + ", " + rcvstr + ", " + (has_seq0(rcvpkt) ? "ACK0" : "ACK1"));
        if (notcorrupt(rcvpkt)
          && has_seq0(rcvpkt)) {
            sndpkt = make_pkt((byte)0, "ACK");
            state = STATE.WAIT1;
        } else {
          sndpkt = make_pkt((byte)1, "ACK");
        }
        break;
      case WAIT1:
        // NOTE: Ternary inserts proper ACK to be transmitted
        log("Waiting 1, " + totalPackets + ", " + rcvstr + ", " + (!has_seq0(rcvpkt) ? "ACK1" : "ACK0"));
        if (notcorrupt(rcvpkt)
          && !has_seq0(rcvpkt)) {
            sndpkt = make_pkt((byte)1, "ACK");
            state = STATE.WAIT0;
        } else {
          sndpkt = make_pkt((byte)0, "ACK");
        }
        break;
    }
    udt_send(sndpkt);
  }

  // Verifies checksum of packet equals the sum of the ASCII values of the packet
  private boolean notcorrupt(Message data) {
    String s = data.getPacket();
    s = s.replaceAll("\\s+","");
    char[] chars = s.toCharArray();
    int checksum = 0;
    for (int i = 0; i < chars.length; i++) {
      checksum += (int)chars[i];
    }
    if (checksum == data.getChecksum()) return true;
    else return false;
  }

  // Checks if the sequence number is 0
  private boolean has_seq0(Message data) {
    if (data.getSeq() == 0) return true;
    else return false;
  }

  // Passes packet to the network
  private void udt_send(Message data) {
    // log(Message.toString(data));
    out.println(Message.toString(data));
  }

  // Convenience function for printing to console
  private static void log(String s) {
    System.out.println(s);
  }

  // Instantiates the receiver and beings receiving packets
  public static void main(String[] args) throws Exception {
    String serverURL = args[0];
    int port = Integer.parseInt(args[1]);

    receiver r = new receiver(serverURL, port);
    r.receivePackets();
  }
}
