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
public enum TimeAction {
    TAKE_CARD(11000),
    DIS_CARD(11000), 
    READY(2000),
    DECLARE(22000), 
    CONFIRM_DECLARE(5000),
    DISCONNECT_ACTION(3000),
    START_COUNTDOWN(15000),
    BOT_PROCESS(3000),
    TIME_CHECK_GET_BOT(5000);
    
    private final int value;

    private TimeAction(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }

}