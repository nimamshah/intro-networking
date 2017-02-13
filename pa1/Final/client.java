import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class client {

  private BufferedReader in;
  private PrintWriter out;

  // Constructor to allow calling member functions
  public client() {

  }

  // Sends query to server
  public void query() throws IOException {
    BufferedReader command = new BufferedReader(new InputStreamReader(System.in));
    String query;
    while (true) {
      query = command.readLine();
      out.println(query);
      String response;
      try {
        response = parseResponse();
      } catch (IOException ex) {
        response = "Error: " + ex;
      }
      log("received: " + response);

      if (response.equals("exit.")) System.exit(0);
    }
  }

  // Translates the response into an appropriate error,
  //  else it just passes the result
  public String parseResponse() throws IOException {
    String response = in.readLine();
    switch (response) {
      case "-1":
        response = "incorrect operation command.";
        break;
      case "-2":
        response = "number of inputs is less than two.";
        break;
      case "-3":
        response = "number of inputs is more than four.";
        break;
      case "-4":
        response = "one or more of the inputs contain(s) non-number(s).";
        break;
      case "-5":
        response = "exit.";
        break;
      }

      return response;
  }

  public static void log(String s) {
    System.out.println(s);
  }

  // Performs initial connection to server,
  //  also initializes IO streams
  public void connectToServer(String serverURL, int port) throws IOException {
    Socket socket = new Socket(serverURL, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    String greeting = in.readLine();
    log("received: " + greeting);
  }

  public static void main(String[] args) throws Exception {
    String serverURL = !(args[0] == null) ? args[0] : "127.0.0.1";
    int port = !(args[1] == null) ? Integer.parseInt(args[1]) : 9659;
    client c = new client();
    c.connectToServer(serverURL, port);
    c.query();
  }
}
