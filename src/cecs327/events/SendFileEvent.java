package cecs327.events;

import cecs327.CustomFile;
import cecs327.Protocol;
import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.*;

public class SendFileEvent implements Event {
    private int type;
    private String senderIP;
    private String senderUUID;
    private String path;
    private String fileName;

    @Override
    public void setEventType(int type) {
        this.type = type;
    }

    @Override
    public int getEventType() {
        return type;
    }

    @Override
    public String getNodeIP() {
        return senderIP;
    }

    @Override
    public String getNodeUUID() {
        return senderUUID;
    }

    public byte[] createSendFileEventData(String senderIP, CustomFile cf) throws IOException {
        this.type = Protocol.SEND_FILE;
        this.senderIP = senderIP;
        this.senderUUID = cf.getOwnerID();
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

        // 2. Write the sender's IP info
        // 2. 1 Get the bytes format of IP info
        byte[] ownIP = senderIP.getBytes();
        // 2. 2 Get the length of the byte arr
        int ipLen = ownIP.length;
        // 2.3 Write the length and the IP info
        dw.writeInt(ipLen);
        dw.write(ownIP);

        // 3. Write the the sender's UUID info
        byte[] ownUUID = senderUUID.getBytes();
        int UUIDLen = ownUUID.length;
        dw.writeInt(UUIDLen);
        dw.write(ownUUID);

        // 4. Write the directory where the file is
        byte[] filePath = path.getBytes();
        int pathLen = filePath.length;
        dw.writeInt(pathLen);
        dw.write(filePath);

        // 5. Write the file name
        byte[] fileNameBytes = fileName.getBytes();
        int fileNameLen = fileNameBytes.length;
        dw.writeInt(fileNameLen);
        dw.write(fileNameBytes);

        // 6. Write the file content
        File f = new File(path, fileName);
        // 6.1 Generate an appropriate size of buffer
        // to store the content in the file
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

        // 1. Read the type
        this.type = dr.readInt();
        System.out.println("Event Type: " + type);

        // 2. Read the sender's IP
        int ipLen = dr.readInt();
        byte[] ipBytes = new byte[ipLen];
        dr.readFully(ipBytes);
        this.senderIP = new String(ipBytes);

        System.out.println("Sender IP: " + senderIP);

        // 3. Read the sender's UUID
        int UUIDLen = dr.readInt();
        byte[] UUIDBytes = new byte[UUIDLen];
        dr.readFully(UUIDBytes);
        this.senderUUID = new String(UUIDBytes);
        System.out.println("Sender UUID: " + senderUUID);

        // 4. Read the directory
        int pathLen = dr.readInt();
        byte[] pathBytes = new byte[pathLen];
        dr.readFully(pathBytes);
        this.path = new String(pathBytes);

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
