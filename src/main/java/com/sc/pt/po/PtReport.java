package com.sc.pt.po;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/17/15
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PtReport implements Serializable {
    private int id;
    private String groupId;
    private String script;
    private Timestamp start;
    private Timestamp end;
    private long duration;
    private long volume;
    private String user;
    private Timestamp createTime;
    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public PtReport(){}

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PtReport ptReport = (PtReport) o;

        if (duration != ptReport.duration) return false;
        if (flag != ptReport.flag) return false;
        if (id != ptReport.id) return false;
        if (volume != ptReport.volume) return false;
        if (createTime != null ? !createTime.equals(ptReport.createTime) : ptReport.createTime != null) return false;
        if (end != null ? !end.equals(ptReport.end) : ptReport.end != null) return false;
        if (groupId != null ? !groupId.equals(ptReport.groupId) : ptReport.groupId != null) return false;
        if (script != null ? !script.equals(ptReport.script) : ptReport.script != null) return false;
        if (start != null ? !start.equals(ptReport.start) : ptReport.start != null) return false;
        if (user != null ? !user.equals(ptReport.user) : ptReport.user != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "PtReport{" +
                "createTime=" + createTime +
                ", id=" + id +
                ", groupId='" + groupId + '\'' +
                ", script='" + script + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", duration=" + duration +
                ", volume=" + volume +
                ", user='" + user + '\'' +
                ", flag=" + flag +
                '}';
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (script != null ? script.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (volume ^ (volume >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + flag;
        return result;
    }
}
