package cecs327.events;

import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.File;
import java.io.IOException;

public class CreateDirEvent implements Event {
    private int type;
    private String path;

    public byte[] createDirEventData(String path) throws IOException {
        this.type = EventType.CREATE_DIR;
        this.path = path.replace("\\", "/");
        return packData();
    }

    @Override
    public void unpackData(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);

        // 1. Read the Event type
        this.type = dr.readInt();
        System.out.println("-------------------------------------------");
        System.out.println("Event Type: " + type);

        // 2. Read the dir path
        int pathLen = dr.readInt();
        byte[] pathBytes = new byte[pathLen];
        dr.readFully(pathBytes);
        this.path = new String(pathBytes);

        File f = new File(path);
        if (!f.exists()) f.mkdir();
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();
        // 1. Write the event type
        dw.writeInt(type);

        // 2. Write the dir path
        byte[] pathBytes = path.getBytes();
        int pathLen = pathBytes.length;
        dw.writeInt(pathLen);
        dw.write(pathBytes);

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
        return null;
    }
}
