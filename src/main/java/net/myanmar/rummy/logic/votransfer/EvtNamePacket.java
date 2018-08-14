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
public class EvtNamePacket implements Serializable{

    public EvtNamePacket(String evt,String Name){
        this.evt = evt;
        this.Name = Name;
    }
    private String evt;
    private String Name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return Name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String Name) {
        this.Name = Name;
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
