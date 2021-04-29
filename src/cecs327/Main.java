package cecs327;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Node n = new Node(9999);
        n.join();
        Scanner sc = new Scanner(System.in);
        String command = sc.next();
        while (!"leave".equals(command.toLowerCase())) {
            command = sc.next();
        }
        n.leave();
        System.exit(0);
    }

}

