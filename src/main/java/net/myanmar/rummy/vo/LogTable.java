/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *
 * @author UserXP
 */
public class LogTable implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8734090722019516789L;
    
    private Date Time;
    private long SumMarkStart;
    private long SumMarkEnd;
    private int iLevel;
    private List<LogPlayer> ListPlayer = new ArrayList<LogPlayer>();
    private String[][] LogGame;
    private int tableid ;
    
    
    public int getTableid() {
		return tableid;
	}

	public void setTableid(int tableid) {
		this.tableid = tableid;
	}

	public String[][] getLogGame() {
		return LogGame;
	}

	public void setLogGame(String[][] logGame) {
		LogGame = logGame;
	}

	/**
     * Get the value of ListPlayer
     *
     * @return the value of ListPlayer
     */
    public List<LogPlayer> getListPlayer() {
        return ListPlayer;
    }

    /**
     * Set the value of ListPlayer
     *
     * @param ListPlayer new value of ListPlayer
     */
    public void setListPlayer(List<LogPlayer> ListPlayer) {
        this.ListPlayer = ListPlayer;
    }

    /**
     * Get the value of iLevel
     *
     * @return the value of iLevel
     */
    public int getILevel() {
        return iLevel;
    }

    /**
     * Set the value of iLevel
     *
     * @param iLevel new value of iLevel
     */
    public void setILevel(int iLevel) {
        this.iLevel = iLevel;
    }

    /**
     * Get the value of SumMarkEnd
     *
     * @return the value of SumMarkEnd
     */
    public long getSumMarkEnd() {
        return SumMarkEnd;
    }

    /**
     * Set the value of SumMarkEnd
     *
     * @param SumMarkEnd new value of SumMarkEnd
     */
    public void setSumMarkEnd(long SumMarkEnd) {
        this.SumMarkEnd = SumMarkEnd;
    }

    /**
     * Get the value of SumMarkStart
     *
     * @return the value of SumMarkStart
     */
    public long getSumMarkStart() {
        return SumMarkStart;
    }

    /**
     * Set the value of SumMarkStart
     *
     * @param SumMarkStart new value of SumMarkStart
     */
    public void setSumMarkStart(long SumMarkStart) {
        this.SumMarkStart = SumMarkStart;
    }

    /**
     * Get the value of Time
     *
     * @return the value of Time
     */
    public Date getTime() {
        return Time;
    }

    /**
     * Set the value of Time
     *
     * @param Time new value of Time
     */
    public void setTime(Date Time) {
        this.Time = Time;
    }

	public void reset(int mark, Date time, long sumMarkStart) {
		iLevel = mark;
		Time = time;
		SumMarkStart = sumMarkStart;
		SumMarkEnd = 0;
		ListPlayer.clear();
	}

}
