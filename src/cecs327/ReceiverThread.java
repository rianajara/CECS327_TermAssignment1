package cecs327;

import cecs327.events.EventHandler;

import java.io.*;
import java.net.Socket;

/**
 * When a new connection complete, a new ReceiverThread
 * will be created to handle the connection. The thread
 * will stop until it receives all data and pass the data
 * to event handler.
 */
public class ReceiverThread extends Thread{

    private Socket socket;
    private EventHandler eventHandler;

    public ReceiverThread(Socket s, EventHandler eh) {
        this.socket = s;
        this.eventHandler = eh;
    }

    public void run() {
        try {
            // The the input stream from the socket
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            // Get how length of the entire event data
            int dataLen = dis.readInt();
            if (dataLen > 0) {
                // Prepare an appropriate size of buffer to store the data
                byte[] data = new byte[dataLen];
                dis.readFully(data, 0, dataLen);
                // Pass the data to event handler
                eventHandler.resolveEvent(data);
            }
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
