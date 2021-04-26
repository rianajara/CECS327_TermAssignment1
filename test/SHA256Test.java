import cecs327.CustomFile;
import org.junit.Test;
import java.io.File;


public class SHA256Test {


    @Test
    public void test() {
        File f1 = new File("./sync/TestA.txt");
        CustomFile cf = new CustomFile(f1, "BlackKnife");

        System.out.println("cf.getSHA256() = " + cf.getSHA256());
    }

}
