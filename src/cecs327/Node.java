package cecs327;

import cecs327.events.Event;
import cecs327.events.EventHandler;
import cecs327.utils.UUIDUtils;
import java.io.IOException;
import java.util.HashMap;

public class Node {
    String nameBasedUUID;
    int port;
    EventHandler eh;
    HashMap<String, String> clientsMap;
    private FileController fileController;

    public Node(int port) {
        nameBasedUUID = UUIDUtils.getNameBasedUUID().toString();
        this.port = port;

        eh = EventHandler.getInstance();
        eh.setNode(this);
        // Use controller
        fileController = new FileController(port, nameBasedUUID, eh);
        // 10000 port is used to transfer files

    }


    // For testing purpose
    public void sendData(String ip, byte[] data) throws IOException {
        fileController.sendData(ip, data);
    }

    public void onEvent(Event e) {

        // TODO: Do different operations based on the type of the event
    }



}
