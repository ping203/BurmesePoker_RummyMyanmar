/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic.votransfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.myanmar.rummy.logic.Player;

/**
 *
 * @author UserXP
 */
public class PlayFinishTrans implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4487975102768572211L;
	private String N;
    private long M;
    private long AG;
    private int S;
    private long TotalAG;
    private int diem;
    private boolean active;
    private List<List<Integer>> arrCard = new ArrayList<>();
    private int lastCard;
    
    public PlayFinishTrans() {
        N = "";
        M = 0;
        AG = 0;
    }

    public PlayFinishTrans(Player op) {
        N = op.getUsername();
        M = 0;
        AG = 0;
    }

    

    public int getS() {
        return S;
    }

    public void setS(int s) {
        S = s;
    }

    /**
     * Get the value of AG
     *
     * @return the value of AG
     */
    public long getAG() {
        return AG;
    }

    /**
     * Set the value of AG
     *
     * @param AG new value of AG
     */
    public void setAG(long AG) {
        this.AG = AG;
    }

    /**
     * Get the value of M
     *
     * @return the value of M
     */
    public long getM() {
        return M;
    }

    /**
     * Set the value of M
     *
     * @param M new value of M
     */
    public void setM(long M) {
        this.M = M;
    }

    /**
     * Get the value of N
     *
     * @return the value of N
     */
    public String getN() {
        return N;
    }

    /**
     * Set the value of N
     *
     * @param N new value of N
     */
    public void setN(String N) {
        this.N = N;
    }

    public long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(long totalAG) {
        TotalAG = totalAG;
    }

    public void setDiem(int diem) {
        this.diem = diem;
    }

    public int getDiem() {
        return diem;
    }

    public List<List<Integer>> getArrCard() {
        return arrCard;
    }

    public void setArrCard(List<List<Integer>> arrCard) {
        this.arrCard = arrCard;
    }

	public int getLastCard() {
		return lastCard;
	}

	public void setLastCard(int lastCard) {
		this.lastCard = lastCard;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
