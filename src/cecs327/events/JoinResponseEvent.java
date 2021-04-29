package cecs327.events;

import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.IOException;

public class JoinResponseEvent implements Event {
    private int type;
    private String responseNodeIP;
    private String responseNodeID;

    public byte[] createJointResponseEventData(String responseNodeID, String responseNodeIP) throws IOException {
        this.type = EventType.JOIN_NETWORK_RESPONSE;
        this.responseNodeID = responseNodeID;
        this.responseNodeIP = responseNodeIP;

        return packData();
    }

    @Override
    public void unpackData(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);

        // 1. Read the event type
        this.type = dr.readInt();


        // 2. Read the UUID of the node that sends back the response
        int idLen = dr.readInt();
        byte[] responseNodeIDBytes = new byte[idLen];
        dr.readFully(responseNodeIDBytes);
        this.responseNodeID = new String(responseNodeIDBytes);
        

        // 3. Read the IP of the node that sends back the response
        int ipLen = dr.readInt();
        byte[] responseNodeIPBytes = new byte[ipLen];
        dr.readFully(responseNodeIPBytes);
        this.responseNodeIP = new String(responseNodeIPBytes);

        dr.close();
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();

        // 1. Write the event type
        dw.writeInt(type);

        // 2. Write the UUID of the node that is going to send the response
        byte[] responseNodeIDBytes = responseNodeID.getBytes();
        int idLen = responseNodeIDBytes.length;
        dw.writeInt(idLen);
        dw.write(responseNodeIDBytes);

        // 3.Write the IP of the node that is going to send the response
        byte[] responseNodeIPBytes = responseNodeIP.getBytes();
        int ipLen = responseNodeIPBytes.length;
        dw.writeInt(ipLen);
        dw.write(responseNodeIPBytes);

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
        return responseNodeIP;
    }

    @Override
    public String getNodeUUID() {
        return responseNodeID;
    }
}
