package cecs327.events;

import cecs327.utils.DataReader;
import cecs327.utils.DataWriter;

import java.io.File;
import java.io.IOException;

public class RemoveDirEvent implements Event{

    private int type;
    private String removedDirPath;

    public byte[] createRemoveDirEvetData(String path) throws IOException {
        this.type = EventType.REMOVE_DIR;
        this.removedDirPath = path.replace("\\", "/");
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
        this.removedDirPath = new String(pathBytes);

        removeDir(new File(removedDirPath));

    }

    @Override
    public byte[] packData() throws IOException {
        byte[] data = null;
        DataWriter dw = new DataWriter();
        // 1. Write the event type
        dw.writeInt(type);

        // 2. Write the dir path
        byte[] pathBytes = removedDirPath.getBytes();
        int pathLen = pathBytes.length;
        dw.writeInt(pathLen);
        dw.write(pathBytes);

        dw.flush();
        data = dw.toByteArray();
        dw.close();

        return data;
    }

    private void removeDir(File removedDir) {
        File[] files = removedDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory()) {
                    f.delete();
                } else {
                    removeDir(f);
                }
            }
        }
        removedDir.delete();
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
