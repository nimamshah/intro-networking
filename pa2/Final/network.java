import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class network {

  // Runs network and awaits connection from sender and receiver
  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(args[0]);
    ServerSocket listener = new ServerSocket(port);
    log("Network running.");

    try {
      while (true) {
        NetworkManager network = new NetworkManager();
        NetworkManager.NetworkClient client1 = network.new NetworkClient(listener.accept(), 1);
        NetworkManager.NetworkClient client2 = network.new NetworkClient(listener.accept(), 2);
        network.nc1 = client1;
        network.nc2 = client2;
        client1.start();
        client2.start();
        while (true) if (!client1.isAlive() && !client2.isAlive()) return;
      }
    } catch (Exception e) {
      // Do nothing
    } finally {
      log("Closing Network.");
      listener.close();
    }
  }

  // Convenience function for printing to console
  public static void log(String s) {
    System.out.println(s);
  }

  // Helper class to generate random numbers
  public static class RandomJava {
    Random r;
    RandomJava() { r = new Random(); }
    public double getRandomValue() { return r.nextDouble(); }
  }
}

class NetworkManager {
  NetworkClient nc1;
  NetworkClient nc2;
  RandomJava rj = new RandomJava();

  // Sets up packet for transmission to sender/receiver
  public void relay(String packet, int clientNumber) {
    if (packet == null) packet = "-1";
    if (packet.equals("-1")) {
      nc1.sendRelay(packet);
      return;
    }
    Message data = Message.extract(packet);

    // Choose random action to emulate lossy connection
    double random = rj.getRandomValue();
    String action;
    Message droppkt = new Message();
    if (random < 0.5) {
      // PASS
      action = "PASS";
    } else if (random > 0.75) {
      // CORRUPT
      action = "CORRUPT";
      data.setChecksum(data.getChecksum()+1);
    } else {
      // DROP
      action = "DROP";
      droppkt = getDropPkt();
    }
    packet = Message.toString(data);

    // log the received data and the action chosen by the Network
    if (data.isACK()) log("Received: " + data.getPacket() + Byte.toString(data.getSeq()) + ", " + action);
    else log("Received: Packet" + Byte.toString(data.getSeq()) + ", " + action);

    // If action is DROP, pre-empt transmission and force transmission to the sender
    if (action.equals("DROP")) {
      // Transmit message to sender
      nc2.sendRelay(Message.toString(droppkt));
      return;
    }

    if (clientNumber == 1) nc2.sendRelay(packet);
    else nc1.sendRelay(packet);
  }

  // Wrapper function for DROP packet
  private static Message getDropPkt() {
    return new Message((byte)2, (byte)0, 0, "ACK");
  }

  // Convenience function for printing to console
  public static void log(String s) {
    System.out.println(s);
  }

  // Private thread to handle message relay between sender and receiver
  class NetworkClient extends Thread {
    private int clientNumber;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done = false;

    // Constructs a handler thread for a given socket and clientNumber,
    //  initializes the stream fields.
    public NetworkClient(Socket socket, int clientNumber) {
      this.socket = socket;
      this.clientNumber = clientNumber;
      try {
        in = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println("Connected to network.");
      } catch (IOException e) {
        log("Error: " + e);
      }
    }

    // Sends result of NetworkManager.relay()
    public void sendRelay(String packet) {
      if (packet.equals("-1")) done = true;
      out.println(packet);
    }

    // Override function from Thread class
    public void run() {
      try {
        // Repeatedly accept packets from client
        while (true) {
          String input = in.readLine();
          if (input == null) input = "-1";
          relay(input, clientNumber);
          if (input.equals("-1")) return;
        }
      } catch (IOException e) {
        if (done) return;
        log("Error: " + e);
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          log("Couldn't close socket, what's going on?");
        }
        log("Connection closed.");
      }
    }
  }
}
