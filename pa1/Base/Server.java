import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Server {
  // Runs the Server
  public static void main(String[] args) throws IOException {
    ServerSocket listener = new ServerSocket(9659);
    System.out.println("Server started.");
    try {
      while (true) {
        Socket socket = listener.accept();
        System.out.println("Server is listening.");
        try {
          BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          out.println("Hello!");

          while (true) {
            String input = in.readLine();
            if (input.equals("bye")) {
              System.out.println("Input received:" + input);
              break;
            }
            out.println("Thanks!");
          }
        } finally {
          socket.close();
        }
      }
    } finally {
      listener.close();
    }
  }
}
