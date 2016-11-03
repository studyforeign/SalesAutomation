package com.sc.pt.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 11/18/15
 * Time: 9:59 AM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class DateUtil {
    /**
     * method 将字符串类型的日期转换为一个timestamp（时间戳记java.sql.Timestamp）
     * dateString 需要转换为timestamp的字符串
     * dataTime timestamp
     */
    public final static java.sql.Timestamp string2Time(String dateString)
            throws java.text.ParseException {
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.SSS", Locale.ENGLISH);//设定格式
        //dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
        dateFormat.setLenient(false);
        java.util.Date timeDate = dateFormat.parse(dateString);//util类型
        java.sql.Timestamp dateTime = new java.sql.Timestamp(timeDate.getTime());//Timestamp类型,timeDate.getTime()返回一个long型
        return dateTime;
    }
    /**
     * method 将字符串类型的日期转换为一个Date（java.sql.Date）
     * dateString 需要转换为Date的字符串
     * dataTime Date
     */
    public final static java.sql.Date string2Date(String dateString)
            throws java.lang.Exception {
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        dateFormat.setLenient(false);
        java.util.Date timeDate = dateFormat.parse(dateString);//util类型
        java.sql.Date dateTime = new java.sql.Date(timeDate.getTime());//sql类型
        return dateTime;
    }

//    public static void main(String[] args){
//        Date da = new Date(99999999);
//        //注意：这个地方da.getTime()得到的是一个long型的值
//        System.out.println("11111111 "+da.getTime());
//
//        //由日期date转换为timestamp
//
//        //第一种方法：使用new Timestamp(long)
//        Timestamp t = new Timestamp(new Date(864000000).getTime());
//        System.out.println("22222222 "+t);
//
//        //第二种方法：使用Timestamp(int year,int month,int date,int hour,int minute,int second,int nano)
//        Timestamp tt = new Timestamp(Calendar.getInstance().get(
//                Calendar.YEAR) - 1900, Calendar.getInstance().get(
//                Calendar.MONTH), Calendar.getInstance().get(
//                Calendar.DATE), Calendar.getInstance().get(
//                Calendar.HOUR), Calendar.getInstance().get(
//                Calendar.MINUTE), Calendar.getInstance().get(
//                Calendar.SECOND), 0);
//        System.out.println(tt);
//
//        try {
//            String sToDate = "2005-8-18";//用于转换成java.sql.Date的字符串
//            String sToTimestamp = "2015-11-17 08:44:10,105";//用于转换成java.sql.Timestamp的字符串
//            Date date1 = string2Date(sToDate);
//            Timestamp date2 = string2Time(sToTimestamp);
//            Timestamp date3 = new Timestamp(string2Time(sToTimestamp).getTime()-1040729);
//            System.out.println("Date:"+date1.toString());//结果显示
//            System.out.println("Timestamp2: "+date2.toString());//结果显示
//            System.out.println("Timestamp3: "+date3.toString());//结果显示
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
}
