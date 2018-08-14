/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author UserXP
 */
public class ItemVPlayer implements Serializable {

    public ItemVPlayer() {

    }
    private int id;
    private boolean A;
    private String N;
    private String Url;
    private long AG;
    private long AGC;
    private Integer LQ;
    private Integer VIP;
    private boolean isStart;
    private Integer IK;
    private List<int[]> Arr;
    private String sIP;
    private Integer G;
    private Integer Av;
    private long FId;
    private long GId;
    private short UserType;
    private long TotalAG;
    private boolean isBocBai;
    private boolean isDeclared;
    private int diemThua;
    private long agWin;
    private long agLose;

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

    public boolean isA() {
        return A;
    }

    public void setA(boolean a) {
        A = a;
    }

    public long getAGC() {
        return AGC;
    }

    public void setAGC(long aGC) {
        AGC = aGC;
    }

    public List<int[]> getArr() {
        return Arr;
    }

    public void setArr(List<int[]> arr) {
        Arr = arr;
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
     * @param Mark new value of Mark
     */
    public void setAG(long AG) {
        this.AG = AG;
    }

    public short getUserType() {
        return UserType;
    }

    public void setUserType(short userType) {
        UserType = userType;
    }

    public long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(long totalAG) {
        TotalAG = totalAG;
    }

    public boolean getIsBocBai() {
        return isBocBai;
    }

    public void setIsBocBai(boolean bocbai) {
        this.isBocBai = bocbai;
    }

    public boolean getIsDeclared() {
        return isDeclared;
    }

    public void setIsDeclared(boolean declared) {
        this.isDeclared = declared;
    }

    public int getDiemThua() {
        return diemThua;
    }

    public void setDiemThua(int diem) {
        this.diemThua = diem;
    }

    public long getAgWin() {
        return agWin;
    }

    public void setAgWin(long ag) {
        this.agWin = ag;
    }

    public long getAgLose() {
        return agLose;
    }

    public void setAgLose(long ag) {
        this.agLose = ag;
    }
}
