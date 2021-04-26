package cecs327.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class SHA256Utils {

    public static String getFileSHA256(File file) {
        String str = "";
        try {
            InputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            MessageDigest md5 = MessageDigest.getInstance("SHA-256");
            for (int numRead = 0; (numRead = fis.read(buffer)) > 0; ) {
                // Read all content of the file
                md5.update(buffer, 0, numRead);
            }
            fis.close();

            // Generate the identifier and convert it into Hex format
            str = toHexString(md5.digest());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String toHexString(byte arr[]) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(Integer.toHexString(b & 0xFF));
        }
        return sb.toString();
    }
}