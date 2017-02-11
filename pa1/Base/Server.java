import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class Server {

  private static String delims = "[ ]+";
  private static String[] commands = {"add", "multiply", "subtract", "bye", "terminate"};

  public static void log(String s) {
    System.out.println(s);
  }

  // Test if token is a number
  public static boolean isNumeric(String str) {
    try {
      int n = Integer.parseInt(str);
    }
    catch(NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  public static boolean tokensAreNumeric(String[] tokens) {
    for (int i = 1; i < tokens.length; i++) {
      if (!isNumeric(tokens[i])) return false;
    }
    return true;
  }

  // Parses tokens and generates appropriate error codes or
  //  call to calculate the answer.
  public static int parseTokens(String[] tokens) {
    int answer = 0;
    if (tokens[0].toLowerCase().equals("bye")) {
      answer = -5;
    } else if (!Arrays.asList(commands).contains(tokens[0])) {
      answer = -1;
    } else if (tokens.length <= 2) {
      answer = -2;
    } else if (tokens.length > 5) {
      answer = -3;
    } else if (!tokensAreNumeric(tokens)){
      answer = -4;
    } else {
      answer = calculateAnswer(tokens);
    }

    return answer;
  }

  // Calculates answer given array of tokens.
  //  First token should specify operation to perform.
  public static int calculateAnswer(String[] tokens) {
    int answer = Integer.parseInt(tokens[1]);
    switch (tokens[0].toLowerCase()) {
      case "add":
        for (int i = 2; i < tokens.length; i++) {
          answer += Integer.parseInt(tokens[i]);
        }
        break;
      case "multiply":
        for (int i = 2; i < tokens.length; i++) {
          answer *= Integer.parseInt(tokens[i]);
        }
        break;
      case "subtract":
        for (int i = 2; i < tokens.length; i++) {
          answer -= Integer.parseInt(tokens[i]);
        }
        break;
    }

    return answer;
  }

  // Runs the Server
  public static void main(String[] args) throws IOException {
    ServerSocket listener = new ServerSocket(9659);
    log("Server started.");
    try {
      while (true) {
        Socket socket = listener.accept();
        log("Server is listening.");
        try {
          BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
          out.println("Hello!");

          while (true) {
            String input = in.readLine();
            String[] tokens = input.split(delims);

            int answer = parseTokens(tokens);
            log("get: " + input + ", return: " + answer);
            out.println(answer);
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
