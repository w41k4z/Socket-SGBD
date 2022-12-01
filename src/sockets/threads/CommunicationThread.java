package sockets.threads;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class CommunicationThread implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public CommunicationThread(Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            // Read data sent from the server.
            // By reading the input stream of the Client Socket.
            String response;
            while ((response = this.in.readLine()) != null) {
                System.out.println(response);
                if (response.equalsIgnoreCase("BYE !")) {
                    break;
                }
            }
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}
