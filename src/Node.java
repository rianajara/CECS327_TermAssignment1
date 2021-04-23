import utils.UUIDUtils;

import java.io.File;
import java.io.IOException;

public class Node {
    String timeBasedId;
    int targetPort;
    int receivingPort;
    private Controller controller;
    // TODO: Encapsulate receiver and sender into Controller
    private Receiver receiver;
    private Sender sender;

    //
    public Node(int sPort, int lPort) {
        timeBasedId = UUIDUtils.getTimeBasedUUID().toString().replace("-", "");

        // 9999 port is used to send and receiving message as default
        this.targetPort = sPort == 10000 ? 9999 : sPort;
        this.receivingPort = lPort == 10000 ? 9999 : sPort;
        // Use controller
        this.controller = new Controller(lPort, sPort, timeBasedId);
        // 10000 port is used to transfer files
        receiver = new Receiver(10000);
        sender = new Sender(10000);
    }

    // For testing purpose
    public void sendMessage(byte[] arr, String ip) throws IOException {
        controller.sendMessage(arr, ip);
    }

    // For testing purpose
    public void send(String ip, File f) throws IOException {
        sender.send(ip, f);
    }


}
