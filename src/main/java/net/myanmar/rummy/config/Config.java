/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.config;

import net.myanmar.rummy.vo.MarkCreateTable;
import net.myanmar.rummy.vo.ObjectRoom;
import net.myanmar.rummy.utils.GameUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.myanmar.rummy.vo.BotCreateTable;

/**
 *
 * @author hoangchau
 */
public class Config {

//    public static final List<MarkCreateTable> MARK_CREATE_TABLES = new ArrayList<>(
//            Arrays.asList(
//            		new MarkCreateTable(10, 50*10, 0),
//                    new MarkCreateTable(100, 200*100, 0),
//                    new MarkCreateTable(500, 200*500, 0),
//                    new MarkCreateTable(1000, 200*1000, 0),
//                    new MarkCreateTable(5000, 400*5000, 0),
//                    new MarkCreateTable(10000, 400*10000, 0),
//                    new MarkCreateTable(50000, 400*50000, 0),
//                    new MarkCreateTable(100000, 400*100000, 0),
//                    new MarkCreateTable(500000, 400*500000, 0),
//                    new MarkCreateTable(1000000, 400*1000000, 0)
//                    //new MarkCreateTable(5000000, 100000000, 0)
//            )
//    );
    public static final List<MarkCreateTable> LIST_MARK_CREATE_TABLES = new ArrayList<>(
            Arrays.asList(
                    new MarkCreateTable(10, 500, 500),
                    new MarkCreateTable(100, 10000, 20000),
                    new MarkCreateTable(500, 50000, 100000),
                    new MarkCreateTable(5000, 750000, 2000000),
                    new MarkCreateTable(10000, 2000000, 4000000),
                    new MarkCreateTable(50000, 10000000, 20000000),
                    new MarkCreateTable(100000, 20000000, 40000000),
                    new MarkCreateTable(500000, 100000000, 200000000),
                    new MarkCreateTable(1000000, 200000000, 400000000),
                    new MarkCreateTable(5000000, 1000000000, 2000000000)
            )
    );

    public static final List<BotCreateTable> BOT_CREATE_TABLE = new ArrayList<>(
            Arrays.asList(
                    new BotCreateTable(1, 1, 0, 11, 5, 10, 50, 1, 3),
                    new BotCreateTable(1, 1, 11, 101, 5, 10, 50, 1, 3),
                    new BotCreateTable(2, 1, 101, 501, 5, 10, 50, 1, 3),
                    new BotCreateTable(3, 1, 501, 1001, 4, 8, 50, 1, 3),
                    new BotCreateTable(5, 1, 5001, 10001, 2, 6, 120, 1, 2),
                    new BotCreateTable(6, 1, 10001, 50001, 1, 5, 180, 1, 2),
                    new BotCreateTable(7, 1, 50001, 100001, 1, 4, 240, 1, 2),
                    new BotCreateTable(8, 1, 100001, 500001, 0, 1, 420, 0, 1),
                    new BotCreateTable(9, 1, 500001, 1000001, 0, 1, 600, 0, 1)
            )
    );

    public static int getBoundGold(int mark) {

        for (MarkCreateTable markCreateTable : LIST_MARK_CREATE_TABLES) {
            if (mark == markCreateTable.getMark()) {
                return markCreateTable.getAg();
            }
        }

        return LIST_MARK_CREATE_TABLES.get(LIST_MARK_CREATE_TABLES.size() - 1).getAg();
    }

//    public static int getBoundGold(int mark) {
//        int min = mark * 50;
//        for (MarkCreateTable mct : MARK_CREATE_TABLES) {
//            //if (mct.getMark() <= 5000) {
//                if (mark == mct.getMark()) {
//                    min = mct.getAg();
//                    break;
//                }
//            //}
//        }
//
//        return min;
//    }
//    
    public static JsonObject configScore = new JsonObject();

    public static void loadConfigGameScore() {
        try {
            List<Integer> ls = new ArrayList<>(Arrays.asList(5, 10, 15, 20, -250));
            JsonArray arr = (JsonArray) (new JsonParser()).parse(GameUtil.gson.toJson(ls));

            configScore.addProperty("name", "Remi");
            configScore.add("score", arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ;
    
    private static final List<ObjectRoom> ROOMS = new ArrayList<>();

    public static List<ObjectRoom> getRooms() {
        if (!ROOMS.isEmpty()) {
            return ROOMS;
        }
        for (int i = 0; i < 4; i++) {
            ObjectRoom or = new ObjectRoom();
            or.setId(i + 1);
            or.setName("room " + (i + 1));
            or.setCurPlay(0);
            or.setCurTable(0);
            or.setMaxPlay(300);
            or.setMaxTable(100);
            or.setMinAG(0);
            switch (i) {
                case 0:
                    or.setMark(50);
                    or.setMinAG(2000);
                    break;
                case 1:
                    or.setMark(100);
                    or.setMinAG(50000);
                    break;
                case 2:
                    or.setMark(1000);
                    or.setMinAG(100000);
                    break;
                case 3:
                    or.setMark(10000);
                    or.setMinAG(1000000);
                    break;
                default:
                    break;
            }
            or.setMaxAG(1000000000);
            ROOMS.add(or);
        }

        return ROOMS;
    }

}
