import cecs327.CustomFile;
import org.junit.Test;

import java.io.*;

public class FileTest {

    @Test
    public void test() throws IOException {
        CustomFile cf = new CustomFile(new File("./sync/test/aaa.md"));
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
    public void removeFolderTest() {
        File dir = new File("./sync/test/");
        System.out.println("f.isDirectory() = " + dir.isDirectory());
        System.out.println("f.delete() = " + dir.delete());


        for (File f : dir.listFiles()) {
            f.delete();
        }

        dir.delete();

    }

    @Test
    public void isFolderTest() {
        File dir = new File("./sync/test");
        File[] files = dir.listFiles();

        for (File f : files) {
            boolean isDir = f.isDirectory();
            System.out.println(f.getName() + (isDir ? " is a dir." : " is a file."));
            System.out.println(f.getPath().replace("\\", "/"));
        }
    }

    @Test
    public void pathFormatTest() {
        File f = new File("./sync/test/testC ");
        System.out.println(f.exists());
        System.out.println(f.getPath());
        System.out.println(f.getAbsolutePath());
        f.mkdirs();

    }

    @Test
    public void removeDirTest() {
        File removedDir = new File("./sync/test/");
        removeDir(removedDir);
    }

    public void removeDir(File removedDir) {
        File[] files = removedDir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory()) {
                    f.delete();
                } else {
                    removeDir(f);
                }
            }
        }
        removedDir.delete();
    }

}
