package cecs327;

import java.io.*;
import java.net.Socket;

/**
 * Sender is responsible to send the event data
 */
public class Sender {
    private int fileSendingPort;

    public Sender(int fileSendingPort) {
        this.fileSendingPort = fileSendingPort;
    }

    public void sendData(String ip, byte[] data) throws IOException {
        Socket socket = new Socket(ip, fileSendingPort);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        int len = data.length;
        synchronized (socket) {
            dos.writeInt(len);
            dos.write(data, 0, len);
            dos.flush();
        }
        dos.close();
        socket.close();
    }
}