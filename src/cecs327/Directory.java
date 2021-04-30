package cecs327;

import java.io.File;
import java.util.HashMap;

public class Directory {
    /**
     * Key is the dir name, and the value is the Directory entity
     */
    private HashMap<String, Directory> subDirs;

    /**
     * Key is the file name, and the value is the CustomFile entity
     */
    private HashMap<String, CustomFile> dirFiles;

    private String dirPath;

    /**
     * The constructor of teh Directory
     * @param dir is the node dir path
     */
    public Directory(File dir) {
        subDirs = new HashMap<>();
        dirFiles = new HashMap<>();

        this.dirPath = dir.getPath().replace("\\", "/");
        File[] files = dir.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    subDirs.put(f.getName(), new Directory(new File(f.getPath())));
                }
                else {
                    dirFiles.put(f.getName(), new CustomFile(f));
                }
            }
        }
    }

    public HashMap<String, CustomFile> getDirFiles() { return dirFiles; }

    public HashMap<String, Directory> getSubDirs() { return subDirs; }

    public String getDirPath() { return dirPath; }

    public void removeFile(String fileName) { dirFiles.remove(fileName); }

    public void removeDir (String dirName) { subDirs.remove(dirName); }
}
