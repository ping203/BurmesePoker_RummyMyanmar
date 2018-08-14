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
public enum TurnStatus {
    TAKECARD,
    DISCARD,
    DECLARE,
    CONFIRM_DECLARE,
    FINISH_DECLARE,
    BOT_TAKE_CARD_PLACE,
    BOT_NOTIDECLARE,
    NULL
}