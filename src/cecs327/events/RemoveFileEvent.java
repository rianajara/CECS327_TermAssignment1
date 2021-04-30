package cecs327.events;

import cecs327.CustomFile;
import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.File;
import java.io.IOException;

public class RemoveFileEvent implements Event{
    private int type;
    private String filePath;
    private String removedFileName;

    public byte[] createRemoveFileEventData(String fileDirPath, String fileName) throws IOException {
        this.type = EventType.REMOVE_FILE;
        this.filePath = fileDirPath;
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
        int pathLen = dr.readInt();
        byte[] removedFilePathBytes = new byte[pathLen];
        dr.readFully(removedFilePathBytes);
        this.filePath = new String(removedFilePathBytes).replace("\\", "/");

        // 3. Read the removed file name
        int fileNameLen = dr.readInt();
        byte[] removedFileBytes = new byte[fileNameLen];
        dr.readFully(removedFileBytes);
        this.removedFileName = new String(removedFileBytes);

        dr.close();

        File removedFile = new File(filePath, removedFileName);
        removedFile.delete();
        System.out.println("Remove the file " + removedFileName);
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();
        // 1. Write the event type
        dw.writeInt(type);

        // 2. Write the path of the removed file
        byte[] filePathBytes = filePath.getBytes();
        int pathLen = filePathBytes.length;
        dw.writeInt(pathLen);
        dw.write(filePathBytes);

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
        return null;
    }
}
