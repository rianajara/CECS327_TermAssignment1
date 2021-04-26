package cecs327.events;

import cecs327.Node;
import cecs327.Protocol;
import cecs327.utils.DataReader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventHandler {
    private static EventHandler instance = null;
    private Node node = null;

    private EventHandler() {}

    public static EventHandler getInstance() {
        if (instance == null) {
            instance = new EventHandler();
        }
        return instance;
    }

    public void setNode(Node n) {
        this.node = n;
    }

    public void resolveEvent(byte[] data) throws IOException {
        int eventType = getType(data);
        System.out.println("Event Type: " + eventType);

        if (eventType == Protocol.SEND_FILE) {
            Event e = new SendFileEvent();
            e.unpackData(data);
            node.onEvent(e);
        }
        else if

    }

    private int getType(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);
        int type = dr.readInt();
        dr.close();

        return type;
    }

}
