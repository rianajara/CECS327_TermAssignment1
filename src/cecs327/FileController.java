package cecs327;

import cecs327.events.EventHandler;
import cecs327.utils.LogPrinter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileController {

    String ownerID;
    private int fileReceivingPort;
    private int fileSendingPort;
    private EventHandler eh;
    private Receiver receiver;
    private Sender sender;
    private HashMap<String, HashMap<String, CustomFile>> globalFileMap;

    private volatile boolean atLeastOneTime = false;



    // TODO: 添加一个类给 cecs327.Node，来跟踪 cecs327.Node 中文件的情况
    private List<String> nodeList;

    public FileController(int rPort, int sPort, String owner, EventHandler eh) {
        this.ownerID = owner;
        this.fileReceivingPort = rPort;
        this.fileSendingPort = sPort;
        this.globalFileMap = new HashMap<>();
        init(eh);
    }

    private void init(EventHandler eh) {
        this.globalFileMap.put(ownerID, new HashMap<String, CustomFile>());
        this.updateLocalNodeFileMap();
        while (!atLeastOneTime) Thread.onSpinWait();
        this.receiver = new Receiver(9999, eh);
        this.sender = new Sender(9999);
        startReceivingFiles();
    }

    private void startReceivingFiles() {
        this.receiver.start();
    }

    private void updateLocalNodeFileMap() {
        new Thread() {
            public void run() {
                // Get the directory we want to synchronize
                File dir = new File("./sync/" + ownerID);

                if (!dir.exists()) {
                    dir.mkdir();
                    System.out.println("Directory sync does not exists, creat directory " +
                            "\nBegin Scanning...");
                } else {
                    System.out.println("Directory sync exists \nBegin scanning...");
                }

                // localFileMap is used to temporarily store the files in local directory
                Map<String, CustomFile> localFileMap = null;

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

                while (true) {
                    synchronized (this) {
                        localFileMap = CustomFile.getFileList(dir.listFiles(), ownerID);
                        // Get the nodeFileMap belonging to the local node
                        HashMap<String, CustomFile> nodeFileMap = globalFileMap.get(ownerID);
                        // Get the entrySet iterator of the local nodeFileMap
                        Iterator<Map.Entry<String, CustomFile>> entryIterator = nodeFileMap.entrySet().iterator();

                        while (entryIterator.hasNext()) {
                            String fileName = entryIterator.next().getKey();

                            // When the directory has the same fileName in the nodeFileMap
                            if (localFileMap.containsKey(fileName)) {
                                CustomFile localFile = localFileMap.get(fileName);
                                CustomFile storedFile = nodeFileMap.get(fileName);
                                // If two files have different SHA256 or timestamp, update the record
                                if (!localFile.equals(storedFile)) {
                                    updateFile(ownerID, fileName, localFileMap.get(fileName));
                                    // TODO: Create a sendFileEvent

                                }
                            }
                            // If the local does not have this file, then remove it in record
                            else {
                                LogPrinter.deleteBegin(fileName);
                                entryIterator.remove();
                                LogPrinter.deleteSuccess(fileName);

                                // TODO: Create a removeFileEvent

                            }
                        }

                        // At this point, the overlap is already updated and th e deleted files were
                        // removed from record
                        // Iterate the localFileMap to find if there are new files
                        for (String fileName : localFileMap.keySet()) {
                            // When find there are new files, add them in record
                            if (!nodeFileMap.containsKey(fileName)) {
                                addFile(ownerID, localFileMap.get(fileName));
                                // TODO: Create a sendFileEvent
                            }
                        }

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
     * @param ownerID
     * @param cf
     */
    public void addFile(String ownerID, CustomFile cf) {
        // When the global file map contains the node
        LogPrinter.addBegin(cf.getFileName());
        if (globalFileMap.containsKey(ownerID)) {
            HashMap<String, CustomFile> nodeFileMap  = globalFileMap.get(ownerID);
            nodeFileMap.put(cf.getFileName(), cf);
        }
        // When the global file map does not contain the node
        else {
            // Initialize a map for the node
            HashMap<String, CustomFile> nodeFileMap = new HashMap<>();
            nodeFileMap.put(cf.getFileName(), cf);
            globalFileMap.put(ownerID, nodeFileMap);
        }
        LogPrinter.addSuccess(cf.getFileName());
    }

    /**
     * This method will delete the file information from the global file map
     * @param ownerID
     * @param fileName
     */
    public void removeFile(String ownerID, String fileName) {
        if (globalFileMap.containsKey(ownerID)) {
            globalFileMap.get(ownerID).remove(fileName);
        }
    }

    public void updateFile(String ownerID, String fileName, CustomFile newFile) {
        HashMap<String, CustomFile> nodeFileMap = null;
        LogPrinter.updateBegin(fileName);
        if ((nodeFileMap = globalFileMap.get(ownerID)) != null) {
            CustomFile oldFile = null;
            if ((oldFile = nodeFileMap.get(fileName)) != null) {
                nodeFileMap.put(fileName, newFile);
                LogPrinter.updateSuccess(fileName);
            }
            else {
                LogPrinter.updateFail(fileName, LogPrinter.UpdateFailType.FILE_NOT_EXIST);
            }
        }
        else {
            LogPrinter.updateFail(fileName, LogPrinter.UpdateFailType.OWNER_NOT_EXIST);
        }
    }

    public void sendData(String ip, byte[] data) throws IOException {
        sender.sendData(ip, data);
    }


}
