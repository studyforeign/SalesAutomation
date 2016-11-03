package com.sc.pt.dao;

import com.sc.pt.po.PtReport;
import com.sc.pt.utils.DBColumnUtil;
import com.sc.pt.utils.DataSourceUtil;
import com.sc.pt.utils.PropertyUtil;
import com.sc.pt.utils.UserAccount;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/11/15
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class JDBCTempleDAO {

    private BasicDataSource dataSource;
    private Connection conn;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet rs;

    public JDBCTempleDAO(String dbType) throws SQLException {
        dataSource = DataSourceUtil.getInstance(dbType);
        conn = dataSource.getConnection();
    }

    /**
     * fetch data records use sql
     *
     * @param sql
     * @return
     */
    public List<Map> query(String sql) {
        List<Map> result = new ArrayList<Map>();
        Map map = null;
        ResultSetMetaData resultSetMetaData = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            resultSetMetaData = rs.getMetaData();
            while (rs.next()) {
                map = new HashMap();
                int columnNum = resultSetMetaData.getColumnCount();
                for (int i = 0; i < columnNum; i++) {
                    DBColumnUtil.putValue2Map(map, resultSetMetaData, rs, i + 1);
                }
                result.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeAll();
        }
        return result;
    }

    /**
     * fetch one data record use sql
     *
     * @param sql
     * @return
     */
    public Map queryOneRecord(String sql) {
        Map map = null;
        ResultSetMetaData resultSetMetaData = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            resultSetMetaData = rs.getMetaData();
            if (rs.next()) {
                map = new HashMap();
                int columnNum = resultSetMetaData.getColumnCount();
                for (int i = 0; i < columnNum; i++) {
                    DBColumnUtil.putValue2Map(map, resultSetMetaData, rs, i + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeAll();
        }
        return map;
    }

    public Long getDataCount(String sql) {
        long count = 0;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeAll();
        }
        return count;
    }

    public void execute(List<String> sqlList) {
        try {
            conn.setAutoCommit(false);
            statement = conn.createStatement();
            for (String sql : sqlList) {
                statement.addBatch(sql);
            }
            statement.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeAll();
        }
    }

    public void executeByParams(String sql, PtReport po) {
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, po.getGroupId());
            preparedStatement.setString(2, po.getScript());
            preparedStatement.setTimestamp(3, po.getStart());
            preparedStatement.setTimestamp(4, po.getEnd());
            preparedStatement.setLong(5, po.getDuration());
            preparedStatement.setLong(6, po.getVolume());
            preparedStatement.setTimestamp(7,po.getCreateTime());
            preparedStatement.setString(8, po.getUser());
            preparedStatement.setInt(9,po.getFlag());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closeAll();
        }
    }

    public void closeAll() {
        if (null != rs) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != statement) {
            try {
                statement.close();
                statement = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != preparedStatement) {
            try {
                preparedStatement.close();
                preparedStatement = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != conn) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (null != dataSource) {
            try {
                dataSource.close();
                dataSource = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) throws Exception {
//        JDBCTempleDAO jdbcTempleDAO = new JDBCTempleDAO(UserAccount.Rep.toString());
//        long a = jdbcTempleDAO.getDataCount(PropertyUtil.getSql("rep.query.count.colllat.tp"));
//        System.out.println(a);
//
//    }
}
