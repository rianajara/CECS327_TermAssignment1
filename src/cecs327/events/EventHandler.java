package cecs327.events;

import cecs327.Node;
import cecs327.utils.DataReader;

import java.io.IOException;

/**
 * EventHandler is another core of the program. It will take
 * the incoming event data and resolve the event.
 */
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

        // Handle the case of joining into the network
        if (eventType == EventType.JOIN_NETWORK) {
            Event e = new JoinEvent();
            e.unpackData(data);
            // Ignore the broadcast to itself
            if (!node.getIPAddress().equals(e.getNodeIP())) {
                System.out.println("-------------------------");
                System.out.println("Event Type: " + e.getEventType());
                node.onEvent(e);
            }
        }
        // Handle the case when a new node receive response from other nodes
        else if (eventType == EventType.JOIN_NETWORK_RESPONSE) {
            Event e = new JoinResponseEvent();
            e.unpackData(data);
            node.onEvent(e);
        }
        // Handle the case of receiving files
        else if (eventType == EventType.SEND_FILE) {
            Event e = new SendFileEvent();
            e.unpackData(data);
        }
        // Handle the case when a file is removed from its owner
        else if (eventType == EventType.REMOVE_FILE) {
            Event e = new RemoveFileEvent();
            e.unpackData(data);
        }
        // Handle the case when a node is leaving the network
        else if (eventType == EventType.LEAVE_NETWORK) {
            Event e = new LeaveEvent();
            e.unpackData(data);
            node.onEvent(e);
        }
        else if (eventType == EventType.CREATE_DIR) {
            Event e = new CreateDirEvent();
            e.unpackData(data);
        }
        else if(eventType == EventType.REMOVE_DIR) {
            Event e = new RemoveDirEvent();
            e.unpackData(data);
        }

    }

    /**
     * Resolve the type of the event
     * @param data the incoming event data
     * @return the type of the event
     * @throws IOException
     */
    private int getType(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);
        int type = dr.readInt();
        dr.close();

        return type;
    }
}
