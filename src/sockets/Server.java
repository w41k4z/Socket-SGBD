package sockets;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import app.NialaSQL;
import sockets.threads.ServiceThread;

public class Server {

    private ServerSocket listener;
    private boolean disconnect;

    public Server() {}

    private void startServer() {
        this.disconnect = false;
        System.out.println("Server is waiting ...");
        
        try {
            this.listener = new ServerSocket(6969);
            while (true) {
                Socket socketOfServer = listener.accept(); // the client
                new ServiceThread(new NialaSQL(), socketOfServer).start();
                System.out.println("New connection with client: " + socketOfServer);
                if (this.disconnect) break;
            }
            this.listener.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String args[]) throws IOException {
        Server server = new Server();
        server.startServer();
    }
}