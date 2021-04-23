import org.junit.experimental.theories.Theories;

import java.io.*;
import java.util.*;

public class MainTest {
    Object mutex = new Object();
    int flag = 1;

    public void run() throws InterruptedException {
        synchronized (this) {
            System.out.println("before wait... run...");

            if (flag != 1) {
                this.wait();
            }

            System.out.println("run...");
//            flag = 2;
//            notify();
        }
    }


    public void eat() throws InterruptedException {
        synchronized (this) {

            if (flag != 2) {
                this.wait();
            }

            System.out.println("eat...");
            flag = 1;
            notify();
        }
    }

    public void setFlag(int n) {
        synchronized (this) {
            this.flag = n;
            notify();
        }
    }



    public static void main(String[] args) throws InterruptedException {
        File f = new File("./sync/FileB.txt");
        File f2 = new File("./sync/FileC.txt");

        System.out.println(f.exists());
        System.out.println(f2.exists());


//        new Thread() {
//            public void run() {
//                try {
//                    for(;;) m.eat();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();



    }



}
