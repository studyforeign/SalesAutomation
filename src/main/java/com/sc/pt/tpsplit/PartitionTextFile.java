package com.sc.pt.tpsplit;

import com.sc.pt.utils.CSVFileUtil;
import com.sc.pt.utils.GZIPUtil;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 11/27/15
 * Time: 4:22 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class PartitionTextFile extends PartitionFile {


    ArrayList masterIDs = new ArrayList();

    /**
     * Get the target file length
     * @param file
     * @return long
     * @throws IOException
     */
    @SuppressWarnings("finally")
    @Override
    public long getFileLength(File file) throws IOException {
        FileReader fr = null;
        BufferedReader br = null;
        long fileSize = 0;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                fileSize += line.length();
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
            return fileSize;
        }
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

    @Override
    public String[] partitionFileByRowNum(File srcFile, int rowNum) throws IOException {

        long fileRows = getFileRows(srcFile);

        double rows = (double)fileRows;

        double num = (double) rowNum;

        double partitionFileNum = Math.ceil(rows/num);

        return partitionFile(srcFile, (int) partitionFileNum);

    }

    /**
     *
     * Split the file by the partNum
     * @param partNum how many records in a file
     * @param srcFile source file, including the file path, file name
     *
     */
    public String[] partitionFileByPart(File srcFile, int partNum) throws IOException {

        long fileRows = getFileRows(srcFile);

        double rows = (double)fileRows;

        double num = (double) partNum;

        long partitionFileNum = (long) Math.ceil((rows-8)/num);

        return partitionFileByNum(srcFile, partitionFileNum);

    }

    @Override
    public String[] partitionFile(File srcFile, int partitionFileNum)
            throws IOException {
        if (partitionFileNum <= 0)
            return null;
        String header = getHeader(srcFile.toString());

        FileReader fr = null;
        BufferedReader br = null;
        long readNum = 0;

        String[] partitions = new String[partitionFileNum];
        try {
            fr = new FileReader(srcFile);
            br = new BufferedReader(fr);
            int i = 0;
            int count=0;
            while (partitionFileNum > i) {
                String name = null;
                if (srcFile.getName().indexOf(".") != -1)
                    name = srcFile.getName().substring(0, srcFile.getName().indexOf("."));
                else {
                    name = srcFile.getName();
                }
                partitions[i] = srcFile.getParent() + "/" + name + "_" + i;

                System.out.println(partitions[i].toString());
                System.out.println(i);

                File wFile = new File(partitions[i]);
                if (!wFile.exists()) {
                    wFile.getParentFile().mkdirs();
                    wFile.createNewFile();
                }
                FileWriter fw = new FileWriter(wFile,false);
                BufferedWriter bw = new BufferedWriter(fw);
                if(i!=0) {
                    bw.write(header);
                }
                String line = br.readLine();
                int flush=0;
                while (line != null) {
                    //remove blank
                    if (line.trim().length() == 0) {
                        line = br.readLine();
                        continue;
                    }

                    readNum += line.length();
                    if (i + 1 == partitionFileNum) {
                        if(i==0) {
                            bw.write(line);
                            bw.newLine();
                        } else {
                            bw.write(line);
                            bw.newLine();
                        }
                    } else {
                        if (readNum >= MAX_BYTE) {
                                bw.write(line);
                                bw.newLine();

                            break;
                        } else {
                           bw.write(line);
                           bw.newLine();

                        }
                    }
                    line = br.readLine();
                    if(flush % 1000 == 0){
                        bw.flush();
                    }

                    count++;
                }
                bw.flush();
                fw.flush();
                bw.close();
                fw.close();
                readNum = 0;
                i++;
            }
        } finally {
            try {
                if (br != null)
                    br.close();
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
            } finally {
                br = null;
                fr = null;
            }
        }
        return partitions;
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

        String filePath=srcFile.getAbsolutePath();
        String path = filePath.substring(0, filePath.indexOf(srcFile.getName()));
        String fileName=srcFile.getName();
//        System.out.println("The target file: "+path + percent+"\\"+ fileName);

        long fileNum = getFileRows(srcFile);
        fr = new FileReader(srcFile);
        br = new BufferedReader(fr);
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
                                srcFile.getAbsolutePath() +"_" + (part+1) +"_"+rowNum+".csv"), "GBK"),1024);
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

                    bw.write(getFooter(rowNum));
                    bw.newLine();
                    bw.flush();

                    bw = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(
                                            srcFile.getAbsolutePath() +"_" + (part+1) +".csv"), "GBK"),1024);

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

        bw.write(getFooter(fileNum - 8 - (part-1)*rowNum));

        bw.flush();
//                fw.flush();
        bw.close();
//                fw.close();
        readNum = 0;

//
//        System.out.println("Start compressing..."+System.currentTimeMillis());
//        GZIPUtil.sevenZip(path + percent+"\\"+ fileName);
//        System.out.println("Complete compressing..."+System.currentTimeMillis());

        return null;
    }

    /**
     *
     * Split the file by the rowNum
     * The big file split, which records are larger than 50000
     * @param rowNum how many records in a file
     * @param srcFile source file, including the file path, file name
     *
     */
    public String partitionFileByNum(File srcFile, long rowNum, int percent)
            throws Exception {
        if (rowNum <= 0)
            return null;
//        System.out.println("percent: "+percent);
//        String header = getHeader(srcFile.toString());
        String header = "";
        String tempData = "";
        int row=1;

        String filePath=srcFile.getAbsolutePath();
        String path = filePath.substring(0, filePath.indexOf(srcFile.getName()));
        String fileName=srcFile.getName();
        System.out.println("The target file: "+path + percent+"\\"+ fileName);

        FileReader fr = null;
        BufferedReader br = null;
        long readNum = 0;

        long fileNum = getFileRows(srcFile);
        fr = new FileReader(srcFile);
        br = new BufferedReader(fr);
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
                                path + percent+"\\"+ fileName), "GBK"),1024);
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

                    bw.write(getFooter(rowNum));
                    bw.newLine();
                    bw.flush();

                    bw = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(
                                            path + percent+"\\"+ fileName +"_" + (part+1) +"_"+rowNum+".csv"), "GBK"),1024);

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

        bw.write(getFooter(fileNum - 8 - (part-1)*rowNum));

        bw.flush();

        if(null != br)
            br.close();
        if(null != fr)
            fr.close();
        if(null != bw)
            bw.close();


        System.out.println("Start compressing..."+System.currentTimeMillis());
        GZIPUtil.sevenZip(path + percent+"\\"+ fileName);
        System.out.println("Complete compressing..."+System.currentTimeMillis());

        return path + percent+"\\"+ fileName;
    }

    /**
     * Split the file by the rowNum
     * data file is less than 50000 rows
     * @param rowNum how many records in a file
     * @param sourceFilePath source file path
     * @param targetDirectoryPath file target directory after splitting
     */
    public void partitionFileByColumn (String sourceFilePath, int rowNum,
                                       String targetDirectoryPath) throws IOException {
        long start1 = System.currentTimeMillis();

        long fileRows = getFileRows(new File(sourceFilePath));

        File sourceFile = new File(sourceFilePath);
        System.out.println(sourceFile.toString());
        File targetFile = new File(targetDirectoryPath);
        System.out.println(targetFile.toString());

        System.out.println(targetFile.getAbsolutePath());
        System.out.println(sourceFile.getName());
        System.out.println(targetFile.getAbsolutePath() + "\\" +  sourceFile.getName() +"_" + (0+1) +".csv");

        BufferedWriter bw = null;
        String header=null;

//        String header = getHeader(sourceFilePath);

        if (!sourceFile.exists() || rowNum <= 0 || sourceFile.isDirectory()) {
            return;
        }
        if (targetFile.exists()) {
            if (!targetFile.isDirectory()) {
                return;
            }
        } else {
            targetFile.mkdirs();
        }
        try {

            InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFilePath),"GBK");
            BufferedReader br=new BufferedReader(in);
            String str = "";
            StringBuffer buf = new StringBuffer();

            String tempData = br.readLine();
            int i = 1, s = 0, row=1;
            long start2 = System.currentTimeMillis();

            while (tempData != null) {
                str += tempData + "\r\n";

                if(row % 7 == 0){
//                    header = buf.toString();
                    header=str;
                    break;
                }

                tempData = br.readLine();

                row++;

            }

            header = str;

            System.out.println(header);
            //Start the data extraction
            tempData = br.readLine();
//            buf = new StringBuffer();
            while (tempData != null && i<fileRows-7) {

                str += tempData + "\r\n";
//                buf.append(tempData + "\r\n");

                    if (i % rowNum == 0 ) {

                        System.out.println(i);
                        bw = new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(
                                targetFile.getAbsolutePath() + "\\" +  sourceFile.getName() +"_" + (s+1) +".csv"), "GBK"),1024);

                        bw.write(header+str+getFooter(rowNum));

                        bw.close();

                        buf = new StringBuffer();
                        start2 = System.currentTimeMillis();
                        s += 1;
                    }

                i++;
                tempData = br.readLine();
            }
            if ((i - 1) % rowNum != 0) {

                bw = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(
                        targetFile.getAbsolutePath() + "\\" +  sourceFile.getName() +"_" + (s+1) +".csv"), "GBK"), 1024);
                bw.write(header+buf.toString()+getFooter(fileRows-s*rowNum-8));

                bw.close();
                br.close();
            }
            in.close();

        } catch (Exception e) {
        }
    }

    private String getHeader(String sourceFile){

        StringBuffer buf = new StringBuffer();
        String header = "";
        int row=1;

        try {

            InputStreamReader in = new InputStreamReader(new FileInputStream(sourceFile),"GBK");
            BufferedReader br=new BufferedReader(in);

            String tempData = br.readLine();

            while (tempData != null) {
                buf.append(tempData + "\r\n");

                if(row % 7 == 0){
                    header = buf.toString();
                    break;
                }

                tempData = br.readLine();

                row++;

            }

            in.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return header;
    }

    /**
     * Generate the data file footer
     * @param rowNumber
     * @return
     */
    private String getFooter(long rowNumber){

        return "c,ControlTrailer,r"+rowNumber+"\r\n";
    }

    /**
     * Generate the TradeID
     * @param currentTradeID
     * @return String
     */
    private String generateTradeID(String currentTradeID){

        //Logic added here
        //Find the largest ID
        //Add one each time

        return "";
    }

    /**
     * Get Max TradeID
     * @param srcFile
     * @param index
     * @return
     * @throws IOException
     */
    public String getMaxTradeID (String srcFile, int index) throws IOException {

        long fileRows=getFileRows(new File(srcFile));

        FileReader fr = null;
        BufferedReader br = null;
        fr = new FileReader(srcFile);
        br = new BufferedReader(fr);

        int row=0;
        ArrayList arrayList = null;

//        String preTradeID="";
//        String currentTradeID="";
        //Process the data header
        String tempData = br.readLine();
        String tradeIDTmp = null;
        String tradeIDMax = null;
        List<String> tradeIDList = new ArrayList();

        while (tempData != null && row<fileRows-1) {

            if(row < 7) {
                tempData = br.readLine();
                row++;
                continue;
            } else {

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                tradeIDTmp = (String) arrayList.get(index);

                int len=0;
                while(len < tradeIDTmp.length()){
                    len++;
                }

                if(StringUtils.isNumeric(tradeIDTmp)) {
                    tradeIDMax=getMaxTradeIDOfNumber(srcFile, index);
                    tradeIDMax=String.format("%0"+len+"d", Long.valueOf(tradeIDMax).longValue());
                } else {
                    tradeIDMax=getMaxTradeIDOfString(srcFile, index);
                }

                break;
            }

        }
        if(null != fr)
            fr.close();
        if(null != br)
            br.close();

        return tradeIDMax;
    }
    /**
     * Get the max tradeID of String type
     *
     * @param srcFile
     * @param index  the index of the tradeID
     *
     */
    private String getMaxTradeIDOfString (String srcFile, int index) throws IOException {

        //Logic added here
        //Find the max trade ID
        //Add one each time

        long fileRows=getFileRows(new File(srcFile));

        FileReader fr = null;
        BufferedReader br = null;
        fr = new FileReader(srcFile);
        br = new BufferedReader(fr);

        int row=0;
        ArrayList arrayList = null;

//        String preTradeID="";
//        String currentTradeID="";
        //Process the data header
        String tempData = br.readLine();
        String tradeIDTmp = null;
        List<String> tradeIDList = new ArrayList();

        while (tempData != null && row<fileRows-1) {

            if(row < 7) {
                tempData = br.readLine();
                row++;
                continue;
            }

            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

            tradeIDTmp = (String) arrayList.get(index);

            if(null != tradeIDTmp && ! "".equals(tradeIDTmp))   {
//                System.out.println((String) arrayList.get(index));
                tradeIDList.add(tradeIDTmp);
            }
            tempData = br.readLine();
            row++;
        }

        String[] tradeIDArrays = tradeIDList.toArray(new String[tradeIDList.size()]);

        Arrays.sort(tradeIDArrays);

        return tradeIDArrays[tradeIDArrays.length-1];

    }

    /**
     * Get the max tradeID of Number type
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
        ArrayList arrayList = null;

//        String preTradeID="";
//        String currentTradeID="";
        //Process the data header
        String tempData = br.readLine();

        List<Long> tradeIDList = new ArrayList();

        while (tempData != null && row<fileRows-1) {

            if(row < 7) {
                tempData = br.readLine();
                row++;
                continue;
            }

            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

            if(null != (String) arrayList.get(index) && ! "".equals((String) arrayList.get(index)))   {
//                System.out.println((String) arrayList.get(index));
                tradeIDList.add(Long.valueOf((String) arrayList.get(index)).longValue());
            }
            tempData = br.readLine();
            row++;
        }

        Long[] tradeIDArrays = tradeIDList.toArray(new Long[tradeIDList.size()]);

        Arrays.sort(tradeIDArrays);

        System.out.println(srcFile+"The minimum trade ID: "+tradeIDArrays[0].toString());
        System.out.println(srcFile+"The maximum trade ID: "+tradeIDArrays[tradeIDArrays.length-1].toString());

        return tradeIDArrays[tradeIDArrays.length-1].toString();

    }

    /**
     * Generate the new file with the new tradeID by percent number
     *
     * @param srcFile
     * @param percent  the records number
     *
     */
    public void replicateFileByPercent(String srcFile, double percent) throws Exception {


        File file = new File(srcFile);
        long fileNum = getFileRows(file);
        long validateFileNum = getValidateRowNum(file, percent);
        System.out.println("The source file: "+srcFile);

        System.out.println("The source file number is "+ fileNum);

        long rowNum = (fileNum-8) + validateFileNum;

        System.out.println("The target file number will be "+rowNum);

        if(rowNum == 0) {
            System.out.println("Invalid row number: " +rowNum);
            System.out.println("End process");
        } else
            replicateFile(srcFile, rowNum, fileNum, (int)(percent*100));

    }
    /**
     * Generate the new file with the new tradeID
     *
     * @param srcFile
     * @param rowNum  the records number
     *
     */
    public void replicateFile(String srcFile, long rowNum) throws IOException {

        //fetch the max trade ID
        String tradeIDtmp = getMaxTradeID(srcFile, 8);
        String tradeIDFormat = "";
        int len=0;
        while(len < getNumber(tradeIDtmp).length()){
            tradeIDFormat+="0";
            len++;
        }
        System.out.println("The Max Trade ID in the source file is: "+tradeIDtmp);

        DecimalFormat df = new DecimalFormat(tradeIDFormat);

//        if(rowNum <= 0 || (rowNum+Long.valueOf(getNumber(tradeIDtmp)).longValue()) > 99999999) {
//            System.out.println("The rowNum is invalid!");
//            System.exit(-1);
//        }

        File file = new File(srcFile);
        long fileNum = getFileRows(file);
        ArrayList arrayList = null;
        //If file rows > rowNum
        //Call partitionFileByRowNum

        //else file rows < rowNum
        //Replicate the file to the rowNum
        //Generate the new and unique ID for each record
        if(fileNum-8 > rowNum) {
            System.out.println("Start to split the file.");
            partitionFileByNum(file, rowNum);
        } else {

            System.out.println("Start to replicate the file.");
            //populate a new file with the increasing part

            File src = new File(srcFile);

            //header
            FileReader fr = new FileReader(srcFile);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;
            //body
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(
                                    src.getAbsolutePath() +"_rep"+".csv"), "GBK"),1024);

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
//            bw.write(tempData);
//            bw.newLine();

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(srcFile);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            while( count < rowNum+7 ) {

                if(tempData == null || cl==fileNum-1) {

                    fr = new FileReader(srcFile);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(StringUtils.isNumeric(tradeIDtmp))
                    arrayList.set(8, df.format(Long.valueOf(tradeIDtmp).longValue()+ind));
                else
                    arrayList.set(8, getCharacter(tradeIDtmp) + df.format(Long.valueOf(getNumber(tradeIDtmp)).longValue() + ind));

                // Ensure the trade is alive, TradeDate/AdjustedMaturityDate
                arrayList.set(30, "2012-08-30");
                arrayList.set(31, "2016-05-29");

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
    }

    /**
     * Generate the new file with the new tradeID
     *
     * @param srcFile
     * @param rowNum  the records number
     * @param fileNum  the file records number
     *
     */
    public void replicateFile(String srcFile, long rowNum, long fileNum, int percent) throws Exception {

        //fetch the max trade ID
        String tradeIDtmp = getMaxTradeID(srcFile, 8);
        System.out.println(tradeIDtmp);
        String tradeIDFormat = "";
        String ch="5";
        int len=0;
        while(len < getNumber(tradeIDtmp).length()){
            tradeIDFormat+="0";
            len++;
        }
        System.out.println("The Max Trade ID in the source file is: "+tradeIDtmp);

        DecimalFormat df = new DecimalFormat(tradeIDFormat);

        if(rowNum <= 0 || (rowNum+Long.valueOf(getNumber(tradeIDtmp)).longValue()) > 99999999) {
            System.out.println("The rowNum is invalid!");
//            System.exit(-1);
        }

        File file = new File(srcFile);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);

//        long fileNum = getFileRows(file);
        ArrayList arrayList = null;
        //If file rows > rowNum
        //Call partitionFileByRowNum
        System.out.println("The current data file percent: "+percent);
        //else file rows < rowNum
        //Replicate the file to the rowNum
        //Generate the new and unique ID for each record
        if(fileNum-8 > rowNum) {
            System.out.println("Start to split the file.");
            partitionFileByNum(file, rowNum, percent);

        } else {

            System.out.println("Start to replicate the file.");
            //populate a new file with the increasing part

            //header
            FileReader fr = new FileReader(srcFile);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;


            //body
            System.out.println(path + percent +"\\"+file.getName() +"_rep_"+rowNum+".csv");

            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path + percent +"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count<fileNum-1) {

                if(count >=7) {
                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
                arrayList.set(30, "2012-08-30");
                arrayList.set(31, "2016-05-29");

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                } else {
                bw.write(tempData);
                }
                bw.newLine();

                if(count%1000==0)
                    bw.flush();
                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(srcFile);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            while( count < rowNum+7 ) {

                if(tempData == null || cl==fileNum-1) {

                    fr = new FileReader(srcFile);
                    br = new BufferedReader(fr);
                    ch = String.valueOf(Integer.parseInt(ch) + 1);
                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(StringUtils.isNumeric(tradeIDtmp))
//                    arrayList.set(8, df.format(Long.valueOf(tradeIDtmp).longValue()+ind));
                    arrayList.set(8, df.format(Long.valueOf(ch+arrayList.get(8)).longValue()));
                else
//                    arrayList.set(8, getCharacter(tradeIDtmp) + df.format(Long.valueOf(getNumber(tradeIDtmp)).longValue() + ind));
                    arrayList.set(8, getCharacter((String)arrayList.get(8)) + df.format(Long.valueOf(ch+Long.valueOf(getNumber((String)arrayList.get(8)))).longValue()));

                // Ensure the trade is alive, TradeDate/AdjustedMaturityDate
                arrayList.set(30, "2012-08-30");
                arrayList.set(31, "2016-05-29");

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

            System.out.println("Starting compressing..."+ System.currentTimeMillis());
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
            System.out.println("Completing compressing..."+System.currentTimeMillis());
        }

    }

    /**
     * Generate the new file with the new tradeID by percent number
     *
     * @param srcFile
     * @param percent  the records number
     *
     */
    public void replicateFileByPercentForLA(String dir, String srcFile, double percent) throws IOException {

        String source = dir+srcFile;
        File file = new File(source);
        long fileNum = getFileRows(file);

        System.out.println("The source file number is "+ fileNum);

        long rowNum = (long) ((fileNum-8) * percent);

        System.out.println("The target file number will be "+rowNum);

        replicateFileForLA(source, rowNum, fileNum, (int)(percent*100));

        replicateForCSA(dir+"LEGALAGRMNT_CSA_20151231.csv", this.masterIDs, (int)(percent*100));

        replicateForBranches(dir+"LEGALAGRMNT_BRANCHES_20151231.csv", this.masterIDs, (int)(percent*100));

        replicateForCashColl(dir+"LEGALAGRMNT_CASH_COLL_20151231.csv", this.masterIDs, (int)(percent*100));

        replicateForMarginTerms(dir+"LEGALAGRMNT_MARGIN_TERMS_20151231.csv", this.masterIDs, (int)(percent*100));

        replicateForProducts(dir+"LEGALAGRMNT_PRODUCTS_20151231.csv", this.masterIDs, (int)(percent*100));

        replicateForSecurityColl(dir+"LEGALAGRMNT_SECURITY_COLL_20151231.csv", this.masterIDs, (int)(percent*100));

    }

    /**
     * Generate the new file with the new tradeID
     *
     * @param srcFile
     * @param rowNum  the records number
     * @param fileNum  the file records number
     *
     */
    public void replicateFileForLA(String srcFile, long rowNum, long fileNum, int percent) throws IOException {

        //fetch the max trade ID
        String tradeIDtmp = getMaxTradeID(srcFile, 8);
        String tradeIDFormat = "";
        int len=0;
        while(len < getNumber(tradeIDtmp).length()){
            tradeIDFormat+="0";
            len++;
        }
        System.out.println("The Max Trade ID in the source file is: "+tradeIDtmp);

        DecimalFormat df = new DecimalFormat(tradeIDFormat);

        if(rowNum <= 0 || (rowNum+Long.valueOf(getNumber(tradeIDtmp)).longValue()) > 99999999999l) {
            System.out.println(rowNum+Long.valueOf(getNumber(tradeIDtmp)).longValue());
            System.out.println("The rowNum is invalid!");
            System.exit(-1);
        }

        File file = new File(srcFile);
//        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);
        System.out.println(path+percent+"\\"+file.getName());

        ArrayList arrayList = null;

        //If file rows > rowNum
        //Call partitionFileByRowNum

        //else file rows < rowNum
        //Replicate the file to the rowNum
        //Generate the new and unique ID for each record
        if(fileNum-8 > rowNum) {
            System.out.println("Start to split the file.");
            partitionFileByNum(file, rowNum);
        } else {

            System.out.println("Start to replicate the Master file.");
            System.out.println("Master file number: "+fileNum);
            System.out.println("Master row number: "+rowNum);
            //populate a new file with the increasing part

            File src = new File(srcFile);

            //header
            FileReader fr = new FileReader(srcFile);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;
            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    src.getAbsolutePath() +"_rep"+rowNum+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path + percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count<fileNum-2) {
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
            fr.close();
            br.close();

            fr = new FileReader(srcFile);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            while( count < rowNum+6 ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(srcFile);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(StringUtils.isNumeric(tradeIDtmp)) {
                    arrayList.set(8, df.format(Long.valueOf(tradeIDtmp).longValue()+ind));
                    arrayList.set(9, df.format(Long.valueOf(tradeIDtmp).longValue()+ind));

                    masterIDs.add(String.valueOf(Long.valueOf(tradeIDtmp).longValue()+ind));
                }
                else {
                    arrayList.set(8, getCharacter(tradeIDtmp) + df.format(Long.valueOf(getNumber(tradeIDtmp)).longValue() + ind));
                }
                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
//To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

    public void replicateForCSA(String srcFile, ArrayList masterIDs, int percent) throws IOException {

        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);

        ArrayList arrayList = null;

        //If file rows > rowNum
        //Call partitionFileByRowNum
        long rowNum = fileNum+masterIDs.size();
        //else file rows < rowNum
        //Replicate the file to the rowNum
        //Generate the new and unique ID for each record
        if(null == masterIDs) {
            System.out.println("Replication stop due to none of the masterIDs to be replicated.");
        }
        else {
            System.out.println("Start to replicate the CSA file.");
            System.out.println("CSA file number: "+fileNum);
            System.out.println("CSA target file row number: "+rowNum);
            //populate a new file with the increasing part
            //header
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;

            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    src.getAbsolutePath() +"_rep"+rowNum+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path+percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count < fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                // Write file every 1000 rows
                if(count%1000==0) bw.flush();

                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();
            count++;

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;

            }

            int increase = 0;

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(null != masterIDs.get(increase)) {
                    arrayList.set(8, masterIDs.get(increase));
                    arrayList.set(9, masterIDs.get(increase)+"_CSA");
                    arrayList.set(10, masterIDs.get(increase));
                    arrayList.set(11, masterIDs.get(increase)+"_CSA");
                    arrayList.set(12, "CSA");

                }
                else {
                    arrayList.set(8, "0000000000");
                    arrayList.set(9, "0000000000"+"_CSA");
                    arrayList.set(10, "0000000000");
                    arrayList.set(11, "0000000000"+"_CSA");
                    arrayList.set(12, "CSA");

                }

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                increase++;
                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

    public void replicateForBranches(String srcFile, ArrayList masterIDs, int percent) throws IOException {


        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);


        ArrayList arrayList = null;
        long rowNum = fileNum+masterIDs.size();

        if(null == masterIDs) {
            System.out.println("Replication stop due to none of the masterIDs to be replicated.");
        }
        else {
            System.out.println("Start to replicate the Branches file.");
            System.out.println("Branches file number: "+fileNum);
            System.out.println("Branches target file row number: "+rowNum);
            //populate a new file with the increasing part
            //header
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;

            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    file.getAbsolutePath() +"_rep_"+masterIDs.size()+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path+percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count < fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                // Write file every 1000 rows
                if(count%1000==0) bw.flush();

                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();
            count++;

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            int increase = 0;

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(null != masterIDs.get(increase)) {
                    arrayList.set(8, masterIDs.get(increase));
                    arrayList.set(9, masterIDs.get(increase)+"_CSA");
                    arrayList.set(10, masterIDs.get(increase));
                    arrayList.set(11, masterIDs.get(increase)+"_CSA");
                    arrayList.set(12, "CSA");
                }

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                increase++;
                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

    public void replicateForCashColl(String srcFile, ArrayList masterIDs, int percent) throws IOException {

        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);

        ArrayList arrayList = null;
        long rowNum = fileNum+masterIDs.size();

        if(null == masterIDs) {
            System.out.println("Replication stop due to none of the masterIDs to be replicated.");
        }
        else {
            System.out.println("Start to replicate the CashColl file.");
            System.out.println("CashColl file number: "+fileNum);
            System.out.println("CashColl target file row number: "+rowNum);
            //populate a new file with the increasing part
            //header
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;

            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    file.getAbsolutePath() +"_rep_"+masterIDs.size()+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path+percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count < fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                // Write file every 1000 rows
                if(count%1000==0) bw.flush();

                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();
            count++;

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            int increase = 0;

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(null != masterIDs.get(increase)) {
                    // column 8, BCDFLegalAgreementMasterID
                    arrayList.set(8, masterIDs.get(increase));
                    // column 9, BCDFLegalAgreementCSAID
                    arrayList.set(9, masterIDs.get(increase)+"_CSA");
                    // column 10, BCDFLegalAgreementCashColID
                    // column 11, AgreementID
                    arrayList.set(11, masterIDs.get(increase));
                    // column 12, AnnexID
                    arrayList.set(12, masterIDs.get(increase)+"_CSA");
                    // column 13, AnnexType
                    arrayList.set(13, "CSA");
                }

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                increase++;
                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

    public void replicateForMarginTerms(String srcFile, ArrayList masterIDs, int percent) throws IOException{

        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);


        ArrayList arrayList = null;
        long rowNum = fileNum+masterIDs.size();

        if(null == masterIDs) {
            System.out.println("Replication stop due to none of the masterIDs to be replicated.");
        }
        else {
            System.out.println("Start to replicate the MarginTerms file.");
            System.out.println("MarginTerms file number: "+fileNum);
            System.out.println("MarginTerms target file row number: "+rowNum);
            //populate a new file with the increasing part
            //header
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;

            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    file.getAbsolutePath() +"_rep_"+masterIDs.size()+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path+percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count < fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                // Write file every 1000 rows
                if(count%1000==0) bw.flush();

                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();
            count++;

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            int increase = 0;

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(null != masterIDs.get(increase)) {
                    // column 8, BCDFLegalAgreementMasterID
                    arrayList.set(8, masterIDs.get(increase));
                    // column 9, BCDFLegalAgreementCSAID
                    arrayList.set(9, masterIDs.get(increase)+"_CSA");
                    // column 10, BCDFLegalAgreementCashColID
                    // column 11, AgreementID
                    arrayList.set(11, masterIDs.get(increase));
                    // column 12, AnnexID
                    arrayList.set(12, masterIDs.get(increase)+"_CSA");
                    // column 13, AnnexType
                    arrayList.set(13, "CSA");
                }

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                increase++;
                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

    public void replicateForProducts(String srcFile, ArrayList masterIDs, int percent) throws IOException{

        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);


        ArrayList arrayList = null;
        long rowNum = fileNum+masterIDs.size();

        if(null == masterIDs) {
            System.out.println("Replication stop due to none of the masterIDs to be replicated.");
        }
        else {
            System.out.println("Start to replicate the Products file.");
            System.out.println("Products file number: "+fileNum);
            System.out.println("Products target file row number: "+rowNum);
            //populate a new file with the increasing part
            //header
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;

            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    file.getAbsolutePath() +"_rep_"+masterIDs.size()+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path+percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count < fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                // Write file every 1000 rows
                if(count%1000==0) bw.flush();

                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();
            count++;

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            int increase = 0;

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(null != masterIDs.get(increase)) {
                    // column 8, BCDFLegalAgreementMasterID
                    arrayList.set(8, masterIDs.get(increase));
                    // column 9, BCDFLegalAgreementCSAID
                    arrayList.set(9, masterIDs.get(increase)+"_CSA");
                    // column 10, BCDFLegalAgreementProductsID
                    // column 11, AgreementID
                    arrayList.set(11, masterIDs.get(increase));
                    // column 13, AnnexID
                    arrayList.set(13, masterIDs.get(increase)+"_CSA");
                    // column 14, AnnexType
                    arrayList.set(14, "CSA");
                }

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                increase++;
                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

    public void replicateForSecurityColl(String srcFile, ArrayList masterIDs, int percent) throws IOException{

        File file = new File(srcFile);
        long fileNum = getFileRows(file);

        String fileName=file.getAbsolutePath();
        String path = fileName.substring(0, fileName.indexOf(file.getName()));
//        String zipFile="";
        // If directory doesn't exist, create it.
        GZIPUtil.mkdir(path+percent);


        ArrayList arrayList = null;
        long rowNum = fileNum+masterIDs.size();

        if(null == masterIDs) {
            System.out.println("Replication stop due to none of the masterIDs to be replicated.");
        }
        else {
            System.out.println("Start to replicate the SecurityColl file.");
            System.out.println("SecurityColl file number: "+fileNum);
            System.out.println("SecurityColl target file row number: "+rowNum);
            //populate a new file with the increasing part
            //header
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
//            String tempData = br.readLine();
            int count=0;
            long ind=1l;

            //body
//            BufferedWriter bw = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(
//                                    file.getAbsolutePath() +"_rep_"+masterIDs.size()+".csv"), "GBK"),1024);
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(path+percent+"\\"+file.getName()), "GBK"),1024);

            String tempData = br.readLine();
            String lastRecord="";
            while(tempData != null && count < fileNum-1) {
                bw.write(tempData);
                bw.newLine();

                // Write file every 1000 rows
                if(count%1000==0) bw.flush();

                tempData = br.readLine();
                count++;
            }


            //Write the last row of the data file
//            bw.write(tempData);
//            bw.newLine();
            count++;

            //The original row to be copied
//            arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);
            fr.close();
            br.close();

            fr = new FileReader(file);
            br = new BufferedReader(fr);

            tempData = br.readLine();
//            long length = getFileRows(new File(srcFile));
            int cl=0;

            //Skip the file header
            while (cl < 7) {
                tempData = br.readLine();
                cl++;
            }

            int increase = 0;

            while( count < rowNum ) {

                if(tempData == null || cl==fileNum-2) {

                    fr = new FileReader(file);
                    br = new BufferedReader(fr);

                    cl=0;
                    tempData = br.readLine();

                    while (cl < 7) {
                        tempData = br.readLine();
                        cl++;
                    }
                }

                arrayList= CSVFileUtil.fromCSVLinetoArray(tempData);

                //change the tradeID
                //System.out.println(arrayList.get(8).toString());

                //If tradeID started with the char
                //substring the number then increase the number part
                if(null != masterIDs.get(increase)) {
                    // column 8, BCDFLegalAgreementMasterID
                    arrayList.set(8, masterIDs.get(increase));
                    // column 9, BCDFLegalAgreementCSAID
                    arrayList.set(9, masterIDs.get(increase)+"_CSA");
                    // column 10, BCDFLegalAgreementSecColID
                    // column 11, AgreementID
                    arrayList.set(11, masterIDs.get(increase));
                    // column 12, AnnexID
                    arrayList.set(12, masterIDs.get(increase)+"_CSA");
                    // column 13, AnnexType
                    arrayList.set(13, "CSA");
                }

                bw.write(CSVFileUtil.toCSVLine(arrayList));
                bw.newLine();

                if(count%1000==0)
                    bw.flush();

                tempData = br.readLine();

                increase++;
                ind++;
                count++;
                cl++;
            }
            bw.flush();
            //footer
            bw.write(getFooter(rowNum-8));

            if(null != br)
                br.close();
            if(null != fr)
                fr.close();
            if(null != bw)
                bw.close();

        }
        System.out.println("Starting compressing..."+ System.currentTimeMillis());
        try {
            GZIPUtil.sevenZip(path + percent + "\\" + file.getName());
        } catch (Exception e) {
            //To change body of catch statement use File | Settings | File Templates.
            e.printStackTrace();
        }
        System.out.println("Completing compressing..."+System.currentTimeMillis());
    }

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
     *
     * @param str
     * @return
     */
    private String getCharacter(String str) {

        str=str.trim();
        String str2="";
        if(str != null && !"".equals(str)){
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)<48 || str.charAt(i)>57){
                    str2+=str.charAt(i);
                }
            }
        }
        return str2;
    }

    public long getValidateRowNum(File file, double percent) throws Exception {

        String currentDate="";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate=sdf.format(dt);

        //Get number1 > 2016.1.4
        long number1=0l;
        //Get number2 > current date
        long number2=0l;
        //Return number1*1.25 - number2
        long number3=0l;
        FileReader fr = null;
        BufferedReader br = null;
        long fileRows = 0l;
        ArrayList arrayList = new ArrayList();
        String fileDate=null;
        String adjustedMaturityDate=null;
        String asOfDate=null;
        String adjustedValueDateCurPeriod1=null;
        String finalPricingDate=null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                fileRows++;
                arrayList= CSVFileUtil.fromCSVLinetoArray(line);
                if(fileRows>7 && !arrayList.get(0).equals("c")) {

                    adjustedMaturityDate = (String)arrayList.get(31);
                    asOfDate = (String)arrayList.get(2);
                    adjustedValueDateCurPeriod1 = (String)arrayList.get(181);
//                    finalPricingDate = (String)arrayList.get(580);
//                    fileDate= max(max(adjustedMaturityDate, asOfDate),
//                            max(adjustedValueDateCurPeriod1,finalPricingDate));
                    fileDate= max(max(adjustedMaturityDate, asOfDate), adjustedValueDateCurPeriod1);
                    if(fileDate.compareTo("2016-01-04")>=0)
                        number1++;
                    if(fileDate.compareTo(currentDate)>=0)
                        number2++;
                }
                line = br.readLine();
            }

            number3 = Math.round(number1*percent) - number2;

            System.out.println(fileRows);
            System.out.println(number1);
            System.out.println(number2);
            System.out.println(number3);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null)
                br.close();
            if (fr != null)
                fr.close();

        }

        return number3;
    }

    /**
     * Unite the file as the one new file
     * @param files
     * @param newFile
     * @throws IOException
     */
    @Override
    public void uniteFile(String[] files, String newFile) throws IOException {
        File wFile = new File(newFile);
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(wFile,false);
            bufferedWriter = new BufferedWriter(writer);
            for (int i = 0; i < files.length; i++) {
                File rFile = new File(files[i]);
                FileReader reader = new FileReader(rFile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    if (line.trim().length() == 0) {
                        line = bufferedReader.readLine();
                        continue;
                    }
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    line = bufferedReader.readLine();
                }
                bufferedWriter.flush();
                writer.flush();
            }
        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
            bufferedWriter = null;
            if (writer != null)
                writer.close();
            writer = null;
        }
    }

    private String max(String s1, String s2){

        if(null==s1 && null == s2) return "";

        if(null == s1) return s2;
        if(null == s2) return s1;

        if(s1.compareTo(s2)>=0) return s1;
        else
            return s2;
    }
    public static void main(String[] args) throws Exception {

        PartitionTextFile p = new PartitionTextFile();

//        double percent[]={0.5, 0.75, 1.25, 1.5, 1.75, 2.00};
        double percent[]={1.25, 1.5, 1.75, 2.00};
        double percentage = 0.0;
        for(int i=0; i<percent.length; i++) {
        percentage= percent[i];
//        System.out.println("Start Murex2.11 partition...");
//        p.replicateFileByPercent("C:\\excel\\data20151231\\murex2\\MXG_TRADE_20151231_1.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\murex2\\MXG_TRADE_20151231_2.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\murex2\\MXG_TRADE_20151231_3.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\murex2\\MXG_TRADE_20151231_4.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\murex2\\MXG_TRADE_20151231_5.csv", percentage);
//
//        System.out.println("Start murex3.1 Razor partition...");
//        p.replicateFileByPercent("C:\\excel\\data20151231\\razor\\MXG3WESTFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\razor\\MXG3WESTMM_TRADE_20151231.csv", percentage);
//
//        System.out.println("Start sophis partition...");
//        p.replicateFileByPercent("C:\\excel\\data20151231\\sophis\\SOPASIAALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\sophis\\SOPLONDONALL_TRADE_20151231.csv", percentage);
//
//        System.out.println("Start opics partition...");
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSAEALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSAOALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSBDALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSBHALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSBRALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSCNALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSDEALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSGBALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSHKALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSIDALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSINALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSIQALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSJEALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSJOALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSJPALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSKHALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSKYALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSLKALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSMOALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSMUALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSMYALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSMZALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSNLALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSOMALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSPHALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSPKALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSQAALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSSAALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSSGALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSTHALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSTWALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSUSALL_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\opics\\OPICSVNALL_TRADE_20151231.csv", percentage);
//
//        System.out.println("Start feds partition...");
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSCNFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSHKFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSIDFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSINFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSJPFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSKRFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSMEFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSMYFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSSGFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSTHFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSTWFX_TRADE_20151231.csv", percentage);
//        p.replicateFileByPercent("C:\\excel\\data20151231\\feds\\FEDSUSFX_TRADE_20151231.csv", percentage);

//            p.getValidateRowNum(new File("C:\\excel\\data20151231\\sophis\\SOPASIAALL_TRADE_20151231.csv"), percentage);
//            System.out.println(p.getFileRows(new File("C:\\excel\\data20151231\\sophis\\SOPASIAALL_TRADE_20151231.csv")));

        //just for legal agreement data
        p.replicateFileByPercentForLA("C:\\excel\\data20151231\\icdms\\", "LEGALAGRMNT_MASTER_20151231.csv", percentage);
        }
    }

}
