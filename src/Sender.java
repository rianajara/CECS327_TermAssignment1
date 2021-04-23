import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Sender {
    private final int targetPort;

    public Sender(int targetPort) {
        this.targetPort = targetPort;
    }

    public void send(String ip, File f) throws IOException {
        // Create a socket and specify the target IP address and port
        Socket socket = new Socket(ip, targetPort);
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