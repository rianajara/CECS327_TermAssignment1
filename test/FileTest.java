import cecs327.CustomFile;
import org.junit.Test;

import java.io.*;

public class FileTest {

    @Test
    public void test() throws IOException {
        CustomFile cf = new CustomFile(new File("./sync/test/aaa.md"), "123");
        String dirPath = cf.getDirPath();
        System.out.println(dirPath);
        System.out.println(cf.getFileName());

    }


    @Test
    public void pathTest() throws IOException {
        File f = new File("./sync/TestA.txt");
        byte[] fileByteArr = new byte[(int)f.length()];

        System.out.println(f.length());

        FileInputStream fis = new FileInputStream(f);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(fileByteArr, 0, fileByteArr.length);


        FileOutputStream fos = new FileOutputStream("./sync/test/" + "test.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(fileByteArr, 0, fileByteArr.length);
        bos.flush();


    }

    @Test
    public void createFolderTest() {
        File f = new File("./sync/aaa/");
        f.mkdirs();

    }

    @Test
    public void removeFileTest() {
        File f = new File("./sync/test/testFile.txt");

        f.delete();

    }

    @Test
    public void removeDirTest() {
        File dir = new File("./sync/test/");

        dir.delete();
    }



}
