import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;

public class Server {
  // Runs the Server
  public static void main(String[] args) throws IOException {
    System.out.println("The capitalization server is running.");
    ServerSocket listener = new ServerSocket(9659);
    try {
      while (true) {
        new CapHandler(listener.accept());
      }
    } finally {
      listener.close();
    }
  }

  private static class CapHandler {
    private Socket socket;

    public CapHandler(Socket socket) {
      this.socket = socket;
      log("New connection with client at " + socket);
    }

    public void run() {
      try {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("Hello!");
        out.println("Enter a line with only a period to quit\n");

        while (true) {
          String input = in.readLine();
          if (input == null || input.equals(".")) {
            break;
          }
          out.println(input.toUpperCase());
        }
      } catch (IOException e) {
        log("Error handling: " + e);
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          log("Couldn't close a socket, what's going on?");
        }
        log("Connection cloased");
      }
    }

    private void log(String message) {
      System.out.println(message);
    }
  }
}
