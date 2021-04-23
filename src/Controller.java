import utils.Printer;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller {

    String ownerID;
    private int listeningPort;
    private int sendingPort;
    private DatagramSocket socket;
    private List<String> nodeList;
    private HashMap<String, HashMap<String, CustomFile>> globalFileMap;

    public Controller(int lPort, int sPort, String owner) {
        this.ownerID = owner;
        this.listeningPort = lPort;
        this.sendingPort = sPort;
        this.nodeList = new ArrayList<>();
        this.globalFileMap = new HashMap<>();
        init();
    }

    private void init() {
        this.globalFileMap.put(ownerID, new HashMap<String, CustomFile>());
        this.updateLocalNodeFileMap();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("startListening Fail!");
            e.printStackTrace();
        }

        this.startListening();

        try {
            socket = new DatagramSocket();
        } catch (Exception e) {
            System.out.println("init() Fail!");
            e.printStackTrace();
        }
    }

    public void startListening() {
        new Thread() {
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket(listeningPort);
                    DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
                    System.out.println("Start listening the message from port  " + listeningPort);
                    while (true) {
                        socket.receive(packet);
                        byte[] arr = packet.getData();
                        int len = packet.getLength();
                        String message = new String(arr, 0, len);

                        // Get sender's IP address
                        String ip = packet.getAddress().getHostAddress();
                        // Add the sender's IP address into global list
                        nodeList.add(ip);

                        System.out.println("Message: " + message);
                        System.out.println("Sender IP: " + ip);
                        System.out.println("nodeList : " + nodeList.toString());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void updateLocalNodeFileMap() {
        new Thread() {
            public void run() {
                // Get the directory we want to synchronize
                File dir = new File("./sync");

                if (!dir.exists()) {
                    if (dir.mkdir()) System.out.println("Directory sync does not exists, creat directory " +
                            "\nBegin Scanning...");
                    ;
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
                                }
                            }
                            // If the local does not have this file, then remove it in record
                            else {
                                Printer.deleteBegin(fileName);
                                entryIterator.remove();
                                Printer.deleteSuccess(fileName);
                            }
                        }

                        // At this point, the overlap is already updated and th e deleted files were
                        // removed from record
                        // Iterate the localFileMap to find if there are new files
                        for (String fileName : localFileMap.keySet()) {
                            // When find there are new files, add them in record
                            if (!nodeFileMap.containsKey(fileName)) {
                                addFile(ownerID, localFileMap.get(fileName));
                            }
                        }

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

    public void sendMessage(byte[] arr, String ip) throws IOException {
        DatagramPacket packet = new DatagramPacket(arr, arr.length, InetAddress.getByName(ip), sendingPort);
        socket.send(packet);
    }

    /**
     * This method will add the file information into the global file map
     * @param ownerID
     * @param cf
     */
    public void addFile(String ownerID, CustomFile cf) {
        // When the global file map contains the node
        Printer.addBegin(cf.getFileName());
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
        Printer.addSuccess(cf.getFileName());
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
        Printer.updateBegin(fileName);
        if ((nodeFileMap = globalFileMap.get(ownerID)) != null) {
            CustomFile oldFile = null;
            if ((oldFile = nodeFileMap.get(fileName)) != null) {
                nodeFileMap.put(fileName, newFile);
                Printer.updateSuccess(fileName);
            }
            else {
                Printer.updateFail(fileName, Printer.UpdateFailType.FILE_NOT_EXIST);
            }
        }
        else {
            Printer.updateFail(fileName, Printer.UpdateFailType.OWNER_NOT_EXIST);
        }
    }


    public void sendFileMap(String ip) {

    }

}
