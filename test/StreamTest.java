import org.junit.Test;

import java.io.*;
import java.util.Arrays;

public class StreamTest {

    @Test
    public void test() throws IOException {
        byte[] buffer = null;
        String ip = "192.168.0.1";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));


        dos.writeInt(5);
        byte[] ipBytesOutput = ip.getBytes();
        int ipLen = ipBytesOutput.length;
        System.out.println("ipLen = " + ipLen);
        dos.writeInt(ipLen);
        dos.write(ipBytesOutput);
        dos.flush();

        buffer = baos.toByteArray();

        System.out.println(Arrays.toString(buffer));


        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        int num = dis.readInt();
        int len = dis.readInt();
        byte[] ipBytes = new byte[len];
        dis.readFully(ipBytes);
        String IPAddress = new String(ipBytes);
        System.out.println(num);
        System.out.println(len);
        System.out.println(IPAddress);

    }

}
