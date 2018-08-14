/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.vo;

/**
 *
 * @author UserXP
 */
public class LogPlayer {

	public LogPlayer(int uid , int iwinm , int iwin, int dev, int sc){
        UserId = uid;
        iWinMark = iwinm;
        iWin = iwin;
        Dev = dev ;
        source = sc ; 
    }
    private int UserId;
    private int iWinMark;
    private int iWin;
    private int Dev; //105 => Web Zing, 107 => Mobile
    private int source ; //1 - LQ, 2 - 68Blue, 3-Dautruong

    public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}
    public int getDev() {
		return Dev;
	}

	public void setDev(int dev) {
		this.Dev = dev;
	}

    /**
     * Get the value of iWin
     *
     * @return the value of iWin
     */
    public int getIWin() {
        return iWin;
    }

    /**
     * Set the value of iWin
     *
     * @param iWin new value of iWin
     */
    public void setIWin(int iWin) {
        this.iWin = iWin;
    }

    /**
     * Get the value of iWinMark
     *
     * @return the value of iWinMark
     */
    public int getIWinMark() {
        return iWinMark;
    }

    /**
     * Set the value of iWinMark
     *
     * @param iWinMark new value of iWinMark
     */
    public void setIWinMark(int iWinMark) {
        this.iWinMark = iWinMark;
    }

    /**
     * Get the value of UserId
     *
     * @return the value of UserId
     */
    public int getUserId() {
        return UserId;
    }

    /**
     * Set the value of UserId
     *
     * @param UserId new value of UserId
     */
    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

}
