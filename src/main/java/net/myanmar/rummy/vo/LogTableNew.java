package net.myanmar.rummy.vo;

import java.util.ArrayList;
import java.util.List;

public class LogTableNew {
    private int gid; //gameiD    
    private int mark; //mark- muc cuoc ban
    private List<ObjLogPlayer> lsP;
    private int tid;
    private long revenue;
    private long time;
    
    //void LogTable(int iLevel, int iRevenue, java.sql.Date dtTime, int gameid, int source) ;

    public LogTableNew(int tid,int gid, int mark) {
        this.tid = tid;
        this.gid = gid;
        this.mark = mark;
        this.lsP = new ArrayList<ObjLogPlayer>();
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    
    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public List<ObjLogPlayer> getLsPl() {
        return lsP;
    }

    public void setLsPl(List<ObjLogPlayer> lsPl) {
        this.lsP = lsPl;
    }

	public long getRevenue() {
		return revenue;
	}

	public void setRevenue(long revenue) {
		this.revenue = revenue;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
