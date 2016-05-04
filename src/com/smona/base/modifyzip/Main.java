package com.smona.base.modifyzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    private static String[] CATEGORY = new String[] { "FJ", "RW", "TY", "QG",
            "DW", "WH", "CY", "AQ", "ET", "KX", "HD", "DC" };

    private static Map<String, String> LABELMAP = new HashMap<String, String>();

    public static void main(String[] args) {
        Logger.init();
        String encode = System.getProperty("file.encoding");
        println(encode);
        String path = System.getProperty("user.dir");
        println(path);
        action(path);
    }

    private static void action(String path) {
        createDir(path);
        readConfig(path);
        unzipAll(path);
        replace(path);
        zipAll(path);
    }

    private static void createDir(String path) {
        FileOperator.deleteDirectory(new File(getTemp1Path(path)));
        FileOperator.deleteDirectory(new File(getTemp2Path(path)));
        FileOperator.deleteDirectory(new File(getTargetPath(path)));

        mkdir(getTemp1Path(path));
        mkdir(getTemp2Path(path));
        mkdir(getTargetPath(path));
    }

    private static void unzipAll(String path) {
        String sourcePath = getSourcePath(path);
        String tempPath = getTemp1Path(path);

        String fileName = null;
        String fileNameNoSuffix = null;

        File source = new File(sourcePath);

        File[] files = source.listFiles();
        ZipFileAction action = new ZipFileAction();
        for (File file : files) {
            fileName = file.getName();
            if (file.isFile() && fileName.endsWith(".zip")) {
                fileNameNoSuffix = fileName.substring(0, fileName.length() - 4);
                try {
                    action.unZip(sourcePath + "/" + file.getName(), tempPath
                            + "/" + fileNameNoSuffix);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void replace(String path) {
        String sourcePath = getTemp1Path(path);
        String tempPath = getTemp2Path(path);
        File source = new File(sourcePath);

        String fileName = null;

        File[] files = source.listFiles();
        for (File file : files) {
            fileName = file.getName();
            String[] replace = needReplace(fileName);
            if (replace == null) {
                continue;
            }
            if (replace[1] == null) {
                continue;
            }

            FileOperator
                    .copyDirectiory(
                            sourcePath + File.separator + fileName,
                            tempPath + File.separator
                                    + fileName.replace(replace[0], replace[1]),
                            replace);

        }
    }

    private static void zipAll(String path) {
        String sourcePath = getTemp2Path(path);
        String targetPath = getTargetPath(path);

        File source = new File(sourcePath);
        ZipFileAction action = new ZipFileAction();

        File[] files = source.listFiles();
        for (File file : files) {
            try {
                action.zip(sourcePath + File.separator + file.getName(),
                        targetPath + File.separator + file.getName() + ".zip");
            } catch (Exception e) {
                e.printStackTrace();
                Logger.printDetail(e.getMessage());
            }
        }
    }

    private static void readConfig(String path) {
        String config = path + "/config.properties";
        readProperty(config);
    }

    private static void readProperty(String configPath) {
        InputStream in = null;
        Properties pro = new Properties();
        try {
            in = new FileInputStream(configPath);
            pro.load(in);
            for (String label : CATEGORY) {
                String value = pro.getProperty(label);
                if (value != null) {
                    LABELMAP.put(label, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
        }

    }

    private static void closeStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getSourcePath(String path) {
        return path + File.separator + "source";
    }

    private static String getTemp1Path(String path) {
        return path + File.separator + "temp1";
    }

    private static String getTemp2Path(String path) {
        return path + File.separator + "temp2";
    }

    private static String getTargetPath(String path) {
        return path + File.separator + "target";
    }

    private static String[] needReplace(String fileName) {
        String[] splits = fileName.split("-");
        if (splits == null) {
            return null;
        }
        if (splits.length != 4) {
            return null;
        }

        String category = splits[2];
        return new String[] { category, LABELMAP.get(category) };
    }

    private static File mkdir(String target) {
        File tempDir = new File(target);
        tempDir.mkdir();
        return tempDir;
    }

    private static void println(String msg) {
        Logger.printDetail(msg);
    }
}
