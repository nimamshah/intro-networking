package pa2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class sender {

  private BufferedReader in;
  private PrintWriter out;
  private static final String messageFileName = "C:/Users/Nick/Documents/CNT 4007/pa2/message.txt";
  private static final String delims = "[ ]+";
  private ArrayList<Message> packets = new ArrayList<Message>();


  // Constructor to allow calling member functions
  public sender() {

  }

  private void rdt_send() {
    out.println(data);
  }

  private void rdt_rcv(String rcvpkt) {
    
  }

  private void sendPackets() {
    int ack = 0;

    while (true) {
      rdt_send(Message.toString(packets.get(NUMBER)));
      try {
        rdt_rcv(in.readLine());
      } catch (IOException ex) {

      }
    }
  }

  // Convenience function for printing to console
  private static void log(String s) {
    System.out.println(s);
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
        packets.add(new Message(seq, (byte)i, 0, words[i]));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void connectToNetwork(String serverURL, int port) throws IOException {
    Socket socket = new Socket(serverURL, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    String greeting = in.readLine();
    log("received: " + greeting);
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

    sender s = new sender();
    s.connectToNetwork(serverURL, port);
    s.createPackets(messageFileName);

    // DEBUGGING
    s.printPackets();
    // END DEBUGGING

    s.sendPackets();
  }
}
