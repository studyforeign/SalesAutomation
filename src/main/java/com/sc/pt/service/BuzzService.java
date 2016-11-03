package com.sc.pt.service;

import com.sc.pt.dao.JDBCTempleDAO;
import com.sc.pt.po.PtReport;
import com.sc.pt.utils.UserAccount;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/12/15
 * Time: 10:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuzzService {
    private JDBCTempleDAO oracleMxDao;
    private JDBCTempleDAO oracleRepDao;
    private JDBCTempleDAO h2Dao;
    private JDBCTempleDAO alloyDao;

    public BuzzService() {
        try {
            oracleMxDao = new JDBCTempleDAO(UserAccount.Mx.toString());
            oracleRepDao = new JDBCTempleDAO(UserAccount.Rep.toString());
//            h2Dao = new JDBCTempleDAO(UserAccount.Sa.toString());
            alloyDao = new JDBCTempleDAO(UserAccount.Alloy.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map> queryDataByMX(String sql) {
        return oracleMxDao.query(sql);
    }

    public List<Map> queryDataByRep(String sql) {
        return oracleRepDao.query(sql);
    }

//    public List<Map> queryDataBySa(String sql) {
//        return h2Dao.query(sql);
//    }

    public List<Map> queryDataByAlloy(String sql) {
        return alloyDao.query(sql);
    }

    public Map queryOneDataByMX(String sql) {
        return oracleMxDao.queryOneRecord(sql);
    }

    public Map queryOneDataByRep(String sql) {
        return oracleMxDao.queryOneRecord(sql);
    }

    public Map queryOneDataFromH2(String sql) {
        return oracleMxDao.queryOneRecord(sql);
    }

    public Long getDataCountByMX(String sql) {
        return oracleRepDao.getDataCount(sql);
    }

    public Long getDataCountByRep(String sql) {
        return oracleRepDao.getDataCount(sql);
    }

//    public void saveData(List<String> sqlList) {
//        h2Dao.execute(sqlList);
//    }
//
//    public void saveData(String sql, PtReport po) {
//        h2Dao.executeByParams(sql, po);
//    }

    public void saveDataByAlloy(String sql, PtReport po) {
        alloyDao.executeByParams(sql, po);
    }

    public void saveDataByAlloy(List<String> sqlList) {
        alloyDao.execute(sqlList);
    }

    public void destroy() {
        oracleMxDao.closeAll();
        oracleRepDao.closeAll();
//        h2Dao.closeAll();
        alloyDao.closeAll();
    }
}
