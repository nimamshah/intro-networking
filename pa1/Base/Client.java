import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

  private BufferedReader in;
  private PrintWriter out;

  private JFrame frame = new JFrame("Arithmetic Client");
  private JTextField dataField = new JTextField(40);
  private JTextArea messageArea = new JTextArea(8, 60);


  public Client() {
    // Layout GUI
    messageArea.setEditable(false);
    frame.getContentPane().add(dataField, "North");
    frame.getContentPane().add(new JScrollPane(messageArea), "Center");

    // Add listener
    dataField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        out.println(dataField.getText());
        String response;
        try {
          response = in.readLine();
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
        } catch (IOException ex) {
          response = "Error: " + ex;
        }
        messageArea.append("received: " + response + "\n");
        log("received: " + response);
        dataField.selectAll();
        
        if (response.equals("exit.")) {
          System.exit(0);
        }
      }
    });
  }

  public static void log(String s) {
    System.out.println(s);
  }

  public void connectToServer(String serverURL, int port) throws IOException {
    Socket socket = new Socket(serverURL, port);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    String greeting = in.readLine();
    messageArea.append("received: " + greeting + "\n");
    log("received: " + greeting);
  }

  public static void main(String[] args) throws Exception {
    Client client = new Client();
    client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    client.frame.pack();
    client.frame.setVisible(true);
    client.connectToServer("127.0.0.1", 9659);

    // System.out.println(in.readLine());
    //
    // BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    // String query;
    // System.out.println("Please enter a query.");
    // while (true) {
    //   query = input.readLine();
    //   if (!query.equals("")) {
    //     System.out.println("Query: " + query);
    //     out.println(query);
    //     String response;
    //     try {
    //       response = in.readLine();
    //       System.out.println("Response: " + response);
    //       if (response == null || response.equals("")) {
    //         System.exit(0);
    //       }
    //     } catch (IOException ex) {
    //       response = "Error: " + ex;
    //     }
    //   }
    // }

  }
}
