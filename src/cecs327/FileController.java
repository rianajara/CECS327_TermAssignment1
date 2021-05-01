package cecs327;

import cecs327.events.*;
import cecs327.utils.LogPrinter;

import java.io.*;
import java.util.*;

/**
 * FileController is the core of the entire program. It
 * will track the local files and perform some operations
 * based on different modification on each files. For
 * example, if a file is updated, it the FileController
 * will send a new copy of the file to other nodes in
 * the network.
 * Also, FileController plays the role
 */
public class FileController {
    private static FileController instance = null;


    public static FileController getInstance(int port, String owner,
                                             EventHandler eh,
                                             Directory nodeDir,
                                             HashMap<String, String> cmap) {
        if (instance == null) {
            instance = new FileController(port, owner, eh, nodeDir, cmap);
        }

        return instance;
    }

    /**
     * The ID of the node possessing this controller
     */
    String ownerID;

    /**
     * The port that used to receive event
     */
    private int port;

    /**
     * The event handler will handle different kinds of incoming events.
     */
    private EventHandler eh;

    /**
     * The receiver is a multi-thread server. It can handle many incoming data
     * simultaneously.
     */
    private Receiver receiver;

    /**
     * Sender is used to send the event data.
     */
    private Sender sender;

    /**
     * Clients map will save the ID and IP of each node in the network.
     */
    private HashMap<String, String> clientsMap;

    /**
     * Node directory is used to track the status(information) of files
     * belonging to the local node(machine)
     */
    private Directory nodeRecordDir;

    /**
     * The flag is to make sure the program get the local file map
     * first, and then start listening.
     */
    private volatile boolean atLeastOneTime = false;

    private FileController() {}
    private FileController(int port, String owner, EventHandler eh,
                          Directory nodeDir,
                          HashMap<String, String> cmap) {
        this.clientsMap = cmap;
        this.ownerID = owner;
        this.port = port;
        this.nodeRecordDir = nodeDir;
        init(eh, port);
    }

    private void init(EventHandler eh, int port) {
        this.updateLocalNodeFileMap();
        while (!atLeastOneTime) Thread.onSpinWait();
        this.receiver = new Receiver(port, eh);
        this.sender = new Sender(port);
        this.receiver.start();
    }

    private void updateLocalNodeFileMap() {
        new Thread() {
            public void run() {
                // Get the directory we want to synchronize
                File dir = new File("./sync/" + ownerID + "/");

                if (!dir.exists()) {
                    dir.mkdirs();
                    System.out.println("Directory " + ownerID + " does not exists, creat directory " +
                            "\nBegin Scanning...");
                } else {
                    System.out.println("Directory " + ownerID + " exists \nBegin scanning...");
                }

                // localFileMap is used to temporarily store the files in local directory
                Directory latestDir = null;

                while (true) {
                    synchronized (this) {
                        // In each loop, dir.listFiles() will get all latest files in the directory in an array
                        latestDir = new Directory(new File("./sync/" + ownerID));
                        syncDir(latestDir, nodeRecordDir);
                        atLeastOneTime = true;
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }

    /**
     * Send event
     * @param ip target node IP address
     * @param data event data
     * @throws IOException
     */
    public void sendData(String ip, byte[] data) throws IOException {
        sender.sendData(ip, data);
    }

    /**
     * This method will take two directories as parameter. It sync the
     * files in the first directory level first, and then it will sync
     * the subdirectories.
     * @param localDir
     * @param recordDir
     */
    private void syncDir(Directory localDir, Directory recordDir) {
        // 1. Compare files in the current dir
        HashMap<String, CustomFile> localDirFiles = localDir.getDirFiles();
        HashMap<String, CustomFile> recordDirFiles = recordDir.getDirFiles();
        syncFiles(localDirFiles, recordDirFiles);

        // 2. Compare subdirectories
        HashMap<String, Directory> localSubDirs = localDir.getSubDirs();
        HashMap<String, Directory> recordSubDirs = recordDir.getSubDirs();
        syncSubDirs(localSubDirs, recordSubDirs);
    }


    /**
     * This method will sync the subdirectories with the same name.
     * @param localSubDirs
     * @param recordSubDirs
     */
    private void syncSubDirs(HashMap<String, Directory> localSubDirs, HashMap<String, Directory> recordSubDirs) {
        Iterator<Map.Entry<String, Directory>> recordDirItr = recordSubDirs.entrySet().iterator();
        while (recordDirItr.hasNext()) {
            // Get the subDir name from the record
            String dirName = recordDirItr.next().getKey();
            // If local has this dir, then go inside the dir and sync files in the folder
            if (localSubDirs.containsKey(dirName)) {
                syncDir(localSubDirs.get(dirName), recordSubDirs.get(dirName));
            }
            // When the local does not have this dir, then it should remove it from record
            else {
                String removedDirPath = recordSubDirs.get(dirName).getDirPath();
                System.out.println("Not find dir [" + dirName + "]");
                System.out.println("Remove dir [" + dirName + "]");
                recordDirItr.remove();
                if (clientsMap.size() > 0) {
                    clientsMap.forEach((nodeID, nodeIP) -> {
                        sendRemoveDirEvent(nodeIP, removedDirPath);
                    });
                }
            }
        }

        // Check is there's a new dir
        for (String dirName : localSubDirs.keySet()) {
            if (!recordSubDirs.containsKey(dirName)) {
                Directory newDir = localSubDirs.get(dirName);
                System.out.println("Find new dir [" + dirName + "]");
                System.out.println("Add [" + dirName + "] into record...");
                recordSubDirs.put(dirName, newDir);
                // Send the entire dir to other nodes
                if (clientsMap.size() > 0) {
                    clientsMap.forEach((nodeID, nodeIP) -> {
                        sendDir(nodeIP, newDir);
                    });
                }
            }
        }
    }

    /**
     * This will synchronize the latest information of local files
     * to the record stored in the program. If it updates some files,
     * then it will also send the new copy of the file to other nodes.
     * @param localDirFiles is the latest files set
     * @param recordDirFiles is the old files set
     */
    private void syncFiles(HashMap<String, CustomFile> localDirFiles,
                           HashMap<String, CustomFile> recordDirFiles) {

        Iterator<Map.Entry<String, CustomFile>> recordFileItr = recordDirFiles.entrySet().iterator();
        while (recordFileItr.hasNext()) {
            // Get the file name in record
            String fileName = recordFileItr.next().getKey();
            // If the local and record both have this file, then compare SHA256 and timestamp
            if (localDirFiles.containsKey(fileName)) {
                CustomFile localFile = localDirFiles.get(fileName);
                CustomFile recordFile = recordDirFiles.get(fileName);

                // If they are not equal, then update the file
                if (!localFile.equals(recordFile)) {
                    LogPrinter.updateBegin(fileName);
                    recordDirFiles.put(fileName, localFile);
                    LogPrinter.updateSuccess(fileName);
                    if (clientsMap.size() > 0) {
                        clientsMap.forEach((nodeID, nodeIP) -> {
                            sendFileEvent(nodeIP, localFile);
                        });
                    }
                }

            }
            // When the local does not have the file, it should remove the file record
            else {
                String fileDirPath = recordDirFiles.get(fileName).getDirPath();
                LogPrinter.deleteBegin(fileName);
                recordFileItr.remove();
                LogPrinter.deleteSuccess(fileName);
                // Tell other nodes to remove this file
                if (clientsMap.size() > 0) {
                    clientsMap.forEach((nodeID, nodeIP) -> {
                        sendRemoveFileEvent(nodeIP, fileDirPath, fileName);
                    });
                }
            }
        }

        // When find new files, add them in record
        for (String fileName : localDirFiles.keySet()) {
            if (!recordDirFiles.containsKey(fileName)) {
                CustomFile newFile = localDirFiles.get(fileName);
                LogPrinter.addBegin(fileName);
                recordDirFiles.put(fileName, newFile);
                LogPrinter.addSuccess(fileName);
                // Send the new file to other nodes
                if (clientsMap.size() > 0) {
                    clientsMap.forEach((nodeID, nodeIP) -> {
                        sendFileEvent(nodeIP, newFile);
                    });
                }
            }
        }
    }

    /**
     * This is a recursive method that will send the entire dir
     * to the target node
     * @param IPAddress is the target node IP address
     * @param newDir is the directory that needs to be sent
     */
    public void sendDir(String IPAddress, Directory newDir) {
        sendCreateDirEvent(IPAddress, newDir.getDirPath());

        HashMap<String, CustomFile> files = newDir.getDirFiles();
        files.forEach((fileName, file) -> {
            sendFileEvent(IPAddress, file);
        });

        HashMap<String, Directory> subDirs = newDir.getSubDirs();
        subDirs.forEach((dirName, dir) -> {
            sendDir(IPAddress, dir);
        });
    }


    public void sendRemoveFileEvent(String IPAddress, String fileDirPath, String fileName) {
        RemoveFileEvent e = new RemoveFileEvent();
        try {
            byte[] data = e.createRemoveFileEventData(fileDirPath, fileName);
            sender.sendData(IPAddress, data);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void sendRemoveDirEvent(String IPAddress, String dirPath) {
        RemoveDirEvent e = new RemoveDirEvent();
        try {
            byte[] data = e.createRemoveDirEvetData(dirPath);
            sender.sendData(IPAddress, data);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void sendCreateDirEvent(String IPAddress, String dirPah) {
        CreateDirEvent e = new CreateDirEvent();
        try {
            byte[] data = e.createDirEventData(dirPah);
            sender.sendData(IPAddress, data);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void sendFileEvent(String IPAddress, CustomFile newFile) {
        SendFileEvent e = new SendFileEvent();
        try {
            byte[] data = e.createSendFileEventData(IPAddress, newFile);
            sender.sendData(IPAddress, data);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
