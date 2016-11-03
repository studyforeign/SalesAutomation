package com.sc.pt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/11/15
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class PropertyUtil {
    static Properties prop;
    static Properties sqlProp;
    static Properties configProp;

    static {
        prop = new Properties();
        InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
        sqlProp = new Properties();
        InputStream sqlIn = PropertyUtil.class.getClassLoader().getResourceAsStream("sql.properties");
        configProp = new Properties();
        InputStream configIn = PropertyUtil.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            prop.load(in);
            sqlProp.load(sqlIn);
            configProp.load(configIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static String getSql(String key) {
        return sqlProp.getProperty(key);
    }

    public static String getConfig(String key) {
        return configProp.getProperty(key);
    }
}
