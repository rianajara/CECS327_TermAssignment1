package cecs327.events;

import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.File;
import java.io.IOException;

public class LeaveEvent implements Event {
    private int type;
    private String leftNodeID;

    public byte[] createLeaveEventData(String id) throws IOException {
        this.type = EventType.LEAVE_NETWORK;
        this.leftNodeID = id;
        return packData();
    }

    @Override
    public void unpackData(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);

        // 1. Read the event type
        this.type = dr.readInt();
        System.out.println("-------------------------------------------");
        System.out.println("Event Type: " + type);

        // 2. Read the ID of the leaving node
        int leftNodeIdLen = dr.readInt();
        byte[] leftNodeIdBytes = new byte[leftNodeIdLen];
        dr.readFully(leftNodeIdBytes);
        this.leftNodeID = new String(leftNodeIdBytes);

        String path = "./sync/" + leftNodeID + "/";
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    System.out.println("Remove " + f.getName() + ".");
                    f.delete();
                }
            }
            dir.delete();
        }
        System.out.println(leftNodeID + " left the network.");
        dr.close();
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();
        // 1. Write the event type
        dw.writeInt(type);

        // 2. Write the file owner's UUID
        byte[] nodeID = leftNodeID.getBytes();
        int idLen = nodeID.length;
        dw.writeInt(idLen);
        dw.write(nodeID);

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
        return null;
    }

    @Override
    public String getNodeUUID() {
        return this.leftNodeID;
    }
}
