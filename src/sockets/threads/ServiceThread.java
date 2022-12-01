package sockets.threads;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import app.NialaSQL;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ServiceThread extends Thread {
    
    private Socket socketOfServer;
    private NialaSQL nialaSQL;

    public ServiceThread(NialaSQL nialaSQL, Socket socketOfServer) {
        this.nialaSQL = nialaSQL;
        this.nialaSQL.connect();
        this.socketOfServer = socketOfServer;
    }

    @Override
    public void run() {

        try {

            // Open input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            PrintWriter out = new PrintWriter(socketOfServer.getOutputStream());

            this.nialaSQL.run(in, out, false);

            out.println("BYE !");
            out.flush();
            this.socketOfServer.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
