package sockets;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import app.NialaSQL;
import sockets.threads.ServiceThread;

public class Server {

    private ServerSocket listener;
    private int clientNumber = 0;

    public Server() {}

    private void incrementClient() {
        this.clientNumber += 1;
    }

    private void startServer() {
        System.out.println("Server is waiting ...");
        
        try {
            this.listener = new ServerSocket(6969);
            while (true) {
                Socket socketOfServer = listener.accept(); // the client
                BufferedReader in = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
                PrintWriter out = new PrintWriter(socketOfServer.getOutputStream());
                new ServiceThread(new NialaSQL(), socketOfServer).start();
                System.out.println("New connection with client# " + this.clientNumber + " at " + socketOfServer);
                    //this.incrementClient();
                if (this.clientNumber == -1) break;
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