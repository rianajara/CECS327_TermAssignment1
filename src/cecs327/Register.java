package cecs327;

import cecs327.events.EventHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Register is used to join the network. It uses
 * UDP to broadcast a join event.
 */
public class Register extends Thread {
    private DatagramSocket UDPReceiver;
    private EventHandler eh;

    public Register(int port, EventHandler eh) {
        try {
            UDPReceiver = new DatagramSocket(port);
            this.eh = eh;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        DatagramPacket dp = new DatagramPacket(new byte[2048], 2048);
        while (true) {
            try {
                // Use UDP to find other nodes in the network and let other
                // nodes know the new node information
                UDPReceiver.receive(dp);
                byte[] data = dp.getData();
                eh.resolveEvent(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
