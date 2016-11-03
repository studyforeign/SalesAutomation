package com.sc.pt;

import com.sc.pt.dao.JDBCTempleDAO;
import com.sc.pt.po.PtReport;
import com.sc.pt.service.BuzzService;
import com.sc.pt.utils.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/12/15
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class RunScript {
    private static BuzzService service;
    private String groupId;
    private long currentTime;
    private HashMap<String, String> hashMapTPSophis = null;
    private HashMap<String, String> hashMapTPOpics = null;
    private HashMap<String, String> hashMapTPMurex2 = null;
    private HashMap<String, String> hashMapTPMurex3 = null;
    private HashMap<String, String> hashMapTPFeds = null;

    public static void instance(){
        service = new BuzzService();
    }

    public RunScript() {

        hashMapTPSophis = ParseLog.parse(PropertyUtil.getConfig("TPSophis"));
        hashMapTPOpics = ParseLog.parse(PropertyUtil.getConfig("TPOpics"));
        hashMapTPMurex2 = ParseLog.parse(PropertyUtil.getConfig("TPMurex2"));
        hashMapTPMurex3 = ParseLog.parse(PropertyUtil.getConfig("TPMurex3"));
        hashMapTPFeds = ParseLog.parse(PropertyUtil.getConfig("TPFeds"));
    }

    public static void destroy() {
        service.destroy();
    }

    public static void main(String[] args) {
        RunScript.instance();
        try {
            RunScript script = new RunScript();
            script.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RunScript.destroy();
        }

    }

    public void run() throws Exception {
        currentTime = System.currentTimeMillis();
        generateGroupId();

        saveData2Report(sophisLoading());
        saveData2Report(opicsLoading());
        saveData2Report(murex2Loading());
        saveData2Report(murex3Loading());
        saveData2Report(fedsLoading());

        saveData2Report(tpLoading());
        saveData2Report(resetScript());
        saveData2Report(runScript());
//        saveData2Report(mock(ScriptType.TPLoading.toString()));
//        saveData2Report(mock(ScriptType.Reset.toString()));
//        saveData2Report(mock(ScriptType.Run.toString()));
    }

    /**
     * TP_LOADING record
     *
     * @return
     */
    private PtReport sophisLoading() throws ParseException {
        PtReport po = null;
        po = createReport(hashMapTPSophis, "Sophis");
        return po;
    }

    /**
     * TP_LOADING record
     *
     * @return
     */
    private PtReport opicsLoading() throws ParseException {
        PtReport po = null;
        po = createReport(hashMapTPOpics, "Opics");
        return po;
    }

    /**
     * TP_LOADING record
     *
     * @return
     */
    private PtReport murex2Loading() throws ParseException {
        PtReport po = null;
        po = createReport(hashMapTPMurex2, "Murex2");
        return po;
    }

    /**
     * TP_LOADING record
     *
     * @return
     */
    private PtReport murex3Loading() throws ParseException {
        PtReport po = null;
        po = createReport(hashMapTPMurex3, "Murex3");
        return po;
    }

    /**
     * TP_LOADING record
     *
     * @return
     */
    private PtReport fedsLoading() throws ParseException {
        PtReport po = null;
        po = createReport(hashMapTPFeds, "Feds");
        return po;
    }

    /**
     * TP_LOADING record
     *
     * @return
     */
    private PtReport tpLoading() throws ParseException {
        PtReport po = new PtReport();
        //calculate the duration and volume
        long duration = Integer.valueOf(hashMapTPSophis.get("Duration")) + Integer.valueOf(hashMapTPOpics.get("Duration"))
                + Integer.valueOf(hashMapTPMurex2.get("Duration")) + Integer.valueOf(hashMapTPMurex3.get("Duration"))
                + Integer.valueOf(hashMapTPFeds.get("Duration"));

        long volume = Long.valueOf(hashMapTPSophis.get("Volume")) + Long.valueOf(hashMapTPOpics.get("Volume"))
                + Long.valueOf(hashMapTPMurex2.get("Volume")) + Long.valueOf(hashMapTPMurex3.get("Volume"))
                + Long.valueOf(hashMapTPFeds.get("Volume"));

        int flagSophis = Integer.valueOf(hashMapTPSophis.get("Flag"));
        int flagOpics = Integer.valueOf(hashMapTPOpics.get("Flag"));
        int flagMurex2 = Integer.valueOf(hashMapTPMurex2.get("Flag"));
        int flagMurex3 = Integer.valueOf(hashMapTPMurex3.get("Flag"));
        int flagFeds = Integer.valueOf(hashMapTPFeds.get("Flag"));

        //compose the report object
        po.setGroupId(groupId);
        po.setScript("TP_Loading");
        po.setStart(new Timestamp(currentTime));
        po.setEnd(new Timestamp(DateUtil.string2Time(new Timestamp(currentTime).toString()).getTime() + duration));
        po.setDuration(duration/1000);
        po.setVolume(volume);
        po.setUser("Sail");
        po.setCreateTime(new Timestamp(currentTime));
        po.setFlag((flagSophis+flagOpics+flagMurex2+flagMurex3+flagFeds)/5);
        return po;
    }

    /**
     * COLLAT_RESET record
     *
     * @return
     */
    private PtReport resetScript() throws Exception {
        return getPtReport(PropertyUtil.getSql("rep.query.count.colllat.tp"), PropertyUtil.getSql("mx.query.eod.collat.reset"), ScriptType.Reset.toString());
    }

    /**
     * COLLAT_RUN record
     *
     * @return
     */
    private PtReport runScript() throws Exception {
        return getPtReport(PropertyUtil.getSql("rep.query.count.coltri"), PropertyUtil.getSql("mx.query.eod.collat.run"), ScriptType.Run.toString());
    }

    /**
     * save it into report db(h2 or mysql)
     *
     * @param po
     */
    private void saveData2Report(PtReport po) {
        service.saveDataByAlloy(PropertyUtil.getSql("sa.insert.report"), po);
    }

    /**
     * generate group id
     */
    private void generateGroupId() {
        groupId = UUIDGenerator.getUUID();
    }

    /**
     * get PtReport by countSql & sql
     *
     * @param countSqlScript
     * @param sqlScript
     * @return
     */
    private PtReport getPtReport(String countSqlScript, String sqlScript, String scriptType) throws Exception {
        PtReport po = new PtReport();
        long volume = service.getDataCountByRep(countSqlScript);
        Map map = service.queryOneDataByMX(sqlScript);
        map2PtReport(po, map);
        po.setVolume(volume);
        po.setScript(scriptType);
        po.setCreateTime(new Timestamp(currentTime));
        po.setFlag(1);
        return po;
    }

    /**
     * Map to PtReport
     *
     * @param po
     * @param map
     */
    private void map2PtReport(PtReport po, Map map) throws Exception {
        if (null != map) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            po.setDuration(Long.parseLong(map.get("DURATION").toString()));
            po.setGroupId(groupId);
            try {
                po.setStart(new Timestamp(sdf.parse(map.get("START_TIME").toString()).getTime()));
                po.setEnd(new Timestamp(sdf.parse(map.get("END_TIME").toString()).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            po.setUser(map.get("M_USER").toString());
        } else{
            throw new Exception("Some Data didn't ready!");
        }
    }

    /**
     * mock test
     *
     * @param script
     * @return
     */
    private PtReport mock(String script) {
        PtReport po = new PtReport();
        po.setDuration(10);
        po.setUser("MurexBo");
        po.setScript(script);
        po.setStart(new Timestamp(System.currentTimeMillis()));
        po.setVolume(1000);
        po.setEnd(new Timestamp(System.currentTimeMillis()));
        po.setGroupId(groupId);
        po.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return po;
    }

    private PtReport createReport(HashMap hashMap, String processName) throws ParseException {
        PtReport pr = new PtReport();
        long duration = Long.valueOf((String) hashMap.get("Duration"));

        pr.setGroupId(groupId);
        pr.setScript(processName);
        pr.setStart(DateUtil.string2Time((String) hashMap.get("Start")));
        pr.setEnd(DateUtil.string2Time((String) hashMap.get("End")));
        pr.setDuration(duration/1000);
        pr.setVolume(Long.valueOf((String) hashMap.get("Volume")));
        pr.setUser("Sail");
        pr.setCreateTime(new Timestamp(currentTime));
        pr.setFlag(Integer.valueOf((String) hashMap.get("Flag")).intValue());

        return pr;
    }
}
