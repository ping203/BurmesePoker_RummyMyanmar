/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import net.myanmar.rummy.logic.Card;
import net.myanmar.rummy.logic.CheckCard;
import net.myanmar.rummy.logic.ConstrainDiscard;
import net.myanmar.rummy.logic.TurnStatus;
import net.myanmar.rummy.utils.GameUtil;

/**
 *
 * @author hoangchau
 */
public class test {

    private final List<ConstrainDiscard> constrainDiscard = new ArrayList<>();

    public List<ConstrainDiscard> getConstrainDiscard() {
        return constrainDiscard;
    }

    private void addConstrainDiscard(int cardId, int playerId, TurnStatus ts) {

        if (ts == TurnStatus.DISCARD) {

            if (!constrainDiscard.isEmpty() && constrainDiscard.get(constrainDiscard.size() - 1).getPlayerTakecard() == 0) {
                constrainDiscard.remove(constrainDiscard.size() - 1);
            }
            constrainDiscard.add(new ConstrainDiscard(cardId, playerId, 0));

            Iterator<ConstrainDiscard> i = constrainDiscard.iterator();
            while (i.hasNext()) {
                ConstrainDiscard s = i.next();

                if (!s.isJustTakePlace()) {
                    s.setJustTakePlace(true);
                }
                if (new Card(s.getCardId()).getN() == new Card(cardId).getN() && s.getPlayerTakecard() == playerId) {
                    i.remove();
                }

            }

        } else {
            ConstrainDiscard cd = constrainDiscard.get(constrainDiscard.size() - 1);

            if (cd.getCardId() == cardId) {
                cd.setPlayerTakecard(playerId);
                cd.setJustTakePlace(false);
            }
        }

    }

    private boolean checkDiscardAble(int cardId, int playerId, int next) {
        for (ConstrainDiscard cd : constrainDiscard) {

            if (cd.getCardId() == cardId && cd.getPlayerTakecard() == playerId && !cd.isJustTakePlace()) {
                return false;
            }

            if (cd.getPlayerTakecard() == next && cd.getPlayerDiscard() == playerId && new Card(cardId).getN() == new Card(cd.getCardId()).getN()) {
                return false;
            }

        }

        return true;
    }

    public static List<Card> genCard(int type) {
        try {
            List<Card> arrReturn = new ArrayList<>();
            List<Card> arrCard = new ArrayList<>();

            for (int k = 0; k < type; k++) {
                int num = 0;
                arrCard.add(new Card(60, 60, 60));
                for (int i = 1; i < 5; i++) {
                    for (int j = 2; j < 15; j++) {
                        num++;
                        Card c = new Card(i, j, num);
                        arrCard.add(c);
                    }
                }
                arrCard.add(new Card(61, 61, 61));
            }

            int numCard = 54 * type;

            for (int i = 0; i < numCard; numCard--) {
                int j = GameUtil.random.nextInt(numCard);
                arrReturn.add(arrCard.get(j));
                arrCard.remove(j);
            }

            return arrReturn;
        } catch (Exception e) {
            //LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public void ss() {

    }

    public static void main(String[] args) {

//        test t = new test();
//        t.addConstrainDiscard(2, 1, TurnStatus.DISCARD);
//        t.addConstrainDiscard(2, 2, TurnStatus.TAKECARD);
//        t.addConstrainDiscard(28, 2, TurnStatus.DISCARD);
//
//        System.out.println(t.getConstrainDiscard());
//        System.out.println(t.checkDiscardAble(15, 1, 2));
//    List<List<Integer>> integers = new ArrayList<>();
//    
//    integers.add(new ArrayList<>(Arrays.asList(48,9,35)));
//    integers.add(new ArrayList<>(Arrays.asList(39,37,38)));
//    integers.add(new ArrayList<>(Arrays.asList(61,43,44)));
//    integers.add(new ArrayList<>(Arrays.asList(22,23,24)));
//    integers.add(new ArrayList<>(Arrays.asList(16)));
//       
//            System.out.println(CheckCard.countStraight(integers));
        List<Card> list = genCard(1);
        List<Integer> listInt = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Card card = list.get(i);
            int key = card.getS();
            int n = card.getN();
            //listInt.add(card.getI());
            //System.out.println("card: " + i + ": " + list.get(i) + "\nkey: " + key + "\nN: " + n);

        }
        listInt.add(13);
        listInt.add(12);
        listInt.add(1);
        listInt.add(2);
        listInt.add(60);
        listInt.add(61);
        Collections.sort(listInt, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                return new Card(o2).getN() % 13 - new Card(o1).getN() % 13;
            }

        });
        
//         Collections.sort(listInt, new Comparator<Integer>() {
//                @Override
//                public int compare(Integer o1, Integer o2) {
//
//                    return new Card(o2).getN() - new Card(o1).getN();
//                }
//
//            });
        for (int i = 0; i < listInt.size(); i++) {
            Card card = new Card(listInt.get(i));
            int key = card.getS();
            int n = card.getN();
            //listInt.add(card.getI());
            System.out.println("card: " + i + ": " + card.toString() + "\nkey: " + key + "\nN: " + n);

        }
    }
}
