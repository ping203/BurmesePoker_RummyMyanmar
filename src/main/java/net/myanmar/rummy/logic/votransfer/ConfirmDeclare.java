/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic.votransfer;

import net.myanmar.rummy.event.EVT;

/**
 *
 * @author hoangchau
 */
public class ConfirmDeclare {
    private String evt = EVT.CLIENT_CONFIRM_DECLARE;
    private boolean confirm;
    private String user;
    private String nextUser;

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
        this.evt = evt;
    }

    public String getNextUser() {
        return nextUser;
    }

    public void setNextUser(String nextUser) {
        this.nextUser = nextUser;
    }
    
    
    
}
