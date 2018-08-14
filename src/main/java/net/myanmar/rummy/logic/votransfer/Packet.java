/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic.votransfer;

import java.io.Serializable;

/**
 *
 * @author UserXP
 */
public class Packet implements Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 7747289897540185961L;

	public Packet(String evt,String data){
	        this.evt = evt;
	        this.data = data;
	}  
    
    private String evt;
    private String data;
    /**
     * Get the value of data
     *
     * @return the value of data
     */
    public String getData() {
        return data;
    }

    /**
     * Set the value of data
     *
     * @param data new value of data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Get the value of evt
     *
     * @return the value of evt
     */
    public String getEvt() {
        return evt;
    }

    /**
     * Set the value of evt
     *
     * @param evt new value of evt
     */
    public void setEvt(String evt) {
        this.evt = evt;
    }
}
