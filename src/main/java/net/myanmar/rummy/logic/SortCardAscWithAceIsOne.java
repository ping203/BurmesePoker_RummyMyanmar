/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import java.util.Comparator;

/**
 *
 * @author hoangchau
 */
public class SortCardAscWithAceIsOne implements Comparator<Card>{


    @Override
    public int compare(Card o1, Card o2) {
        
        if (o1.getN() > o2.getN()) {
            if(o1.getN() == 14) return -1;
            return 1;
        } else if (o1.getN() == o2.getN()) {
            if (o1.getS() > o2.getS()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
    
}