package com.sc.pt.utils;

import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/11/15
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataSourceUtil {

    private DataSourceUtil() {
    }

    public static BasicDataSource getInstance(String dbType) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(PropertyUtil.getProperty(dbType+".jdbc.driverClassName"));
        dataSource.setUrl(PropertyUtil.getProperty(dbType+".jdbc.url"));
        dataSource.setUsername(PropertyUtil.getProperty(dbType+".jdbc.username"));
        dataSource.setPassword(PropertyUtil.getProperty(dbType+".jdbc.password"));
        return dataSource;
    }

}
