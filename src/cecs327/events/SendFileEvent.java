package cecs327.events;

import cecs327.CustomFile;
import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.*;

public class SendFileEvent implements Event {
    private int type;
    private String senderUUID;
    private String path;
    private String fileName;

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
        return senderUUID;
    }

    public byte[] createSendFileEventData(String senderUUID ,CustomFile cf) throws IOException {
        this.type = EventType.SEND_FILE;
        this.senderUUID = senderUUID;
        this.path = cf.getDirPath();
        this.fileName = cf.getFileName();

        return packData();
    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();
        // 1. Write the event type
        dw.writeInt(type);


        // 2. Write the sender's ID
        byte[] ownUUID = senderUUID.getBytes();
        int UUIDLen = ownUUID.length;
        dw.writeInt(UUIDLen);
        dw.write(ownUUID);

        // 3. Write the path of the file
        byte[] filePath = path.getBytes();
        int pathLen = filePath.length;
        dw.writeInt(pathLen);
        dw.write(filePath);

        // 4. Write the file name
        byte[] fileNameBytes = fileName.getBytes();
        int fileNameLen = fileNameBytes.length;
        dw.writeInt(fileNameLen);
        dw.write(fileNameBytes);

        // 6. Write the content of the file
        File f = new File(path, fileName);
        // Create the buffer that used to store the data
        byte[] fileBytes = new byte[(int) f.length()];
        int fileBytesLen = fileBytes.length;

        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(fileBytes, 0, fileBytesLen);
        dw.writeInt(fileBytesLen);
        dw.write(fileBytes);

        dw.flush();
        data = dw.toByteArray();
        dw.close();
        fis.close();
        bis.close();

        return data;
    }

    @Override
    public void unpackData(byte[] data) throws IOException {
        DataReader dr = new DataReader(data);

        // 1. Read the Event type
        this.type = dr.readInt();
        System.out.println("-------------------------------------------");
        System.out.println("Event Type: " + type);

        // 2. Read the UUID
        int UUIDLen = dr.readInt();
        byte[] UUIDBytes = new byte[UUIDLen];
        dr.readFully(UUIDBytes);
        this.senderUUID = new String(UUIDBytes);
        System.out.println("Sender UUID: " + senderUUID);

        // 4. Read the path of the file
        int pathLen = dr.readInt();
        byte[] pathBytes = new byte[pathLen];
        dr.readFully(pathBytes);
        this.path = new String(pathBytes);
        // Format the path, otherwise, it will fail to create the folder
        this.path = path.replace("\\", "/");
        System.out.println("File dir: " + path);

        // 5. Read the file name
        int fileNameLen = dr.readInt();
        byte[] fileNameBytes = new byte[fileNameLen];
        dr.readFully(fileNameBytes);
        this.fileName = new String(fileNameBytes);

        System.out.println("Filename: " + fileName);

        // 6. Read the content of the file
        int fileSize = dr.readInt();
        byte[] fileData = new byte[fileSize];
        dr.readFully(fileData);

        // Create the folder
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        FileOutputStream fos = new FileOutputStream(path + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        bos.write(fileData, 0, fileSize);
        bos.flush();

        dr.close();
        bos.close();
        fos.close();
    }
}
