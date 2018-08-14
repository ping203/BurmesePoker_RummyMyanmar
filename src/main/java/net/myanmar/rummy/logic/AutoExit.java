/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

/**
 *
 * @author hoangchau
 */
public class AutoExit {
    private String evt;
    private boolean reg;

    public AutoExit(String evt, boolean reg) {
        this.evt = evt;
        this.reg = reg;
    }

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
        this.evt = evt;
    }

    public boolean isReg() {
        return reg;
    }

    public void setReg(boolean reg) {
        this.reg = reg;
    }
}
