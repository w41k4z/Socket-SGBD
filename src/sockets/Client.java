package sockets;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

import sockets.threads.CommunicationThread;
import sockets.threads.RequestsThread;

public class Client {

  public static void main(String[] args) {
    new Client().connect();
  }

  public void connect() {
    Scanner scanner = new Scanner(System.in);
    String serverIp = null;
    int port = -1;

    Socket socketOfClient = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try {
      System.out.print("Enter the server ip: ");
      serverIp = scanner.nextLine();
      System.out.print("Enter the port: ");
      port = Integer.parseInt(scanner.nextLine());

      // Send a request to connect to the listening server
      socketOfClient = new Socket(serverIp, port);

      // Create output stream at the client (to send data to the server)
      out = new PrintWriter(
          socketOfClient.getOutputStream());

      // Input stream at Client (Receive data from the server).
      in = new BufferedReader(
          new InputStreamReader(socketOfClient.getInputStream()));

    } catch (NumberFormatException e) {
      System.err.println("This is not a valid port\n");
      this.connect();
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host " + serverIp + "\n");
      this.connect();
    } catch (IOException e) {
      System.err.println(
          "Couldn't get I/O for the connection to " + serverIp + "\n");
      this.connect();
    }

    // to send
    new Thread(new RequestsThread(out)).start();

    // to receive
    new Thread(new CommunicationThread(socketOfClient, in, out)).start();
  }
}
