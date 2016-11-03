package com.sc.pt.tpsplit;

import com.sc.pt.utils.CSVFileUtil;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 1/21/16
 * Time: 9:33 AM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class ReplicateCpty {

    /**
     * Generate the new file with the new tradeID by percent number
     *
     * @param srcFile
     * @param percent  the records number
     *
     */
    public void replicateFileByPercent(String srcFile, double percent) throws IOException {

        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        System.out.println("The source file number is "+ fileNum);

        long rowNum = (long) ((long) (fileNum-1) * percent);

        System.out.println("The target file number will be "+rowNum);

        replicateFile(srcFile, rowNum, fileNum);

    }

    /**
     * Generate the new file with the new tradeID
     *
     * @param srcFile
     * @param rowNum  the records number
     * @param fileNum  the file records number
     *
     */
    public void replicateFile(String srcFile, long rowNum, long fileNum) throws IOException {

        //fetch the max trade ID
        String tradeIDtmp = getMaxTradeIDOfNumber(srcFile, 0);
        int len=0;

        System.out.println("The Max FM ID in the source file is: "+tradeIDtmp);

        if(rowNum <= 0 || (rowNum+Long.valueOf(getNumber(tradeIDtmp)).longValue()) > 999999999l) {
            System.out.println(rowNum+Long.valueOf(getNumber(tradeIDtmp)).longValue());
            System.out.println("The rowNum is invalid!");
            System.exit(-1);
        }

        File file = new File(srcFile);
//        long fileNum = getFileRows(file);
        List<String> arrayList = null;
        //If file rows > rowNum
        //Call partitionFileByRowNum

        //else file rows < rowNum
        //Replicate the file to the rowNum
        //Generate the new and unique ID for each record
        if(fileNum-1 > rowNum) {
            System.out.println("Start to split the file.");
            partitionFileByNum(file, rowNum);
        } else {

            System.out.println("Start to replicate the file.");
            //populate a new file with the increasing part

            //header
//            FileReader fr = new FileReader(srcFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(srcFile), "CP1252"),1024);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;
            //body
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(
                                    file.getAbsolutePath() +"_rep_"+rowNum+".txt"), "CP1252"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count<fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                if(count%1000==0)
                    bw.flush();
                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
            bw.write(tempData);
            bw.newLine();

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
//            fr.close();
            br.close();

//            fr = new FileReader(srcFile);
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(srcFile), "CP1252"),1024);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 1) {
                tempData = br.readLine();
                cl++;
            }

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-1) {

//                    fr = new FileReader(srcFile);
                    br = new BufferedReader(new InputStreamReader(
                            new FileInputStream(srcFile), "CP1252"),1024);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 1) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                char a = 167;

//                tempData = toHexString(tempData);
                arrayList= Arrays.asList(tempData.split(String.valueOf(a)));

                //If tradeID started with the char
                //substring the number then increase the number part
                arrayList.set(0,String.valueOf(Long.valueOf(tradeIDtmp).longValue()+ind));

//                System.out.println(String.valueOf(Long.valueOf(tradeIDtmp).longValue()+ind));
//
//                System.out.println(arrayList.toString());

                bw.write(toPlainLine(arrayList.toArray(new String[8])));


                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                ind++;
                count++;
                cl++;
            }
            bw.flush();

            if(null != br)
                br.close();
//            if(null != fr)
//                fr.close();
            if(null != bw)
                bw.close();

        }
    }

     /**
     * Get the max FMID of Number type
     *
     * @param srcFile
     * @param index  the index of the tradeID
     *
     */
    private String getMaxTradeIDOfNumber (String srcFile, int index) throws IOException {

        //Logic added here
        //Find the max trade ID
        //Add one each time

        long fileRows=getFileRows(new File(srcFile));

        FileReader fr = null;
        BufferedReader br = null;
        fr = new FileReader(srcFile);
        br = new BufferedReader(fr);

        int row=0;
        List<String> arrayList = null;

//        String preTradeID="";
//        String currentTradeID="";
        //Process the data header
        String tempData = br.readLine();

        List<Long> tradeIDList = new ArrayList();

        while (tempData != null && row<fileRows) {

            // Ignore the table head
            if(row < 1) {
                tempData = br.readLine();
                row++;
                continue;
            }

            tempData = toHexString(tempData);

            //分隔符 fffd
            arrayList= Arrays.asList(tempData.split("fffd"));

            if(null != (String) arrayList.get(index) && ! "".equals((String) arrayList.get(index)))   {
                tradeIDList.add(Long.valueOf((String) arrayList.get(index)).longValue());
            }
            tempData = br.readLine();
            row++;
        }

        Long[] tradeIDArrays = tradeIDList.toArray(new Long[tradeIDList.size()]);

        Arrays.sort(tradeIDArrays);

        br.close();
        fr.close();

        return toStringHex(tradeIDArrays[tradeIDArrays.length - 1].toString());

    }
    /**
     * 字符串类型的List转换成一个CSV行。（输出CSV文件的时候用）
     */
    public static String toPlainLine(ArrayList strArrList) {
        if (strArrList == null) {
            return "";
        }
        String[] strArray = new String[strArrList.size()];
        for (int idx = 0; idx < strArrList.size(); idx++) {
            strArray[idx] = (String) strArrList.get(idx);
        }
        return toPlainLine(strArray);
    }

    /**
     * 把字符串类型的数组转换成一个CSV行。（输出CSV文件的时候用）
     */
    public static String toPlainLine(String[] strArray) {
        if (strArray == null) {
            return "";
        }
        StringBuffer cvsLine = new StringBuffer();
        char a = 167;
        for (int idx = 0; idx < strArray.length; idx++) {
//            String item = addQuote(strArray[idx]);
            String item = strArray[idx];
            cvsLine.append(item);
            if (strArray.length - 1 != idx) {
                cvsLine.append(a);
            }
        }
        return cvsLine.toString();
    }

    /**
     *
     * Split the file by the rowNum
     * The big file split, which records are larger than 50000
     * @param rowNum how many records in a file
     * @param srcFile source file, including the file path, file name
     *
     */
    public String[] partitionFileByNum(File srcFile, long rowNum)
            throws IOException {
        if (rowNum <= 0)
            return null;

//        String header = getHeader(srcFile.toString());
        String header = "";
        String tempData = "";
        int row=1;

        FileReader fr = null;
        BufferedReader br = null;
        long readNum = 0;

        long fileNum = getFileRows(srcFile);
//        fr = new FileReader(srcFile);
        br = new BufferedReader(new InputStreamReader(
                new FileInputStream(srcFile), "CP1252"),1024);
        int count=0;
        int part=0;

        //Process the data header
        tempData = br.readLine();
        while (tempData != null) {
            header += tempData + "\r\n";

            if(row > 6){
                break;
            }

            tempData = br.readLine();

            row++;

        }

        //First data file
        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(
                                srcFile.getAbsolutePath() +"_" + (part+1) +"_"+rowNum+".txt"), "CP1252"),1024);
        //Write the header
        bw.write(header);

        // Partition increase
        part++;

        int flush=0;

        tempData = br.readLine();

        while (tempData != null && count<fileNum-8) {
            //remove blank
            if (tempData.trim().length() == 0) {
                tempData = br.readLine();
                continue;
            }

//                    readNum += line.length();
            if (rowNum >= fileNum) {

                bw.write(tempData);
                bw.newLine();

            } else {
//                        if (readNum >= MAX_BYTE) {
                if (count!=0 && count%rowNum==0) {
                    bw.newLine();
                    bw.flush();

                    bw = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(
                                            srcFile.getAbsolutePath() +"_" + (part+1) +".txt"), "CP1252"),1024);

                    bw.write(header);
                    bw.write(tempData);
                    bw.newLine();
                    part++;
                } else {
                    bw.write(tempData);
                    bw.newLine();

                }
            }
            tempData = br.readLine();
            if(flush % 1000 == 0){
                bw.flush();
            }

            count++;
            flush++;
        }

        bw.flush();
//                fw.flush();
        bw.close();
//                fw.close();
        readNum = 0;

        return null;
    }

    /**
     *
     * @param str
     * @return
     */
    private String getNumber(String str) {

        str=str.trim();
        String str2="";
        if(str != null && !"".equals(str)){
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)>=48 && str.charAt(i)<=57){
                    str2+=str.charAt(i);
                }
            }
        }
        return str2;
    }

    /**
     * Get target data file rows (exclude the header and the footer)
     * @param file
     * @return long
     * @throws IOException
     */
    private long getFileRows(File file) throws IOException {
        FileReader fr = null;
        BufferedReader br = null;
        long fileRows = 0l;

        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                fileRows++;
                line = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null)
                br.close();
            if (fr != null)
                fr.close();
            return fileRows;
        }
    }

    // 转化字符串为十六进制编码
    public static String toHexString(String s)
    {
        String str="";
        for (int i=0;i<s.length();i++)
        {
            int ch = (int)s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    // 转化十六进制编码为字符串
    public static String toStringHex(String s)
    {
        byte[] baKeyword = new byte[s.length()/2];
        for(int i = 0; i < baKeyword.length; i++)
        {
            try
            {
                baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            s = new String(baKeyword, "GBK");//UTF-16le:Not
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        return s;
    }

    public static void main(String[] args) throws Exception {
        ReplicateCpty replicateCpty = new ReplicateCpty();
        double percent = 0.8;
        try {
            replicateCpty.replicateFileByPercent("C:\\excel\\sci\\FM_MCMS_EXTRACT_10012016.txt", percent);

        } catch (IOException e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }

//        System.out.println(replicateCpty.getMaxTradeIDOfNumber("C:\\excel\\sci\\FM_MCMS_EXTRACT_11122015.txt", 0));

//        replicateCpty.replicateFileByPercent("C:\\excel\\sci\\FM_MCMS_EXTRACT_11122015.txt", 2.00);

//        File file = new File("C:\\excel\\sci\\FM_MCMS_EXTRACT_11122015.txt");
//
////        FileReader fr = new FileReader(file);
//        BufferedReader br = new BufferedReader(new InputStreamReader(
//                new FileInputStream(
//                        "C:\\excel\\sci\\FM_MCMS_EXTRACT_11122015.txt"), "CP1252"),1024);
//
////        File fileTo = new File("C:\\excel\\sci\\FM_MCMS_EXTRACT_11122015_b.txt");
////        FileWriter fw = new FileWriter(fileTo);
//        BufferedWriter bw = new BufferedWriter(
//               new OutputStreamWriter(
//                new FileOutputStream(
//                        "C:\\excel\\sci\\FM_MCMS_EXTRACT_11122015_b.txt"), "CP1252"),1024);
//        int count=0;
//
//        String tempData = br.readLine();
//
//        while(null != tempData) {
//
//            bw.write(toHexString(tempData));
//            bw.newLine();
//            tempData = br.readLine();
//
//            count++;
//        }
//
//        if(count % 1000 ==0)
//            bw.flush();
//
//        bw.flush();
//
//        br.close();
////        fr.close();
//        bw.close();
////        fw.close();

    }
}
