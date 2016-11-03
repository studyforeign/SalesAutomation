package com.sc.pt.utils;

import java.io.*;
//import org.apache.commons
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 3/22/16
 * Time: 4:49 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class GZIPUtil {

//    private static final Logger log = Logger.getLogger(GZIPUtil.class);

    public static void compressFile(String inFileName) {
        String outFileName = inFileName + ".gz";
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(inFileName));
        }catch (FileNotFoundException e) {
            e.printStackTrace();
//            log.debug("Could not find the inFile..."+inFileName);
//            log.error(CommonUtil.stackToString(e));
        }

        GZIPOutputStream out = null;
        try {
            out = new GZIPOutputStream(new FileOutputStream(outFileName));
        }catch (IOException e) {
            e.printStackTrace();
//            log.debug("Could not find the outFile..."+outFileName);
//            log.error(CommonUtil.stackToString(e));
        }
        byte[] buf = new byte[1024];
        int len = 0;
        try {
            while ((len = in.read()) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
//            log.debug("Completing the GZIP file..."+outFileName);
            System.out.println("Completing the GZIP file..."+outFileName);
            out.flush();
            out.close();
        }catch (IOException e) {
            e.printStackTrace();
//            log.error(CommonUtil.stackToString(e));
        }
    }

    /** * 用于创建文件夹的方法
     * * @param mkdirName */
    public static void mkdir(String mkdirName) {
        try {
            File dirFile = new File(mkdirName);
            boolean bFile = dirFile.exists();
            if( bFile == true ) {
                System.out.println("The folder exists.");
            } else {
                System.out.println("The folder do not exist,now trying to create a one...");
                bFile = dirFile.mkdir();
                if( bFile == true ) {
                    System.out.println("Create successfully!");
                    System.out.println("创建文件夹");
                } else {
                    System.out.println("Disable to make the folder,please check the disk is full or not.");
                    System.out.println(" 文件夹创建失败，清确认磁盘没有写保护并且空件足够");
                    System.exit(1);
                }
            }
        } catch(Exception err) {
            System.err.println("ELS - Chart : 文件夹创建发生异常");
            err.printStackTrace();
        }
    }

    public static void sevenUnZip(String filename) throws Exception {
        File zipFile = new File(filename);
        if (!zipFile.exists()) {
            return;
        }

        File zipExeFile = new File("C:\\Program Files\\7-Zip\\7z.exe");
        String exec = String.format("%s x -aoa \"%s\" -o\"%s\"", zipExeFile.getAbsolutePath(),
                zipFile.getAbsolutePath(), zipFile.getParent());
        Runtime runtime = Runtime.getRuntime();
        runtime.exec(exec);
    }

    public static void sevenZip(String filename) throws Exception {
        File zipFile = new File(filename);
        if (!zipFile.exists()) {
            return;
        }

        System.out.println("Compressing... "+"C:\\temp\\7-Zip\\7z.exe a -tgzip "+filename+".gz " +filename);
//        File zipExeFile = new File("C:\\Program Files\\7-Zip\\7z.exe");
//        String exec = String.format("-tgzip", zipExeFile.getAbsolutePath(),
//                zipFile.getAbsolutePath(), zipFile.getParent());
        Process runtime = Runtime.getRuntime().exec("C:\\temp\\7-Zip\\7z.exe a -tgzip "+filename+".gz " +filename);
        runtime.waitFor();

    }


    public static void main(String[] args) throws Exception {
//        String str = "C:\\excel\\data20151231\\murex2\\MXG_TRADE_20151231_2.csv";
//        compressFile(str);

//        sevenZip("C:\\excel\\data20151231\\opics\\50\\OPICSVNALL_TRADE_20151231.csv");

//        Process runtime0 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
//        Process runtime1 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
//        Process runtime2 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
//        Process runtime3 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
//        Process runtime4 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
//        Process runtime5 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
//        Process runtime6 = Runtime.getRuntime().exec("C:\\dev tools\\mx3\\client-200.cmd");
    }
}