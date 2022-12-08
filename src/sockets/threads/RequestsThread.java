package sockets.threads;

import java.io.PrintWriter;
import java.util.Scanner;

public class RequestsThread implements Runnable {

    private PrintWriter out;

    public RequestsThread(PrintWriter out) {
        this.out = out;
    }


    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.print("NialaSQL> ");
            String request = scanner.nextLine();
            this.out.println(request);
            this.out.flush();
            if (request.equalsIgnoreCase("EXIT")) {
                this.out.close();
                scanner.close();
                return;
            }
        }
    }
}
