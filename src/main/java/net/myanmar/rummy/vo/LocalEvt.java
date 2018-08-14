/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.vo;

import java.io.Serializable;
import net.myanmar.rummy.logic.TurnStatus;

/**
 *
 * @author hoangchau
 */
public class LocalEvt implements Serializable{
    private  String evt;
    private int pid;
    private TurnStatus turnStatus;
    private int cardId ;

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
        this.evt = evt;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public TurnStatus getTurnStatus() {
        return turnStatus;
    }

    public void setTurnStatus(TurnStatus turnStatus) {
        this.turnStatus = turnStatus;
    }

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
    
    
}
