import org.junit.Test;
import utils.UUIDUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class IPTest {

    @Test
    public void test() throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println(localhost.getHostName());
        System.out.println(localhost.getHostAddress());

        UUID id = UUIDUtils.getTimeBasedUUID();
        System.out.println(id);
        System.out.println(UUID.randomUUID());

    }

}
