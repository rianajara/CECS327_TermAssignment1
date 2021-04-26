package cecs327;

import cecs327.events.EventHandler;

import java.io.*;
import java.net.Socket;

public class ReceiverThread extends Thread{

    private Socket socket;
    private EventHandler eventHandler;

    public ReceiverThread(Socket s, EventHandler eh) {
        this.socket = s;
        this.eventHandler = eh;
    }

    public void run() {
        try {
            System.out.println("Start receiving...");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int dataLen = dis.readInt();
            if (dataLen > 0) {
                byte[] data = new byte[dataLen];
                dis.readFully(data, 0, dataLen);
                eventHandler.resolveEvent(data);
            }
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
