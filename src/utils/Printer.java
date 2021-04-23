package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Printer {

    public enum UpdateFailType {
        FILE_NOT_EXIST,
        OWNER_NOT_EXIST
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public static void updateBegin(String fileName) {
        System.out.println("[Update] [" + sdf.format(new Date()) + "] Update " + fileName + " ...");
    }

    public static void updateSuccess(String fileName) {
        System.out.println("[Update] [" + sdf.format(new Date()) + "] Update " + fileName + " success！");
    }

    public static void updateFail(String fileName, UpdateFailType type) {
        System.out.println("[Update Error] [" + sdf.format(new Date()) + "] Update " + fileName + " Fail！");
        if (type == UpdateFailType.FILE_NOT_EXIST) {
            System.out.println("[Update Error] [" + sdf.format(new Date()) + "] " + fileName + " does not exits!");
        }
        else if (type == UpdateFailType.OWNER_NOT_EXIST) {
            System.out.println("[Update Error] [" + sdf.format(new Date()) + "] " + fileName + "'s owner does not exits!");
        }

    }

    public static void deleteBegin(String fileName) {
        System.out.println("[Delete File] [" + sdf.format(new Date()) + "] Not find " + fileName + " in local, remove record...");
    }

    public static void deleteSuccess(String fileName) {
        System.out.println("[Delete File] [" + sdf.format(new Date()) + "] Success remove" + fileName + " record!");
    }

    public static void addBegin(String fileName) {
        System.out.println("[New File] [" + sdf.format(new Date()) + "] Find new file " + fileName + ", add it in record...");
    }
    public static void addSuccess(String fileName) {
        System.out.println("[New File] [" + sdf.format(new Date()) + "] File " + fileName + " is added successfully!");
    }

}
