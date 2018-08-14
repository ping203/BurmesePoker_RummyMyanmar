/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.myanmar.rummy.vo;

/**
 *
 * @author Admin
 */

import java.io.Serializable;

public class UAGPlayer implements Serializable {

    public UAGPlayer(){
    	N = "";
    	AG = 0;
    }
    public UAGPlayer(String name, long ag){
    	N = name;
    	AG = ag;
    }
    private String N;
    private long AG;
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

}