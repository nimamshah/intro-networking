import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class network {

  // Convenience function for printing to console
  private static void log(String s) {
    System.out.println(s);
  }

  // Helper class to generate random numbers
  public static class RandomJava {
    Random r;
    RandomJava() { r = new Random(); }
    public double getRandomValue() { return r.nextDouble(); }
  }

  // Private thread to handle message relay between sender and receiver
  private static class NetworkSocket extends Thread {
    private Socket socket;
    private String endpoint;

    public NetworkSocket(Socket socket) {
      this.socket = socket;
      // this.endpoint = endpoint;
    }

    // Override function from Thread class
    public void run() {
      try {
        BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("Connected to network.");

        while (true) {
          String input = in.readLine();
          log(input);
          out.println("Hiya");
          // Parse input HERE
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

  public static void main(String[] args) throws IOException {
    int port = Integer.parseInt(args[0]);
    ServerSocket listener = new ServerSocket(port);
    log("Network running.");

    RandomJava rj = new RandomJava();

    try {
      while (true) {
        new NetworkSocket(listener.accept()).start();
      }
    } finally {
      listener.close();
    }
  }
}
