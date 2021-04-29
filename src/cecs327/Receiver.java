package cecs327;

import cecs327.events.EventHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A multi-thread server listing to a certain port
 */
public class Receiver extends Thread {
    private ServerSocket server;
    private EventHandler eh;

    public Receiver(int dataReceivingPort, EventHandler eh) {
        try {
            // Use a multi-thread server
            server = new ServerSocket(9999);
            this.eh = eh;
            System.out.println("File Receiver starts successfully! Bind " + dataReceivingPort + " port.");
        } catch (Exception e) {
            System.out.println("Initialize Receiver fail!");
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                Socket socket = null;
                // It will block here until a connection complete
                socket = server.accept();
                new ReceiverThread(socket, eh).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}