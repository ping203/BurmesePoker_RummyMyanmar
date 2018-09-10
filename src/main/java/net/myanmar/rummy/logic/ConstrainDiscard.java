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
public class ConstrainDiscard {
    private int cardId;
    private int playerDiscard;
    private int playerTakecard;
    private boolean  justTakePlace = true;///card vua an
    private boolean required = true;

    public ConstrainDiscard(int cardId, int playerDiscard, int playerTakecard) {
        this.cardId = cardId;
        this.playerDiscard = playerDiscard;
        this.playerTakecard = playerTakecard;
    }

    public boolean isJustTakePlace() {
        return justTakePlace;
    }

    public void setJustTakePlace(boolean justTakePlace) {
        this.justTakePlace = justTakePlace;
    }

    
    
    

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getPlayerDiscard() {
        return playerDiscard;
    }

    public void setPlayerDiscard(int playerDiscard) {
        this.playerDiscard = playerDiscard;
    }

    public int getPlayerTakecard() {
        return playerTakecard;
    }

    public void setPlayerTakecard(int playerTakecard) {
        this.playerTakecard = playerTakecard;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    
    @Override
    public String toString() {
        return String.format("{cardId:%d,playerDiscard:%d,playerTakecard:%d,discardAble:%b, required:%b}", cardId, playerDiscard, playerTakecard, justTakePlace,required);
    }
    
   

    
    
    
    
}
