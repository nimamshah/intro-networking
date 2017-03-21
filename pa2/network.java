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

    RandomJava rj = new RandomJava();

    try {
      while (true) {
        NetworkManager network = new NetworkManager();
        NetworkManager.NetworkClient client1 = network.new NetworkClient(listener.accept(), 1);
        NetworkManager.NetworkClient client2 = network.new NetworkClient(listener.accept(), 2);
        client1.setTarget(client2);
        client2.setTarget(client1);
        network.recipient = client2;
        client1.start();
        client2.start();
      }
    } finally {
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
  // NetworkClient client1;
  // NetworkClient client2;
  NetworkClient recipient;

  public void relay(String packet, int clientNumber) {
    Message data = Message.extract(packet);
    log("Received: " + Byte.toString(data.getId()) + ", " + "ACTION");
    recipient.sendRelay(packet);
    recipient = recipient.target;
    // if (clientNumber == 1) {
    //   client2.sendRelay(packet);
    // } else {
    //   client1.sendRelay(packet);
    // }
  }

  // Convenience function for printing to console
  public static void log(String s) {
    System.out.println(s);
  }

  // Private thread to handle message relay between sender and receiver
  class NetworkClient extends Thread {
    private NetworkClient target;
    private int clientNumber;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

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

    public void setTarget(NetworkClient target) {
      this.target = target;
    }

    // Sends result of NetworkManager.sendRelay()
    public void sendRelay(String packet) {
      // log("relaying: " + packet);
      out.println(packet);
    }

    // Override function from Thread class
    public void run() {
      try {
        // Repeatedly accept packets from client
        while (true) {
          String input = in.readLine();
          log(input);
          relay(input, clientNumber);
        }
      } catch (IOException e) {
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
