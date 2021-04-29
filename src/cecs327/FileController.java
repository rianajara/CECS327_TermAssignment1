package cecs327;

import cecs327.events.EventHandler;
import cecs327.events.RemoveFileEvent;
import cecs327.events.SendFileEvent;
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


    public static FileController getInstance(int port, String owner, EventHandler eh,
                                             HashMap<String, CustomFile> map,
                                             HashMap<String, String> cmap) {
        if (instance == null) {
            instance = new FileController(port, owner, eh, map, cmap);
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
     * Local file map is used to track the status(information) of files
     * belonging to the local node(machine)
     */
    private HashMap<String, CustomFile> filesRecordMap;

    /**
     * The flag is to make sure the program get the local file map
     * first, and then start listening.
     */
    private volatile boolean atLeastOneTime = false;

    private FileController() {}
    private FileController(int port, String owner, EventHandler eh,
                          HashMap<String, CustomFile> map,
                          HashMap<String, String> cmap) {
        this.clientsMap = cmap;
        this.ownerID = owner;
        this.port = port;
        this.filesRecordMap = map;
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
                File dir = new File("./sync/" + ownerID);

                if (!dir.exists()) {
                    dir.mkdir();
                    System.out.println("Directory " + ownerID + " does not exists, creat directory " +
                            "\nBegin Scanning...");
                } else {
                    System.out.println("Directory " + ownerID + " exists \nBegin scanning...");
                }

                // localFileMap is used to temporarily store the files in local directory
                Map<String, CustomFile> localFileMap = null;

                while (true) {
                    synchronized (this) {
                        // In each loop, dir.listFiles() will get all latest files in the directory in an array
                        localFileMap = CustomFile.getFileList(dir.listFiles(), ownerID);

                        // Get the entrySet iterator of the
                        Iterator<Map.Entry<String, CustomFile>> entryIterator = filesRecordMap.entrySet().iterator();

                        while (entryIterator.hasNext()) {
                            String fileName = entryIterator.next().getKey();

                            // When the localFileMap has the same fileName in key
                            if (localFileMap.containsKey(fileName)) {
                                CustomFile localFile = localFileMap.get(fileName);
                                CustomFile storedFile = filesRecordMap.get(fileName);
                                // If two files have different SHA256 or timestamp, update the record
                                if (!localFile.equals(storedFile)) {
                                    updateFile(fileName, localFile);

                                    // THIS WILL HAPPEN ONLY WHEN IT HAS THE RECORD OF OTHER NODES
                                    if (clientsMap.size() > 0) {
                                        // Send the newly updated file to other nodes
                                        SendFileEvent e = new SendFileEvent();
                                        clientsMap.forEach((k, v) -> {
                                            try {
                                                byte[] data = e.createSendFileEventData(localFile);
                                                sender.sendData(v, data);
                                            } catch (IOException ioException) {
                                                ioException.printStackTrace();
                                            }
                                        });
                                    }
                                }
                            }
                            // If the local does not have this file, then remove it in record
                            else {
                                LogPrinter.deleteBegin(fileName);
                                entryIterator.remove();
                                LogPrinter.deleteSuccess(fileName);

                                // THIS WILL HAPPEN ONLY WHEN IT HAS THE RECORD OF OTHER NODES
                                if (clientsMap.size() > 0) {
                                    // Tell other nodes to remove this file
                                    RemoveFileEvent e = new RemoveFileEvent();
                                    clientsMap.forEach((k, v) -> {
                                        try {
                                            byte[] data = e.createRemoveFileEventData(ownerID, fileName);
                                            sender.sendData(v, data);
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                    });
                                }
                            }
                        }

                        // At this point, the overlap is already updated and the deleted files were
                        // removed from record
                        // Iterate the localFileMap to find if there are new files that are not in
                        // the filesRecordMap
                        for (String fileName : localFileMap.keySet()) {
                            // When find there are new files, add them in record
                            if (!filesRecordMap.containsKey(fileName)) {
                                CustomFile newAddedFile = localFileMap.get(fileName);
                                addFile(newAddedFile);

                                // THIS WILL HAPPEN ONLY WHEN IT HAS THE RECORD OF OTHER NODES
                                if (clientsMap.size() > 0) {
                                    // Send the newly added file to other nodes
                                    SendFileEvent e = new SendFileEvent();
                                    clientsMap.forEach((k, v) -> {
                                        try {
                                            byte[] data = e.createSendFileEventData(newAddedFile);
                                            sender.sendData(v, data);
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                    });
                                }
                            }
                        }

                        //
                        atLeastOneTime = true;

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * This method will add the file information into the global file map
     * @param cf
     */
    private void addFile(CustomFile cf) {
        // Add a file record into local file map
        LogPrinter.addBegin(cf.getFileName());
        filesRecordMap.put(cf.getFileName(), cf);
        LogPrinter.addSuccess(cf.getFileName());
    }

    /**
     * This method will update the information of the file stored
     * in the local file map
     * @param fileName the target file name
     * @param newFile the new file information
     */
    private void updateFile(String fileName, CustomFile newFile) {
        LogPrinter.updateBegin(fileName);
        if (filesRecordMap.containsKey(fileName)) {
            filesRecordMap.put(fileName, newFile);
            LogPrinter.updateSuccess(fileName);
        }
        else {
            LogPrinter.updateFail(fileName);
        }
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
}
