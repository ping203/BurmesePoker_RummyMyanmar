/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author UserXP
 */
public class TableInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5881906525788363900L;

	public TableInfo() {
        ArrP = new ArrayList<>();
    }
    private String N;
    private Integer M;
    private List<ItemPlayer> ArrP;
    private int Id;
    private Integer T;
    private Integer V;
    private Integer AG;
    private Integer S;
    private boolean issd;

    public void setIssd(boolean issd) {
        this.issd = issd;
    }

    public boolean getIssd() {
        return issd;
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

    public String getN() {
        return N;
    }

    public Integer getT() {
        return T;
    }

    public void setT(Integer t) {
        T = t;
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

    public List<ItemPlayer> getArrP() {
        return ArrP;
    }

    public void setArrP(List<ItemPlayer> arrP) {
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

}
