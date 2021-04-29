package cecs327.events;

import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.IOException;

public class JoinEvent implements Event {
    private int type;
    private String newNodeIP;
    private String newNodeID;

    public byte[] createJointEventData(String nodeID, String nodeIP) throws IOException {
        this.type = EventType.JOIN_NETWORK;
        this.newNodeID = nodeID;
        this.newNodeIP = nodeIP;

        return packData();
    }

    @Override
    public void unpackData(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);

        // 1. Read event type
        this.type = dr.readInt();

        // 2. Read the UUID of the new node
        int idLen = dr.readInt();
        byte[] newNodeIDBytes = new byte[idLen];
        dr.readFully(newNodeIDBytes);
        this.newNodeID = new String(newNodeIDBytes);

        // 3. Read the IP of the new node
        int ipLen = dr.readInt();
        byte[] newNodeIPBytes = new byte[ipLen];
        dr.readFully(newNodeIPBytes);
        this.newNodeIP = new String(newNodeIPBytes);

        dr.close();
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();

        // 1. Write the type of the event
        dw.writeInt(type);

        // 2. Write the UUID of new node
        // 2. 1 Get the byte arr format of new node UUID
        byte[] nodeIDBytes = newNodeID.getBytes();
        // 2. 2 Get the len of the arr
        int idLen =nodeIDBytes.length;
        // 2.3 Write the len and the arr
        dw.writeInt(idLen);
        dw.write(nodeIDBytes);

        // 3. Write the IP of the new Node
        byte[] nodeIPBytes = newNodeIP.getBytes();
        int ipLen = nodeIPBytes.length;
        dw.writeInt(ipLen);
        dw.write(nodeIPBytes);

        dw.flush();
        data = dw.toByteArray();
        dw.close();

        return data;
    }


    @Override
    public int getEventType() {
        return type;
    }

    @Override
    public String getNodeIP() {
        return newNodeIP;
    }

    @Override
    public String getNodeUUID() {
        return newNodeID;
    }
}
