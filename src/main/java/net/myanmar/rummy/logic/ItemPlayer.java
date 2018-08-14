/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import java.io.Serializable;

/**
 *
 * @author UserXP
 */
public class ItemPlayer implements Serializable {

    public ItemPlayer() {

    }
    private int id;
    private String N;
    private String Url;
    private long AG;
    private Integer LQ;
    private Integer VIP;
    private boolean isStart;
    private Integer IK;
    private String sIP;
    private Integer G;
    private Integer Av;
    private long FId;
    private long GId;
    private int UserType;
    private long TotalAG;
    private int timeToStart;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFId() {
        return FId;
    }

    public void setFId(long fId) {
        FId = fId;
    }

    public long getGId() {
        return GId;
    }

    public void setGId(long gId) {
        GId = gId;
    }

    public Integer getAv() {
        return Av;
    }

    public void setAv(Integer av) {
        Av = av;
    }

    public Integer getG() {
        return G;
    }

    public void setG(Integer g) {
        this.G = g;
    }

    public String getsIP() {
        return sIP;
    }

    public void setsIP(String sIP) {
        this.sIP = sIP;
    }

    public Integer getIK() {
        return IK;
    }

    public void setIK(Integer iK) {
        IK = iK;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public Integer getVIP() {
        return VIP;
    }

    public void setVIP(Integer vIP) {
        VIP = vIP;
    }

    public Integer getLQ() {
        return LQ;
    }

    public void setLQ(Integer lQ) {
        LQ = lQ;
    }

    /**
     * Get the value of isStart
     *
     * @return the value of isStart
     */
    public boolean isIsStart() {
        return isStart;
    }

    /**
     * Set the value of isStart
     *
     * @param isStart new value of isStart
     */
    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    /**
     * Get the value of Mark
     *
     * @return the value of Mark
     */
    public long getAG() {
        return AG;
    }

    /**
     * Set the value of Mark
     *
     * @param AG
     */
    public void setAG(long AG) {
        this.AG = AG;
    }

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int userType) {
        UserType = userType;
    }

    public long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(long totalAG) {
        TotalAG = totalAG;
    }

    public void setTimeToStart(int time) {
        this.timeToStart = time;
    }

    public int getTimeToStart() {
        return timeToStart;
    }
}
