import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class receiver {

  private BufferedReader in;
  private PrintWriter out;
  private static final String delims = "[ ]+";

  // Constructor to allow calling member functions
  public receiver(String serverURL, int port) throws IOException {
    Socket socket = new Socket(serverURL, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    String greeting = in.readLine();
    log("received: " + greeting);
  }

  // Wrapper function for RDT 3.0 Receiver protocol
  private void receivePackets() {
    while(true) {
      try {
        rdt_rcv(in.readLine());
      } catch (IOException e) {
        log("Error: " + e);
      }
    }
  }

  private void rdt_rcv(String data) {
    log("received: " + data);
    String ack = "0 0 300 ACK";
    udt_send(ack);
  }

  private void udt_send(String data) {
    log("sending: " + data);
    out.println(data);
  }

  // Convenience function for printing to console
  private static void log(String s) {
    System.out.println(s);
  }

  public static void main(String[] args) throws Exception {
    String serverURL = args[0];
    int port = Integer.parseInt(args[1]);

    receiver r = new receiver(serverURL, port);
    r.receivePackets();
  }
}
