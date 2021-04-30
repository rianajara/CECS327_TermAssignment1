import cecs327.utils.IPUtils;
import org.junit.Test;
import cecs327.utils.UUIDUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.UUID;

public class IPTest {

    @Test
    public void test() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println(localhost.getHostName());
        System.out.println(localhost.getHostAddress());

        UUID id = UUIDUtils.getNameBasedUUID();
        System.out.println(id);
        System.out.println(UUID.randomUUID());

    }

    @Test
    public void hashIP() throws UnknownHostException {
        InetAddress host = InetAddress.getLocalHost();
        System.out.println(host.getHostAddress());
        System.out.println( host.hashCode());

        System.out.println(host.getHostAddress().hashCode());
    }

    @Test
    public void getIPTest() {
        try {
            Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
            while (faces.hasMoreElements()) { // 遍历网络接口
                NetworkInterface face = faces.nextElement();
                if (face.isLoopback() || face.isVirtual() || !face.isUp()) {
                    continue;
                }
                System.out.print("网络接口名：" + face.getDisplayName() + "，地址：");

                Enumeration<InetAddress> address = face.getInetAddresses();
                while (address.hasMoreElements()) { // 遍历网络地址
                    InetAddress addr = address.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress() && !addr.isAnyLocalAddress()) {
                        System.out.print(addr.getHostAddress() + " ");
                    }
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void broadCastAddrTest() {
        System.out.println("IPUtils.getBroadCastAddr() = " + IPUtils.getBroadCastAddr());
    }


}
