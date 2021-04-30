package cecs327.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;

public class IPUtils {

    private static boolean isWindowOS() {
        boolean isWindowOS = false;
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().contains("windows")) {
            return true;
        }
        return false;
    }

    private static InetAddress getInetAddress() {
        InetAddress inetAddress = null;
        try {
            // If the node is running on Windows OS
            if (isWindowOS()) {
                inetAddress = InetAddress.getLocalHost();
            } else {
                // Get all network interfaces information in enumeration which is iterable
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

                boolean findTargetAddress = false;
                // Iterate the network interfaces
                while (networkInterfaces.hasMoreElements()) {
                    if (findTargetAddress) {
                        break;
                    }
                    // Get the next network interface
                    NetworkInterface nInterface = networkInterfaces.nextElement();
                    // Iterate all IP addresses
                    Enumeration<InetAddress> ips = nInterface.getInetAddresses();
                    while (ips.hasMoreElements()) {
                        inetAddress = ips.nextElement();
                        if (inetAddress.isSiteLocalAddress()
                             && !inetAddress.isLoopbackAddress()  // Exclude 127.0.0.1
                             && !inetAddress.getHostAddress().contains(":")) {   // Exclude IPv6
                            findTargetAddress = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inetAddress;
    }

    public static String getLocalIP() {
        return getInetAddress().getHostAddress();
    }

    public static String getBroadCastAddr() {
        String ip = getInetAddress().getHostAddress();
        // We assume the subnet mask is 255.255.255.0
        String[] arr = ip.split("\\.");
        arr[3] = "255";

        return String.join(".", arr);
    }

}