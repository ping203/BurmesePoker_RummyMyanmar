/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import com.google.gson.JsonObject;
/**
 *
 * @author UserXP
 */
public class TableVInfo implements Serializable {

    public TableVInfo() {
        ArrP = new ArrayList<>();
    }

    private String N;
    private Integer M;
    private List<ItemVPlayer> ArrP;
    //private int[] Arr;
    private int Id;
    private Integer T; //0 - Chia 2 quan bai, 1-Chia 3 quan
    private Integer V;
    private Integer AG;
    private Integer S;
    private String CN; //Nguoi choi hien tai
    private Integer CT; //So thoi gian con lai
    private Integer CA; //Current Action 0 - Boc, 1 - Danh, 2- Ha bai
    private long AGBuyIn;
    private long TotalAG;
    private int sizeNoc;
    private boolean notiDeclared;
    private List<Integer> cardTakeAble;
    
    private List<ConstrainDiscard> constrainDiscards = new ArrayList<>();

    public long getAGBuyIn() {
        return AGBuyIn;
    }

    public void setAGBuyIn(long AGBuyIn) {
        this.AGBuyIn = AGBuyIn;
    }

    public long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(long TotalAG) {
        this.TotalAG = TotalAG;
    }

    public Integer getCT() {
        return CT;
    }

    public void setCT(Integer cT) {
        CT = cT;
    }

    public Integer getCA() {
        return CA;
    }

    public void setCA(Integer cA) {
        CA = cA;
    }

    public String getCN() {
        return CN;
    }

    public void setCN(String cN) {
        CN = cN;
    }

    public Integer getS() {
        return S;
    }

    public void setS(Integer s) {
        S = s;
    }

    public Integer getV() {
        return V;
    }

    public void setV(Integer v) {
        V = v;
    }

    public Integer getAG() {
        return AG;
    }

    public void setAG(Integer aG) {
        AG = aG;
    }

    
    public Integer getT() {
        return T;
    }

    public void setT(Integer t) {
        T = t;
    }
//	public int[] getArr() {
//		return Arr;
//	}
//
//	public void setArr(int[] arr) {
//		Arr = arr;
//	}

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public Integer getM() {
        return M;
    }

    public void setM(Integer m) {
        M = m;
    }

    public List<ItemVPlayer> getArrP() {
        return ArrP;
    }

    public void setArrP(List<ItemVPlayer> arrP) {
        ArrP = arrP;
    }

    /**
     * Get the value of Id
     *
     * @return the value of Id
     */
    public int getId() {
        return Id;
    }

    /**
     * Set the value of Id
     *
     * @param Id new value of Id
     */
    public void setId(int Id) {
        this.Id = Id;
    }

    public int getSizeNoc() {
        return sizeNoc;
    }

    public void setSizeNoc(int size) {
        this.sizeNoc = size;
    }


    public void setNotiDeclared(boolean isNoti) {
        this.notiDeclared = isNoti;
    }

    public List<Integer> getCardTakeAble() {
        return cardTakeAble;
    }

    
    public void setCardTakeAble(List<Integer> cardTakeAble) {
        this.cardTakeAble = cardTakeAble;
    }

    public List<ConstrainDiscard> getConstrainDiscards() {
        return constrainDiscards;
    }

    public void setConstrainDiscards(List<ConstrainDiscard> constrainDiscards) {
        this.constrainDiscards = constrainDiscards;
    }
    
    
    
}
