package com.sc.pt.utils;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 11/18/15
 * Time: 11:48 AM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class ParseLog {

    public ParseLog(){

    }

    /**
     *
     * 1.boolean canExecute()判断文件是否可执行
     * 2.boolean canRead()判断文件是否可读
     * 3.boolean canWrite() 判断文件是否可写
     * 4.boolean exists() 判断文件是否存在
     * 5.boolean isDirectory()
     * 6.boolean isFile()
     * 7.boolean isHidden()
     * 8.boolean isAbsolute()判断是否是绝对路径 文件不存在也能判断
     * @param file C:\\excel\\log.txt
     */
    public static HashMap<String, String> parse(String file){

        HashMap<String, String> hashMap = new HashMap();

        try {

            File f1 = new File(file);
            if(! f1.exists()) {

                System.out.println("Target log file didn't exist, please double check!");

                System.exit(100);
            }

            FileReader fr = new FileReader(file);

            BigDecimal tmp1=new BigDecimal("0");
            BigDecimal tmp2=new BigDecimal("0");
            BigDecimal tmp3=new BigDecimal("0");
            BigDecimal failed=new BigDecimal("0");
            BigDecimal processed=new BigDecimal("0");
            BigDecimal ignored=new BigDecimal("0");
            BigDecimal ms=null;
            BigDecimal volume=null;
            String startTimeStamp="";
            String endTimeStamp="";

            BufferedReader br=new BufferedReader(fr);

            String temp=null;

            temp=br.readLine();

            while(temp!=null){

//                System.out.println(temp);
                if(temp.indexOf("FailedRecords ") > 0)
                    failed = tmp1.add(new BigDecimal(temp.substring(temp.indexOf("FailedRecords ")+"FailedRecords ".length(), temp.length())));
//                    System.out.println(temp.substring(temp.indexOf("FailedRecords ")+"FailedRecords ".length(), temp.length()));

                if(temp.indexOf("processed: ") > 0)
                    processed = tmp2.add(new BigDecimal(temp.substring(temp.indexOf("processed: ")+"processed: ".length(), temp.indexOf(", ignored "))));
//                    System.out.println(temp.substring(temp.indexOf("processed: ")+"processed: ".length(), temp.indexOf(", ignored ")));

                if(temp.indexOf("ignored ") > 0)
                    ignored = tmp3.add(new BigDecimal(temp.substring(temp.indexOf("ignored ")+"ignored ".length(), temp.length())));
//                    System.out.println(temp.substring(temp.indexOf("ignored ")+"ignored ".length(), temp.length()));

                if(temp.indexOf("Time taken ") > 0)
                    ms = new BigDecimal(temp.substring(temp.indexOf("Time taken ")+"Time taken ".length(), temp.indexOf("ms. Bye!!")));
//                    System.out.println(temp.substring(temp.indexOf("Time taken ")+"Time taken ".length(), temp.indexOf("ms. Bye!!")));

                if(temp.indexOf("hope to flush asyncappender") > 0) {

                    endTimeStamp= temp.substring(0, temp.indexOf(" INFO")).replaceAll(",",".");
                    startTimeStamp = new Timestamp(DateUtil.string2Time(endTimeStamp).getTime()-ms.longValue()).toString();
//                    System.out.println(temp.substring(0, temp.indexOf(" INFO")));
                }

                temp=br.readLine();
            }

            //add the error indicator, if the value didn't change then put the indicator as 0(false) or 1(success)
            if(null == failed || null == processed || null == ignored || null == ms){

                hashMap.put("Volume", "0");
                hashMap.put("Start", startTimeStamp);
                hashMap.put("End", endTimeStamp);
                hashMap.put("Duration", "0");
                hashMap.put("Flag", "0");

            } else {
                //calculate the total records number
                volume = new BigDecimal(failed.longValue()+processed.longValue()+ignored.longValue());

                hashMap.put("Volume", volume.toString());
                hashMap.put("Start", startTimeStamp);
                hashMap.put("End", endTimeStamp);
                hashMap.put("Duration", ms.toString());
                hashMap.put("Flag", "1");
                }
            if(null != fr)
                fr.close();
            if(null != br)
                br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashMap;
    }

//    public static void main(String[] args){

//        System.out.println(PropertyUtil.getConfig("TPSophis"));
//        System.out.println(PropertyUtil.getConfig("TPOpics"));
//        System.out.println(PropertyUtil.getConfig("TPMurex2"));
//        System.out.println(PropertyUtil.getConfig("TPMurex3"));
//        System.out.println(PropertyUtil.getConfig("TPFeds"));

//        HashMap<String, String> hashMapTPSophis = ParseLog.parse(PropertyUtil.getConfig("TPSophis"));
//        HashMap<String, String> hashMapTPOpics = ParseLog.parse(PropertyUtil.getConfig("TPOpics"));
//        HashMap<String, String> hashMapTPMurex2 = ParseLog.parse(PropertyUtil.getConfig("TPMurex2"));
//        HashMap<String, String> hashMapTPMurex3 = ParseLog.parse(PropertyUtil.getConfig("TPMurex3"));
//        HashMap<String, String> hashMapTPFeds = ParseLog.parse(PropertyUtil.getConfig("TPFeds"));

//        System.out.println(hashMapTPSophis.toString());
//        try {
//            System.out.println(DateUtil.string2Time((String) hashMapTPSophis.get("End")).toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println(hashMapTPOpics.toString());
//        System.out.println(hashMapTPMurex2.toString());
//        System.out.println(hashMapTPMurex3.toString());
//        System.out.println(hashMapTPFeds.toString());

//    }
}
