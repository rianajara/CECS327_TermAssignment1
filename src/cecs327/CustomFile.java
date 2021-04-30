package cecs327;

import cecs327.utils.SHA256Utils;

import java.io.File;
import java.util.*;

/**
 * The main purpose of this class is to easily track the location
 * of each file; also, it provides an easier way to compare if
 * a file is the latest or not.
 */
public class CustomFile {

    /**
     * SHA256 is a encrypted number based on the content of the file
     */
    private String SHA256;

    private File file;
    private String fileName;

    /**
     * The last modified time
     */
    private long timeStamp;

    /**
     * This method will take the local files array as parameter and
     * return the local files in Map format. The key is the file name,
     * and the value is the CustomFile
     * @param files local file array
     * @return
     */
    public static Map<String, CustomFile> getFileList(File[] files) {
        HashMap<String, CustomFile> map = new HashMap<>();

        if (files == null || files.length == 0) {
            return map;
        }

        for (File f : files) {
            CustomFile cf = new CustomFile(f);
            map.put(cf.getFileName(), cf);
        }
        return map;
    }

    public CustomFile(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.SHA256 = SHA256Utils.getFileSHA256(file);
        this.timeStamp = file.lastModified();
    }

    public String getFileName() {
        return fileName;
    }

    public String getSHA256() { return SHA256; }

    public String getDirPath() {
        String path = this.file.getAbsolutePath().replace(fileName, "").split("\\.")[1];
        return "." + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomFile that = (CustomFile) o;
        return timeStamp == that.timeStamp &&
                Objects.equals(SHA256, that.SHA256) &&
                Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, timeStamp);
    }

    @Override
    public String toString() {
        return "CustomFile{" +
                ", SHA256='" + SHA256 + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
