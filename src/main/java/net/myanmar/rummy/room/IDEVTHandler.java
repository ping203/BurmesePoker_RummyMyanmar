/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.room;

import com.athena.services.api.ServiceContract;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.lobby.LobbyTable;
import com.cubeia.firebase.api.lobby.LobbyAttributeAccessor;
import com.cubeia.firebase.api.service.dosprotect.DosProtector;
import com.cubeia.firebase.api.service.dosprotect.FrequencyRule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import net.myanmar.rummy.table.attribute.TableLobbyAttribute;
import net.myanmar.rummy.utils.GameUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author hoangchau
 */
public class IDEVTHandler {

    private final static Logger LOGGER = Logger.getLogger(IDEVTHandler.class);
    public static final int GET_LIST_TABLE = 600;

    private static final String DOS_SELECT_ROOM_RULE = "selectRoomSpam";

    private final ActivatorContext context;

    private static IDEVTHandler _instance;

    private final DosProtector dos;

    public static IDEVTHandler getInstance(ActivatorContext context) {
        if (_instance == null) {
            _instance = new IDEVTHandler(context);
        }
        return _instance;
    }

    private IDEVTHandler(ActivatorContext context) {
        this.context = context;
        dos = this.context.getServices().getServiceInstance(DosProtector.class);
        dos.config(DOS_SELECT_ROOM_RULE, new FrequencyRule(1, 1000));
    }

    public void process(JsonObject jo) {
        try {
            JsonObject jsonObject = jo.get("idevtdata").getAsJsonObject();
            switch (jsonObject.get("idevt").getAsInt()) {

                case GET_LIST_TABLE:

                    int userId = jsonObject.get("pid").getAsInt();
                    if (dos.allow(DOS_SELECT_ROOM_RULE, userId)) {
                        getListTable(jo.get("idevtdata").getAsJsonObject());
                    }else{
                        ServiceContract contract = context.getServices().getServiceInstance(ServiceContract.class);
                        contract.sendErrorMsg(userId, GameUtil.SELECT_ROOM_TOO_FAST);
                    }

                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void getListTable(JsonObject jo) {
        try {
            int roomid = jo.get("RoomID").getAsInt();
            if (roomid > 4 || roomid < 1) {
                roomid = 4;
            }
            ArrayList<TableInRoom> lsReturn = new ArrayList<>();

            LobbyTable[] tables = context.getTableFactory().listTables();
            for (short i = 0; i < tables.length; i++) {
                LobbyTable table = tables[i];
                
                
                long mark = (long) table.getAttributes().get(TableLobbyAttribute.MARK).getIntValue();
                if (checkTableInRoom(roomid, mark)) {
                    int seat = table.getAttributes().get(TableLobbyAttribute._SEATED).getIntValue();
                    int capacity = table.getAttributes().get(TableLobbyAttribute.PLAYER).getIntValue();
                    if (seat > capacity || seat < 1) {
                        seat = capacity;
                    }
                    lsReturn.add(new TableInRoom(table.getTableId(), mark,
                            (long) table.getAttributes().get(TableLobbyAttribute.CHIP).getIntValue(),
                            (short) seat, (short) capacity));
                }
            }

            JsonObject json = new JsonObject();
            json.addProperty("idevt", jo.get("idevt").getAsInt());
            json.addProperty("pid", jo.get("pid").getAsInt());
            json.addProperty("tables", new Gson().toJson(lsReturn));
            json.addProperty("gameid", context.getGameId());
            json.addProperty("roomid", roomid);
            
            ServiceContract contract = context.getServices().getServiceInstance(ServiceContract.class);
            
            contract.processRoom(json);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private boolean checkTableInRoom(int roomid, long mark) {
        try {
            switch (roomid) {
                case 1:
                    if(mark < 5000){
                        return true;
                    }
                    break;
                case 2:
                    if(mark < 50000){
                        return true;
                    }
                    break;
                case 3:
                    if(mark < 500000){
                        return true;
                    }
                    break;
                case 4:
                    return true;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }
}
