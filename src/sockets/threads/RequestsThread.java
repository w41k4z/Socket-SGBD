package sockets.threads;

import java.io.PrintWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class RequestsThread implements Runnable {

    private PrintWriter out;

    public RequestsThread(PrintWriter out) {
        this.out = out;
    }


    @Override
    public void run() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("NialaSQL> ");
            String request = scanner.nextLine();
            this.out.println(request);
            this.out.flush();
            if (request.equalsIgnoreCase("EXIT"))
                return;
            try{ Thread.sleep(30); } catch (Exception e) { System.out.println(e); }
        }
    }
}
