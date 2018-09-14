/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import net.myanmar.rummy.logic.Card;
import net.myanmar.rummy.logic.CheckCard;
import static net.myanmar.rummy.logic.CheckCard.checkCoupleDoc;
import static net.myanmar.rummy.logic.CheckCard.checkCoupleNgang;
import static net.myanmar.rummy.logic.CheckCard.checkDiscardAble;
import static net.myanmar.rummy.logic.CheckCard.checkDoc;
import static net.myanmar.rummy.logic.CheckCard.checkGroup;
import static net.myanmar.rummy.logic.CheckCard.checkNgang;
import static net.myanmar.rummy.logic.CheckCard.countStraight;
import net.myanmar.rummy.logic.ConstrainDiscard;
import net.myanmar.rummy.logic.SortCardAsc;
import net.myanmar.rummy.logic.TurnStatus;
import net.myanmar.rummy.utils.GameUtil;

/**
 *
 * @author hoangchau
 */
public class test {

    private static final int MAX_CARD_GROUP = 4;
    private static final int MIN_CARD_GROUP = 3;

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

    private static void removeAllListElement(List PaList, List ChiList) {
        List list = new ArrayList(ChiList);
        Iterator iterator = PaList.iterator();

        while (iterator.hasNext()) {
            Object next = iterator.next();
            /// kiểm tra từng lá bài của player có tồn tại trong từng bộ phỏm của list ko
            if (list.contains(next)) {
                list.remove(list.indexOf(next));
                iterator.remove();
            }

        }
    }

    private static List<List<Integer>> groupRankJoker(List<Integer> listCards) {
        List<List<Integer>> ranks = new ArrayList<>();

        List<Integer> jokers = getJoker(listCards);

        List<Card> cards = new ArrayList<>();

        if (jokers.size() > 0) {
            for (Integer i : listCards) {
                cards.add(new Card(i));
            }

            Map<Integer, List<Integer>> m = getMapRank(cards);

            ///sap xep cac gia tri cua card tu lon den be
            Map<Integer, List<Integer>> treeMap = new TreeMap<Integer, List<Integer>>(
                    new Comparator<Integer>() {

                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2.compareTo(o1);
                }

            });

            treeMap.putAll(m);

            for (Map.Entry<Integer, List<Integer>> entry : treeMap.entrySet()) {
                List<Integer> value = entry.getValue();
                if (jokers.isEmpty()) {
                    break;
                }

                if (value.size() < 2) {
                    continue;
                }

                value.add(jokers.get(0));
                ranks.add(value);
                jokers.remove(0);
            }

        }

        return ranks;
    }

    private static List<Integer> getJoker(List<Integer> lcs) {

        List<Integer> listCards = new ArrayList<>(lcs);

        List<Integer> is = new ArrayList<>();

        for (Iterator<Integer> iterator = listCards.iterator(); iterator.hasNext();) {
            Integer next = iterator.next();
            if (new Card(next).isJocker()) {
                is.add(next);
                iterator.remove();
            }
        }

        return is;
    }

    private static List<List<Integer>> getSuitCanAppendJoker(List<Card> cards) {
        Map<Integer, List<Integer>> map = getMapSuit(cards);/// map các lá bài có gía trị số được liệt kê theo key: laf chất của lá bài

        List<List<Integer>> suits = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            List<Integer> list = entry.getValue();

            //A23
            /// sap xep list theo thứ tự để  xuống dưới so sánh chi cần so sanh 2 card liên tiếp nhau
            /// list đc sắp xếp từ K->Q->J->10->9->...->2->A
            /// tính cho trường hợp A23
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    ///check
                    return (new Card(o2).getN() - 1) % 13 - (new Card(o1).getN() - 1) % 13;
                }

            });

            int i = 0;

            while (i < list.size() - 1) {
                List<Integer> is = new ArrayList<>();
                is.add(list.get(i));

                int j = i + 1;
                boolean chk = false;

                while (j < list.size()) {
                    /// 2 lá liền kề hoặc 2 lá cách nhau 1 giá trị để có thể add thêm lá joker
                    if (new Card(is.get(is.size() - 1)).getN() % 13 - 1 == new Card(list.get(j)).getN() % 13
                            || new Card(is.get(is.size() - 1)).getN() % 13 - 2 == new Card(list.get(j)).getN() % 13) {
                        is.add(list.get(j));
                    }
                    // neu có 2 lá trong is
                    if (is.size() == MIN_CARD_GROUP - 1) {

                        for (int k = is.size() - 1; k >= 0; k--) {
                            list.remove(list.indexOf(is.get(k)));
                        }

                        suits.add(is);

                        chk = true;
                        break;
                    }

                    j++;
                }

                if (!chk) {
                    i++;
                }

            }

            //QKA
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

                    return new Card(o2).getN() - new Card(o1).getN();
                }

            });
            i = 0;

            while (i < list.size() - 1) {
                List<Integer> is = new ArrayList<>();
                is.add(list.get(i));

                int j = i + 1;
                boolean chk = false;

                while (j < list.size()) {
                    if (new Card(is.get(is.size() - 1)).getN() - 1 == new Card(list.get(j)).getN()
                            || new Card(is.get(is.size() - 1)).getN() - 2 == new Card(list.get(j)).getN()) {
                        is.add(list.get(j));
                    }
                    if (is.size() == MIN_CARD_GROUP - 1) {

                        for (int k = is.size() - 1; k >= 0; k--) {
                            list.remove(list.indexOf(is.get(k)));
                        }

                        suits.add(is);

                        chk = true;
                        break;
                    }

                    j++;
                }

                if (!chk) {
                    i++;
                }

            }
        }

        return suits;
    }

    private static List<List<Integer>> groupSuitJoker(List<Integer> listCards) {
        List<List<Integer>> suits = new ArrayList<>();

        List<Integer> jokers = getJoker(listCards);/// lay ra nhưng con joker trong list card

        List<Card> cards = new ArrayList<>();

        if (jokers.size() > 0) {
            for (Integer i : listCards) {
                cards.add(new Card(i));
            }

            List<List<Integer>> is = getSuitCanAppendJoker(cards);/// la ra danh sach- mỗi phần tử của is có 2 lá mà có thể thêm đc con joker

            //for (int j = is.size() - 1; j >= 0; j--) {
            for (int j = 0; j < is.size(); j++) {

                List<Integer> i = is.get(j);

                ///kiem tra xem con joker nao trong list card của player
                System.out.println("is: " + is);
                if (jokers.isEmpty()) {
                    break;
                }
                /// thêm joker vao mỗi cặp để tạo thành 1 phỏm
                i.add(jokers.get(0));
                suits.add(i);
                jokers.remove(0);
            }

        }

        return suits;

    }

    public static List<List<Integer>> group(List<Integer> listCards, int numStraight) {

        List<List<Integer>> suits = groupSuit(listCards);

        for (List<Integer> list : suits) {
            removeAllListElement(listCards, list);

        }

        List<List<Integer>> suitsJoker = groupSuitJoker(listCards);

        for (List<Integer> list : suitsJoker) {
            removeAllListElement(listCards, list);
        }

        suits.addAll(suitsJoker);

        List<List<Integer>> ranks = groupRank(listCards);

        for (List<Integer> list : ranks) {
            removeAllListElement(listCards, list);

        }

        List<List<Integer>> ranksJoker = groupRankJoker(listCards);

        for (List<Integer> list : ranksJoker) {
            removeAllListElement(listCards, list);

        }
        ranks.addAll(ranksJoker);

        //tìm cây lẻ ghép vào phỏm 3
        Iterator<Integer> it = listCards.iterator();
        while (it.hasNext()) {
            int num = it.next();
            Card card = new Card(num);
            if (card.isJocker()) {
                continue;
            }

            for (List<Integer> rank : ranks) {
                if (rank.size() < MIN_CARD_GROUP && rank.size() >= MAX_CARD_GROUP) {
                    continue;
                }
                if (new Card(rank.get(0)).getN() == card.getN()) {
                    rank.add(num);
                    it.remove();
                    break;
                }
            }

            for (List<Integer> suit : suits) {
                if (suit.size() < MIN_CARD_GROUP && suit.size() >= MAX_CARD_GROUP) {
                    continue;
                }

                List<Integer> temp = new ArrayList<>(Arrays.asList(suit.get(0), suit.get(0) - 1, suit.get(0) - 2));

                if (temp.get(0) + 1 == num || temp.get(temp.size() - 1) - 1 == num || (num == 13 && temp.get(temp.size() - 1) == 1)) {
                    suit.add(num);
                    it.remove();
                    break;
                }
            }

        }

        //tìm phỏm dọc có 4 lá tách ra ghép vào cặp ngang
        List<Card> cards = new ArrayList<>();

        for (Integer i : listCards) {
            cards.add(new Card(i));
        }

        Map<Integer, List<Integer>> map = getMapRank(cards);

        for (Iterator<Map.Entry<Integer, List<Integer>>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<Integer, List<Integer>> entry = iterator.next();
            List<Integer> list = entry.getValue();
            Integer key = entry.getKey();

            if (list.size() == 2) {
                for (List<Integer> is : suits) {
                    if (is.size() == MIN_CARD_GROUP) {
                        continue;
                    }
                    Collections.sort(is, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return new Card(o1).getN() % 13 - new Card(o2).getN() % 13;
                        }

                    });

                    int end = is.size() - 1;

                    if (is.contains(60) || is.contains(61)) {
                        end = end - 1;
                    }

                    if (new Card(is.get(0)).getN() == new Card(list.get(0)).getN()
                            || new Card(is.get(end)).getN() == new Card(list.get(0)).getN()) {
                        int index = new Card(is.get(0)).getN() == new Card(list.get(0)).getN() ? 0 : end;
                        removeAllListElement(listCards, list);
                        list.add(is.get(index));
                        ranks.add(list);
                        is.remove(index);
                    }

                }
            }

        }

        //ghép 2 cây joker vào 1 cây lẻ
        List<Integer> jokers = getJoker(listCards);
        removeAllListElement(listCards, jokers);
        while (jokers.size() >= 2 && listCards.size() > 0) {

            List<Integer> l = new ArrayList<>(Arrays.asList(listCards.get(0), jokers.get(0), jokers.get(1)));
            suits.add(l);
            jokers.remove(0);
            jokers.remove(1);
            listCards.remove(0);
        }
        listCards.addAll(jokers);

        //ghép joker vào phỏm 3
        Iterator<Integer> it1 = listCards.iterator();
        int k = 0;
        while (it1.hasNext()) {
            int num = it1.next();
            Card card = new Card(num);
            if (!card.isJocker()) {
                continue;
            }

            for (List<Integer> rank : ranks) {
                if (rank.size() >= MIN_CARD_GROUP && rank.size() < MAX_CARD_GROUP) {
                    rank.add(num);
                    it1.remove();
                    k++;
                    break;
                }
            }
            if (k > 0) {
                continue;
            }
            System.out.println("it11111: " + card);
            for (List<Integer> suit : suits) {
                if (suit.size() >= MIN_CARD_GROUP && suit.size() < MAX_CARD_GROUP) {
                    suit.add(num);
                    it1.remove();
                    break;
                }
            }

        }

        List<List<Integer>> list = new ArrayList<>();

        System.out.println("suits: " + suits);
        System.out.println("ranks: " + ranks);
        System.out.println("lítcard: " + listCards);

        list.addAll(suits);
        list.addAll(ranks);
        if (!listCards.isEmpty()) {
            list.add(listCards);
        }

        System.out.println("list: " + list);

        return list;
    }

    public static Map<Integer, List<Integer>> getMapRank(List<Card> cards) {
        List<Integer> is = new ArrayList<>();
        for (Card card : cards) {
            is.add(card.getI());
        }
        Collections.sort(is, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                return (new Card(o1).getN() - 1) % 13 - (new Card(o2).getN() - 1) % 13;
            }

        });
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < cards.size(); i++) {
            ///bo ra con joker
            if (cards.get(i).getN() == 60 || cards.get(i).getN() == 61) {
                continue;
            }
            List<Integer> list;
            int key = cards.get(i).getN(); /// key là so của lá
            if (map.containsKey(key)) { /// xem trong map da có so đó
                list = map.get(key);/// lấy ra giá trị số của các lá
                list.add(cards.get(i).getI());
            } else {
                list = new ArrayList<>();
                list.add(cards.get(i).getI());  /// lấy ra giá trị số của lá
                map.put(key, list);             /// đưa giá trị số vào map đúng với key
            }

        }
        return map;
    }

    private static List<List<Integer>> groupRank(List<Integer> listCards) {
        List<List<Integer>> ranks = new ArrayList<>();
        List<Card> cards = new ArrayList<>();

        for (Integer i : listCards) {
            cards.add(new Card(i));
        }

        Map<Integer, List<Integer>> map = getMapRank(cards); /// tra về map vs key là so của lá   
        /// value là cac la co gia tri so do

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            List<Integer> value = entry.getValue();

            int i = 0;
            while (i < value.size() - 1) {
                List<Integer> l = new ArrayList<>();
                l.add(value.get(i));
                int j = i + 1;

                while (j < value.size()) {

                    Card cardi = new Card(value.get(i));
                    Card cardj = new Card(value.get(j));

                    if (cardi.getN() == cardj.getN()) {/// nếu cùng giá trị

                        l.add(value.get(j));
                    }

                    if (l.size() == 3) {
                        break;
                    }
                    j++;
                }

                if (l.size() == 3) {
                    ranks.add(l);
                    i = i + l.size();
                } else {
                    i++;
                }

            }
        }

        return ranks;

    }

    private static List<List<Integer>> groupRank1(List<Integer> listCards) {
        List<List<Integer>> ranks = new ArrayList<>();
        List<Card> cards = new ArrayList<>();

        for (Integer i : listCards) {
            cards.add(new Card(i));
        }

        Map<Integer, List<Integer>> map = getMapRank(cards); /// tra về map vs key là so của lá   
        /// value là cac la co gia tri so do

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            List<Integer> value = entry.getValue();

            if (value.size() == 3) {
                ranks.add(value);
            }

            if (value.size() > 3) {
                List<Integer> l = new ArrayList<>();
                for (Integer integer : value) {
                    l.add(integer);
                    if (l.size() == 3) {
                        break;
                    }
                }
                ranks.add(l);
            }

        }

        return ranks;

    }

    ///
    private static List<List<Integer>> groupSuit(List<Integer> listCards) {
        List<Card> cards = new ArrayList<>();

        for (Integer i : listCards) {
            cards.add(new Card(i));
        }

        Map<Integer, List<Integer>> map = getMapSuit(cards);/// map các lá bài có gía trị số được liệt kê theo key: laf chất của lá bài

        List<List<Integer>> suits = new ArrayList<>();

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            List<Integer> list = entry.getValue(); /// lay ra giá trị số của các lá theo chất

            //A23
            /// sap xep list theo thứ tự để  xuống dưới so sánh chi cần so sanh 2 card liên tiếp nhau
            /// list đc sắp xếp từ Q->J->A->K
            /// tính cho trường hợp A23
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

                    return ((new Card(o2).getN() % 13) + 1) - ((new Card(o1).getN() % 13) + 1);
                }

            });
            int i = 0;

            while (i < list.size() - 1) {
                List<Integer> is = new ArrayList<>();
                is.add(list.get(i));

                int j = i + 1;
                boolean chk = false;

                while (j < list.size()) {
                    /// is.get(is.size() - 1)) lá bài vừa thêm vào gần nhất de ss voi la trong list
                    if (new Card(is.get(is.size() - 1)).getN() % 13 - 1 == new Card(list.get(j)).getN() % 13) {/// kiem tra xem có phai là 2 lá có giá trị liền nhau
                        /// (vd: 4♣ và 5♣)
                        is.add(list.get(j));
                    }
                    /// is có tối thiểu 3 lá tạo thành 1 phỏm
                    if (is.size() == 3) {

                        for (int k = is.size() - 1; k >= 0; k--) {
                            list.remove(list.indexOf(is.get(k)));
                        }

                        suits.add(is);/// them vao suits bộ phỏm thỏa mãn

                        chk = true;
                        break;
                    }

                    j++;
                }

                if (!chk) {
                    i++;
                }

            }

            //QKA
            /// sap xep list theo thứ tự để  xuống dưới so sánh chi cần so sanh 2 card liên tiếp nhau
            ///list dc sap xep A->K->Q->2
            /// tính cho trường hợp QKA
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

                    return new Card(o2).getN() - new Card(o1).getN();
                }

            });
            i = 0;

            while (i < list.size() - 1) {
                List<Integer> is = new ArrayList<>();
                is.add(list.get(i));

                int j = i + 1;
                boolean chk = false;

                while (j < list.size()) {
                    if (new Card(is.get(is.size() - 1)).getN() - 1 == new Card(list.get(j)).getN()) {
                        is.add(list.get(j));
                    }
                    if (is.size() == 3) {

                        for (int k = is.size() - 1; k >= 0; k--) {
                            list.remove(list.indexOf(is.get(k)));
                        }

                        suits.add(is);

                        chk = true;
                        break;
                    }

                    j++;
                }

                if (!chk) {
                    i++;
                }

            }

        }

        return suits;
    }

    ///
    private static Map<Integer, List<Integer>> getMapSuit(List<Card> cards) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < cards.size(); i++) {
            /// neu la joker thi cho tiep tuc
            if (cards.get(i).getN() == 60 || cards.get(i).getN() == 61) {
                continue;
            }
            List<Integer> list;
            int key = cards.get(i).getS();/// lay ra chất cua la hien tai theo int (1,2,3,4)
            /// dung chất theo int để làm key trong map
            /// nhung card có cùng chất thì lưu trong cùng key ở map
            /// kiem tra xem chất đó có trong map hay chua
            if (map.containsKey(key)) {
                list = map.get(key);    /// lay ra cac card co cùng chất
                list.add(cards.get(i).getI());/// them card trong cards vao list
            } else {
                ///neu chua co chất bai của lá đang xet trong map
                list = new ArrayList<>();
                list.add(cards.get(i).getI()); /// lay ra giá trị số của lá bài
                map.put(key, list);             ///them vao map
            }

        }
        ///map(key: là các chất của lá bài
        ///value: là giá trị số của từng lá bài theo từng key
        return map;
    }

    //
    public static boolean IsTakeCard(List<Card> lsCard, Card cardCheck, int mark) { //Kiem tra quan bai co an duoc khong
        try {
            //Check phom doc
            if (cardCheck.getI() == 60 || cardCheck.getI() == 61) {
                return true;
            }
            Collections.sort(lsCard, new SortCardAsc());
            for (int i = 0; i < lsCard.size() - 1; i++) {
                if (lsCard.get(i).getS() != cardCheck.getS()) {/// kiem tra co cùng chất ko
                    continue;
                }
                for (int j = i + 1; j < lsCard.size(); j++) {
                    if (lsCard.get(j).getS() != cardCheck.getS()) {/// kiem tra tiep la cung chat
                        continue;
                    }
                    ///check < i < j
                    if (lsCard.get(i).getN() == cardCheck.getN() + 1 && lsCard.get(j).getN() == cardCheck.getN() + 2) {
                        return true;
                    }
                    /// nam giữa
                    if (lsCard.get(i).getN() == cardCheck.getN() - 1 && lsCard.get(j).getN() == cardCheck.getN() + 1) {
                        return true;
                    }
                    ///to nhất
                    if (lsCard.get(i).getN() == cardCheck.getN() - 2 && lsCard.get(j).getN() == cardCheck.getN() - 1) {
                        return true;
                    }
                    ///A23
                    if (cardCheck.getN() == 14 && lsCard.get(i).getN() == 2 && lsCard.get(j).getN() == 3) {
                        return true;
                    }

                    ///23A
                    if (cardCheck.getN() == 2 && lsCard.get(i).getN() == 3 && lsCard.get(j).getN() == 14) {
                        return true;
                    }
                    ///32A
                    if (cardCheck.getN() == 3 && lsCard.get(i).getN() == 2 && lsCard.get(j).getN() == 14) {
                        return true;
                    }
                }
            }
            //Check phom ngang
            /// các lá có gía trị = nhau và chất #
            for (int i = 0; i < lsCard.size() - 1; i++) {
                if (lsCard.get(i).getN() != cardCheck.getN()) {/// có cùng giá trị
                    continue;
                }
                if (lsCard.get(i).getS() == cardCheck.getS()) {/// cùng chất
                    continue;
                }/// cùng giá trị và khác chất
                for (int j = i + 1; j < lsCard.size(); j++) {
                    if (lsCard.get(j).getN() != cardCheck.getN()) {/// ko có lá nào = card check
                        continue;
                    }
                    if (lsCard.get(j).getS() == cardCheck.getS()) { /// cùng chất
                        continue;
                    }
                    if (lsCard.get(j).getS() == lsCard.get(i).getS()) { /// i va j cung chat
                        continue;
                    }
//					Logger.getLogger("RummyHandler").info("==>Check An phom ngang:" + cardCheck.getN() + "-" + lsCard.get(i).getN() + "-" + lsCard.get(j).getN());
                    return true;
                }
            }
            //Random ty le de tim ca doc
            int rd = (new Random()).nextInt(100);
            if ((mark > 5000 && rd > 60) || (mark > 500 && rd > 70) || (mark <= 500 && rd > 80)) {
//				Logger.getLogger("RummyHandler").info("==>Check An Ca:" + mark + "-" + rd);
                for (int i = 0; i < lsCard.size(); i++) {
                    if (lsCard.get(i).getS() != cardCheck.getS()) {/// ko cung chất
                        continue;
                    }
//					Logger.getLogger("RummyHandler").info("==>Check An Ca:" + cardCheck.getN() + "-" + lsCard.get(i).getN());
                    if (lsCard.get(i).getN() == cardCheck.getN() + 1 || lsCard.get(i).getN() == cardCheck.getN() - 1
                            || lsCard.get(i).getN() == cardCheck.getN() + 2 || lsCard.get(i).getN() == cardCheck.getN() - 2) {
                        return true;
                    }
                    if (cardCheck.getN() == 14 && (lsCard.get(i).getN() == 2 || lsCard.get(i).getN() == 3)) {
                        return true;
                    }
                    if ((cardCheck.getN() == 2 || cardCheck.getN() == 3) && lsCard.get(i).getN() == 14) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception

        }
        return false;
    }

    private boolean checkDiscardAble(int cardId, int playerId, int next, List<ConstrainDiscard> constrainDiscard) {

        for (ConstrainDiscard cd : constrainDiscard) {

            // check đánh cây bài vừa ăn
            if (cd.getCardId() == cardId && cd.getPlayerTakecard() == playerId && !cd.isJustTakePlace()) {
                return false;
            }

            //check đánh cây bài có số trùng với cây bài trước đó đã đánh cho người kia ăn.
            ///check trong list ConstrainDiscard xem co quan bai trung so vs cardId
            ///&& la day co phai do playerId da tung danh ra hay ko
            ///&& la day co phai do nguoi kia da an hay ko
            ///&& la day da dc next danh ra chua
            if (cd.getPlayerTakecard() == next && cd.getPlayerDiscard() == playerId && new Card(cardId).getN() == new Card(cd.getCardId()).getN() && cd.isRequired()) {
                return false;
            }

        }

        return true;
    }

    private static boolean checkCouple(int iCardCheck, List<Integer> iListCard) {

        List<Integer> listTmp = new ArrayList<>(iListCard);
        //System.out.println("size1: " + listTmp.size());
        listTmp.remove(listTmp.indexOf(iCardCheck));
        //System.out.println("size2: " + listTmp.size());
        Card cardCheck = new Card(iCardCheck);
        System.out.println("CardCheck: " + cardCheck.toString());
        List<Card> listCard = new ArrayList<>();
        for (int i = 0; i < listTmp.size(); i++) {
            listCard.add(new Card(listTmp.get(i)));

        }

        for (Card card : listCard) {
            if (card.getS() != cardCheck.getS()) {
                continue;
            }

            if (card.getN() + 1 == cardCheck.getN() || card.getN() + 2 == cardCheck.getN()
                    || card.getN() - 1 == cardCheck.getN() || card.getN() - 2 == cardCheck.getN()) {
                //System.out.println("Couple lien nhau: " + cardCheck.toString());
                return true;
            }

            if (cardCheck.getN() == 14 && card.getN() == 2 || cardCheck.getN() == 14 && card.getN() == 3) {
                //System.out.println("check 2");
                return true;
            }
            if (cardCheck.getN() == 2 && card.getN() == 14 || cardCheck.getN() == 3 && card.getN() == 14) {
                //System.out.println("check 3");
                return true;
            }

        }

        for (Card card : listCard) {
            if (card.getN() == cardCheck.getN()) {
                System.out.println("check 4");
                return true;
            }
        }

        return false;
    }

    public static int checkCardToDis(List<Card> listCard) {

        List<Integer> is = new ArrayList<>();
        for (Card card : listCard) {
            is.add(card.getI());
        }
        Collections.sort(is, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                return (new Card(o1).getN() - 1) % 13 - (new Card(o2).getN() - 1) % 13;
            }

        });
        int cardDis = is.get(0);
        List<List<Integer>> lsphom = CheckCard.group(is);

        for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
            List<Integer> list = lsphom.get(i1);
            System.out.println("Phom1: ");
            for (Integer integer : list) {
                Card c2 = new Card(integer);
                System.out.println("\tCard1: " + c2.toString());
            }
            if (!checkGroup(list)) {

                System.out.println("not group");
                for (int i = 0; i < list.size(); i++) {

                    System.out.println("Checkkkkkk: " + new Card(list.get(i)));
                    if (!CheckCard.checkCoupleDoc(list.get(i), list) && !CheckCard.checkCoupleNgang(list.get(i), list)) {
                        //System.out.println("Not couple");
                        if (list.get(i) == 60 || list.get(i) == 61) {
                            continue;
                        }
                        System.out.println("Card dis1: " + new Card(cardDis).toString());
                        cardDis = list.get(i);
                        return cardDis;

                    }
                }
                for (int i = 0; i < list.size(); i++) {

                    cardDis = list.get(i);
                    System.out.println("Card dis2: " + cardDis);
                    return cardDis;

                }
            } else {
                if (list.size() == 4) {
                    //System.out.println("Card dis3: " + cardDis);
                    cardDis = list.get(0);
                }

            }
        }
        ///ko tim dc la bai nao thoa man
        ///tach rank 3 nho nhat
        for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
            List<Card> cardPhom = new ArrayList<>();
            List<Integer> list = lsphom.get(i1);
            for (int i = 0; i < list.size(); i++) {
                cardPhom.add(new Card(list.get(i)));
            }
            if (checkNgang(cardPhom)) {
                cardDis = list.get(0);
                return cardDis;
            }
        }
        ///ko co rank 3 la
        ///tach suit nho nhat
        for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
            List<Card> cardPhom = new ArrayList<>();
            List<Integer> list = lsphom.get(i1);
            for (int i = 0; i < list.size(); i++) {
                cardPhom.add(new Card(list.get(i)));
            }
            ///sap xep de danh ra quan bai lon nhat
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

                    return (new Card(o2).getN() - 1) % 13 - (new Card(o1).getN() - 1) % 13;
                }

            });
            if (checkDoc(cardPhom)) {
                cardDis = list.get(0);
                return cardDis;
            }
        }

        return cardDis;
    }

    public static int checkCardToDis1(List<Card> listCard, int numberStraight) {

        List<Integer> is = new ArrayList<>();
        for (Card card : listCard) {
            is.add(card.getI());
        }
        Collections.sort(is, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                return (new Card(o1).getN() - 1) % 13 - (new Card(o2).getN() - 1) % 13;
            }

        });
        int cardDis = is.get(0);
        List<List<Integer>> lsphom = CheckCard.group(is);

        if (countStraight(lsphom) < numberStraight) {
            for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
                ///loai bo nhung phom doc da co
                List<Integer> list = lsphom.get(i1);
                if (CheckCard.checkStraight(list)) {
                    removeAllListElement(is, list);
                }
            }
            for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
                List<Integer> list = lsphom.get(i1);
                if (!checkGroup(list)) {

                    for (int i = 0; i < list.size(); i++) {

                        if (!checkCoupleDoc(list.get(i), is)) {

                            if (list.get(i) == 60 || list.get(i) == 61) {
                                continue;
                            }
                            cardDis = list.get(i);
                            return cardDis;

                        }
                    }
                    for (int i = 0; i < list.size(); i++) {

                        cardDis = list.get(i);
                        System.out.println("Card dis2: " + cardDis);
                        return cardDis;

                    }
                }
            }
        } else {

            for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
                List<Integer> list = lsphom.get(i1);
               
                for (Integer integer : list) {
                    Card c2 = new Card(integer);
                   
                }
                if (!checkGroup(list)) {

                    System.out.println("not group");
                    for (int i = 0; i < list.size(); i++) {

                        System.out.println("Checkkkkkk: " + new Card(list.get(i)));
                        if (!CheckCard.checkCoupleDoc(list.get(i), list) && !CheckCard.checkCoupleNgang(list.get(i), list)) {
                            //System.out.println("Not couple");
                            if (list.get(i) == 60 || list.get(i) == 61) {
                                continue;
                            }
                            System.out.println("Card dis1: " + new Card(cardDis).toString());
                            cardDis = list.get(i);
                            return cardDis;

                        }
                    }
                    for (int i = 0; i < list.size(); i++) {

                        cardDis = list.get(i);
                        System.out.println("Card dis2: " + cardDis);
                        return cardDis;

                    }
                } else {
                    if (list.size() == 4) {
                        //System.out.println("Card dis3: " + cardDis);
                        cardDis = list.get(0);
                    }

                }
            }
        }
        ///ko tim dc la bai nao thoa man
        ///tach rank 3 nho nhat
        for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
            List<Card> cardPhom = new ArrayList<>();
            List<Integer> list = lsphom.get(i1);
            for (int i = 0; i < list.size(); i++) {
                cardPhom.add(new Card(list.get(i)));
            }
            if (checkNgang(cardPhom)) {
                cardDis = list.get(0);
                return cardDis;
            }
        }
        ///ko co rank 3 la
        ///tach suit nho nhat
        for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
            List<Card> cardPhom = new ArrayList<>();
            List<Integer> list = lsphom.get(i1);
            for (int i = 0; i < list.size(); i++) {
                cardPhom.add(new Card(list.get(i)));
            }
            ///sap xep de danh ra quan bai lon nhat
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

                    return (new Card(o2).getN() - 1) % 13 - (new Card(o1).getN() - 1) % 13;
                }

            });
            if (checkDoc(cardPhom)) {
                cardDis = list.get(0);
                return cardDis;
            }
        }

        return cardDis;
    }

    ///
    public static void main(String[] args) {

        List<Integer> listInt = new ArrayList<>();

        List<Integer> listTmp = new ArrayList<>();
        List<Integer> listTmp1 = new ArrayList<>();
        listTmp1.add(12);
        listTmp1.add(13);
        listTmp1.add(26);
//        boolean checkCouple = CheckCard.checkCoupleDoc(12, listTmp1);
//        System.out.println("Check couple: " + checkCouple);
//        System.out.println("**************************************");
//        listTmp.add(60);
//        listTmp.add(61);
//        listTmp.add(23); 
//        listTmp.add(25);
//        listTmp.add(23);
//        listTmp.add(49);
//        listTmp.add(49);
        ///listTest
        //33,32,31,30],[5,18,44],[20,20,46],[10,23,23
        //27 2♦,27 2♦,28 3♦,3 4♠,42 4♥,44 6♥,45 7♥,7 8♠,7 8♠,9 10♠,22 10♣,22 10♣,35 10♦,40 2♥
        listTmp.add(3);
        listTmp.add(1);
        listTmp.add(5);
        listTmp.add(42);
        listTmp.add(29);
//        listTmp.add(44);
//        listTmp.add(45);
//        listTmp.add(7);
//        listTmp.add(7);
//        listTmp.add(9);
//        listTmp.add(22);
//        listTmp.add(22);
//        listTmp.add(35);
//        listTmp.add(40);
        //listTmp.add(17);
//        listTmp.add(36);
//        listTmp.add(24);

///
        List<Card> list = genCard(2);
        for (int i = 0; i < list.size(); i++) {

            //listInt.add(list.get(i).getI());
//            if (listInt.size() == 13) {
//                break;
//            }
//            listTmp.add(list.get(i).getI());
//            if (listTmp.size() == 14) {
//                break;
//            }
            Card card = list.get(i);
            int key = card.getS();
            int n = card.getN();
            listInt.add(card.getI());
            System.out.println("card: " + card.getI() + ": " + list.get(i) + "\nkey: " + key + "\nN: " + n);

        }

        System.out.println("=====================================");

        List<ConstrainDiscard> constrainDiscard = new ArrayList<>();
        ConstrainDiscard cd1 = new ConstrainDiscard(61, 100004537, 100003656);
        ConstrainDiscard cd2 = new ConstrainDiscard(11, 100004537, 100003656);

        ConstrainDiscard cd3 = new ConstrainDiscard(32, 100003656, 100004537);
        //ConstrainDiscard cd4 = new ConstrainDiscard(10, 100004537, 100003656);
        constrainDiscard.add(cd1);
        constrainDiscard.add(cd2);
        constrainDiscard.add(cd3);
        //constrainDiscard.add(cd4);
        List<Card> listCardCheck = new ArrayList<>();
        for (int i = 0; i < listTmp.size(); i++) {
            Card card = new Card(listTmp.get(i));
            listCardCheck.add(card);
        }

        //System.out.println("check take card: " + CheckCard.IsTakeCard(listCardCheck, new Card(42), 10));
        //tìm cây lẻ ghép vào phỏm 3
        ///tim la bai lon nhat co the ghep dc
        List<Card> cardSort = new ArrayList<>();
        for (Integer cardInt : listTmp) {
            Card c = new Card(cardInt);
            if (cardInt == 11) {
                c.setIsTaked(true);
            }
            cardSort.add(c);
        }
        Collections.sort(listTmp, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                return (new Card(o2).getN() - 1) % 13 - (new Card(o1).getN() - 1) % 13;
            }

        });
        int cardInt = checkCardToDis1(listCardCheck, 3);
        Card c = new Card(cardInt);
        System.out.println("Card Dis: " + c.toString());

        List<List<Integer>> group = CheckCard.group(listTmp);
        int countSanh = 0;
        int fund = 0;
        for (int i = 0; i < group.size(); i++) {
            List<Integer> lst = group.get(i);
            List<Card> listCard = new ArrayList<>();
            System.out.println("Phom: ");
            boolean checkGroup = CheckCard.checkGroup(lst);
            boolean checkValiGroup = CheckCard.checkCardTakePlaceInGroup(listCardCheck, lst, constrainDiscard, 100003656);
            System.out.println("Check group: " + checkGroup + " ---- " + checkValiGroup);
            if (!CheckCard.checkGroup(lst) || !CheckCard.checkCardTakePlaceInGroup(listCardCheck, lst, constrainDiscard, 100003656)) {
                System.out.println("1111111111111111111111111");
            }
            Collections.sort(lst, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

                    return (new Card(o2).getN() - 1) % 13 - (new Card(o1).getN() - 1) % 13;
                }

            });
            for (int j = 0; j < lst.size(); j++) {
                Card c1 = new Card(lst.get(j));
                listCard.add(c1);
                System.out.println("\tCard: " + c1.toString());
            }
            if (CheckCard.checkNgang(listCard)) {
                System.out.println("Check Ngang");
            }
            if (CheckCard.checkDoc(listCard)) {
                System.out.println("Check doc");
            }
        }
        boolean checkIsTakeCard = CheckCard.IsTakeCard(cardSort, new Card(10), 10000, constrainDiscard, 100003656);
        System.out.println("Check Card is Take: " + checkIsTakeCard);
//            boolean check = CheckCard.checkGroup(lst);
//            //System.out.println("Check : " + check);
//            boolean checkPhomDoc = CheckCard.checkDoc(listCard);
//            if (CheckCard.checkDoc(listCard) && CheckCard.checkGroup(lst)) {
//                countSanh++;
//            }
//
//            //System.out.println("Check phom doc: " + checkPhomDoc);
//            if (!CheckCard.checkGroup(lst)) {
//                for (Integer i1 : lst) {
//                    Card c = new Card(i1);
//                    if (!c.isJocker()) {
//                        //player.setFund(player.getFund() - c.calc());
//                        fund += c.calc();
//                    }
//                }
//            }
//        }
//        System.out.println("Fund: " + fund);
//        //System.out.println("Count sanh: " + countSanh);
//        boolean checkU = CheckCard.checkDeclare(listCardCheck, 2);
//        System.out.println("check u: " + checkU);
//        int countStraigh = CheckCard.countStraight(group);
//        System.out.println("Count sanh 2: " + countStraigh);
        System.out.println("=======================================");
        System.out.println("***************************************");
        System.out.println("size: " + listTmp.size());

        Card card1 = new Card(3);
        System.out.println("char: " + card1.toString());
        System.out.println("xxx: " + card1.getI());
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        List<Integer> listTest = new ArrayList<>();
        listTest.add(1);
        listTest.add(2);
        listTest.add(3);
        listTest.add(4);
        System.out.println("After");
        for (int i = 0; i < listTest.size(); i++) {
            System.out.println("\tTest: " + listTest.get(i));

        }
        listTest.remove(0);
        System.out.println("Before");
        for (int i = 0; i < listTest.size(); i++) {
            System.out.println("\tTest: " + listTest.get(i));

        }
    }
}
