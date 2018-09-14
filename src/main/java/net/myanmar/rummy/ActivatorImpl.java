/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy;

import com.athena.services.api.ServiceContract;
import com.athena.services.utils.GAMEID;

import net.myanmar.rummy.vo.LogPlayer;
import net.myanmar.rummy.vo.LogTable;
import net.myanmar.rummy.vo.UserGame;
import com.cubeia.firebase.api.action.JoinRequestAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.activator.CreationRequestDeniedException;
import com.cubeia.firebase.api.game.activator.GameActivator;
import com.cubeia.firebase.api.game.activator.RequestAwareActivator;
import com.cubeia.firebase.api.game.activator.RequestCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTable;
import com.cubeia.firebase.api.game.lobby.LobbyTableFilter;
import com.cubeia.firebase.api.routing.ActivatorAction;
import com.cubeia.firebase.api.routing.RoutableActivator;
import com.cubeia.firebase.api.server.SystemException;
import com.cubeia.firebase.api.service.config.ServerConfigProviderContract;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.JsonSyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import net.myanmar.rummy.config.Config;
import net.myanmar.rummy.event.ActivatorEVT;
import net.myanmar.rummy.event.ServiceEVT;
import net.myanmar.rummy.log.LogIFRSPlayer;
import net.myanmar.rummy.logic.MarkCreateTableTran;
import net.myanmar.rummy.logic.RummyBoard;
import net.myanmar.rummy.logic.SearchTable;
import net.myanmar.rummy.room.IDEVTHandler;
import net.myanmar.rummy.table.ZingParticipant;
import net.myanmar.rummy.table.attribute.TableLobbyAttribute;
import net.myanmar.rummy.utils.GameUtil;
import net.myanmar.rummy.vo.BotCreateTable;
import net.myanmar.rummy.vo.MarkCreateTable;
import org.apache.log4j.Logger;

/**
 *
 * @author hoangchau
 */
public class ActivatorImpl implements GameActivator, RequestAwareActivator, RoutableActivator {

    private ActivatorContext context;
    private static final Logger LOGGER = Logger.getLogger("RUMMY_ACTIVATOR");

    private final Object threadLock = new Object();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    private List<ScheduledFuture<?>> bctSchedule = new ArrayList<>();

    ServerConfigProviderContract configProviderContract;

    private static final List<LogTable> ListLogTable = new ArrayList<LogTable>();

    private static long peopleInRoomCheck = 0l;
    //public static ServiceContract serviceContract;

    public static void addLogTable(LogTable log) {
        synchronized (ListLogTable) {
            ListLogTable.add(log);
        }
    }
    private static final List<List<LogIFRSPlayer>> ListLogIFRSPlayer = new ArrayList<List<LogIFRSPlayer>>();

    public static void addLogIFRSTable(List<LogIFRSPlayer> log) {
        synchronized (ListLogIFRSPlayer) {
            ListLogIFRSPlayer.add(log);
        }
    }

    @Override
    public void init(ActivatorContext c) throws SystemException {
        context = c;

        configProviderContract = context.getServices().getServiceInstance(ServerConfigProviderContract.class);
        setupThreading();
        Config.loadConfigGameScore();

    }

    public ActivatorContext getContext() {
        return context;
    }

    @Override
    public void destroy() {
        destroyThreading();
    }

    @Override
    public void start() {
        startThreading();
    }

    @Override
    public void stop() {
        stopThreading();
    }

    @Override
    public RequestCreationParticipant getParticipantForRequest(int pid, int seats, Attribute[] attributes) throws CreationRequestDeniedException {
        UserGame ui = GameUtil.gson.fromJson(getServiceContract().getUserInfoByPid(pid, 0), UserGame.class);
        LOGGER.info("===>RequestCreationParticipant: " + ui);
        if (ui == null) {
            throw new CreationRequestDeniedException(1);
        }
        if (ui.getUnlockPass() == 0) {
            //LOGGER.info("===>RequestCreationParticipant 1");
            throw new CreationRequestDeniedException(2);
        }
        if (ui.getTableId() != 0) {
            
            throw new CreationRequestDeniedException(3);
        }
        if (attributes.length < 1) {
            
            throw new CreationRequestDeniedException(4);
        }

        if (seats <= 0 || seats > RummyBoard.MAX_PLAYER) {
           
            throw new CreationRequestDeniedException(10);
        }

        int mark = 0;
        for (Attribute attribute : attributes) {
//            if (attribute.name.equals(TableLobbyAttribute.CHIP)) {
//                if (ui.getAG() < attribute.value.getIntValue()) {
//                    getServiceContract().sendErrorMsg(pid, GameUtil.strCreate_UnderAG);
//
//                    throw new CreationRequestDeniedException(5);
//                }
//            }
            if (attribute.name.equals(TableLobbyAttribute.MARK)) {
                if (ui.getAG() < attribute.value.getIntValue()) {
                    getServiceContract().sendErrorMsg(pid, GameUtil.strCreate_0AG);
                    //LOGGER.info("===>RequestCreationParticipant 5");
                    throw new CreationRequestDeniedException(6);
                }
                mark = attribute.value.getIntValue();
            }
        }

        if (ui.getAG() < Config.getBoundGold(mark)) {
            getServiceContract().sendErrorMsg(pid, GameUtil.strCreate_UnderAG);
            //LOGGER.info("===>RequestCreationParticipant 6");
            throw new CreationRequestDeniedException(5);
        }

        return new ZingParticipant(pid, attributes, ui, context);
    }

    @Override
    public void onAction(ActivatorAction<?> action) {
        try {
            String message = (String) action.getData();

            JsonObject jo = (JsonObject) new JsonParser().parse(message);

            LOGGER.info("activator: " + message);

//            Type type = new TypeToken<Map<String, String>>() {
//            }.getType();
//            Map<String, String> myMap = GameUtil.gson.fromJson(message, type);
            if (jo.has("idevtdata")) {
                IDEVTHandler.getInstance(context).process((JsonObject) new JsonParser().parse(message));
                return;
            }
            int playerId = jo.get("pid").getAsInt();
            String evt = jo.get("evt").getAsString();
            String strui = getServiceContract().getUserInfoByPid(playerId, 0);

            if (strui.length() == 0) {
                getServiceContract().sendErrorMsg(playerId, GameUtil.strConnectGame);
            } else {
                UserGame ui = GameUtil.gson.fromJson(strui, UserGame.class);

                switch (evt) {

                    case ActivatorEVT.GET_ROOM_LIST:
                        LOGGER.info(ActivatorEVT.GET_ROOM_LIST);
                        getServiceContract().sendToClient(playerId, ServiceEVT.ROOM_LIST,
                                GameUtil.gson.toJson(Config.getRooms()));
                        break;
                    case ActivatorEVT.GET_ROOM_LIST_2:
                        LOGGER.info(ActivatorEVT.GET_ROOM_LIST_2);
//                        LOGGER.debug(GameUtil.gson.toJson(Config.getRooms()));
                        getServiceContract().sendToClient(playerId, ServiceEVT.ROOM_LIST,
                                GameUtil.gson.toJson(Config.getRooms()));
                        getServiceContract().sendToClient(playerId, ServiceEVT.TABLE_MARK_LIST, GameUtil.gson.toJson(Config.LIST_MARK_CREATE_TABLES));
                        break;
                    case ActivatorEVT.SELECT_ROOM:
                        LOGGER.info(ActivatorEVT.SELECT_ROOM);
                        int roomId = jo.get("id").getAsInt();
                        getServiceContract().confirmSelectRoom(playerId, roomId, context.getGameId());
                        break;
                    case ActivatorEVT.PLAYNOW:
                        LOGGER.info(ActivatorEVT.PLAYNOW);
                        int markCreate = jo.get("M").getAsInt();

                        long goldBound = 0;
                        for (MarkCreateTable markCreateTable : Config.LIST_MARK_CREATE_TABLES) {
                            if (markCreateTable.getMark() == markCreate) {
                                goldBound = markCreateTable.getAg();
                            }
                        }

                        if (goldBound == 0 || ui.getAG() < goldBound) {
                            getServiceContract().sendToClient(playerId, ServiceEVT.MESSAGE, GameUtil.strNotAGChip);
                        } else {
                            int tableId = SearchTable.searchByMark(context, markCreate, markCreate);

                            if (tableId > 0) {
                                pushUserToTable(playerId, tableId);
//                            serviceContract.AutoJoinTable(playerId, tableId, context.getGameId());
                            } else {
                                createTable(ui, markCreate);
                            }
                        }
                        break;
                    case ActivatorEVT.PLAYNOW2:

                        int markCurrent = jo.get("M").getAsInt();
                        int idTableCurrent = jo.get("idtable").getAsInt();
                        LOGGER.info("===>" + ActivatorEVT.PLAYNOW2 + "=> idTableCurrent: " + idTableCurrent + "=>Mark: " + markCurrent);
                        long goldBound2 = 0;
                        for (MarkCreateTable markCreateTable : Config.LIST_MARK_CREATE_TABLES) {
                            if (markCreateTable.getMark() == markCurrent) {
                                goldBound2 = markCreateTable.getAg();
                            }
                        }
                        if (goldBound2 == 0 || ui.getAG() < goldBound2) {
                            LOGGER.info("===> goldBound2: " + goldBound2);
                            getServiceContract().sendToClient(playerId, ServiceEVT.MESSAGE, GameUtil.strNotAGChip);
                        } else {
                            int tableId = SearchTable.searchByMark(context, markCurrent, markCurrent);
                            LOGGER.info("===> Table Id search: " + tableId);
                            if (tableId > 0 && tableId != idTableCurrent) {
                                pushUserToTable(playerId, tableId);
                            } else {
                                LOGGER.info("==>>Create Table");
                                //ui.setTableId(0);
                                createTable(ui, markCurrent);
                            }
                        }
                        break;
                    case ActivatorEVT.SEARCHT:
                        LOGGER.info(ActivatorEVT.SEARCHT);
                        int markMin = 0,
                         markMax = 0;

                        for (int i = Config.LIST_MARK_CREATE_TABLES.size() - 1; i >= 0; i--) {
                            MarkCreateTable markCreateTable = Config.LIST_MARK_CREATE_TABLES.get(i);
                            if (ui.getAG() >= markCreateTable.getCondition()) {
                                markMax = markCreateTable.getMark();
                                markMin = i == 0 ? markMax : Config.LIST_MARK_CREATE_TABLES.get(i - 1).getMark();
                                break;
                            }
                        }

                        if (markMax == 0) {
                            getServiceContract().sendToClient(playerId, ServiceEVT.MESSAGE, GameUtil.strNotAGChip);
                        } else {
                            int tableId = SearchTable.searchByMark(context, markMin, markMax);
                            LOGGER.info("====>Search table: " + tableId);
                            if (tableId > 0) {
                                pushUserToTable(playerId, tableId);
//                            serviceContract.AutoJoinTable(playerId, tableId, context.getGameId());
                            } else {
                                LOGGER.info("====>Search table create table");
                                createTable(ui, markMin);
                            }
                        }
                        break;
                    case ActivatorEVT.SELECT_G2:
                        LOGGER.info(ActivatorEVT.SELECT_G2);
                        getServiceContract().sendToClient(playerId, "ltv", GameUtil.gson.toJson(Config.LIST_MARK_CREATE_TABLES));
                        LOGGER.info("===> List tables: " + GameUtil.gson.toJson(Config.LIST_MARK_CREATE_TABLES));
                        break;
                    case ActivatorEVT.CREATE_TABLE:

                        int markCreate3 = jo.get("M").getAsInt();
                        long goldBound3 = 0;
                        for (MarkCreateTable markCreateTable : Config.LIST_MARK_CREATE_TABLES) {
                            if (markCreateTable.getMark() == markCreate3) {
                                goldBound3 = markCreateTable.getAg();
                            }
                        }

                        if (goldBound3 == 0 || ui.getAG() < goldBound3) {
                            getServiceContract().sendToClient(playerId, ServiceEVT.MESSAGE, GameUtil.strNotAGChip + "," + markCreate3 + "," + ui.getAG() + "," + goldBound3);
                        } else {
                            createTable(ui, markCreate3);
                        }
                        break;
                    case ActivatorEVT.BOT_CREATE_TABLE:

                        int markCreate4 = jo.get("M").getAsInt();
                        long goldBound4 = 0;
                        for (MarkCreateTable markCreateTable : Config.LIST_MARK_CREATE_TABLES) {
                            if (markCreateTable.getMark() == markCreate4) {
                                goldBound4 = markCreateTable.getAg();
                            }
                        }

                        if (goldBound4 == 0 || ui.getAG() < goldBound4) {

                        } else {
                            createTable(ui, markCreate4);
                        }

                        break;
                    case ActivatorEVT.LIST_MARK_CREATE:
                        LOGGER.info(ActivatorEVT.LIST_MARK_CREATE);
                        getListMarkForCreateTable(playerId, (int) ui.getAG(), ui.getOperatorid(), ui.getVIP());
                        break;
                    default:
                        break;
                }
            }

        } catch (JsonSyntaxException | NumberFormatException | CreationRequestDeniedException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }
    // Lay danh sach cac muc cuoc de tao ban 0- Disable, 1 - Enable, 2- Default

    public void getListMarkForCreateTable(int pid, int ag, int operator, int vip) {
        try {
            List<MarkCreateTableTran> lsMark = new ArrayList<>();
            for (int i = 0; i < Config.LIST_MARK_CREATE_TABLES.size(); i++) {
                if (vip < 1 && Config.LIST_MARK_CREATE_TABLES.get(i).getMark() > 100) {
                    lsMark.add(new MarkCreateTableTran(0, Config.LIST_MARK_CREATE_TABLES.get(i).getMark()));
                } else if (ag >= Config.LIST_MARK_CREATE_TABLES.get(i).getAg()) {
                    lsMark.add(new MarkCreateTableTran(1, Config.LIST_MARK_CREATE_TABLES.get(i).getMark()));
                } else {
                    lsMark.add(new MarkCreateTableTran(0, Config.LIST_MARK_CREATE_TABLES.get(i).getMark()));
                }
            }
            boolean t = false;
            for (int i = lsMark.size() - 1; i >= 0; i--) {
                if (lsMark.get(i).getD() == 1) {
                    if (lsMark.get(i).getM() * 100 <= ag) {
                        lsMark.get(i).setD(2);
                        t = true;
                        break;
                    }
                }
            }
            if (!t) {
                lsMark.get(0).setD(2);
            }
            getServiceContract().sendToClient(pid, ServiceEVT.TABLE_MARK_LIST, GameUtil.gson.toJson(lsMark));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //LOG_DEBUG.error(e.getMessage(), e);
        }
    }

    private void pushUserToTable(int playerId, int tableId) {
        JoinRequestAction joinRequestAction = new JoinRequestAction(playerId, tableId, -1, "");
        context.getActivatorRouter().dispatchToGame(context.getGameId(), joinRequestAction);
    }

    private void createTable(UserGame ui, int mark) throws CreationRequestDeniedException {
        Attribute[] temp = new Attribute[]{
            //            new Attribute(TableLobbyAttribute.NAME, new AttributeValue("Auto")),
            //new Attribute(TableLobbyAttribute.STATED, new AttributeValue(0)),
            new Attribute(TableLobbyAttribute.MARK, new AttributeValue(mark)),
            new Attribute(TableLobbyAttribute.CHIP, new AttributeValue(Config.getBoundGold(mark))),
            new Attribute(TableLobbyAttribute.VIP, new AttributeValue(0)),
            new Attribute(TableLobbyAttribute.PLAYER, new AttributeValue(RummyBoard.MAX_PLAYER))

        };

        context.getTableFactory().createTable(RummyBoard.MAX_PLAYER, getParticipantForRequest(ui.getUserid(), RummyBoard.MAX_PLAYER, temp));
    }

    private void setupThreading() {
        synchronized (threadLock) {
            if (scheduler != null) {
                return; // SANITY CHECK
            }
            scheduler = Executors.newScheduledThreadPool(2, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("TableActivator");
                    thread.setDaemon(true);
                    return thread;
                }
            });
        }
    }

    private void startThreading() {
        synchronized (threadLock) {
            stopThreading();

            future = scheduler.scheduleWithFixedDelay(new CheckTable(), 10, 5, TimeUnit.SECONDS);
            for (int i = Config.BOT_CREATE_TABLE.size() - 1; i >= 0; i--) {
                int idcase = Config.BOT_CREATE_TABLE.get(i).getIDCase();
                int mark = Config.LIST_MARK_CREATE_TABLES.get(i).getMark();
                int timeOut = Config.BOT_CREATE_TABLE.get(i).getTimeCheck();
                bctSchedule.add(scheduler.scheduleWithFixedDelay(new callBotCreateTable(idcase, mark), 0, timeOut, TimeUnit.SECONDS));
            }
            Thread threadLog = new Thread(new WriteLog(), "ThreadLog");
            threadLog.setDaemon(true);
            threadLog.start();
        }

    }

    private void stopThreading() {
        synchronized (threadLock) {
            if (future == null) {
                return; // SANITY CHECK
            }
            for (ScheduledFuture<?> sf : bctSchedule) {
                if (sf == null) {
                    return;
                }
                sf.cancel(true);
                sf = null;
            }
            future.cancel(true);
            future = null;
        }
    }

    private void destroyThreading() {
        synchronized (threadLock) {
            if (scheduler == null) {
                return; // SANITY CHECK
            }
            stopThreading();

            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private class callBotCreateTable implements Runnable {

        private int idcase;
        //private int tableCount;
        private int mark;

        public callBotCreateTable(int idcase, int mark) {
            this.idcase = idcase;
            //this.tableCount = context.getTableFactory().listTables().length;
            this.mark = mark;
        }

        @Override
        public void run() {
            try {
                botCreateTable(idcase, mark);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private void botCreateTable(int idcase, int mark) {
        BotCreateTable bct = Config.BOT_CREATE_TABLE.get(idcase - 1);
        int tableCap = bct.getTableMin() + (new Random().nextInt(bct.getTableMax() - bct.getTableMin() + 1));
        int tableCount = getTableCountByMark(context, mark);

        if (tableCount >= tableCap) {
            return;
        } else {
            int tableToAdd = bct.getTableToAddMin() + (new Random().nextInt(bct.getTableToAddMax() - bct.getTableToAddMin() + 1));
            if (tableToAdd == 0) {
                return;
            } else {
                for (int i = 0; i < tableToAdd; i++) {
                    getServiceContract().BotCreateTable(context.getGameId(), mark);
                    //DEBUG.info("BotCreateTable:" + mark + ";" + tableToAdd);
                }
            }
        }
    }

    private static int getTableCountByMark(ActivatorContext context, int mark) {
        try {

            LobbyTable[] lobbyTables = context.getTableFactory().listTables(new FilterLobby(mark));

            return lobbyTables.length;
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.getMessage(), e);

        }
        return 0;
    }

    private static class FilterLobby implements LobbyTableFilter {

        private final int mark;

        public FilterLobby(int mark) {
            this.mark = mark;
        }

        @Override
        public boolean accept(Map<String, AttributeValue> map) {
            for (Map.Entry<String, AttributeValue> entry : map.entrySet()) {
                String key = entry.getKey();
                AttributeValue value = entry.getValue();

                if (key.equals(TableLobbyAttribute.MARK)) {
                    if (value.getIntValue() != mark) {
                        return false;
                    }
                }

            }
            return true;
        }
    }

    private class CheckTable implements Runnable {

        @Override
        public void run() {

            try {
                destroyLongTimeTable();

                destroyEmptyTable();

                checkTables();

//                writelog();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private void destroyLongTimeTable() {
        try {
            //destroy table play long time(1800 s)
            LobbyTable[] tables = SearchTable.findLongTimeTable(context);
            for (LobbyTable table : tables) {
                AttributeValue ArrId = table.getAttributes().get(TableLobbyAttribute.ARRAY_PLAYER_ID);
                if (ArrId != null) {
                    @SuppressWarnings("unchecked")
                    List<Integer> arrId = GameUtil.gson.fromJson(ArrId.getStringValue(), ArrayList.class);
                    for (int j = 0; j < arrId.size(); j++) {
                        getServiceContract().PlayerLeaveTable(arrId.get(j));
                    }
                }
                LOGGER.info("destroy table: " + table.getTableId() + " - " + GameUtil.gson.toJson(table.getAttributes()));
                context.getTableFactory().destroyTable(table.getTableId(), true);
            }
        } catch (JsonSyntaxException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    // Lay danh sach cac muc cuoc de tao ban 0- Disable, 1 - Enable, 2- Default
    private class WriteLog implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (ListLogTable) {
                        if (ListLogTable.size() > 0) {
                            for (int i = 0; i < ListLogTable.get(0).getListPlayer().size(); i++) {
                                LogPlayer logP = ListLogTable.get(0).getListPlayer().get(i);
                                try {
                                    getServiceContract().LogPlayer(logP.getUserId(), context.getGameId(), ListLogTable.get(0).getILevel(),
                                            logP.getIWin(), new Date(ListLogTable.get(0).getTime().getTime()),
                                            logP.getSource(), logP.getIWinMark(), logP.getDev());
                                } catch (Exception ex) {
                                    LOGGER.error(ex.getMessage(), ex);
                                }
                            }
                            try {
                                getServiceContract().LogTable(ListLogTable.get(0).getILevel(),
                                        (int) (ListLogTable.get(0).getSumMarkStart() - ListLogTable.get(0).getSumMarkEnd()),
                                        new Date(ListLogTable.get(0).getTime().getTime()), context.getGameId(), 10);
                            } catch (Exception ex) {
                                LOGGER.error(ex.getMessage(), ex);
                            }
                            //Ghi Log van choi
                            Logger.getLogger("MYANMAR_POKER_LOG").info("G:" + context.getGameId() + " - " + ListLogTable.get(0).getILevel()
                                    + " - " + GameUtil.gson.toJson(ListLogTable.get(0).getLogGame()));
                            ListLogTable.remove(0);
                        }
                    }
                    synchronized (ListLogIFRSPlayer) {
                        if (ListLogIFRSPlayer.size() > 0) {
                            //Ghi Log chi tiet cua nguoi choi phuc vu IFRS
                            if (Logger.getLogger("MYANMAR_POKER_LOG_PLAYER") != null) {
                                List<LogIFRSPlayer> temp = ListLogIFRSPlayer.remove(0);
                                for (int i = 0; i < temp.size(); i++) {
                                    Logger.getLogger("MYANMAR_POKER_LOG_PLAYER").info(String.valueOf(temp.get(i).getUserid()) + "#" + temp.get(i).getGold() + "#" + temp.get(i).getGameid() + "#" + temp.get(i).getTableid() + "#" + temp.get(i).getMarkunit() + "#" + temp.get(i).getGoldtransfer() + "#" + String.valueOf(temp.get(i).getDatetransfer().getTime()));
                                }
                            }
                        }
                    }
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void destroyEmptyTable() {
        try {
            LobbyTable[] tables = SearchTable.findEmptyTable(context);
            for (LobbyTable table : tables) {
                AttributeValue ArrId = table.getAttributes().get(TableLobbyAttribute.ARRAY_PLAYER_ID);
                if (ArrId != null) {
                    @SuppressWarnings("unchecked")
                    List<Double> arrIdDouble = GameUtil.gson.fromJson(ArrId.getStringValue(), ArrayList.class);
                    //List<Integer> arrId = GameUtil.gson.fromJson(ArrId.getStringValue(), ArrayList.class);
                    for (int j = 0; j < arrIdDouble.size(); j++) {
                        int tmp = arrIdDouble.get(j).intValue();
                        getServiceContract().PlayerLeaveTable(tmp);
                    }
                }
                LOGGER.info("destroy table: " + table.getTableId());
                context.getTableFactory().destroyTable(table.getTableId(), true);
            }
        } catch (JsonSyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private ServiceContract getServiceContract() {
        return context.getServices().getServiceInstance(ServiceContract.class);
    }

    protected void checkTables() {
        try {

            // Check Get List Room
            if ((new java.util.Date()).getTime() / 1000 - peopleInRoomCheck > 300) {
                peopleInRoomCheck = (new java.util.Date()).getTime() / 1000;
                // Cap nhat so nguoi trong tung Room.
                for (int i = 0; i < Config.LIST_MARK_CREATE_TABLES.size() - 1; i++) {
                    Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(getServiceContract().GetCurrentPlayerInMark(Config.LIST_MARK_CREATE_TABLES.get(i).getMark(), context.getGameId()));
                    Calendar c = Calendar.getInstance();
                    int iHours = c.get(Calendar.HOUR_OF_DAY);
                    int iMinute = c.get(Calendar.MINUTE);
                    if (((iHours == 11) && (iMinute >= 30)) || ((iHours == 13) && (iMinute <= 30)) || (iHours == 12) || (iHours == 21) || ((iHours == 20) && (iMinute >= 30)) || ((iHours == 22) && (iMinute <= 30))) {
                        if (i == 0) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(300 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 1) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(250 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 2) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(100 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 3) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(50 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 4) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(20 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(50));
                        } else if (i == 5) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(10 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(50));
                        } else if (i == 6) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(5 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(20));
                        } else if (i == 7) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(1 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(5));
                        } else if (i == 8) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(1 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(5));
                        } else if (i == 9) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(1 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(5));
                        }
                    } else {
                        if (i == 0) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(200 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 1) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(150 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 2) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(100 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(100));
                        } else if (i == 3) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(100 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(50));
                        } else if (i == 4) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(50 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(50));
                        } else if (i == 5) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(20 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(50));
                        } else if (i == 6) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(5 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(20));
                        } else if (i == 7) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(1 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(5));
                        } else if (i == 8) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(1 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(5));
                        } else if (i == 9) {
                            Config.LIST_MARK_CREATE_TABLES.get(i).setCurrplay(1 + Config.LIST_MARK_CREATE_TABLES.get(i).getCurrplay() + (new Random()).nextInt(5));
                        }
                    }
                }
                for (int i = 0; i < Config.getRooms().size() - 1; i++) {
                    Config.getRooms().get(i).setCurPlay(getServiceContract().GetCurrentPlayerInRoom(Config.getRooms().get(i).getId(), context.getGameId()));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
