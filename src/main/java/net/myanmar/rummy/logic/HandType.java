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
public class HandType {
    private Type type;
    private int value;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    
       
    public enum Type{
        RANK,
        SUIT,
        JOKER
    }
}


