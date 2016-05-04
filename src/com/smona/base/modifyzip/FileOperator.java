package com.smona.base.modifyzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileOperator {

    public static void fileChannelCopy(File source, String tar) {
        File target = new File(tar);
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(source);
            fo = new FileOutputStream(target);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean deleteDirectory(File dirFile) {
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                flag = deleteDirectory(files[i]);
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;

        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static void copyDirectiory(String sourceDir, String targetDir,
            String[] replace) {
        File dir = new File(targetDir);
        dir.mkdirs();

        File[] file = (new File(sourceDir)).listFiles();

        String fileName = null;

        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceFile = file[i];

                fileName = sourceFile.getName();

                if (replace != null && replace[1] != null
                        && fileName.contains(replace[0])) {
                    fileName = fileName.replace(replace[0], replace[1]);
                }

                fileChannelCopy(sourceFile, targetDir + File.separator
                        + fileName);
            } else if (file[i].isDirectory()) {
                fileName = file[i].getName();

                String dir1 = sourceDir + File.separator + fileName;
                String dir2 = targetDir + File.separator + fileName;

                if (replace != null && replace[1] != null
                        && fileName.contains(replace[0])) {
                    dir2 = targetDir + File.separator
                            + fileName.replace(replace[0], replace[1]);
                }

                copyDirectiory(dir1, dir2, replace);
            }
        }

    }
}
