import org.junit.Test;
import cecs327.utils.UUIDUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
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


}
