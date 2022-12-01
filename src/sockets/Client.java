package sockets;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.*;
import sockets.threads.CommunicationThread;
import sockets.threads.RequestsThread;

public class Client {

  public static void main(String[] args) {
    final String serverHost = "localhost";

    Socket socketOfClient = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try {
      // Send a request to connect to the server is listening
      // on machine 'localhost' port 6969.
      socketOfClient = new Socket(serverHost, 6969);

      // Create output stream at the client (to send data to the server)
      out =
        new PrintWriter(
          socketOfClient.getOutputStream()
        );

      // Input stream at Client (Receive data from the server).
      in =
        new BufferedReader(
          new InputStreamReader(socketOfClient.getInputStream())
        );
        
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host " + serverHost);
      return;
    } catch (IOException e) {
      System.err.println(
        "Couldn't get I/O for the connection to " + serverHost
      );
      return;
    }

    // to send
    new Thread(new RequestsThread(out)).start();

    // to receive
    new Thread(new CommunicationThread(socketOfClient, in, out)).start();
  }
}
