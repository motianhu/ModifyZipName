package com.smona.base.modifyzip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {

    private static String report = System.getProperty("user.dir")
            + "/logger/report.txt";
    private static String detail = System.getProperty("user.dir")
            + "/logger/detail.txt";

    public static void init() {
        createFile(report);
        createFile(detail);
    }

    private static void createFile(String file) {
        File r = new File(file);
        if (r.exists()) {
            r.delete();
        } else if (r.getParentFile().exists()) {
            try {
                r.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            r.getParentFile().mkdirs();
            try {
                r.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printReport(String msg) {
        writeContent(msg, report);
        System.out.println(msg);
    }

    public static void printDetail(String msg) {
        writeContent(msg, detail);
        System.out.println(msg);
    }

    private static void writeContent(String msg, String file) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(msg);
            out.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
