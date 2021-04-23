import org.junit.Test;

import java.io.*;

public class FileStreamTest {

    @Test
    public void test() throws IOException {
        FileInputStream fis = new FileInputStream(new File("sync/FileA.txt"));
        File nFile = new File("sync/newFile.txt");

        FileOutputStream fos = new FileOutputStream(nFile);
        byte[] arr = new byte[8192];
        int len;
        while ((len = fis.read(arr)) != -1) {
            fos.write(arr, 0, len);
        }
    }

}
