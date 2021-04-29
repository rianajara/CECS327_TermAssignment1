package cecs327.events;

import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.File;
import java.io.IOException;

public class RemoveFileEvent implements Event{
    private int type;
    private String originNodeID;
    private String removedFileName;

    public byte[] createRemoveFileEventData(String id, String fileName) throws IOException {
        this.type = EventType.REMOVE_FILE;
        this.originNodeID = id;
        this.removedFileName = fileName;
        return packData();
    }

    @Override
    public void unpackData(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);

        // 1. Read the event type
        this.type = dr.readInt();
        System.out.println("-------------------------------------------");
        System.out.println("Event Type: " + type);

        // 2. Read the UUID of the file's owner
        int idLen = dr.readInt();
        byte[] fileOwnerIdBytes = new byte[idLen];
        dr.readFully(fileOwnerIdBytes);
        this.originNodeID = new String(fileOwnerIdBytes);

        // 3. Read the removed file name
        int fileNameLen = dr.readInt();
        byte[] removedFileBytes = new byte[fileNameLen];
        dr.readFully(removedFileBytes);
        this.removedFileName = new String(removedFileBytes);

        dr.close();

        String dir = "./sync/" + originNodeID + "/";
        File removedFile = new File(dir, removedFileName);
        removedFile.delete();

        System.out.println("Remove the file " + removedFileName + " in folder [" + originNodeID + "]");
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();
        // 1. Write the event type
        dw.writeInt(type);

        // 2. Write the UUID of the file's owner
        byte[] nodeID = originNodeID.getBytes();
        int UUIDLen = nodeID.length;
        dw.writeInt(UUIDLen);
        dw.write(nodeID);

        // 3. Write the removed file name
        byte[] removedFileNameBytes = removedFileName.getBytes();
        int fileNameLen = removedFileNameBytes.length;
        dw.writeInt(fileNameLen);
        dw.write(removedFileNameBytes);

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
        return this.originNodeID;
    }
}
