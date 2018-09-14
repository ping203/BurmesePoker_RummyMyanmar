/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.myanmar.rummy.vo.RemiCardU;

import org.apache.log4j.Logger;

/**
 *
 * @author hoangchau
 */
public class CheckCard {

    private static final int MAX_CARD_GROUP = 4;
    private static final int MIN_CARD_GROUP = 3;

    private static final Logger LOGGER = Logger.getLogger(RummyBoard.class);

    /// listCards la list card cua player + listCardDesk[i]
    /**
     * @param listCards = 14 la
     */
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
            /// list đc sắp xếp từ K->Q->J->10->9->...->2->A
            /// tính cho trường hợp A23
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {

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
                    /// is.get(is.size() - 1)) lá bài vừa thêm vào gần nhất de ss voi la trong list
                    if (new Card(is.get(is.size() - 1)).getN() % 13 - 1 == new Card(list.get(j)).getN() % 13) {/// kiem tra xem có phai là 2 lá có giá trị liền nhau
                        /// (vd: 4♣ và 5♣)
                        is.add(list.get(j));
                    }
                    /// is có tối thiểu 3 lá tạo thành 1 phỏm
                    if (is.size() == MIN_CARD_GROUP) {

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
            ///list dc sap xep Joker->A->K->Q->...->2
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
                    if (is.size() == MIN_CARD_GROUP) {

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

    /**
     * tim cac bo phom chua joker
     */
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

    /**
     * tra ve list ms phan tu la 1 list co 3 phan tu co gia tri giong nhau ko
     * phan biet chat
     */
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

    /**
     * Taoj ra cac phom?
     */
    public static List<List<Integer>> group(List<Integer> listCards) {

        List<List<Integer>> suits = groupSuit(listCards);///lấy ra đc từng bộ có 3 lá có giá trị N liền nhau

        for (List<Integer> list : suits) {
            removeAllListElement(listCards, list); ///loai bỏ những lá bài trong listCards mà đã có trong suits để tìm các bộ tiếp theo

        }

        List<List<Integer>> suitsJoker = groupSuitJoker(listCards);/// tim bộ phỏm có chứa Joker

        for (List<Integer> list : suitsJoker) {
            removeAllListElement(listCards, list); /// loại bỏ đi những bộ phỏm trong listCard để tiếp tục vs những lá lẻ còn lại
        }

        suits.addAll(suitsJoker);///them suitsJoker vao suits ban dau

        List<List<Integer>> ranks = groupRank(listCards);///lay ra 3 la bai co phan so giong nhau

        for (List<Integer> list : ranks) {
            removeAllListElement(listCards, list);

        }

        List<List<Integer>> ranksJoker = groupRankJoker(listCards);///3 cay co chua joker

        for (List<Integer> list : ranksJoker) {
            removeAllListElement(listCards, list);

        }

        ranks.addAll(ranksJoker);///add vao ranks

        //tìm cây lẻ ghép vào phỏm 3
        ///tim la bai lon nhat co the ghep dc
//        List<Card> cardSort = new ArrayList<>();
//        for (Integer cardInt : listCards) {
//            Card c = new Card(cardInt);
//            cardSort.add(c);
//        }
//        Collections.sort(cardSort, new SortCardAsc());
//        listCards.clear();
//        for (int i = cardSort.size()-1; i >=0; i--) {
//            listCards.add(cardSort.get(i).getI());
//        }
        Collections.sort(listCards, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {

                return (new Card(o2).getN() - 1) % 13 - (new Card(o1).getN() - 1) % 13;
            }

        });

        Iterator<Integer> it = listCards.iterator();

        while (it.hasNext()) {
            int k1 = 0;
            int num = it.next();
            Card card = new Card(num);
            System.out.println("it: " + card.toString());
            if (card.isJocker()) {
                continue;
            }

            for (List<Integer> rank : ranks) {
                if (rank.size() < MIN_CARD_GROUP || rank.size() >= MAX_CARD_GROUP) {
                    continue;
                }
                if (new Card(rank.get(0)).getN() == card.getN()) {
                    rank.add(num);
                    k1++;
                    it.remove();

                    break;
                }
            }
            if (k1 > 0) {
                continue;
            }
            for (List<Integer> suit : suits) {
                if (suit.size() < MIN_CARD_GROUP || suit.size() >= MAX_CARD_GROUP) {
                    continue;
                }

                List<Integer> temp = new ArrayList<>(Arrays.asList(suit.get(0), suit.get(0) - 1, suit.get(0) - 2));
//                    System.out.println("c1: " + suit.get(0));
//                    System.out.println("c1: " + (suit.get(0) - 1));
//                    System.out.println("c1: " + (suit.get(0) - 2));

                if (new Card(suit.get(0)).getS() == card.getS()) {
//                        System.out.println("Check dong chat");
                    if (temp.get(0) + 1 == num || temp.get(temp.size() - 1) - 1 == num || (num == 13 && temp.get(temp.size() - 1) == 1)) {
                        suit.add(num);
                        it.remove();
                        break;
                    }
                }
            }

        }

        //tìm phỏm dọc có 4 lá tách ra ghép vào cặp ngang
        List<Card> cards = new ArrayList<>();

        for (Integer i : listCards) {
            cards.add(new Card(i));
        }
        ///lay ra cac la bai co phan so giong nhau
        Map<Integer, List<Integer>> map = getMapRank(cards);

        for (Iterator<Map.Entry<Integer, List<Integer>>> iterator = map.entrySet().iterator();
                iterator.hasNext();) {
            Map.Entry<Integer, List<Integer>> entry = iterator.next();
            List<Integer> list = entry.getValue();
            Integer key = entry.getKey();
            ///co 2 la gia tri giong nhau
            if (list.size() == 2) {
                for (List<Integer> is : suits) {
                    ///bo qua cac phom doc chi co 3 cay
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
                    ///neu phom doc chua joker
                    if (is.contains(60) || is.contains(61)) {
                        end = end - 1;
                    }
                    ///kiem tra so bang nhau
                    ///kiem tra con dau tien trong is hoac con cuoi cung trong is
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
        /// neu co tren 2 con joker va con cay le? trong listCards
        for (int i = 0; i < jokers.size(); i++) {
            System.out.println("joker: " + i + ": " + jokers.get(i));
        }
        while (jokers.size()
                >= 2 && listCards.size() > 0) {

            List<Integer> l = new ArrayList<>(Arrays.asList(listCards.get(0), jokers.get(0), jokers.get(1)));
            suits.add(l);
            System.out.println("After");
            for (int i = 0; i < jokers.size(); i++) {
                System.out.println("joker: " + i + ": " + jokers.get(i));
            }
            jokers.remove(0);
            jokers.remove(0);
            listCards.remove(0);
        }

        listCards.addAll(jokers);

        //ghép joker vào phỏm 3
        Iterator<Integer> it1 = listCards.iterator();

        while (it1.hasNext()) {
            int k = 0;
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
            for (List<Integer> suit : suits) {
                if (suit.size() >= MIN_CARD_GROUP && suit.size() < MAX_CARD_GROUP) {
                    suit.add(num);
                    it1.remove();
                    break;
                }
            }

        }

        List<List<Integer>> list = new ArrayList<>();

        list.addAll(suits);

        list.addAll(ranks);

        if (!listCards.isEmpty()) {

            list.add(listCards);
        }

        return list;
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

    /**
     * tra ve 1 map vs key la cac so của lá và value là các la co gia tri do
     */
    private static Map<Integer, List<Integer>> getMapRank(List<Card> cards) {
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

    /**
     * check phom trong listCard cua player
     */
    public static boolean checkGroup(List<Integer> list) {

        if (list.size() < MIN_CARD_GROUP || list.size() > MAX_CARD_GROUP) {
            return false;
        }

        List<Card> cards = new ArrayList<>();
        for (int integer : list) {
            Card card = new Card(integer);
            cards.add(card);
        }

        Collections.sort(cards, new SortCardAsc());
        if (cards.isEmpty()) {
            return true;
        }

//        if(cards.get(0).isJocker()) return true;
        boolean ngang = checkNgang(cards);
        if (ngang) {
            return ngang;
        }

        boolean thung = checkThung(cards);

        if (!thung) {
            return thung;
        }

        boolean doc = checkDoc(cards);

        return doc;
    }

    public static int countStraight(List<List<Integer>> lists) {
        int n = 0;
        for (List<Integer> list : lists) {
            if (checkStraight(list)) {
                n++;
            }
        }

        return n;
    }

    public static boolean checkStraight(List<Integer> list) {

        if (list.size() < MIN_CARD_GROUP || list.size() > MAX_CARD_GROUP) {
            return false;
        }
        List<Card> cards = new ArrayList<>();
        for (int integer : list) {
            Card card = new Card(integer);
            cards.add(card);
        }

        boolean thung = checkThung(cards);

        if (!thung) {
            return thung;
        }

        boolean doc = checkDoc(cards);

        return doc;
    }

    public static boolean checkNgang(List<Card> listCards) {
        boolean check = true;

        List<Card> cards = new ArrayList<>(listCards);
        for (int i = 0, j = i + 1; i < cards.size() - 1; i++, j++) {
            if (!(cards.get(i).getN() == cards.get(j).getN()
                    || cards.get(i).isJocker() || cards.get(j).isJocker())) {
                check = false;
                break;
            }
        }

        return check;
    }

    /**
     * check cac suit cung chat
     *
     * @return true - cung chat
     */
    private static boolean checkThung(List<Card> listCards) {
        boolean check = true;

        List<Card> cards = new ArrayList<>(listCards);
        for (int i = 0, j = i + 1; i < cards.size() - 1; i++, j++) {
            if (!(cards.get(i).getS() == cards.get(j).getS()
                    || cards.get(i).isJocker() || cards.get(j).isJocker())) {
                check = false;
                break;
            }
        }

        return check;
    }

    /**
     *
     */
    public static boolean checkDoc(List<Card> listCards) {

        List<Integer> nonJoker = new ArrayList<>();///chua gia tri cua card ko phai joker
        int numJoker = 0;
        for (Card card : listCards) {
            if (!card.isJocker()) {
                nonJoker.add(card.getN());
            } else {
                numJoker++;
            }
        }

        ///sap xep tu nho den lon
        Collections.sort(nonJoker, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }

        });

        ///tim kiem trong cac suits
        // the thay joker = la bat ky de ghep thanh 1 bo
        for (int i = 0; i < nonJoker.size() - 1; i++) {

            if (nonJoker.get(i) + 1 < nonJoker.get(i + 1)) {
                if (numJoker > 0) {
                    nonJoker.add(i + 1, nonJoker.get(i) + 1);
                    numJoker--;
                }
            }
        }

        while (numJoker > 0) {
            nonJoker.add(nonJoker.get(nonJoker.size() - 1) + 1);
            numJoker--;
        }

        boolean check = true;

        for (int i = 0, j = i + 1; i < nonJoker.size() - 1; i++, j++) {
            if (nonJoker.get(i) + 1 != nonJoker.get(j)) {
                check = false;
                break;
            }
        }

        if (check) {
            return check;
        }

        nonJoker.clear();
        numJoker = 0;
        for (Card card : listCards) {
            if (!card.isJocker()) {
                if (card.getN() == 14) {/// them con A
                    nonJoker.add(1);
                } else {
                    nonJoker.add(card.getN());
                }
            } else {
                numJoker++;
            }
        }

        Collections.sort(nonJoker, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }

        });

        for (int i = 0; i < nonJoker.size() - 1; i++) {
            if (nonJoker.get(i) + 1 < nonJoker.get(i + 1)) {
                if (numJoker > 0) {
                    nonJoker.add(i + 1, nonJoker.get(i) + 1);
                    numJoker--;
                }
            }
        }

        while (numJoker > 0) {
            nonJoker.add(nonJoker.get(nonJoker.size() - 1) + 1);
            numJoker--;
        }

        for (int i = 0, j = i + 1; i < nonJoker.size() - 1; i++, j++) {
            if (nonJoker.get(i) + 1 != nonJoker.get(j)) {
                return false;
            }
        }

        return true;

    }

    public static boolean valid(List<List<Integer>> lists, List<Card> arrCard) {
        List<Integer> cards = new ArrayList<>();
        List<Integer> groups = new ArrayList<>();
        for (Card card : arrCard) {
            cards.add(card.getI());
        }

        for (List<Integer> list : lists) {
            for (Integer integer : list) {
                groups.add(integer);
            }
        }

        if (cards.size() != groups.size()) {
            return false;
        }
        removeAllListElement(cards, groups);
        return cards.isEmpty();
    }

    /**
     * Check xem cardCheck co an dc ko
     *
     * @param lsCard : danh sach bai cua player
     * @param cardCheck: card ma player co an ko
     * @param constrainDiscard : list card da dc an
     */
    public static boolean IsTakeCard(List<Card> lsCard, Card cardCheck, int mark, List<ConstrainDiscard> constrainDiscard, int playerId) { //Kiem tra quan bai co an duoc khong
        try {
            //Check phom doc
            if (cardCheck.getI() == 60 || cardCheck.getI() == 61) {
                return true;
            }
            Collections.sort(lsCard, new SortCardAsc());
            for (int i = 0; i < lsCard.size() - 1; i++) {

                if (lsCard.get(i).getS() != cardCheck.getS() || lsCard.get(i).getIsTaked()) {/// kiem tra co cùng chất ko

                    continue;
                }

                for (int j = i + 1; j < lsCard.size(); j++) {
                    if (lsCard.get(j).getS() != cardCheck.getS() || lsCard.get(j).getIsTaked()) {
                        //System.out.println("check 1");
                        continue;
                    }
                    ///check < i < j
                    if (lsCard.get(i).getN() == cardCheck.getN() + 1 && lsCard.get(j).getN() == cardCheck.getN() + 2) {
                        System.out.println("check 1");
                        return true;
                    }
                    /// nam giữa
                    if (lsCard.get(i).getN() == cardCheck.getN() - 1 && lsCard.get(j).getN() == cardCheck.getN() + 1) {
                        System.out.println("check 12");
                        return true;
                    }
                    ///to nhất
                    if (lsCard.get(i).getN() == cardCheck.getN() - 2 && lsCard.get(j).getN() == cardCheck.getN() - 1) {
                        System.out.println("check 13");
                        return true;
                    }
                    ///A23
                    if (cardCheck.getN() == 14 && lsCard.get(i).getN() == 2 && lsCard.get(j).getN() == 3) {
                        System.out.println("check 14");
                        return true;
                    }

                    ///23A
                    if (cardCheck.getN() == 2 && lsCard.get(i).getN() == 3 && lsCard.get(j).getN() == 14) {
                        System.out.println("check 15");
                        return true;
                    }
                    ///32A
                    if (cardCheck.getN() == 3 && lsCard.get(i).getN() == 2 && lsCard.get(j).getN() == 14) {
                        System.out.println("check 16");
                        return true;
                    }
                }
            }
            //Check phom ngang
            /// các lá có gía trị = nhau và chất #
            for (int i = 0; i < lsCard.size() - 1; i++) {

                if (lsCard.get(i).getN() != cardCheck.getN()) {/// ko cùng giá trị
                    continue;
                }
                if (lsCard.get(i).getIsTaked()) {
                    continue;
                }
                if (lsCard.get(i).getS() == cardCheck.getS()) {/// cùng chất
                    continue;
                }/// cùng giá trị và khác chất
                for (int j = i + 1; j < lsCard.size(); j++) {
                    if (lsCard.get(j).getN() != cardCheck.getN()) {/// ko có lá nào = card check
                        continue;
                    }
                    if (lsCard.get(j).getIsTaked()) {
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
            ///check xem card nay da nam trong ca nao chua
            ///neu chua nam trong ca nao thi moi dc duyet

            int rd = (new Random()).nextInt(100);
            if ((mark > 5000 && rd > 60) || (mark > 500 && rd > 70) || (mark <= 500 && rd > 80)) {
//				Logger.getLogger("RummyHandler").info("==>Check An Ca:" + mark + "-" + rd);
                List<Integer> listInt = new ArrayList<>();
                for (Card card : lsCard) {
                    listInt.add(card.getI());
                }
                for (int i = 0; i < lsCard.size(); i++) {

                    if (lsCard.get(i).getS() != cardCheck.getS()) {/// ko cung chất
                        continue;
                    }
//			Logger.getLogger("RummyHandler").info("==>Check An Ca:" + cardCheck.getN() + "-" + lsCard.get(i).getN());
                    if (lsCard.get(i).getIsTaked()) {

                        continue;
                    }
                    if (checkCoupleDoc(lsCard.get(i).getI(), listInt)) {
                        continue;
                    }
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
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /*
        * lsCard bao gồm listCard của player và từng lá 1 trong listCardDesk = 14
        * numStraight  = 5 - playerNumber
    **/
    public static int CheckU(List<Card> lsCard, String username) {
        try {
            /// lay ra gia tri thực
            List<Integer> is = new ArrayList<>();
            for (Card card : lsCard) {
                is.add(card.getI());
            }

            List<List<Integer>> lsphom = CheckCard.group(is);/// danh sach cac phom co the ghep dc

            if (lsphom == null) {
                return 0;
            }

            int countNotGroup = 0;
            int cardId = 0;
            for (List<Integer> list : lsphom) {
                if (!checkGroup(list)) {
                    countNotGroup += list.size();
                    cardId = list.get(0);
                }
            }
            LOGGER.info("==>CheckU==>Full==>countNotGroup:" + username + "-" + countNotGroup);
            if (countNotGroup <= 1) {//Co lon nhat 1 con le ==> U duoc
                //Tim quan de gan cho Notideclare
                if (cardId != 0) //Con 1 con le
                {
                    return cardId;
                } else { //Tim trong bo bai 4 con de loc 1 con de danh
                    for (List<Integer> listTemp : lsphom) {
                        if (listTemp.size() > 3) {
                            cardId = listTemp.get(0);
                            for (int i = 0; i < listTemp.size(); i++) {
                                if (cardId > listTemp.get(i)) {
                                    cardId = listTemp.get(i);
                                }
                            }
                            break;
                        }
                    }
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * Kiem tra so sanh? co thoa man ko
     */
    public static boolean checkDeclare(List<Card> listCard, int numberStraight) {
        List<Integer> is = new ArrayList<>();
        for (Card card : listCard) {
            is.add(card.getI());
        }

        List<List<Integer>> lsphom = group(is);
        int countSanh = 0;
        for (int i = 0; i < lsphom.size(); i++) {

            List<Integer> lst = lsphom.get(i);
            List<Card> listCardGroup = new ArrayList<>();

            for (int j = 0; j < lst.size(); j++) {
                Card c = new Card(lst.get(j));
                listCardGroup.add(c);

            }

            if (checkDoc(listCardGroup) && checkGroup(lst)) {
                countSanh++;
            }

        }
        System.out.println("count sanh: " + countSanh);
        if (countSanh >= numberStraight) {

            return true;
        }

        return false;
    }

    /*
        * lsCard cua player
        *lsCardBoc: ls card desk
        *numStraight: so sanh?toi thieu can co = 5 - so luong nguoi choi
    **/
    public static int GetCardToAdd(List<Card> lsCard, String username, List<Card> lsCardBoc, int mark, boolean isBot, int vip, RemiCardU objU) {
        try {
            LOGGER.info("==>GetCardToAdd==>username:" + username + "-" + isBot + "-" + mark);
            Collections.sort(lsCard, new SortCardAsc());
            int percentTakePlace = 0;
            int percentJoker = 0;
            ///tao ra % lay cac cards theo cac tieu chi
            if (isBot) {
                if (mark > 20000) {
                    percentTakePlace = 60;
                    percentJoker = 10;
                } else if (mark > 10000) {
                    percentTakePlace = 55;
                    percentJoker = 8;
                } else if (mark > 5000) {
                    percentTakePlace = 50;
                    percentJoker = 5;
                } else if (mark > 1000) {
                    percentTakePlace = 40;
                    percentJoker = 5;
                } else if (mark > 100) {
                    percentTakePlace = 10;
                }
            } else {
                if (vip == 0 && mark < 500) {
                    percentTakePlace = 80;
                    percentJoker = 10;
                } else if (vip == 1 && mark < 1000) {
                    percentTakePlace = 30;
                    percentJoker = 5;
                }
            }
            LOGGER.info("==>GetCardToAdd==>GetU==>username:" + username + "-" + percentTakePlace + "-" + percentJoker);
            //Tim kiem con U duoc
            for (int i = 0; i < lsCardBoc.size(); i++) {
                List<Card> lsTemp = new ArrayList<>();
                for (int j = 0; j < lsCard.size(); j++) {
                    lsTemp.add(lsCard.get(j));
                }
                /// lsTemp = listCard cua player + listCardDesk[i]
                lsTemp.add(lsCardBoc.get(i));
                /// check xem la bai nao co the ù
                int cardId = CheckU(lsTemp, username);
                if (cardId != 0) {
                    objU.setiCard(cardId);
                    return i;
                }
            }
            LOGGER.info("==>GetCardToAdd==>GetTake==>username:" + username + "-" + percentTakePlace + "-" + percentJoker);
            //Tim kiem con an duoc
            if ((new Random()).nextInt(100) < percentTakePlace) { //Tim quan an duoc
                for (int i = 0; i < lsCardBoc.size(); i++) {
                    for (int j = 0; j < lsCard.size() - 1; j++) {
                        for (int j2 = j + 1; j2 < lsCard.size(); j2++) {
                            List<Card> lsCheck = new ArrayList<>();
                            lsCheck.add(lsCardBoc.get(i));
                            lsCheck.add(lsCard.get(j));
                            lsCheck.add(lsCard.get(j2));
                            if (checkNgang(lsCheck)) {
//								Logger.getLogger("RummyHandler").info("==>GetCardToAdd ==>Search Dc Pure:" + username + "-" + lsCheck.get(0).getN() + "-" + lsCheck.get(0).getS()) ;
                                return i;
                            } else if (checkThung(lsCheck)) {
                                if (checkDoc(lsCheck)) {
                                    return i;
                                }
                            }
                        }
                    }
                }
            }
            LOGGER.info("==>GetCardToAdd==>GetJoker==>username:" + username + "-" + percentTakePlace + "-" + percentJoker);
            //Random ty le lay Poker
            if ((new Random()).nextInt(100) < percentJoker) {
                for (int i = 0; i < lsCardBoc.size(); i++) {
                    if (lsCardBoc.get(i).isJocker()) {
                        return i;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error(e.getMessage(), e);
        }
        return 0;
    }

    public static int GetCardToRemove(List<Card> lsCard, String username, int cardIdPlace) {
        LOGGER.info("==>GetCardToRemove==>username:" + username);
        Collections.sort(lsCard, new SortCardAsc());
        try {
            List<Integer> is = new ArrayList<>();
            for (Card card : lsCard) {
                is.add(card.getI());
            }

            List<List<Integer>> lsphom = CheckCard.group(is);
            if (lsphom == null) {
                return -1;
            }
            LOGGER.info("==>GetCardToRemove==>username:" + username + "-" + lsphom.size());
            for (List<Integer> list : lsphom) {
                if (!checkGroup(list)) {
                    LOGGER.info("==>GetCardToRemove==>username:" + username + "-" + list.size());
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) < 60 && list.get(i) != cardIdPlace) {
                            return list.get(i);
                        }
                    }

                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error(e.getMessage(), e);
        }
        return -1;
    }

    /**
     * replace for List.removeAll() , don't remove duplicate element
     *
     * @param PaList
     * @param ChiList
     */
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

    /**
     * check cây bài có được đánh ra hay không
     *
     * @param cardId
     * @param playerId
     * @param next
     * @param constrainDiscard
     * @return false: card ko thoa man
     */
    public static boolean checkDiscardAble(int cardId, int playerId, int next, List<ConstrainDiscard> constrainDiscard) {

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

    public static boolean checkCoupleDoc(int iCardCheck, List<Integer> iListCard) {
        List<Integer> listTmp = new ArrayList<>(iListCard);

        listTmp.remove(listTmp.indexOf(iCardCheck));
        Card cardCheck = new Card(iCardCheck);
        List<Card> listCard = new ArrayList<>();
        for (int i = 0; i < listTmp.size(); i++) {
            listCard.add(new Card(listTmp.get(i)));
        }
        //listCard.remove(cardCheck);
        for (Card card : listCard) {
            if (card.getS() != cardCheck.getS()) {
                continue;
            }

            if (card.getN() + 1 == cardCheck.getN() || card.getN() + 2 == cardCheck.getN()
                    || card.getN() - 1 == cardCheck.getN() || card.getN() - 2 == cardCheck.getN()) {
                return true;
            }

            if (cardCheck.getN() == 14 && card.getN() == 2 || cardCheck.getN() == 14 && card.getN() == 3) {
                return true;
            }
            if (cardCheck.getN() == 2 && card.getN() == 14 || cardCheck.getN() == 3 && card.getN() == 14) {
                return true;
            }

        }
        return false;
    }

    public static boolean checkCoupleNgang(int iCardCheck, List<Integer> iListCard) {

        List<Integer> listTmp = new ArrayList<>(iListCard);

        listTmp.remove(listTmp.indexOf(iCardCheck));

        Card cardCheck = new Card(iCardCheck);
        List<Card> listCard = new ArrayList<>();
        for (int i = 0; i < listTmp.size(); i++) {
            listCard.add(new Card(listTmp.get(i)));
        }

        for (Card card : listCard) {
            if (card.getN() == cardCheck.getN()) {
                return true;
            }
        }
        return false;
    }

    /**
     * lay ra la bai dc phep danh ra
     */
    public static int checkCardToDis(List<Card> listCard, List<ConstrainDiscard> constrainDiscard, int playerId, int next) {

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
            List<Card> cardPhom = new ArrayList<>();
            List<Integer> list = lsphom.get(i1);
            for (int i = 0; i < list.size(); i++) {
                cardPhom.add(new Card(list.get(i)));
            }
            if (!checkGroup(list)) {

                for (int i = 0; i < list.size(); i++) {

                    if (!checkCoupleDoc(list.get(i), list) && !checkCoupleNgang(list.get(i), list)) {
                        if (checkDiscardAble(list.get(i), playerId, next, constrainDiscard)) {
                            if (list.get(i) == 60 || list.get(i) == 61) {
                                continue;
                            }
                            cardDis = list.get(i);
                            return cardDis;
                        }
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (checkDiscardAble(list.get(i), playerId, next, constrainDiscard)) {
                        cardDis = list.get(i);
                        return cardDis;
                    }
                }
            } else {
                if (list.size() == 4) {

                    if (checkDiscardAble(list.get(0), playerId, next, constrainDiscard)) {
                        cardDis = list.get(0);
                        return cardDis;
                    } else {
                        if (checkDiscardAble(list.get(3), playerId, next, constrainDiscard)) {
                            cardDis = list.get(3);
                            return cardDis;
                        }
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
                if (checkDiscardAble(cardDis, playerId, next, constrainDiscard)) {
                    return cardDis;
                }

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
                if (checkDiscardAble(cardDis, playerId, next, constrainDiscard)) {
                    return cardDis;
                }
            }
        }
        ///ko tim dc card nao thi card nao thoa man thi danh
        for (int i = 0; i < is.size(); i++) {
            if (checkDiscardAble(is.get(i), playerId, next, constrainDiscard)) {
                cardDis = is.get(i);
                break;
            }

        }
        return cardDis;
    }

    /**
     * Check 2 la bai an dc ko nam trong 1 phom
     *
     * @return true: listCard thoa man chi chua 1 la
     */
    public static boolean checkCardTakePlaceInGroup(List<Card> listCard, List<Integer> listCardGroup, List<ConstrainDiscard> constrainDiscard, int playerId) {
        int countCard = 0;
        int countcardChung = 0;
        List<ConstrainDiscard> tmp = new ArrayList<>(constrainDiscard);

        ///đếm số lượng lá bài đã ăn so với lá bài đã có trong listCard của player
//        for (int i = 0; i < listCard.size(); i++) {
//            for (int j = 0; j < tmp.size(); j++) {
//                if (listCard.get(i).getI() == tmp.get(j).getCardId() && tmp.get(j).getPlayerTakecard() == playerId){
//                    countcardChung++;
//                }
//            }
//        }     
        for (int i = 0; i < listCardGroup.size(); i++) {
            for (int j = 0; j < tmp.size(); j++) {
                if (tmp.get(j).getPlayerTakecard() == playerId
                        && tmp.get(j).getCardId() == listCardGroup.get(i)
                        && !checkNumberCardTakedPlace(listCard, tmp.get(j).getCardId(), constrainDiscard, playerId)) {
                    ///neu so luong card cua tmp.get(i) < so luong cua card do trong list cua player
                    /// thi van thoa man
                    countCard++;
                    tmp.remove(j);

                }
            }

        }

        if (countCard > 1) {
            return false;
        }
        return true;
    }

    /**
     * dem so lan xuat hien cua cardCheck trong card da an va trong listCard cua
     * player
     *
     * @return true: card cua player > card ma player da an
     */
    private static boolean checkNumberCardTakedPlace(List<Card> listCard, int cardCheck, List<ConstrainDiscard> constrainDiscard, int playerId) {
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < constrainDiscard.size(); i++) {
            ///so lan card dc an boi player        
            if (constrainDiscard.get(i).getPlayerTakecard() == playerId
                    && constrainDiscard.get(i).getCardId() == cardCheck) {
                count1++;
            }
        }
        if (count1 > 0) {
            ///so card da co trong list bai
            for (int k = 0; k < listCard.size(); k++) {
                if (cardCheck == listCard.get(k).getI()) {
                    count2++;
                }
            }
        }
        if (count2 <= count1) {
            return false;
        }
        return true;
    }

    public static int checkCardToDis1(List<Card> listCard, List<ConstrainDiscard> constrainDiscard, int playerId, int next, int numberStraight) {

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

        ///dem so luong phom doc thoa man chua
        ///neu chua du so luong phom doc
        ///thi ko danh ra nhung card co the tao thanh ca doc
        if (countStraight(lsphom) < numberStraight) {
            for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
                ///loai bo nhung phom doc da co
                List<Integer> list = lsphom.get(i1);
                if (checkStraight(list)) {
                    removeAllListElement(is, list);
                }
            }
            for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
                List<Integer> list = lsphom.get(i1);
                if (!checkGroup(list)) {

                    for (int i = 0; i < list.size(); i++) {

                        if (!checkCoupleDoc(list.get(i), is)) {
                            if (checkDiscardAble(list.get(i), playerId, next, constrainDiscard)) {
                                if (list.get(i) == 60 || list.get(i) == 61) {
                                    continue;
                                }
                                cardDis = list.get(i);
                                return cardDis;
                            }
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (checkDiscardAble(list.get(i), playerId, next, constrainDiscard)) {
                            cardDis = list.get(i);
                            return cardDis;
                        }
                    }
                }
            }
        } else {

            for (int i1 = lsphom.size() - 1; i1 >= 0; i1--) {
                List<Card> cardPhom = new ArrayList<>();
                List<Integer> list = lsphom.get(i1);
                for (int i = 0; i < list.size(); i++) {
                    cardPhom.add(new Card(list.get(i)));
                }
                if (!checkGroup(list)) {

                    for (int i = 0; i < list.size(); i++) {

                        if (!checkCoupleDoc(list.get(i), list) && !checkCoupleNgang(list.get(i), list)) {
                            if (checkDiscardAble(list.get(i), playerId, next, constrainDiscard)) {
                                if (list.get(i) == 60 || list.get(i) == 61) {
                                    continue;
                                }
                                cardDis = list.get(i);
                                return cardDis;
                            }
                        }
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (checkDiscardAble(list.get(i), playerId, next, constrainDiscard)) {
                            cardDis = list.get(i);
                            return cardDis;
                        }
                    }
                } else {
                    if (list.size() == 4) {

                        if (checkDiscardAble(list.get(0), playerId, next, constrainDiscard)) {
                            cardDis = list.get(0);
                            return cardDis;
                        } else {
                            if (checkDiscardAble(list.get(3), playerId, next, constrainDiscard)) {
                                cardDis = list.get(3);
                                return cardDis;
                            }
                        }

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
                if (checkDiscardAble(cardDis, playerId, next, constrainDiscard)) {
                    return cardDis;
                }

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
                if (checkDiscardAble(cardDis, playerId, next, constrainDiscard)) {
                    return cardDis;
                }
            }
        }
        ///ko tim dc card nao thi card nao thoa man thi danh
        for (int i = 0; i < is.size(); i++) {
            if (checkDiscardAble(is.get(i), playerId, next, constrainDiscard)) {
                cardDis = is.get(i);
                break;
            }

        }
        return cardDis;
    }

}
