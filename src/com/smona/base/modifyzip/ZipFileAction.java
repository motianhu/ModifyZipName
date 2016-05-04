package com.smona.base.modifyzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipFileAction {
    public void zip(String inputFileName, String outputFileName)
            throws Exception {
        OutputStream output = new FileOutputStream(outputFileName);
        CheckedOutputStream stream = new CheckedOutputStream(output,
                new CRC32());
        ZipOutputStream out = new ZipOutputStream(stream);
        compress(out, new File(inputFileName), "");
        out.closeEntry();
        out.close();
    }

    private void compress(ZipOutputStream out, File file, String basedir) {
        if (file.isDirectory()) {
            compressDirectory(file, out, basedir);
        } else {
            println("zip: " + file.getAbsolutePath() + ", basedir: " + basedir);
            compressFile(file, out, basedir);
        }
    }

    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        basedir = basedir.length() == 0 ? "" : basedir + File.separator;
        for (int i = 0; i < files.length; i++) {
            compress(out, files[i], basedir + files[i].getName());
        }
    }

    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir);
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[1024];
            while ((count = bis.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void unZip(String zipFileName, String outputDirectory)
            throws Exception {
        println("unZip: " + zipFileName);
        ZipFile zipFile = new ZipFile(zipFileName);
        try {
            Enumeration<?> e = zipFile.getEntries();
            ZipEntry zipEntry = null;
            createDirectory(outputDirectory, "");
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
                if (zipEntry.isDirectory()) {
                    String name = zipEntry.getName();
                    name = name.substring(0, name.length() - 1);
                    File f = new File(outputDirectory + File.separator + name);
                    f.mkdir();
                } else {
                    String fileName = zipEntry.getName();
                    println("unZip: " + fileName);
                    fileName = fileName.replace('\\', '/');
                    if (fileName.indexOf("/") != -1) {
                        createDirectory(outputDirectory, fileName.substring(0,
                                fileName.lastIndexOf("/")));
                        fileName = fileName.substring(
                                fileName.lastIndexOf("/") + 1,
                                fileName.length());
                    }
                    File f = new File(outputDirectory + File.separator
                            + zipEntry.getName());
                    f.createNewFile();
                    InputStream in = zipFile.getInputStream(zipEntry);
                    FileOutputStream out = new FileOutputStream(f);
                    byte[] by = new byte[8192];
                    int c;
                    while ((c = in.read(by)) != -1) {
                        out.write(by, 0, c);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            println(ex.getMessage());
        } finally {
            zipFile.close();
        }
    }

    private void createDirectory(String directory, String subDirectory) {
        String dir[];
        File fl = new File(directory);
        try {
            if (subDirectory == "" && fl.exists() != true) {
                fl.mkdir();
            } else if (subDirectory != "") {
                dir = subDirectory.replace('\\', '/').split("/");
                for (int i = 0; i < dir.length; i++) {
                    File subFile = new File(directory + File.separator + dir[i]);
                    if (subFile.exists() == false)
                        subFile.mkdir();
                    directory += File.separator + dir[i];
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            println(ex.getMessage());
        }
    }

    public void copyFileWithDirector(String srcDirector, String desDirector)
            throws IOException {
        (new File(desDirector)).mkdirs();
        File[] file = (new File(srcDirector)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                FileInputStream input = new FileInputStream(file[i]);
                FileOutputStream output = new FileOutputStream(desDirector
                        + "/" + file[i].getName());
                byte[] b = new byte[1024 * 8];
                int len;
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (file[i].isDirectory()) {
                copyFileWithDirector(srcDirector + "/" + file[i].getName(),
                        desDirector + "/" + file[i].getName());
            }
        }
    }

    public void delFolder(String folderPath) throws Exception {
        delAllFile(folderPath);
        String filePath = folderPath;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
        myFilePath.delete();
    }

    public boolean delAllFile(String path) throws Exception {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (file.isFile()) {
            return file.delete();
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    private void println(String msg) {
        Logger.printDetail(msg);
    }
}
