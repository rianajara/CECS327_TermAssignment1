package cecs327;

import cecs327.events.*;
import cecs327.utils.IPUtils;
import cecs327.utils.UUIDUtils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Scanner;

public class Node {

    /**
     * nameBasedUUID is actually a IP based UUID, which is unique to
     * different nodes.
     */
    private String nameBasedUUID;

    /**
     * The IP address of the Node
     */
    private String IPAddress;

    /**
     * The port used to receive event
     */
    private int port;

    /**
     * The register will use port 10000 to notify all other nodes
     * in the network by using UDP transmit.
     */
    private Register register;

    /**
     * The event handler will handle different kinds of incoming events.
     */
    private EventHandler eh;

    /**
     * The clients map is to store other nodes' UUID and IP info.
     */
    private HashMap<String, String> clientsMap;

    /**
     * Local file map is used to track the status(information) of files
     * belonging to the local node(machine)
     */
    // private HashMap<String, CustomFile> localFileMap;
    private Directory nodeDir;

    /**
     * FileController is responsible for different operations of the
     * entire program.
     */
    private FileController fileController;

    public String getNameBasedUUID() { return nameBasedUUID; }
    public String getIPAddress() { return IPAddress; }

    public Node(int port) {
        show();
        nameBasedUUID = UUIDUtils.getNameBasedUUID().toString();
        IPAddress = IPUtils.getLocalIP();

        this.port = port == 10000 ? 9999 : port;

        eh = EventHandler.getInstance();
        eh.setNode(this);
//        localFileMap = new HashMap<>();
        nodeDir = new Directory(new File("./sync/" + nameBasedUUID));
        clientsMap = new HashMap<>();
        fileController = FileController.getInstance(port, nameBasedUUID, eh, nodeDir, clientsMap);
        register = new Register(10000, eh);
        register.start();
    }

    /**
     * When a node wants to join the local synchronization network,
     * it needs to register itself to other nodes.
     */
    public void join() {
        try {
            JoinEvent e = new JoinEvent();
            byte[] data = e.createJointEventData(this.nameBasedUUID, this.IPAddress);
            InetAddress addr = InetAddress.getByName(IPUtils.getBroadCastAddr());
            DatagramSocket client = new DatagramSocket();
            DatagramPacket dp = new DatagramPacket(data, data.length, addr, 10000);
            client.send(dp);
            System.out.println("Node [" + this.nameBasedUUID + "] broadcast join request");
            client.close();
        } catch (IOException ex) {
            System.out.println("Fail to Join");
            ex.printStackTrace();
        }
    }

    /**
     * When a node leaves the network, it should tell other nodes that it's
     * going to leave, and remove the files belonging to it on other nodes
     */
    public void leave() {
        if (clientsMap.size() > 0) {
            LeaveEvent e = new LeaveEvent();
            clientsMap.forEach((k, v) -> {
                try {
                    byte[] data = e.createLeaveEventData(this.nameBasedUUID);
                    sendData(v, data);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
    }

    /**
     * This method will be triggered when some special events com.
     * The action of this event depends on different types of events.
     *
     * @param e represents the incoming event.
     */
    public void onEvent(Event e) {

        if (e.getEventType() == EventType.JOIN_NETWORK) {
            // 1. When a node receive the join request,
            // then add the info of the node into the clients map
            String newNodeID = e.getNodeUUID();
            String newNodeIP = e.getNodeIP();
            System.out.println("Receive the join request from " + newNodeID);
            clientsMap.put(newNodeID, newNodeIP);
            // 2. Send a response to the new node
            System.out.println("Send response to " + newNodeID);
            sendResponse(newNodeIP);
            // 3. Send all local files to the node
            System.out.println("Send all local copies to " + newNodeID);
            sendLocalFiles(newNodeIP);
        }
        else if (e.getEventType() == EventType.JOIN_NETWORK_RESPONSE) {
            System.out.println("-------------------------------------------");
            System.out.println("Event Type: " + e.getEventType());
            System.out.println("Get the response from [" + e.getNodeUUID() + "] successfully!");
            // Receive the response and send the local file to
            // the node that is already in the network
            clientsMap.put(e.getNodeUUID(), e.getNodeIP());
            System.out.println("Send all local copies to [" + e.getNodeUUID() + "]");
            sendLocalFiles(e.getNodeIP());
        }
        else if (e.getEventType() == EventType.LEAVE_NETWORK) {
            clientsMap.remove(e.getNodeUUID());
        }
    }

    /**
     * It will send all local copy to another node in the same LAN.
     * @param ipAddress is the target node's IP address.
     */
    private void sendLocalFiles(String ipAddress) {
        fileController.sendCreateDirEvent(ipAddress, nodeDir.getDirPath());
        fileController.sendDir(ipAddress, nodeDir);
    }

    /**
     * This method will let a node to send a response to the new node
     * It tells the basic information of the node to the new node.
     * @param ip is the target node's IP address.
     */
    private void sendResponse(String ip) {
        JoinResponseEvent e = new JoinResponseEvent();
        try {
            byte[] data = e.createJointResponseEventData(this.nameBasedUUID, this.IPAddress);
            sendData(ip, data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is done by sender. It sends different kinds of events to others.
     *
     * @param ip is the target node's IP address
     * @param data is all data of a event has
     * @throws IOException
     */
    private void sendData(String ip, byte[] data) throws IOException {
        fileController.sendData(ip, data);
    }

    private void show() {
        System.out.println(
                        "\n* --------------------------------------- *\n" +
                        "|                                         |\n" +
                        "|   ENTER \"LEAVE\" TO LEAVE THE NETWORK    |\n" +
                        "|                                         |\n" +
                        "* --------------------------------------- *\n"
        );
        System.out.println("Press Enter to continue...");
        Scanner sc = new Scanner(System.in);
        sc.nextLine();

    }

}
