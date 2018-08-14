/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.lobby.LobbyTable;
import com.cubeia.firebase.api.game.lobby.LobbyTableFilter;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.myanmar.rummy.table.attribute.TableLobbyAttribute;
import net.myanmar.rummy.utils.GameUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author hoangchau
 */
public class SearchTable {

    private static final Logger LOGGER = Logger.getLogger(SearchTable.class);

    public static int searchByMark(ActivatorContext context, int markMin, int markMax) {
        try {

            LobbyTable[] lobbyTables = context.getTableFactory().listTables(new FilterLobby(markMin, markMax));

            List<Integer> ListTemp = new ArrayList<>();

            List<Integer> ListTempStarted = new ArrayList<>();

            if (lobbyTables != null) {
                for (LobbyTable tables : lobbyTables) {
                    if (tables.getAttributes().get(TableLobbyAttribute.STATED).getIntValue() == 1) {
                        continue;
                    }
                    AttributeValue ArrId = tables.getAttributes().get(TableLobbyAttribute.ARRAY_PLAYER_ID);
                    if (ArrId != null) {
                        @SuppressWarnings("unchecked")
                                List<Double> arrId = GameUtil.gson.fromJson(ArrId.getStringValue(), ArrayList.class);
                        if (arrId.size() >= RummyBoard.MAX_PLAYER) {
                            continue;
                        }
                    }
                    if (tables.getAttributes().get(TableLobbyAttribute.START_GAME).getIntValue() == 1) {
                        ListTempStarted.add(tables.getTableId());
                    } else {
                        ListTemp.add(tables.getTableId());
                    }
                }
            }

            if (ListTemp.size() > 0) {
                int a = GameUtil.random.nextInt(ListTemp.size());
                return ListTemp.get(a);
            } else if (ListTempStarted.size() > 0) {
                int a = GameUtil.random.nextInt(ListTempStarted.size());
                return ListTempStarted.get(a);
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.getMessage(), e);

        }
        return 0;
    }

    public static LobbyTable[] findLongTimeTable(ActivatorContext context) {
        return context.getTableFactory().listTables(new LobbyTableFilter() {
            @Override
            public boolean accept(Map<String, AttributeValue> map) {
                AttributeValue lastConnect = null;
                for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
                    String key = entry.getKey();
                    AttributeValue value = entry.getValue();
                    if (key.equals(TableLobbyAttribute.LAST_CONNECT)) {
                        lastConnect = value;
                    }
                }

                if (lastConnect != null && lastConnect.getType() == AttributeValue.Type.INT) {
                    long secondsCheck = (new java.util.Date()).getTime() / 1000 - lastConnect.getDateValue().getTime() / 1000;
                    if (secondsCheck > 1800) {
                        return true;
                    }
                }
                return false;
            }

        });
    }

    public static LobbyTable[] findEmptyTable(ActivatorContext context) {
        return context.getTableFactory().listTables(new LobbyTableFilter() {
            @Override
            public boolean accept(Map<String, AttributeValue> map) {
                for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
                    String key = entry.getKey();
                    AttributeValue value = entry.getValue();
                    if (key.equals(TableLobbyAttribute.STATED)) {
                        return value.data.equals(1);
                    }
                }
                return false;
            }
        });
    }

    private static class FilterLobby implements LobbyTableFilter {

        private final int markMin;
        private final int markMax;

        public FilterLobby(int markMin, int markMax) {
            this.markMin = markMin;
            this.markMax = markMax; 
        }

        @Override
        public boolean accept(Map<String, AttributeValue> map) {
            for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
                String key = entry.getKey();
                AttributeValue value = entry.getValue();

                if (key.equals(TableLobbyAttribute.STATED) || key.equals(TableLobbyAttribute.START_GAME)) {
                    if (value.getIntValue() == 1) {
                        return false;
                    }
                }
                if (key.equals(TableLobbyAttribute.MARK)) {
                    if (value.getIntValue() < markMin || value.getIntValue() > markMax) {
                        return false;
                    }
                }

            }
            return true;
        }
    }
}
