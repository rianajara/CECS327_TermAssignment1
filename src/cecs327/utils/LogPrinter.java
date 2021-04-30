package cecs327.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LogPrinter is only used to print the log
 */
public class LogPrinter {

    private static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public static void updateBegin(String fileName) {
        System.out.println("[Update] [" + sdf.format(new Date()) + "] Update " + fileName + " ...");
    }

    public static void updateSuccess(String fileName) {
        System.out.println("[Update] [" + sdf.format(new Date()) + "] Update " + fileName + " success！");
    }

    public static void updateFail(String fileName) {
        System.out.println("[Update Error] [" + sdf.format(new Date()) + "] Update " + fileName + " Fail！");
        System.out.println("[Update Error] [" + sdf.format(new Date()) + "] " + fileName + " does not exits!");
    }

    public static void deleteBegin(String fileName) {
        System.out.println("[Delete File] [" + sdf.format(new Date()) + "] Not find " + fileName + " in local, remove record...");
    }

    public static void deleteSuccess(String fileName) {
        System.out.println("[Delete File] [" + sdf.format(new Date()) + "] Success remove " + fileName + " record!");
    }

    public static void addBegin(String fileName) {
        System.out.println("[New File] [" + sdf.format(new Date()) + "] Find new file " + fileName + ", add it in record...");
    }
    public static void addSuccess(String fileName) {
        System.out.println("[New File] [" + sdf.format(new Date()) + "] File " + fileName + " is added successfully!");
    }

}
