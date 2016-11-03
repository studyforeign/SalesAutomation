package com.sc.pt.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/11/15
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBColumnUtil {
    public static void putValue2Map(Map map, ResultSetMetaData resultSetMetaData, ResultSet rs, int currentColumnNo) {
        try {
            String col_name = resultSetMetaData.getColumnLabel(currentColumnNo);
            Object val = rs.getObject(col_name);
            if (val instanceof Integer) {
                map.put(col_name, rs.getInt(col_name));
            } else if (val instanceof Long) {
                map.put(col_name, rs.getLong(col_name));
            } else if (val instanceof Short) {
                map.put(col_name, rs.getShort(col_name));
            } else if (val instanceof Byte) {
                map.put(col_name, rs.getByte(col_name));
            } else if (val instanceof Double) {
                map.put(col_name, rs.getDouble(col_name));
            } else if (val instanceof Float) {
                map.put(col_name, rs.getFloat(col_name));
            } else if (val instanceof Date) {
                map.put(col_name, new Date(rs.getDate(col_name).getTime()));
            } else if (val instanceof Timestamp) {
                map.put(col_name, rs.getTimestamp(col_name).getTime());
            }else if (val instanceof Boolean) {
                map.put(col_name, rs.getBoolean(col_name));
            } else if (val instanceof String) {
                map.put(col_name, rs.getString(col_name));
            } else {
                map.put(col_name, rs.getObject(col_name));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
