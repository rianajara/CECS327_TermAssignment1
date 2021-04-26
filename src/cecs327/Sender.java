package cecs327;

import java.io.*;
import java.net.Socket;


public class Sender {
    private int fileSendingPort;

    public Sender(int fileSendingPort) {
        this.fileSendingPort = fileSendingPort;
    }

    // 这里就是协议+文件一起发过去
    public void sendData(String ip, byte[] data) throws IOException {
        Socket socket = new Socket(ip, 9999);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("Send data to " + ip);
        int len = data.length;
        synchronized (socket) {
            dos.writeInt(len);
            dos.write(data, 0, len);
            dos.flush();
        }
        dos.close();
        socket.close();
    }

    public void send(String ip, File f) throws IOException {
        // Create a socket and specify the target IP address and port
        Socket socket = new Socket(ip, fileSendingPort);
        // PrintStream is used to send data out
        PrintStream ps = new PrintStream(socket.getOutputStream());
        // BufferedReader is used to read the message coming from the target
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Send the file name
        ps.println(f.getName());
        // TODO: Need to handle the filename conflict
        System.out.println("Result of operation: " + br.readLine());
        // InputStream is used to read the local file
        InputStream is = new FileInputStream(f);
        int len;
        // Buffer
        byte[] arr = new byte[8192];
        while ((len = is.read(arr)) != -1) {
            ps.write(arr, 0, len);
        }

        is.close();
        socket.close();
    }

}