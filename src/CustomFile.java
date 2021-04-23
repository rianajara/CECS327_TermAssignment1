import utils.SHA256Utils;

import java.io.File;
import java.util.*;

public class CustomFile {
    String ownerID;
    String SHA256;
    private File file;
    private String fileName;
    private long timeStamp;

    public static Map<String, CustomFile> getFileList(File[] files, String ownerID) {
        HashMap<String, CustomFile> map = new HashMap<>();

        if (files == null || files.length == 0) {
            return map;
        }


        for (File f : files) {
            CustomFile cf = new CustomFile(f, ownerID);
            map.put(cf.getFileName(), cf);
        }
        return map;
    }

    public CustomFile(File file, String ownewr) {
        this.file = file;
        this.ownerID = ownewr;
        this.fileName = file.getName();
        this.SHA256 = SHA256Utils.getFileSHA256(file);
        this.timeStamp = file.lastModified();
    }

    public String getOwnerID() { return ownerID; }

    public String getFileName() {
        return fileName;
    }

    public String getUniqueFileName() {
        return ownerID + "-" +  fileName;
    }

    public String getSHA256() { return SHA256; }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomFile that = (CustomFile) o;
        return timeStamp == that.timeStamp &&
                Objects.equals(ownerID, that.ownerID) &&
                Objects.equals(SHA256, that.SHA256) &&
                Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, timeStamp);
    }
}
