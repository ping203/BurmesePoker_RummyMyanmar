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

                    return new Card(o2).getN() % 13 - new Card(o1).getN() % 13;
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

    /// tạo ra 1 phỏm có chứa joker
    private static List<List<Integer>> groupSuitJoker(List<Integer> listCards) {
        List<List<Integer>> suits = new ArrayList<>();

        List<Integer> jokers = getJoker(listCards);/// lay ra nhưng con joker trong list card

        List<Card> cards = new ArrayList<>();

        if (jokers.size() > 0) {
            for (Integer i : listCards) {
                cards.add(new Card(i));
            }

            List<List<Integer>> is = getSuitCanAppendJoker(cards);/// la ra danh sach- mỗi phần tử của is có 2 lá mà có thể thêm đc con joker

            for (List<Integer> i : is) {
                ///kiem tra xem con joker nao trong list card của player
                if (jokers.isEmpty()) {
                    break;
                }
                /// thêm joker vao mỗi cặp bất kỳ để tạo thành 1 phỏm
                i.add(jokers.get(0));
                suits.add(i);
                jokers.remove(0);
            }

        }

        return suits;

    }

    private static List<List<Integer>> groupRank(List<Integer> listCards) {
        List<List<Integer>> ranks = new ArrayList<>();
        List<Card> cards = new ArrayList<>();

        for (Integer i : listCards) {
            cards.add(new Card(i));
        }

        Map<Integer, List<Integer>> map = getMapRank(cards); /// tra về map vs key là chất của lá   
                                                             /// value là giá trị số của các lá

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            List<Integer> value = entry.getValue();

            int i = 0;
            while (i < value.size() - 1) {
                List<Integer> l = new ArrayList<>();/// chứa các lá cùng chất và bằng nhau
                l.add(value.get(i));
                int j = i + 1;

                while (j < value.size()) {

                    Card cardi = new Card(value.get(i));
                    Card cardj = new Card(value.get(j));

                    if (cardi.getN() == cardj.getN()) {/// nếu cùng giá trị

                        l.add(value.get(j));
                    }

                    if (l.size() == MIN_CARD_GROUP) {
                        break;
                    }
                    j++;
                }

                if (l.size() == MIN_CARD_GROUP) {
                    ranks.add(l);
                    i = i + l.size();
                } else {
                    i++;
                }

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

            for (Map.Entry<Integer, List<Integer>> entry : m.entrySet()) {
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
            /// list đc sắp xếp từ JKR(61)->JKB(60)->Q->J->A->K
            /// tính cho trường hợp A23
            Collections.sort(list, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return new Card(o2).getN() % 13 - new Card(o1).getN() % 13;
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

    public static List<List<Integer>> group(List<Integer> listCards, int numStraight) {

        List<List<Integer>> suits = groupSuit(listCards);///lấy ra đc từng bộ có 3 lá có giá trị N liền nhau

        for (List<Integer> list : suits) {
            removeAllListElement(listCards, list); ///loai bỏ những lá bài trong listCards mà đã có trong suits để tìm các bộ tiếp theo

        }

        List<List<Integer>> suitsJoker = groupSuitJoker(listCards);/// tim bộ phỏm có chứa Joker

        for (List<Integer> list : suitsJoker) {
            removeAllListElement(listCards, list); /// loại bỏ đi những bộ phỏm trong listCard để tiếp tục vs những lá lẻ còn lại
        }

        suits.addAll(suitsJoker);

        List<List<Integer>> ranks = groupRank(listCards);///

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
        while(jokers.size() >= 2 && listCards.size() > 0){
            
            List<Integer> l = new ArrayList<>(Arrays.asList(listCards.get(0), jokers.get(0), jokers.get(1)));
            suits.add(l);
            jokers.remove(0);jokers.remove(1);
            listCards.remove(0);
        }
        listCards.addAll(jokers);
        
        //ghép joker vào phỏm 3
        Iterator<Integer> it1 = listCards.iterator();
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
                    break;
                }
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

    /// tra ve 1 map vs key la cac chất của lá và value là các giá trị số
    private static Map<Integer, List<Integer>> getMapRank(List<Card> cards) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getN() == 60 || cards.get(i).getN() == 61) {
                continue;
            }
            List<Integer> list;
            int key = cards.get(i).getN(); /// key là chất của lá
            if (map.containsKey(key)) { /// xem trong map có chất đó chưa
                list = map.get(key);/// lấy ra giá trị số của các lá có cùng chất
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

    private static boolean checkStraight(List<Integer> list) {
        
        
        if(list.size() < MIN_CARD_GROUP || list.size() > MAX_CARD_GROUP){
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

    private static boolean checkDoc(List<Card> listCards) {

        List<Integer> nonJoker = new ArrayList<>();
        int numJoker = 0;
        for (Card card : listCards) {
            if (!card.isJocker()) {
                nonJoker.add(card.getN());
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
                if (card.getN() == 14) {
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

    public static boolean IsTakeCard(List<Card> lsCard, Card cardCheck, int mark) { //Kiem tra quan bai co an duoc khong
        try {
            //Check phom doc
            if (cardCheck.getI() == 60) {
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
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    /*
        * lsCard bao gồm listCard của player và từng lá 1 trong listCardDesk
    **/
    public static int CheckU(List<Card> lsCard, String username, int numStraight) {
        try {
            /// lay ra gia tri thực
            List<Integer> is = new ArrayList<>();
            for (Card card : lsCard) {
                is.add(card.getI());
            }

            List<List<Integer>> lsphom = CheckCard.group(is, numStraight);/// danh sach cac phom co the ghep dc
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

    /*
        * lsCard cua player
        *lsCardBoc: ls card desk
        *numStraight = 5 - so luong nguoi choi
    **/
    public static int GetCardToAdd(List<Card> lsCard, String username, List<Card> lsCardBoc, int mark, boolean isBot, int vip, RemiCardU objU, int numStraight) {
        try {
            LOGGER.info("==>GetCardToAdd==>username:" + username + "-" + isBot + "-" + mark);
            Collections.sort(lsCard, new SortCardAsc());
            int percentTakePlace = 0;
            int percentJoker = 0;
            ///tao ra % lay cac card theo cac tieu chi
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
                int cardId = CheckU(lsTemp, username, numStraight);
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
            //Random ty le lay con Wild hoac Poker
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

    public static int GetCardToRemove(List<Card> lsCard, String username, int cardIdPlace, int numStraight) {
        LOGGER.info("==>GetCardToRemove==>username:" + username);
        Collections.sort(lsCard, new SortCardAsc());
        try {
            List<Integer> is = new ArrayList<>();
            for (Card card : lsCard) {
                is.add(card.getI());
            }

            List<List<Integer>> lsphom = CheckCard.group(is, numStraight);
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
}
