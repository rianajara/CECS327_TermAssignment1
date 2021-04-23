import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Receiver extends Thread {
    private ServerSocket server;

    public Receiver(int receivingPort) {
        try {
            // Use a multi-thread server
            // TODO: encapsulate a new class to run the server and when file comes, create a new thread to handle
            server = new ServerSocket(receivingPort);
            System.out.println("File Receiver starts successfully! Bind " + receivingPort + " port.");
            init();
        } catch (Exception e) {
            System.out.println("Initialize Receiver fail!");
            e.printStackTrace();
        }
    }

    private void init() {
        this.start();
    }

    public void run() {
        while (true) {
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                // Get the input stream from the socket
                InputStream is = socket.getInputStream();
                // Cache the content sent from another node
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                // Get the file name
                PrintStream ps = new PrintStream(socket.getOutputStream());

                String fileName = br.readLine();
                // NOTICE: THIS IS HARDCODE
                File dir = new File("./sync");

                if (!dir.exists()) {
                    dir.mkdir();
                }
                // Generate the complete directory
                File file = new File(dir, fileName);

                // Tell the node is the operation is success or not
                // TODO: Change the file name if two nodes have the same file name

                String msg = file.exists() ? "Update old file" : "Create new file";
                ps.println(msg);

                // FileOutputStream is used to write the incoming data to local file
                FileOutputStream fos = new FileOutputStream(file);
                byte[] arr = new byte[8192];
                int len;
                while ((len = is.read(arr)) != -1) {
                    fos.write(arr, 0, len);
                }

                fos.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}