/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.logic;

import com.athena.services.api.ServiceContract;

import net.myanmar.rummy.vo.UserGame;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.SeatRequest;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableScheduler;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.myanmar.rummy.ActivatorImpl;
import net.myanmar.rummy.config.Config;
import net.myanmar.rummy.event.EVT;
import net.myanmar.rummy.log.PlayerAction;
import net.myanmar.rummy.vo.LocalEvt;
import net.myanmar.rummy.vo.LogPlayer;
import net.myanmar.rummy.vo.LogTable;
import net.myanmar.rummy.vo.RemiCardU;
import net.myanmar.rummy.log.LogIFRSPlayer;
import net.myanmar.rummy.logic.votransfer.AddMoney;
import net.myanmar.rummy.logic.votransfer.ConfirmDeclare;
import net.myanmar.rummy.logic.votransfer.EvtNamePacket;
import net.myanmar.rummy.logic.votransfer.LCardSend;
import net.myanmar.rummy.logic.votransfer.Packet;
import net.myanmar.rummy.logic.votransfer.PlayFinishTrans;
import net.myanmar.rummy.table.attribute.TableLobbyAttribute;
import net.myanmar.rummy.utils.GameUtil;

import org.apache.log4j.Logger;

/**
 *
 * @author hoangchau
 */
public class RummyBoard implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final boolean DEBUG = false;
    private final Object lock = new Object();
    private static final Logger LOGGER = Logger.getLogger(RummyBoard.class);

    /**
     * Trang thái bàn
     *
     */
    private GameStatus gameStatus = GameStatus.WAIT_FOR_START;

    /**
     * Turn danh bài hiện tại
     */
    private int currTurn;

    /**
     * Id của chủ bàn
     */
    private int ownerId;

    /**
     * Số người chơi tối đa
     */
    public static int MAX_PLAYER = 5;

    /**
     * số người tối thiểu để bắt đầu
     */
    public static final int MIN_PLAYER = 2;

    /**
     *
     */
    private Date IdleTime;

    /**
     * vip tối thiếu để vào bàn
     */
    private int minVip = 0;

    /**
     * mức cược bàn
     */
    private int mark = 10;

    /**
     * gold tối thiểu để vào bàn
     */
//    private int minGold = mark;
    /**
     * danh sách player ngồi trong bàn
     * index = currTurn
     */
    private final List<Player> players = new ArrayList<>();

    /**
     * danh sách player ngồi xem
     */
    private final List<Player> viewPlayers = new ArrayList<>();

    /**
     * danh sach bài bốc
     */
    private final List<Card> listCardDeck = new ArrayList<>();

    /**
     * danh sách bài đánh ra
     */
    private final List<Card> listCardPlace = new ArrayList<>();

    /**
     * phòng hiện tại
     */
    private int roomId;

    //private final List<LogIFRSPlayer> logIFRSPlayers = new ArrayList<>();
//    private LogTable logTable;
//    private LogTableNew logTableNew;
    private int winnerId = 0;

    /**
      *danh sach la bai ngua ma player tiep theo co the an
     */
    private final List<Integer> cardTakeAble = new ArrayList<>();

    /**
      *list bai da bi an cua all player
     */
    private final List<ConstrainDiscard> constrainDiscard = new ArrayList<>();

//    private boolean firstTakeDeck = true;
    public static final int FUND = 80; // quy diem so khi vao ban ch
    public static final int LOSE_FIRST = 20; // up baitrc khi boc la bai dau tien
    public static final int LOSE_SECOND = 40;   // up bai trc khi co nguoi declare

    public void setBoard(int pid, Attribute[] atts, ActivatorContext context, Table table) {
        synchronized (lock) {
            ownerId = pid;
            for (Attribute att : atts) {
                switch (att.name) {
//                    case TableLobbyAttribute.NAME:
//                        break;
                    case TableLobbyAttribute.MARK:
                        mark = att.value.getIntValue() < mark ? mark : att.value.getIntValue();
                        break;
                    case TableLobbyAttribute.CHIP:
                        // So AG toi thieu
//                        minGold = att.value.getIntValue() < minGold ? minGold : att.value.getIntValue();

                        break;
                    case TableLobbyAttribute.VIP:
                        // So Vip toi thieu
                        minVip = att.value.getIntValue() < minVip ? minVip : att.value.getIntValue();
                        break;
                    case TableLobbyAttribute.PLAYER:
                        // So nguoi choi toi da
                        MAX_PLAYER = att.value.getIntValue();
                        break;
                    case "Diamond":

                        break;
                    case "P":

                        break;
                    default:
                        break;
                }
            }

//            minGold  = Config.getBoundGold(this.mark);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getViewPlayers() {
        return viewPlayers;
    }

    public int getMinVip() {
        return minVip;
    }

    public int getMark() {
        return mark;
    }

//    public int getMinGold() {
//        return minGold;
//    }
    public List<String> getArrName() {
        synchronized (lock) {
            List<String> arrName = new ArrayList<>();
            for (int j = 0; j < players.size(); j++) {
                arrName.add(players.get(j).getUsername());
            }
            for (int i = 0; i < viewPlayers.size(); i++) {
                arrName.add(viewPlayers.get(i).getUsername());
            }
            return arrName;
        }
    }

    public List<Integer> getArrId() {
        synchronized (lock) {
            List<Integer> arrId = new ArrayList<>();
            for (int j = 0; j < players.size(); j++) {
                arrId.add(players.get(j).getUserid());
            }
            for (int i = 0; i < viewPlayers.size(); i++) {
                arrId.add(viewPlayers.get(i).getUserid());
            }
            return arrId;
        }
    }

    public List<Integer> getArrAG() {
        synchronized (lock) {
            List<Integer> arrId = new ArrayList<>();
            for (int j = 0; j < players.size(); j++) {
                arrId.add(players.get(j).getAG().intValue());
            }
            for (int i = 0; i < viewPlayers.size(); i++) {
                arrId.add(viewPlayers.get(i).getAG().intValue());
            }
            return arrId;
        }
    }

    /**
     * Cho phép user vào bàn hay không
     *
     * @param table
     * @param request
     * @param serviceContract
     * @return
     */
    public InterceptionResponse allowJoin(Table table, SeatRequest request, ServiceContract serviceContract) {
        synchronized (lock) {
            try {
                if (table.getAttributeAccessor().getIntAttribute(TableLobbyAttribute.STATED) == 1) {
                    LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                    return new InterceptionResponse(false, -2);
                }
                // avaiable
                ///neu khong phai la chu ban
                if (ownerId != request.getPlayerId()) {
                    UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(request.getPlayerId(), 0),
                            UserGame.class);
                    if (ui == null) {
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -4);
                    }
                    /// chua hieu tai sao lai quy dinh nhung con so 0 va 5
                    if (ui.getUnlockPass() == 0) {
                        if (ui.getSource() == 5) {
                            serviceContract.sendErrorMsg(request.getPlayerId(),
                                    "<vi>Bàn chưa mở khóa cấp 2.</vi><en>Unknow</en><kh>Unknow KH</kh><la>​ທ່ານ​ຍັງ​ບໍ່​ເປີ​ດ​ກະ​ແຈ​ລ​ະ​ດັບ2</la>");
                        } else {
                            serviceContract.sendErrorMsg(request.getPlayerId(), "Bàn chưa mở khóa cấp 2.");
                        }
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -4);
                    }
                    if (table.getAttributeAccessor().getIntAttribute(TableLobbyAttribute.CHIP) <= 0) {
                        serviceContract.sendErrorMsg(request.getPlayerId(), GameUtil.strTableError);
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -4);
                    }
                    if (table.getAttributeAccessor().getIntAttribute(TableLobbyAttribute.MARK) <= 0) {
                        serviceContract.sendErrorMsg(request.getPlayerId(), GameUtil.strTableError);
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -4);
                    }
                    if (ui.getGameId() != table.getMetaData().getGameId()) {
                        serviceContract.sendErrorMsg(request.getPlayerId(), GameUtil.strNotGame);
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -4);
                    }
                    if (ui.getTableId() != 0 && ui.getTableId() != table.getId()) {
                        serviceContract.sendErrorMsg(request.getPlayerId(), GameUtil.strInGame);
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -4);
                    }
                    if (ui.getVIP() < table.getAttributeAccessor().getIntAttribute(TableLobbyAttribute.VIP)) {
                        serviceContract.sendErrorMsg(request.getPlayerId(), GameUtil.strUnderVip);
                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -7); // Khong du cap Vip
                    }
                    if (ui.getAG() < table.getAttributeAccessor().getIntAttribute(TableLobbyAttribute.CHIP)) {// && this.Mark !=
                        // ActivatorImpl.FreeID) {
                        serviceContract.sendErrorMsg(request.getPlayerId(), GameUtil.strUnderChip);

                        LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                        return new InterceptionResponse(false, -8); // Khong du AG
                    }
                    for (int i = 0; i < viewPlayers.size(); i++) {
                        if (viewPlayers.get(i).getPid() == request.getPlayerId()) {
                            LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                            return new InterceptionResponse(false, -5);///kiem tra viewer co phai player ko
                        }
                    }
                }
                // get new
                if (ownerId == 0 || table.getId() == 0) {
                    LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                    return new InterceptionResponse(false, -2);
                }
            } catch (ClassCastException | JsonSyntaxException ex) {
                // ex.printStackTrace();
                LOGGER.error(new PlayerAction("ALLOW_JOIN", "", request.getPlayerId(), table.getId()));
                return new InterceptionResponse(false, -2);
            }
            return new InterceptionResponse(true, 0);
        }
    }

    /**
     * cho phép user rời bàn
     *
     * @param table
     * @param playerId
     * @return
     */
    public InterceptionResponse allowLeave(Table table, int playerId) {
        synchronized (lock) {
            try {
                if (gameStatus == GameStatus.WAIT_FOR_START) {
                    return new InterceptionResponse(true, 0);
                } else {
                    for (int i = 0; i < viewPlayers.size(); i++) {
                        if (viewPlayers.get(i).getPid() == playerId) {
                            ///neu la nguoi xem thi cho thoat
                            return new InterceptionResponse(true, 0);
                        }
                    }
                    return new InterceptionResponse(false, 0);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                return new InterceptionResponse(false, -2);
            }
        }
    }

    /**
     * User vào bàn
     *
     * @param table
     * @param playerId
     * @param serviceContract
     */
    public void playerJoin(Table table, int playerId, ServiceContract serviceContract) {
        synchronized (lock) {
            try {
                UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(playerId, table.getId()), UserGame.class);
                if (ui != null) {
                    if (ui.getUsertype() > 10) {
                        ui.setsIP(ui.getPid().toString());
                    }
                    LOGGER.info(new PlayerAction("PLAYER_JOIN", ui.getUsername(), playerId, table.getId()));

                    ///game da bat dau
                    if (gameStatus != GameStatus.WAIT_FOR_START) {
                        for (int i = 0; i < players.size(); i++) {
                            Player playerR = players.get(i);
                            ///cho người chơi kết nối lại ván chơi khi bị disconnect
                            if (playerR.getUserid().intValue() == ui.getUserid().intValue()) { /// Kiem tra xem player co phai da ơ trong bàn không
                                playerR.setDisconnect(false);
///
                                TableVInfo tableVInfo = getTableForDis(table, playerId, playerR.getAG());

                                table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                        new Packet(EVT.CLIENT_RECONNECT, GameUtil.gson
                                                .toJson(tableVInfo))));

                                LOGGER.info(new PlayerAction(EVT.CLIENT_RECONNECT,
                                        playerR.getUsername(), playerR.getUserid(), table.getId(), tableVInfo));

                                return;
                            }
                        }
                    }
                    ///nếu là chủ bàn => set ở phương thức setBoard() phía trên
                    if (ownerId == playerId) {
                        Player player = getPlayerById(playerId); ///de ghi log
                        player.setIsStart(true);
                        player.setAG(ui.getAG());
                        // listPlayer.get(0).setIsNew(0);

                        TableInfo tableInfo = getTable(table);

                        table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                new Packet(EVT.CLIENT_CREATE_TABLE, GameUtil.gson.toJson(tableInfo))));

                        LOGGER.info(new PlayerAction(EVT.CLIENT_CREATE_TABLE,
                                player.getUsername(), player.getUserid(), table.getId(), tableInfo));

                        this.roomId = players.get(0).getRoomId();
                        ///
                        confirmRoom(playerId, this.roomId, table.getId(), ui.getRoomId(), this.mark, serviceContract);
                        ///goi bot vao ban choi
                        startTimeOutGetBot(table);
                    } else {
                        for (int i = 0; i < players.size(); i++) {
                            ///neu nguoi choi thoat ra trong khi dang wait start
                            /// va join lai trong khi game van chua start
                            if (players.get(i).getUserid().intValue() == ui.getUserid().intValue()) {
                                players.get(i).setIsStart(true);
                                players.get(i).setDisconnect(false);
                                players.get(i).setAG(ui.getAG());

                                TableInfo tableInfo = getTable(table);///

                                table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                        new Packet(EVT.CLIENT_OTHER_JOIN, GameUtil.gson.toJson(tableInfo))));

                                LOGGER.info(new PlayerAction(EVT.CLIENT_OTHER_JOIN,
                                        getPlayerById(playerId).getUsername(), getPlayerById(playerId).getUserid(), table.getId(),
                                        tableInfo));

                                return;
                            }
                        }
                        ///nguoi choi moi join vao 
                        Player opnew = new Player(ui);
                        opnew.setIsStart(true);
                        opnew.setAG(ui.getAG());

                        ///bàn choi chua start va chua full nguoi
                        if ((gameStatus == GameStatus.WAIT_FOR_START) && (players.size() + viewPlayers.size() < MAX_PLAYER)) {
                            players.add(opnew);

                            Packet packet = new Packet(EVT.CLIENT_JOIN_TABLE, GameUtil.gson.toJson(opnew.getItemPlayer()));
                            table.getNotifier().notifyAllPlayersExceptOne(GameUtil.toDataAction(playerId, table.getId(),
                                    packet), playerId);
                            LOGGER.info(new PlayerAction(EVT.CLIENT_JOIN_TABLE, "", playerId, table.getId(), packet));

                            Packet packet1 = new Packet(EVT.CLIENT_OTHER_JOIN, GameUtil.gson.toJson(getTable(table)));
                            table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                    packet1));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_OTHER_JOIN, ui.getUsername(), playerId, table.getId(), packet1));
                            ///du so nguoi choi thi bat dau dem nguoc
                            if (players.size() == MIN_PLAYER) {

                                Packet packet2 = new Packet(EVT.CLIENT_COUNTDOWN_START, TimeAction.START_COUNTDOWN.getValue() / 1000 + "");
                                table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(0, table.getId(),
                                        packet2));
                                LOGGER.info(new PlayerAction(EVT.CLIENT_COUNTDOWN_START, "", 0, table.getId(), packet2));

                                if (table.getAttributeAccessor().getDateAttribute(TableLobbyAttribute.COUNTDOWN_START) == null) {
                                    table.getAttributeAccessor().setDateAttribute(TableLobbyAttribute.COUNTDOWN_START, new Date());
                                }
                            } else if (players.size() > MIN_PLAYER) {
                                ///???? khi co nguoi choi thứ 3 trở lên join bàn thì lấy ra thời gian đã set ở trên (câu if trên)
                                /// va tiếp tục đếm ngược thời gian đó
                                AttributeValue oldTime = table.getAttributeAccessor().getAttribute(TableLobbyAttribute.COUNTDOWN_START);
                                long secondsCheck = (new Date()).getTime() / 1000
                                        - oldTime.getDateValue().getTime() / 1000; ///do khoang thoi gian giua 2 thoi diem
                                ///thời điểm khi có 2 player và thời điểm hiện tại
                                long timeLeft = TimeAction.START_COUNTDOWN.getValue() / 1000 - secondsCheck;///lay thoi gian con lai
                                if (timeLeft < 0) {
                                    timeLeft = 0;
                                } else if (timeLeft > TimeAction.START_COUNTDOWN.getValue() / 1000) {
                                    timeLeft = TimeAction.START_COUNTDOWN.getValue() / 1000;
                                }

                                Packet packet2 = new Packet(EVT.CLIENT_COUNTDOWN_START, timeLeft + "");
                                table.getNotifier().notifyAllPlayers(
                                        GameUtil.toDataAction(0, table.getId(), packet2));

                                LOGGER.info(new PlayerAction(EVT.CLIENT_COUNTDOWN_START, "", 0, table.getId(), packet2));
                            }
                            /// xac nhan tao bàn chơi
                            confirmRoom(players.get(players.size() - 1).getUserid(), this.roomId,
                                    table.getId(), ui.getRoomId(), this.mark, serviceContract);
                            readyTable(table, playerId); // Tu dong Ready luon
                        } else {/// neu bàn choi da bat dau hoac so luong nguoi choi da du
                            viewPlayers.add(opnew);

                            TableVInfo tableVInfo = getVTable(table, opnew.getAG());
                            table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                    new Packet(EVT.CLIENT_VIEW_TABLE, GameUtil.gson.toJson(tableVInfo))));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_VIEW_TABLE, opnew.getUsername(), playerId, table.getId(), tableVInfo));
                            ///????? 
                            for (int i = 0; i < players.size(); i++) {
                                JsonObject send = new JsonObject();
                                send.addProperty("Name", GameUtil.strSystem);
                                send.addProperty("Data", opnew.getUsername() + GameUtil.strJoinTable);
                                table.getNotifier().notifyPlayer(players.get(i).getPid(),
                                        GameUtil.toDataAction(players.get(i).getPid(), table.getId(),
                                                new Packet(EVT.CLIENT_SYSTEM_CHAT, GameUtil.gson.toJson(send))));
                            }
                            ///??? cofirm cho player cuoi cung dc join vao
                            confirmRoom(players.get(players.size() - 1).getUserid(), this.roomId,
                                    table.getId(), ui.getRoomId(), this.mark, serviceContract);
                        }
                    }
//                    table.getAttributeAccessor().setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_NAME, GameUtil.gson.toJson(getArrName()));
                    table.getAttributeAccessor().setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_ID, GameUtil.gson.toJson(getArrId())); /// getArrId là list chua id của player và viewer
//                    table.getAttributeAccessor().setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_GOLD, GameUtil.gson.toJson(getArrAG()));
                }
            } catch (JsonSyntaxException | ClassCastException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private List<UUID> BotTask = new ArrayList<>();

    public void startTimeOutGetBot(Table table) {
        try {
            LOGGER.info("==>Remi==>Get bot mark: " + this.mark + " - size players = " + players.size() + " - gamestatus = " + gameStatus);

            if (mark > 20000) {
                return;
            }
            if (gameStatus != GameStatus.WAIT_FOR_START) {
                return;
            }
            if (players.size() < 1 && players.size() > 3) {
                return;
            }
            int countBot = 0;
            int countUser = 0;
            int numBot = 1;
            boolean checkvip0 = false;
            ///dem so luong BOT da co
            for (Player p : players) {
                if (p.getUsertype() > 10) {
                    countBot++;
                } else {
                    /// so luong nguoi cho da co
                    countUser++;
                }
                if (p.getVIP() == 0 && p.getUsertype() < 10) {
                    checkvip0 = true;
                }
                LOGGER.info("==>Remi==>startTimeOutGetBot: " + table.getId() + "-type: " + p.getUsertype() + " - name"
                        + p.getUsername() + " - vip: " + p.getVIP() + " - id: " + p.getPid() + "-AG " + p.getAG()
                        + "-Disconnect: " + p.isDisconnect());
            }
/// co 1 nguoi choi
            if (countUser == 1) {
                /// co 1 bot thi Ok
                if (countBot > 1) {
                    return;
                }
            } else {
                return;
            }
///ko phai VIP 0
            if (!checkvip0) {
                /// co toi thieu 1 bot thi OK
                if (countBot > 1) {
                    return;
                }
                /// xac suat de co the lay 2 BOT
                if (GameUtil.random.nextInt(5) == 0) {
                    numBot = 2;
                }
            }

            for (UUID id : BotTask) {
                table.getScheduler().cancelScheduledAction(id); /// huy bo thoi gian chờ cua BotTask
            }
            BotTask.clear();
            for (int i = 0; i < numBot; i++) {
                getOneBot(table);///action dc set Evt = "getbot"
                ///them vao BotTask mot obj
            }

            LocalEvt local = new LocalEvt();
            local.setEvt("checkGetBot"); // kiem tra get them bot
            ///goi ở processor khi startTimeOutGetBot
            GameObjectAction action = new GameObjectAction(table.getId());
            action.setAttachment(local);
            table.getScheduler().scheduleAction(action, TimeAction.TIME_CHECK_GET_BOT.getValue());

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void getOneBot(Table table) {
        try {
            LocalEvt local = new LocalEvt();
            local.setEvt("getbot");
            GameObjectAction action = new GameObjectAction(table.getId());
            action.setAttachment(local);
            BotTask.add(table.getScheduler().scheduleAction(action, 1000 + (GameUtil.random.nextInt(3000))));
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private TableInfo getTable(Table table) {
        synchronized (lock) {
            try {
                TableInfo ti = new TableInfo();
                ti.setId(table.getId());
                ti.setN(table.getMetaData().getName());
                ti.setM(this.mark);
                ti.setV(this.minVip);
                ti.setIssd(true);
                ti.setS(MAX_PLAYER);
                for (int i = 0; i < players.size(); i++) {
                    ti.getArrP().add(players.get(i).getItemPlayer());
                }
                return ti;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        }
    }

    public void removeBotIfExist(Table table, ServiceContract serviceContract) {
        synchronized (lock) {
            try {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getUsertype() > 10 && !players.get(i).isDisconnect()
                            && players.get(i).getAG() < Config.getBoundGold(this.mark)) {
                        boolean isLeft = false;
                        if (this.mark == Config.LIST_MARK_CREATE_TABLES.get(0).getMark() && players.size() > 2) {
                            if (GameUtil.random.nextInt(3) == 0) { ///25%
                                isLeft = true;
                            }
                        } else if (players.size() > 2) {
                            if (GameUtil.random.nextInt(4) == 0) {
                                isLeft = true;
                            }
                        } else {
                            if (GameUtil.random.nextInt(20) == 0) {
                                isLeft = true;
                            }
                        }
                        if (isLeft) {/// cho phep roi ban
                            scheduleLeft(table, players.get(i).getPid());
                        } else {
                            scheduleReadyBot(table, players.get(i).getPid());
                        }
                    }
                }
                startTimeOutGetBot(table);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void scheduleReadyBot(Table table, int pid) {
        try {
            LocalEvt local = new LocalEvt();
            local.setEvt("readyBot");
            local.setPid(pid);

            GameObjectAction action = new GameObjectAction(table.getId());
            action.setAttachment(local);

            table.getScheduler().scheduleAction(action, 3000 + GameUtil.random.nextInt(3000));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
///roi ban cua BOT

    private void scheduleLeft(Table table, int pid) {
        try {
            LeaveAction la = new LeaveAction(pid, table.getId());
            table.getScheduler().scheduleAction(la, 500 + GameUtil.random.nextInt(2000));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
/// du lieu cua table

    private TableVInfo getTableForDis(Table table, int pid, long ag) {
        synchronized (lock) {
            try {
                TableVInfo ret = new TableVInfo();
                ///CN la nguoi choi hien tai
                ret.setCN(players.get(currTurn).getUsername());

                int totalTime;
                if (null == players.get(currTurn).getTurnStatus()) {
                    totalTime = 20;
                } else {
                    switch (players.get(currTurn).getTurnStatus()) {
                        case CONFIRM_DECLARE:
                            totalTime = TimeAction.CONFIRM_DECLARE.getValue();
                            break;
                        case DECLARE:
                            totalTime = TimeAction.DECLARE.getValue();
                            break;
                        case DISCARD:
                            totalTime = TimeAction.DIS_CARD.getValue();
                            break;
                        case TAKECARD:
                            totalTime = TimeAction.TAKE_CARD.getValue();
                            break;
                        default:
                            totalTime = 20;
                            break;
                    }
                }
                ///CT la so thoi gian con lai
                ret.setCT(
                        totalTime - Integer.parseInt(String.valueOf(GameUtil.getSecondsBetween2Dates(new Date(), IdleTime))));
                if (ret.getCT() < 1) {
                    ret.setCT(1);
                }
                ret.setId(table.getId());
                ret.setN(table.getMetaData().getName());
                ret.setM(this.mark);
                // ret.setT(this.TableType);
                ret.setV(this.minVip);
                ret.setS(MAX_PLAYER);
                ret.setAGBuyIn(ag);
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPid() == pid) {
                        ret.getArrP().add(players.get(i).getItemVPlayer(1));
                    } else {
                        ret.getArrP().add(players.get(i).getItemVPlayer(0));
                    }
                }

                ret.setCardTakeAble(cardTakeAble);
                ret.setSizeNoc(listCardDeck.size());
                ret.setNotiDeclared(gameStatus == GameStatus.WAIT_FOR_DECLARE);

                for (ConstrainDiscard discard : constrainDiscard) {
                    if (discard.getPlayerTakecard() != 0) {
                        ret.getConstrainDiscards().add(discard);
                    }
                }

                return ret;
            } catch (NumberFormatException e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        }
    }

    private TableVInfo getVTable(Table table, long ag) {
        synchronized (lock) {
            try {
                TableVInfo ret = new TableVInfo();
                ret.setId(table.getId());
                ret.setN(table.getMetaData().getName());
                ret.setM(this.mark);
                ret.setV(this.minVip);
                ret.setAGBuyIn(ag);
                ret.setS(MAX_PLAYER);
                // ret.setArr(getCardBoardTrans());
                for (int i = 0; i < players.size(); i++) {
                    ret.getArrP().add(players.get(i).getItemVPlayer(0));
                }
                ret.setCardTakeAble(cardTakeAble);
                ret.setSizeNoc(listCardDeck.size());
                ret.setNotiDeclared(gameStatus == GameStatus.WAIT_FOR_DECLARE);
                return ret;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
            return null;
        }
    }

    public void readyTable(Table table, int pid) {
        synchronized (lock) {
            try {
                for (int i = 0; i < players.size(); i++) {
                    if ((players.get(i).getPid() == pid) && !players.get(i).isIsStart()) {
                        players.get(i).setIsStart(true);

                        EvtNamePacket packet = new EvtNamePacket(EVT.CLIENT_READY, players.get(i).getUsername());

                        table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(pid, table.getId(),
                                packet));

                        LOGGER.info(new PlayerAction(EVT.CLIENT_READY, "", 0, table.getId(), packet));
                    }
                }

                if (players.size() == MIN_PLAYER) {
                    startCountTimeOwner(table);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public void startCountTimeOwner(Table table) { // Number Readied = Total Player ==> Start Time for Owner
        synchronized (lock) {
            try {

                if (gameStatus != GameStatus.WAIT_FOR_START) {
                    return;
                }
                int dem = 0;
                for (int j = 0; j < players.size(); j++) {
                    if (players.get(j).isIsStart()) {
                        dem++;
                    }
                }

                if (dem == players.size() && (players.size() > 1)) {
                    GameObjectAction goa = new GameObjectAction(table.getId());/// tao ra action
                    LocalEvt le = new LocalEvt();
                    le.setEvt(EVT.OBJECT_AUTO_START);
                    le.setPid(players.get(0).getPid());
                    goa.setAttachment(le);

                    table.getAttributeAccessor().setDateAttribute(TableLobbyAttribute.COUNTDOWN_START, new Date());
                    table.getScheduler().scheduleAction(goa, TimeAction.START_COUNTDOWN.getValue());/// goi den handle method cua GOA
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void playerLeft(Table table, int playerId, ServiceContract serviceContract) {
        synchronized (lock) {

            LOGGER.info("tableId: " + table.getId() + " player: " + playerId);

            try {
                if (gameStatus == GameStatus.WAIT_FOR_START) {
                    serviceContract.PlayerLeaveTable(playerId);

                    if (players.size() < MIN_PLAYER) {
                        this.ownerId = 0;
                        this.players.clear();

                        EvtNamePacket packet = new EvtNamePacket(EVT.CLIENT_LEFT, String.valueOf(table.getId()));
                        table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                                packet));
                        LOGGER.info(new PlayerAction(EVT.CLIENT_LEFT, "", 0, table.getId(), packet));

                        table.getScheduler().cancelAllScheduledActions();

                        table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.STATED, 1);
                    } else {
                        for (int i = 0; i < players.size(); i++) {
                            if (players.get(i).getPid() == playerId) {

                                EvtNamePacket packet = new EvtNamePacket(EVT.CLIENT_LEFT, players.get(i).getUsername());
                                table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                                        packet));
                                LOGGER.info(new PlayerAction(EVT.CLIENT_LEFT, "", 0, table.getId(), packet));
                                players.remove(i);

                                break;
                            }
                        }

                        if (players.size() > 0) {

                            // Chu ban thoat ban
                            if (ownerId == playerId) {
                                this.ownerId = players.get(0).getPid();

                                EvtNamePacket packet = new EvtNamePacket(EVT.CLIENT_CHANGE_OWNER, players.get(0).getUsername());
                                table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                                        packet));
                                LOGGER.info(new PlayerAction(EVT.CLIENT_CHANGE_OWNER, "", 0, table.getId(), packet));
                                players.get(0).setIsStart(true);
                            }
///cho viewer dau tien vao ban choi
                            for (int i = players.size(); i < MAX_PLAYER; i++) {
                                if (viewPlayers.size() > 0) {
                                    Player opChange = viewPlayers.get(0);
                                    viewPlayers.remove(0);
                                    players.add(opChange);

                                    table.getNotifier().notifyAllPlayersExceptOne(
                                            GameUtil.toDataAction(playerId, table.getId(),
                                                    new Packet(EVT.CLIENT_JOIN_TABLE,
                                                            GameUtil.gson.toJson(opChange.getItemPlayer()))),
                                            opChange.getPid());
                                    LOGGER.info(new PlayerAction(EVT.CLIENT_JOIN_TABLE, "", playerId, table.getId(), opChange.getItemPlayer()));

                                    TableInfo tableInfo = getTable(table);
                                    table.getNotifier().notifyPlayer(opChange.getPid(), GameUtil.toDataAction(playerId,
                                            table.getId(), new Packet(EVT.CLIENT_OTHER_JOIN, GameUtil.gson.toJson(tableInfo))));
                                    LOGGER.info(new PlayerAction(EVT.CLIENT_OTHER_JOIN, opChange.getUsername(), playerId, table.getId(), tableInfo));
                                }
                            }

                            boolean checkBot = false;
                            for (int i = 0; i < players.size(); i++) {
                                if (players.get(i).getUsertype() < 10) { //Co user choi that khong
                                    checkBot = true;
                                    break;
                                }
                            }
                            if (checkBot) {
                                startTimeOutGetBot(table);/// co the co bot join vao
                                // Count Readied ==> Start count Time for Owner
                                startCountTimeOwner(table);
                            } else {
                                prepareDestroyTable(table);/// ko cho ton tai ban choi chi co BOT
                            }
                        } else {/// ko co nguoi choi thi cung huy ban
                            prepareDestroyTable(table);
                        }
                    }
                } else {/// trang thai ban choi da bat dau
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).getPid() == playerId) {
                            players.get(i).setDisconnect(true);
                            /// neu den luot choi
                            if (i == currTurn) {
                                /// tu dong boc bai
                                /*
                                    * check ham foldCard()
                                 */
                                GameObjectAction goa = genGameObjectAction(EVT.OBJECT_FOLD_CARD, players.get(i).getPid(), table.getId(), TurnStatus.NULL);
                                table.getScheduler().scheduleAction(goa, 0);
                                /// neu chua den luot
                            } else if (players.get(i).isActive()) {
                                players.get(i).setActive(false);/// 
                                /// 
                                BlindTrans sender = new BlindTrans();
                                sender.setN(players.get(i).getUsername());
                                sender.setEvt(EVT.CLIENT_FOLD_CARD);
                                table.getNotifier().notifyAllPlayers(
                                        GameUtil.toDataAction(players.get(i).getPid(), table.getId(), sender));
                                LOGGER.info(new PlayerAction(EVT.CLIENT_FOLD_CARD, "", 0, table.getId(), sender));
//                                ActivatorImpl.updateInfoLog(playerId, table.getId(), 0);
                                int songuoiActive = 0;
                                for (int k = 0; k < players.size(); k++) {
                                    if (players.get(k).isActive()) {
                                        songuoiActive++;
                                    }
                                }
                                if (songuoiActive == 1) {/// finishGame
                                    GameObjectAction goa = genGameObjectAction(EVT.OBJECT_FINISH, 0, table.getId(), TurnStatus.NULL);
                                    table.getScheduler().scheduleAction(goa, 0);
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < viewPlayers.size(); i++) {
                    if (viewPlayers.get(i).getPid() == playerId) {///neu viewer roi di thi thong bao cho nhung nguoi con lai biet
                        JsonObject send = new JsonObject();
                        send.addProperty("Name", GameUtil.strSystem);
                        send.addProperty("Data", viewPlayers.get(i).getUsername() + GameUtil.strLeftTable);
                        serviceContract.PlayerLeaveTable(playerId);
                        table.getNotifier().notifyAllPlayersExceptOne(GameUtil.toDataAction(playerId, table.getId(),
                                new Packet(EVT.CLIENT_SYSTEM_CHAT, GameUtil.gson.toJson(send))), playerId);

                        EvtNamePacket packet = new EvtNamePacket(EVT.CLIENT_LEFT, viewPlayers.get(i).getUsername());
                        table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                packet));
                        LOGGER.info(new PlayerAction(EVT.CLIENT_LEFT, viewPlayers.get(i).getUsername(), playerId, table.getId(), packet));

                        viewPlayers.remove(i);
                        break;
                    }
                }
//                table.getAttributeAccessor().setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_NAME, GameUtil.gson.toJson(getArrName()));
                table.getAttributeAccessor().setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_ID, GameUtil.gson.toJson(getArrId()));
//                table.getAttributeAccessor().setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_GOLD, GameUtil.gson.toJson(getArrAG()));
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private void prepareDestroyTable(Table table) {
        try {
            this.ownerId = 0;
            table.getScheduler().cancelAllScheduledActions();
            table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.STATED, 1);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    protected GameObjectAction genGameObjectAction(String evt, int pid, int tid, TurnStatus turnStatus) {
        synchronized (lock) {
            GameObjectAction goa = new GameObjectAction(tid);
            LocalEvt packet = new LocalEvt();
            packet.setEvt(evt);
            packet.setTurnStatus(turnStatus);
            packet.setPid(pid);
            goa.setAttachment(packet);
            return goa;
        }
    }
///khi cac player da ss choi

    public void startGame(Table table, int playerId) {
        synchronized (lock) {
            try {
                if (gameStatus == GameStatus.STARTED) {
                    return;
                }

                if (players.size() < MIN_PLAYER || players.size() > MAX_PLAYER) {
                    return;
                }

                for (int i = 0; i < players.size(); i++) {
                    if (!players.get(i).isIsStart()) {
                        return;
                    }
                }

                table.getScheduler().cancelAllScheduledActions();
                table.getAttributeAccessor().setDateAttribute(TableLobbyAttribute.LAST_CONNECT, new Date());
                table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.START_GAME, 1);
                int tablebot = 0;
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getUsertype() < 10) {/// chua co bot trong ban choi
                        //BOT HANDLE
                        tablebot = 1;
                    }
                }

                ///??????????????
                table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.TABLE_BOT, tablebot);
                table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.DECLARE, 0);

                Logtable.reset(mark, Calendar.getInstance().getTime(), getSumMark());
                Logtable.setLogGame(new String[players.size()][3]);
                listlogIFRSPlayer.clear();
                for (int i = 0; i < players.size(); i++) {
                    getLogtable().getLogGame()[i][0] = players.get(i).getUsername();
                    getLogtable().getLogGame()[i][1] = players.get(i).getAG().toString();

                    listlogIFRSPlayer.add(new LogIFRSPlayer(players.get(i).getUserid(), players.get(i).getAG().intValue(),
                            table.getMetaData().getGameId(), table.getId(), mark, 0, Calendar.getInstance().getTime(), players.get(i).getSource()));

                }

                gameStatus = GameStatus.STARTED;

                reset(table);

                /// set arrCard cho tung player
                /// set cac la bai con lai cho listCardDesk
                dealCard(genCard(2), table);

                //send to all player
                for (Player player : players) {
                    int[] arr = new int[player.getArrCard().size()];///13 14
                    for (int j = 0; j < arr.length; j++) {
                        arr[j] = player.getArrCard().get(j).getI();/// la ra gia tri
                    }
                    /// gui cho player thong tin bo bai cua minh. 
                    table.getNotifier().notifyPlayer(player.getPid(),
                            GameUtil.toDataAction(player.getPid(), table.getId(),
                                    new LCardSend(EVT.CLIENT_DEAL, arr, players.get(currTurn).getUsername(), listCardDeck.size())));

                    LOGGER.info(new PlayerAction("DEAL",
                            player.getUsername(), player.getUserid(), table.getId(), player.getDisplayCard()));
                }

                //send to view player
                for (Player player : viewPlayers) {
                    table.getNotifier().notifyPlayer(player.getPid(),
                            GameUtil.toDataAction(player.getPid(), table.getId(),
                                    new LCardSend(EVT.CLIENT_DEAL, null, players.get(currTurn).getUsername(), listCardDeck.size())));
                }

                Player player = players.get(currTurn);
                player.setTurnStatus(TurnStatus.TAKECARD);

                //start timeout action for first turn
                startTimeoutAction(TurnStatus.TAKECARD, table, TimeAction.TAKE_CARD, currTurn);///Take card goi den takeCardDeck
                /// trong takeCardDeck xu ly discard

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void reset(Table table) {
/// random luot ai dau tien
        currTurn = GameUtil.random.nextInt(players.size());

        //logIFRSPlayers.clear();
        listCardDeck.clear();
        listCardPlace.clear();
        winnerId = 0;
        cardTakeAble.clear();
        constrainDiscard.clear();

//        firstTakeDeck = true;
        for (Player op : players) {
            op.reset();
        }
    }

    private long getSumMark() {
        synchronized (lock) {
            long ret = 0;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUsertype() < 10) {
                    ret += players.get(i).getAG();
                }
            }
            return ret;
        }
    }

    /// tao ra 104 la bai
    private List<Card> genCard(int type) {
        try {
            List<Card> arrReturn = new ArrayList<>();
            List<Card> arrCard = new ArrayList<>();

            for (int k = 0; k < type; k++) {
                int num = 0;
                arrCard.add(new Card(60, 60, 60));/// 2 con joker black
                for (int i = 1; i < 5; i++) {
                    for (int j = 2; j < 15; j++) {
                        num++;
                        Card c = new Card(i, j, num);
                        arrCard.add(c);
                    }
                }
                arrCard.add(new Card(61, 61, 61));/// 2 con joker red
            }

            int numCard = 54 * type;

            /// tron bai
            for (int i = 0; i < numCard; numCard--) {
                int j = GameUtil.random.nextInt(numCard);
                arrReturn.add(arrCard.get(j));
                arrCard.remove(j);
            }

            return arrReturn;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /// chia 13 la bai cho moi nguoi choi
    private void dealCard(List<Card> listCard, Table table) {
        List<Card> listCardChia = new ArrayList<>(listCard);
        /// listCardChia bao gom 108 lá đã đc random
        int numCard = getNumCard();///13

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            player.getArrCard().clear();
            for (int j = 0; j < numCard; j++) {
                Card card = listCardChia.get(j);
                if (card != null) {
                    player.getArrCard().add(card);
                    listCardChia.remove(j);
                }
            }
            IdleTime = new Date();
            //logIFRSPlayers.add(new LogIFRSPlayer(player.getUserid(),
            //player.getAG().intValue(), table.getMetaData().getGameId(), table.getId(), this.mark, 0,
            //new java.util.Date(), player.getSource()));
        }
        /// listCardDeck bao gom cac lá con lại trong listCardChia
        listCardDeck.addAll(listCardChia);

        //for test
        if (DEBUG) {
            while (listCardDeck.size() > 10) {
                listCardDeck.remove(0);
            }
        }

    }

    private int getNumCard() {
        return 13;
    }

    private void startTimeoutAction(TurnStatus turnStatus, Table table, TimeAction timeAction, int turn) {
        Player player = players.get(turn);
        player.setTurnStatus(turnStatus);
        if (player.getUsertype() < 10) {
            IdleTime = new Date();
            /// tu dong choi neu player disconnect
            if (player.isDisconnect()) {
                if (DEBUG) {
                    LOGGER.info("disconnect timeout");
                    LOGGER.debug(turnStatus);
                    LOGGER.debug(player.getUsername());
                }
                GameObjectAction goa = genGameObjectAction(EVT.OBJECT_TURN_DISCONNECT, player.getPid(), table.getId(), turnStatus);
                table.getScheduler().scheduleAction(goa, TimeAction.DISCONNECT_ACTION.getValue());
            } else {
                startTimeout(table, player.getPid(), timeAction.getValue(), turnStatus);/// time action de take card = 11s
                /// goi ve handle cua GOA va goi playTimeout() trong nay         
            }
        } else {
            //BOT HANDLE
            /// nue la bot
            GameObjectAction goa = genGameObjectAction(EVT.OBJECT_BOT_SHOT, player.getPid(), table.getId(), turnStatus);/// chay vao botshot()
            table.getScheduler().scheduleAction(goa, 0); /// goi ve handle cua GOA va goi ve botShot() sau 0s
        }
    }

    private void startTimeout(Table table, int pid, int timeout, TurnStatus turnStatus, int... cardId) {
        if (gameStatus != GameStatus.STARTED && gameStatus != GameStatus.WAIT_FOR_DECLARE) {
            return;
        }
        ///trc khi start thi dung het cac time out khac
        stopTimeout(table, pid);
        TableScheduler tableScheduler = table.getScheduler();

        LocalEvt local = new LocalEvt();
        local.setEvt(EVT.OBJECT_TIMEOUT);/// playTimeout()
        local.setTurnStatus(turnStatus);
        ///????

        if (turnStatus == TurnStatus.BOT_TAKE_CARD_PLACE) {
            local.setCardId(cardId[0]);///cardId[0] là card mà BOT có thể ăn
        } else {
            local.setCardId(0);
        }
        local.setPid(pid);

        GameObjectAction action = new GameObjectAction(table.getId());
        action.setAttachment(local);

        UUID id = tableScheduler.scheduleAction(action, timeout);/// time out = time action = 11s

        Player player = getPlayerById(pid);
        player.setTimeoutActionId(id);
    }

    ///lay bai
    /*
        * startTimeoutAction() trong startTimeOut()
     **/
    public void takeCardDeck(Table table, int playerId, boolean b) {///1. b = true;
        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.STARTED) {
                    return;
                }
                Player player = players.get(currTurn);
/// chi dc lay bai khi den luot
                if (playerId != player.getUserid()) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.TAKECARD) {
                    return;
                }

                if (player.isFirstTakeDeck()) {
                    player.setFirstTakeDeck(false);
                }
/// chi dc lay bai khi so la bai <13 va con bai tren ban
                if (player.getArrCard().size() <= getNumCard() && listCardDeck.size() > 0) {
                    int index = listCardDeck.size() - 1;
                    ///???????? RemiCardU?????????????

                    RemiCardU objU = new RemiCardU();
                    if (player.getUsertype() > 10) {/// neu la BOT
                        //BOT HANDLE
                        /// lay ra la bai co chủ đích
                        /*
                            * getNumberStraightRequired = 5 - so luong nguoi choi
                            *
                        
                        
                        
                         */
                        index = CheckCard.GetCardToAdd(player.getArrCard(), player.getUsername(), listCardDeck,
                                this.mark, true, player.getVIP(), objU, getNumberStraightRequired());
                    } else {
                        index = 0;  //CheckCard.GetCardToAdd(player.getArrCard(), player.getUsername(), listCardDeck,
                        //this.mark, false, player.getVIP(), objU, getNumberStraightRequired());
                    }

                    Card c = listCardDeck.get(index);
                    listCardDeck.remove(index);
                    player.getArrCard().add(c);/// them vao bo bai cho nguoi choi

                    /// set so lan lấy bài auto lien tiếp cua nguoi choi
                    if (!b) {
                        player.setNumberAuto(0);
                    }
                    LOGGER.info(new PlayerAction("TAKE_CARD_DECK",
                            player.getUsername(), player.getUserid(), table.getId(), player.getDisplayCard()));

                    stopTimeout(table, playerId);/// sau khi lay bai xong thi cancel thoi gian

                    //notify to client
                    PlayCard card = new PlayCard(player.getUsername(), EVT.CLIENT_TAKE_CARD_DECK, 0);

//                    if(player.getArrCard().size() == getNumCard()){
//                        int nextTurn = getNextTurn();
//                        card.setNS(players.get(nextTurn).getUsername());
//                        
//                    }
                    table.getNotifier().notifyAllPlayersExceptOne(
                            GameUtil.toDataAction(0, table.getId(), card),
                            playerId);

                    card.setC(c.getI());
                    table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(0, table.getId(),
                            card));
                    LOGGER.info(new PlayerAction(EVT.CLIENT_TAKE_CARD_DECK, player.getUsername(), playerId, table.getId(), card));
                    //Check la Bot thi cho U
                    if (player.getUsertype() > 10) { //BOT HANDLE ==> Noti DECLARE
                        LOGGER.info("==>Bot Declare:");///
                        LOGGER.info("==>Bot Declare ==> CardRemove:" + objU.getiCard());
                        if (objU.getiCard() > 0) { //Co kha nang ù ==> Noti Declare
                            startTimeout(table, playerId, TimeAction.BOT_PROCESS.getValue(), TurnStatus.BOT_NOTIDECLARE, objU.getiCard());
                        } else { //Danh bai binh thuong
                            startTimeoutAction(TurnStatus.DISCARD, table, TimeAction.BOT_PROCESS, currTurn);
                        }
                        /// ko phai la bot
                    } else {
                        if (player.getArrCard().size() == getNumCard()) {///=13

                            ConfirmDeclare packet = new ConfirmDeclare();/// xac nhan declare
                            packet.setUser(getPlayerById(playerId).getUsername());

                            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), packet));

                            LOGGER.info(new PlayerAction(EVT.CLIENT_CONFIRM_DECLARE, "", 0, table.getId(), packet));

                            gameStatus = GameStatus.CONFIRM_DECLARE;

                            startTimeoutAction(TurnStatus.CONFIRM_DECLARE, table, TimeAction.CONFIRM_DECLARE, currTurn);

                        } else {
                            startTimeoutAction(TurnStatus.DISCARD, table, TimeAction.DIS_CARD, currTurn);/// danh bai
                        }

                    }
                }

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private void stopTimeout(Table table, int pid) {

        TableScheduler sch = table.getScheduler();

        Player player = getPlayerById(pid);
        if (player != null) {
            UUID id = player.getTimeoutActionId();
            if (id != null) {
                sch.cancelScheduledAction(id);
            }
        }

    }

    private Player getPlayerById(int playerId) {
        for (Player player : players) {
            if (player.getUserid() == playerId) {
                return player;
            }
        }
        return null;
    }

    private void addCardTakeAble(int cardId) {
        if (cardTakeAble.size() > 0) {
            cardTakeAble.remove(0);
        }
        cardTakeAble.add(cardId);
    }

    private void removeCardTakeAble(int cardId) {
        if (cardTakeAble.contains(cardId)) {
            cardTakeAble.remove(new Integer(cardId));
        }
    }

    /// danh bai
    public void disCard(Table table, int playerId, int[] cards, boolean b) {
        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.STARTED) {
                    return;
                }

                Player player = players.get(currTurn);
                if (player.getUserid() != playerId) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.DISCARD) {
                    return;
                }

                if (cards != null) {
                    if (cards.length > 2) {
                        return;
                    }
                    if (cards.length == 2 && cards[0] != cards[1]) {
                        return;
                    }
                }
                if (player.getUsertype() > 10) {//BOT Remove Card
//                    LOGGER.info("==>BOT Start RemoveCard:" + player.getUsername());
//                    cardId = CheckCard.GetCardToRemove(player.getArrCard(), player.getUsername(), player.getCardPlaceId());
//                    LOGGER.info("==>BOT RemoveCard:" + player.getUsername() + "-" + cardId);
                }

                if (cards == null) {
                    int n = player.getArrCard().size() - 1;
                    cards = new int[1];
                    while (n >= 0) {
                        int cardId = player.getArrCard().get(n).getI();
                        /// check cardId co the danh ra ko
                        if (checkDiscardAble(cardId, playerId, players.get(getNextTurn()).getUserid())) {
                            n = -1;
                            cards[0] = cardId;
                        } else {
                            n = n - 1;
                        }
                    }
                }

                if (!checkDiscardAble(cards[0], playerId, players.get(getNextTurn()).getUserid())) {
                    Packet packet = new Packet(EVT.CLIENT_ACTION_ERROR, GameUtil.DIS_CARD_JUST_TAKE_PLACE);
                    table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(), packet));
                    return;
                }

                boolean cardValid = false;

                List<Card> l = new ArrayList<>();
                for (Card card : player.getArrCard()) {
                    if (card.getI() == cards[0]) {
                        l.add(card);
                        if (l.size() == cards.length) {
                            break;
                        }
                    }
                }
                /// remove la bai danh dc ra khoi arrCard cua player
                if (l.size() == cards.length) {
                    cardValid = true;

                    player.getArrCard().removeAll(l);
                    listCardPlace.addAll(l);/// them vao danh sach bai danh ra
                    addCardTakeAble(cards[0]);///danh sach la bai ngua ma player tiep theo co the an
                }

                if (!b) {
                    player.setNumberAuto(0);
                }
                LOGGER.info(new PlayerAction("DISCARD",
                        player.getUsername(), player.getUserid(), table.getId(), player.getDisplayCard()));

                /// la bai da duoc danh ra
                if (cardValid) {
                    player.setTurnStatus(TurnStatus.TAKECARD);/// set turn take card cho vong tiep theo
                    int nextTurn = getNextTurn();///getNextTurn tra ve turn cua nguoi tiep theo

                    ///?????
                    addConstrainDiscard(cards[0], playerId, TurnStatus.DISCARD);

                    stopTimeout(table, playerId);
                    ///
                    boolean fn = checkFinish(table, nextTurn);

                    Discard card = new Discard(player.getUsername(), EVT.CLIENT_DISCARD, cards, players.get(nextTurn).getUsername());
                    if (fn) {
                        card.setNS("");
                    }
                    table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                            card));
                    LOGGER.info(new PlayerAction(EVT.CLIENT_DISCARD, "", 0, table.getId(), card));

                    if (!fn) {
                        currTurn = nextTurn;
                        startTimeoutAction(TurnStatus.TAKECARD, table, TimeAction.TAKE_CARD, currTurn);
                    } else {
                        gameStatus = GameStatus.WAIT_FOR_DECLARE;
                        noticeDeclareSuccess(table);
                    }

                }

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     *
     * @param cardId
     * @param playerId
     * @param ts
     */
    private void addConstrainDiscard(int cardId, int playerId, TurnStatus ts) {

        LOGGER.info(ts);
        LOGGER.info(constrainDiscard);

        if (ts == TurnStatus.DISCARD) {
            /// neu list ko rong, va ko co ai an la bai nay
            if (!constrainDiscard.isEmpty() && constrainDiscard.get(constrainDiscard.size() - 1).getPlayerTakecard() == 0) {
                constrainDiscard.remove(constrainDiscard.size() - 1);/// loai ra khoi list
            }

            Iterator<ConstrainDiscard> i = constrainDiscard.iterator();
            while (i.hasNext()) {
                ConstrainDiscard s = i.next();

                if (!s.isJustTakePlace()) {/// check đánh cây bài vừa ăn
                    s.setJustTakePlace(true);
                }
                /// kiem tra nhung la da dc an xem player da danh ra la bai nay bh chua
                if (new Card(s.getCardId()).getN() == new Card(cardId).getN() && s.getPlayerTakecard() == playerId) {
                    if (s.isRequired()) {//
                        s.setRequired(false);/// player da tung an la bai
                    }
                }

            }

            constrainDiscard.add(new ConstrainDiscard(cardId, playerId, 0));

            LOGGER.info(constrainDiscard);

        } else {

            ConstrainDiscard cd = constrainDiscard.get(constrainDiscard.size() - 1);

            if (cd.getCardId() == cardId) {
                cd.setPlayerTakecard(playerId);
                cd.setJustTakePlace(false);
            }

            LOGGER.info(constrainDiscard);
        }

    }

    /**
     * check cây bài có được đánh ra hay không
     *
     * @param cardId
     * @param playerId
     * @param next
     * @return
     */
    private boolean checkDiscardAble(int cardId, int playerId, int next) {

        LOGGER.info(constrainDiscard + " -- " + playerId + "  --  " + next);
        for (ConstrainDiscard cd : constrainDiscard) {

            // check đánh cây bài vừa ăn
            if (cd.getCardId() == cardId && cd.getPlayerTakecard() == playerId && !cd.isJustTakePlace()) {
                return false;
            }

            //check đánh cây bài có số trùng với cây bài trước đó đã đánh cho người kia ăn.
            if (cd.getPlayerTakecard() == next && cd.getPlayerDiscard() == playerId && new Card(cardId).getN() == new Card(cd.getCardId()).getN() && cd.isRequired()) {
                return false;
            }

        }

        return true;
    }

    private int getNextTurn() {
        int nextPlayer = currTurn < players.size() - 1 ? currTurn + 1 : 0;

        while (!players.get(nextPlayer).isActive()) {
            nextPlayer = nextPlayer < players.size() - 1 ? nextPlayer + 1 : 0;
            if (nextPlayer == currTurn) {
                break;
            }
        }
        return nextPlayer;
    }

    private boolean checkFinish(Table table, int nextTurn) {
        //finish game
//            GameObjectAction goa = genGameObjectAction(EVT.OBJECT_FINISH, 0, table.getId(), TurnStatus.DECLARE);
//            table.getScheduler().scheduleAction(goa, 0);

        int c = 0;
        for (Player player : players) {
            if (player.isActive()) {
                c++;
            }
        }
        return nextTurn == currTurn || listCardDeck.isEmpty() || c <= 1;

    }
///
    private void noticeDeclareSuccess(Table table) {
        if (winnerId != 0) {
            Player winnerPlayer = getPlayerById(winnerId);
            Packet packet = new Packet(EVT.CLIENT_WINNER, winnerPlayer.getUsername());
            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(0, table.getId(),
                    packet));
            LOGGER.info(new PlayerAction(EVT.CLIENT_WINNER, "", 0, table.getId(), packet));
        } else {
            Packet packet = new Packet(EVT.CLIENT_WINNER, "");
            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(0, table.getId(),
                    packet));
            LOGGER.info(new PlayerAction(EVT.CLIENT_WINNER, "", 0, table.getId(), packet));

        }

        startTimeoutDeclareAction(table);

    }

    private void startTimeoutDeclareAction(Table table) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.isActive() && player.getPid() != winnerId) {
                player.setTurnStatus(TurnStatus.DECLARE);
                startTimeoutAction(TurnStatus.DECLARE, table, TimeAction.DECLARE, i);
            }
        }
    }

    public void takeCardPlace(Table table, int playerId, int cardId, boolean b) {
        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.STARTED) {
                    return;
                }

                Player player = players.get(currTurn);

                if (playerId != player.getUserid()) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.TAKECARD) {
                    return;
                }

                if (listCardPlace.isEmpty()) {
                    return;
                }

                if (!cardTakeAble.contains(cardId)) {
                    return;
                }

                if (player.getNumTake() >= 3) {
                    return;
                }

                if (player.isFirstTakeDeck()) {
                    player.setFirstTakeDeck(false);
                }

                player.setNumTake(player.getNumTake() + 1);

                Card card = new Card(cardId);
                if (!b) {
                    player.setNumberAuto(0);
                }
                player.setTurnStatus(TurnStatus.DISCARD);

                stopTimeout(table, playerId);

                player.getArrCard().add(card);

                addConstrainDiscard(cardId, playerId, TurnStatus.TAKECARD);

                listCardPlace.remove(listCardPlace.size() - 1);

                removeCardTakeAble(cardId);

                LOGGER.info(new PlayerAction("TAKE_CARD_PLACE", player.getUsername(), player.getUserid(), table.getId(),
                        player.getDisplayCard()));

                PlayCard playCard = new PlayCard(player.getUsername(), EVT.CLIENT_TAKE_CARD_PLACE, cardId);
                table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                        playCard));
                LOGGER.info(new PlayerAction(EVT.CLIENT_TAKE_CARD_PLACE, "", 0, table.getId(), playCard));

                if (player.getArrCard().size() == getNumCard()) {

                    ConfirmDeclare packet = new ConfirmDeclare();
                    packet.setUser(getPlayerById(playerId).getUsername());

                    table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), packet));

                    LOGGER.info(new PlayerAction(EVT.CLIENT_CONFIRM_DECLARE, "", 0, table.getId(), packet));

                    gameStatus = GameStatus.CONFIRM_DECLARE;

                    startTimeoutAction(TurnStatus.CONFIRM_DECLARE, table, TimeAction.CONFIRM_DECLARE, currTurn);

                } else {
                    startTimeoutAction(TurnStatus.DISCARD, table, TimeAction.DIS_CARD, currTurn);
                }

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public void notiDeclare(Table table, int playerId, int cardId) {
        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.STARTED && gameStatus != GameStatus.CONFIRM_DECLARE) {
                    return;
                }

                Player player = players.get(currTurn);

                if (playerId != player.getUserid()) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.DISCARD && player.getTurnStatus() != TurnStatus.CONFIRM_DECLARE) {
                    return;
                }

                table.getScheduler().cancelAllScheduledActions();
                boolean cardValid = false;
                player.setNumberAuto(0);

                if (cardId == -1 && player.getTurnStatus() == TurnStatus.CONFIRM_DECLARE) {
                    cardValid = true;
                } else {
                    if (!checkDiscardAble(cardId, playerId, players.get(getNextTurn()).getUserid())) {
                        return;
                    }

                    for (Card card : player.getArrCard()) {
                        if (card.getI() == cardId) {
                            cardValid = true;
                            player.getArrCard().remove(card);
                            listCardPlace.add(card);
                            addCardTakeAble(cardId);
                            break;
                        }
                    }
                }

                LOGGER.info(new PlayerAction("NOTI_DECLARE",
                        player.getUsername(), player.getUserid(), table.getId(), player.getDisplayCard()));

                if (cardValid) {
                    int nextTurn = getNextTurn();

                    Discard card = new Discard(player.getUsername(), EVT.CLIENT_DISCARD, new int[]{cardId}, players.get(nextTurn).getUsername());
                    table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                            card));
                    LOGGER.info(new PlayerAction(EVT.CLIENT_DISCARD, "", 0, table.getId(), card));

                    Packet packet = new Packet(EVT.CLIENT_NOTICE_DECLARE, player.getUsername());
                    table.getNotifier().notifyAllPlayers(
                            GameUtil.toDataAction(playerId, table.getId(), packet));
                    LOGGER.info(new PlayerAction(EVT.CLIENT_NOTICE_DECLARE, "", 0, table.getId(), packet));

                    stopTimeout(table, playerId);
                    player.setTurnStatus(TurnStatus.DECLARE);
                    player.setIsDeclared(true);

                    gameStatus = GameStatus.WAIT_FOR_DECLARE;

                    winnerId = playerId;

//                    declare(table, playerId, null, false);
                    startTimeoutAction(TurnStatus.DECLARE, table, TimeAction.DECLARE, currTurn);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void cancelConfirmDeclare(Table table, int playerId) {
        synchronized (lock) {

            try {
                if (gameStatus != GameStatus.CONFIRM_DECLARE) {
                    return;
                }

                Player player = getPlayerById(playerId);

                if (player == null) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.CONFIRM_DECLARE) {
                    return;
                }

                if (!player.isActive()) {
                    return;
                }

                int nextTurn = getNextTurn();

                ConfirmDeclare cd = new ConfirmDeclare();
                cd.setConfirm(false);
                cd.setUser(player.getUsername());
                cd.setNextUser(players.get(nextTurn).getUsername());

                table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), cd));
                LOGGER.info(new PlayerAction(EVT.CLIENT_CONFIRM_DECLARE, player.getUsername(), 0, table.getId(), cd));

                currTurn = nextTurn;
                gameStatus = GameStatus.STARTED;
                startTimeoutAction(TurnStatus.TAKECARD, table, TimeAction.TAKE_CARD, currTurn);

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
/*
    *cong bo
    **/
    public void declare(Table table, int playerId, List<List<Integer>> lsphom, boolean auto) {
        synchronized (lock) {

            try {
                if (gameStatus != GameStatus.WAIT_FOR_DECLARE) {
                    return;
                }

                Player player = getPlayerById(playerId);

                if (player == null) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.DECLARE) {
                    return;
                }

                if (!player.isActive()) {
                    return;
                }
                if (lsphom == null || !CheckCard.valid(lsphom, player.getArrCard())) {
                    try {
                        List<Integer> is = new ArrayList<>();
                        for (Card card : player.getArrCard()) {
                            is.add(card.getI());
                        }

                        lsphom = CheckCard.group(is, getNumberStraightRequired());/// tu dong sap xep phom
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                }

                player.getListGroup().addAll(lsphom);
//                

                stopTimeout(table, playerId);

                player.setTurnStatus(TurnStatus.FINISH_DECLARE);

                LOGGER.info(new PlayerAction("DECLARE", player.getUsername(), player.getUserid(), table.getId(),
                        player.getDisplayCard(), lsphom));

                if (winnerId == playerId) {
                    boolean check = true;

                    for (List<Integer> list : lsphom) {
                        if (!CheckCard.checkGroup(list)) {
                            check = false;
                            break;
                        }
                    }

                    if (check) {
                        //finish game
                        noticeDeclareSuccess(table);
                    } else {
                        player.setActive(false);
                        player.setFund(0);
                        winnerId = 0;

                        int nexturn = getNextTurn();

                        if (!checkFinish(table, nexturn)) {
                            BlindTrans objSend = new BlindTrans(EVT.CLIENT_FOLD_CARD, player.getUsername(), players.get(nexturn).getUsername());
                            objSend.setAgLose(FUND);
                            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), objSend));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_FOLD_CARD, "", 0, table.getId(), objSend));

                            gameStatus = GameStatus.STARTED;
                            currTurn = nexturn;
                            startTimeoutAction(TurnStatus.TAKECARD, table, TimeAction.TAKE_CARD, currTurn);
                        } else {
                            Packet packet = new Packet(EVT.CLIENT_FOLD_CARD, player.getUsername());
                            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), packet));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_FOLD_CARD, "", 0, table.getId(), packet));

                            int uactive = 0;

                            int playerWin = 0;

                            for (Player pl : players) {
                                if (pl.isActive()) {
                                    uactive++;
                                    playerWin = pl.getUserid();
                                }

                            }
                            if (uactive == 1) {
                                gameStatus = GameStatus.FINISHED;
                                winnerId = playerWin;

                                GameObjectAction goa = genGameObjectAction(EVT.OBJECT_FINISH, 0, table.getId(), null);
                                table.getScheduler().scheduleAction(goa, 0);
                            } else {
                                noticeDeclareSuccess(table);
                                startTimeoutAction(TurnStatus.DECLARE, table, TimeAction.DECLARE, nexturn);
                            }

                        }

                    }
                } else {

                    Packet packet = new Packet(EVT.CLIENT_DECLARED, player.getUsername());
                    table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), packet));
                    LOGGER.info(new PlayerAction(EVT.CLIENT_DECLARED, player.getUsername(), playerId, table.getId(), packet));

                    int n = 0;

                    for (Player p : players) {
                        if (!p.isActive() || p.getTurnStatus() == TurnStatus.FINISH_DECLARE) {
                            n++;
                        }
                    }

                    if (n == players.size()) {
                        gameStatus = GameStatus.FINISHED;
                        GameObjectAction goa = genGameObjectAction(EVT.OBJECT_FINISH, 0, table.getId(), null);
                        table.getScheduler().scheduleAction(goa, 0);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

//    @Deprecated
    public void foldCard(Table table, int playerId, boolean auto) {

        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.STARTED) {
                    return;
                }

                Player player = players.get(currTurn);

                if (player.getUserid() != playerId) {
                    return;
                }

                if (player.getTurnStatus() != TurnStatus.TAKECARD) {
//                  return;
                }
                if (!auto) {
                    player.setNumberAuto(0);
                }

                if (player.isFirstTakeDeck()) {
                    player.setFund(player.getFund() - LOSE_FIRST);
                } else {
                    player.setFund(player.getFund() - LOSE_SECOND);
                }

                player.setActive(false);

                int nexturn = getNextTurn();

                if (!checkFinish(table, nexturn)) {
                    BlindTrans objSend = new BlindTrans(EVT.CLIENT_FOLD_CARD, player.getUsername(), players.get(nexturn).getUsername());

                    long agLose = (FUND - player.getFund()) * this.mark;
                    if (agLose > player.getAG()) {
                        agLose = player.getAG();
                    }
                    objSend.setAgLose(agLose);
                    table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), objSend));
                    LOGGER.info(new PlayerAction(EVT.CLIENT_FOLD_CARD, "", 0, table.getId(), objSend));

                    currTurn = nexturn;
                    startTimeoutAction(TurnStatus.TAKECARD, table, TimeAction.TAKE_CARD, currTurn);
                } else {
                    gameStatus = GameStatus.FINISHED;
                    winnerId = players.get(nexturn).getUserid();

                    GameObjectAction goa = genGameObjectAction(EVT.OBJECT_FINISH, 0, table.getId(), null);
                    table.getScheduler().scheduleAction(goa, 0);
                }

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

    }

    public void serviceUpAG(Table table, int playerId, long ag, ServiceContract serviceContract) {
        synchronized (lock) {
            boolean t = false;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getPid() == playerId) {
                    t = true;
                    if (players.get(i).getVIP() == 0) {
                        players.get(i).setVIP(1);
                    }
                    UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(playerId, 0), UserGame.class);
                    players.get(i).setAG(ui.getAG());

                    AddMoney addMoney = new AddMoney(EVT.CLIENT_AM, players.get(i).getUsername(), ag, 1);
                    table.getNotifier().notifyAllPlayersExceptOne(GameUtil.toDataAction(0, table.getId(),
                            addMoney), playerId);
                    LOGGER.info(new PlayerAction(EVT.CLIENT_AM, "", playerId, table.getId(), addMoney));
                }
            }
            if (!t) {
                for (int i = 0; i < viewPlayers.size(); i++) {
                    if (viewPlayers.get(i).getPid() == playerId) {
                        UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(playerId, 0), UserGame.class);
                        viewPlayers.get(i).setAG(ui.getAG());
                    }
                }
            }
        }
    }

    public void serviceUpAGIAP(Table table, int playerId, ServiceContract serviceContract) {
        synchronized (lock) {
            boolean t = false;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getPid() == playerId) {
                    t = true;
                    UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(playerId, 0), UserGame.class);
                    players.get(i).setAG(ui.getAG());

                    AddMoney addMoney = new AddMoney(EVT.CLIENT_AGIAP, players.get(i).getUsername(), ui.getAG(), 1);
                    table.getNotifier().notifyAllPlayersExceptOne(GameUtil.toDataAction(0, table.getId(),
                            addMoney), playerId);
                    LOGGER.info(new PlayerAction(EVT.CLIENT_AGIAP, "", playerId, table.getId(), addMoney));

                    break;
                }
            }
            if (!t) {
                for (int i = 0; i < viewPlayers.size(); i++) {
                    if (viewPlayers.get(i).getPid() == playerId) {
                        UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(playerId, 0), UserGame.class);
                        viewPlayers.get(i).setAG(ui.getAG());
                        break;
                    }
                }
            }
        }
    }

    public void chatTable(Table table, GameDataAction action) {
        try {
            int source = 0;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getPid() == action.getPlayerId()) {
                    source = players.get(i).getSource();
                    break;
                }
            }
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getSource() == source) {
                    table.getNotifier().notifyPlayer(players.get(i).getPid(), action);
                }
            }
            for (int i = 0; i < viewPlayers.size(); i++) {
                if (viewPlayers.get(i).getSource() == source) {
                    table.getNotifier().notifyPlayer(viewPlayers.get(i).getPid(), action);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public int kickTable(String username, int playerId) {
        synchronized (lock) {
            try {
                if (playerId != this.ownerId) {
                    return 0;
                }

                if (gameStatus == GameStatus.WAIT_FOR_START) {
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).getUsername().equals(username)
                                && (players.get(0).getVIP() > players.get(i).getVIP())) {
                            return players.get(i).getPid();
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            return 0;
        }
    }

    public void autoExit(Table table, int playerId) {
        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.WAIT_FOR_START) {

                    for (Player player : players) {
                        if (player.getPid() == playerId) {
                            player.setAutoexit(!player.isAutoexit());
                            AutoExit autoExit = new AutoExit(EVT.CLIENT_AUTO_EXIT, player.isAutoexit());
                            table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                    autoExit));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_AUTO_EXIT, player.getUsername(), playerId, table.getId(), autoExit));
                            break;
                        }
                    }

                    for (Player player : viewPlayers) {
                        if (player.getPid() == playerId) {

                            LeaveAction la = new LeaveAction(playerId, table.getId());
                            table.getScheduler().scheduleAction(la, 0);
                            break;
                        }
                    }
                } else {
                    Player player = getPlayerById(playerId);
                    if (player != null) {
                    }
                    LeaveAction la = new LeaveAction(playerId, table.getId());
                    table.getScheduler().scheduleAction(la, 0);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public void UVipTable(Table table, int playerId, int gold) {
        synchronized (lock) {
            try {
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPid() == playerId) {
                        players.get(i).setAG(players.get(i).getAG() + gold);
                        players.get(i).setVIP(1);
                        LeaveAction la = new LeaveAction(players.get(i).getPid(), table.getId());
                        table.getScheduler().scheduleAction(la, 500);
                        break;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void tipDealer(Table table, int playerId, ServiceContract serviceContract) {
        synchronized (lock) {
            try {

                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getPid() == playerId) {
                        if (((gameStatus != GameStatus.WAIT_FOR_START) && players.get(i).getAG() - (80 * this.mark) <= 2 * this.mark)
                                || ((gameStatus == GameStatus.WAIT_FOR_START) && players.get(i).getAG() < 3 * this.mark)) {

                            Packet packet = new Packet(EVT.CLIENT_TIP_DEALER, GameUtil.strTipNotEnoughAG_TH);
                            table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(),
                                    packet));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_TIP_DEALER, players.get(i).getUsername(), playerId, table.getId(), packet));
                            return;
                        } else {
                            players.get(i).setAG(serviceContract.UpdateMarkChessById(players.get(i).getUserid(),
                                    -2 * this.mark, table.getMetaData().getGameId(), this.mark));
                            players.get(i).setAG(players.get(i).getAG() - 2 * this.mark);

                            TipSend send = new TipSend(EVT.CLIENT_TIP_DEALER, 2 * this.mark, players.get(i).getUsername());
                            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(),
                                    send));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_TIP_DEALER, "", 0, table.getId(), send));

                            break;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void autoReadyTable(Table table) {
        synchronized (lock) {
            try {
                for (int i = 0; i < players.size(); i++) {
                    readyTable(table, players.get(i).getPid());
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public void playerDisconnected(Table table, int playerId) {
        synchronized (lock) {
            try {
                if (gameStatus == GameStatus.WAIT_FOR_START) {
                    LeaveAction la = new LeaveAction(playerId, table.getId());
                    table.getScheduler().scheduleAction(la, 0);
                } else {
                    Player player = getPlayerById(playerId);
                    if (player != null) {
                        player.setDisconnect(true);
                        if (playerId == players.get(currTurn).getUserid()) {
//                            if (gameStatus != GameStatus.WAIT_FOR_DECLARE) {

                            GameObjectAction goa = genGameObjectAction(EVT.OBJECT_TURN_DISCONNECT, player.getPid(), table.getId(), player.getTurnStatus());
                            table.getScheduler().scheduleAction(goa, TimeAction.DISCONNECT_ACTION.getValue());

//                            }
                        }
                    }

                    for (int i = 0; i < viewPlayers.size(); i++) {
                        if (viewPlayers.get(i).getPid() == playerId) {
                            LeaveAction la = new LeaveAction(playerId, table.getId());
                            table.getScheduler().scheduleAction(la, 0);
                            break;
                        }
                    }
                }

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public void botShot(Table table, int pid, boolean b, ServiceContract serviceContract, TurnStatus turnStatus) {
        try {
            LOGGER.info("==>Remmi table " + table.getId() + " pid " + pid + " Bot Process:" + turnStatus);
            if (turnStatus == TurnStatus.TAKECARD) {
                if (players.size() > 0) {
                    for (int a = 0; a < players.size(); a++) {
                        LOGGER.info("==>Remi table " + table.getId() + " pid " + pid + " Bot Process: Find player " + players.get(a).getPid());
                        if (players.get(a).getPid() == pid) {
                            boolean check = false;
                            if (cardTakeAble.size() > 0) {
                                for (int i = 0; i < cardTakeAble.size(); i++) {
                                    Integer cardId = cardTakeAble.get(i);
                                    Card cardAn = new Card(cardId);
                                    if (CheckCard.IsTakeCard(players.get(a).getArrCard(), cardAn, this.mark)) { // An quan bai
                                        /// de ghep thanh 1 phom hoac tạo ra 1 cạ 2 lá
                                        check = true;
                                        LOGGER.info("==>Remmi table " + table.getId() + " pid " + pid + " Bot Process: An card " + cardAn.getI()); // Card
                                        if (GameUtil.random.nextInt(100) > 93) {
                                            tipDealer(table, pid, serviceContract);
                                        }
                                        startTimeout(table, pid, TimeAction.BOT_PROCESS.getValue() + GameUtil.random.nextInt(9) * 1000, TurnStatus.BOT_TAKE_CARD_PLACE, cardAn.getI());
                                        break;
                                    }
                                }
                            }
                            if (!check) { // Boc card
                                LOGGER.info("==>Remmi table " + table.getId() + " pid " + pid + " Bot Process: Boc card");
                                startTimeout(table, pid, TimeAction.BOT_PROCESS.getValue() + GameUtil.random.nextInt(9) * 1000, TurnStatus.TAKECARD);
                            }
                            break;
                        }
                    }
                }
            } else {
                startTimeout(table, pid, TimeAction.BOT_PROCESS.getValue() + GameUtil.random.nextInt(9) * 1000, turnStatus);
            }
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error(e.getMessage(), e);
        }

//        BOT HANDLE
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void finishGame(Table table, ServiceContract serviceContract) {
        LOGGER.info(new PlayerAction("FINISH_GAME", "", 0, table.getId()));
        synchronized (lock) {
            try {
                if (gameStatus != GameStatus.FINISHED) {
                    return;
                }

                if (players.size() < 2) {
                    return;
                }

                gameStatus = GameStatus.WAIT_FOR_START;

                table.getScheduler().cancelAllScheduledActions();
                List<PlayFinishTrans> result = new ArrayList<>();

                for (Player player : players) {
                    if (player.getUserid() == winnerId) {
                        continue;
                    }
                    if (!player.isActive()) {
                        continue;
                    }

//                    if (CheckCard.countStraight(player.getListGroup()) < getNumberStraightRequired()) {
//                        player.setFund(0);
//                        continue;
//                    }
                    if (CheckCard.countStraight(player.getListGroup()) < getNumberStraightRequired()) {
                        for (List<Integer> is : player.getListGroup()) {
                            for (Integer i : is) {
                                Card c = new Card(i);
                                if (!c.isJocker()) {
                                    player.setFund(player.getFund() - c.calc());
                                }
                            }
                        }
                    } else {
                        for (List<Integer> is : player.getListGroup()) {

                            if (!CheckCard.checkGroup(is)) {
                                for (Integer i : is) {
                                    Card c = new Card(i);
                                    if (!c.isJocker()) {
                                        player.setFund(player.getFund() - c.calc());
                                    }
                                }
                            }
                        }
                    }

                    if (player.getFund() < 0) {
                        player.setFund(0);
                    }
                }

                int max = Collections.max(players, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return Integer.valueOf(o1.getFund()).compareTo(o2.getFund());
                    }

                }).getFund();

                int win = 0;
                long winGold = 0;

                int cWin = 0;//so nguoi thang cuoc
                for (Player player : players) {
                    if (player.getFund() == max) {
                        cWin++;
                    } else {
                        int score = max - player.getFund();
                        long goldResult = score * this.mark;
                        if (goldResult > player.getAG()) {
                            goldResult = player.getAG();
                        }

                        player.setScore(-score);
                        player.setGoldResult(-goldResult);
                        win += score;
                        winGold += goldResult;
                    }

                }

                if (winnerId == 0) {

                    for (Player player : players) {
                        if (player.getFund() == max) {
                            player.setScore(win);
                            player.setGoldResult(winGold / cWin);
                        }

                    }

                } else {
                    Player playerWin = getPlayerById(winnerId);
                    playerWin.setScore(win);
                    playerWin.setGoldResult(winGold);
                }

                for (Player player : players) {
                    long goldAdd = player.getGoldResult() >= 0
                            ? Hesovip(player.getGoldResult(), player.getVIP())
                            : player.getGoldResult();
                    player.setAG(serviceContract.UpdateMarkChessById(player.getUserid(), goldAdd,
                            table.getMetaData().getGameId(), this.mark));

                    PlayFinishTrans pft = new PlayFinishTrans();
                    pft.setN(player.getUsername());
                    pft.setM(goldAdd);
                    pft.setDiem(player.getScore());
                    pft.setAG(player.getAG());

                    //win do thang khca declare lao. 
                    List<Integer> l = new ArrayList<>();
                    if (player.getListGroup().isEmpty()) {
                        for (Card card : player.getArrCard()) {
                            l.add(card.getI());
                        }
                    }
                    player.getListGroup().add(l);

                    pft.setArrCard(player.getListGroup());
                    pft.setActive(player.isActive());

                    result.add(pft);
                    int r = 1;
                    if (goldAdd < 0) {
                        r = -1;
                    }
                    Logtable.getListPlayer().add(new LogPlayer(player.getUserid(), (int) goldAdd, r, player.getOperatorId(), player.getSource()));
                }

                Packet packet = new Packet(EVT.CLIENT_FINISH, GameUtil.gson.toJson(result));
                table.getNotifier().notifyAllPlayers(
                        GameUtil.toDataAction(0, table.getId(), packet));
                LOGGER.info(new PlayerAction(EVT.CLIENT_FINISH, "", 0, table.getId(), packet));

                Packet packet1 = new Packet(EVT.CLIENT_COUNTDOWN_START, TimeAction.START_COUNTDOWN.getValue() / 1000 + "");
                table.getNotifier().notifyAllPlayers(
                        GameUtil.toDataAction(0, table.getId(),
                                packet1));
                LOGGER.info(new PlayerAction(EVT.CLIENT_COUNTDOWN_START, "", 0, table.getId(), packet1));

                writeLog(table);

                restartGame(table, serviceContract);

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    private void restartGame(Table table, ServiceContract serviceContract) {
        try {
            for (Player player : players) {
                player.setGameCount(player.getGameCount() + 1);
                if (player.getUsertype() < 10
                        && serviceContract.CheckPromotion(player.getAG(), player.getVIP(), player.getSource())
                        && !player.isDisconnect()) {
                    Long agTang = serviceContract.PromotionByUid(player.getUserid(), false);
                    if (agTang > 0) {
                        player.setAG(player.getAG() + agTang);
                        if (player.getGameCount() >= 10 && !player.isNap()) {
                            AddMoney addMoney = new AddMoney(EVT.CLIENT_AM, player.getUsername(), agTang, 1);
                            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(0, table.getId(),
                                    addMoney));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_AM, "", 0, table.getId(), addMoney));

                        } else {
                            AddMoney addMoney = new AddMoney(EVT.CLIENT_AM, player.getUsername(), agTang, 0);
                            table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(0, table.getId(),
                                    addMoney));
                            LOGGER.info(new PlayerAction(EVT.CLIENT_AM, "", 0, table.getId(), addMoney));
                        }
                    }
                }
            }

            boolean bool = true;
            for (int i = players.size() - 1; i >= 0; i--) {
                Player player = players.get(i);
                player.setIsStart(true);
                if (player.isDisconnect() || player.isAutoexit() || player.getNumberAuto() > 1) {

                    LOGGER.info("player finish leave : " + player.getUsername());

                    LeaveAction la = new LeaveAction(player.getPid(), table.getId());
                    table.getScheduler().scheduleAction(la, 500);
                    bool = false;
                } else {

                    long boundMoney = Config.getBoundGold(this.mark);

                    if (player.getAG() < boundMoney) {
                        LOGGER.info("player finish leave het tien: " + player.getUsername());
                        serviceContract.sendErrorMsg(player.getPid(), GameUtil.strKick_Err3_TH);
                        LeaveAction la = new LeaveAction(player.getPid(), table.getId());
                        table.getScheduler().scheduleAction(la, 0);
                        bool = false;
                    }

                    if (player.getUsertype() > 10) {
                        //BOT HANDLE
                    }
                }
            }

            //table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(0, table.getId(), new Packet("uag", GameUtil.gson.toJson(lsP))));
            if (bool) {
                for (int i = players.size(); i < MAX_PLAYER; i++) {
                    if (viewPlayers.size() > 0) {
                        Player opChange = viewPlayers.get(0);
                        viewPlayers.remove(0);
                        players.add(opChange);

                        Packet packet2 = new Packet(EVT.CLIENT_JOIN_TABLE,
                                GameUtil.gson.toJson(opChange.getItemPlayer()));
                        table.getNotifier()
                                .notifyAllPlayersExceptOne(
                                        GameUtil.toDataAction(0, table.getId(),
                                                packet2),
                                        opChange.getPid());
                        LOGGER.info(new PlayerAction(EVT.CLIENT_JOIN_TABLE, "", opChange.getPid(), table.getId(), packet2));

                        Packet packet3 = new Packet(EVT.CLIENT_OTHER_JOIN, GameUtil.gson.toJson(getTable(table)));
                        table.getNotifier().notifyPlayer(opChange.getPid(), GameUtil.toDataAction(0, table.getId(),
                                packet3));
                        LOGGER.info(new PlayerAction(EVT.CLIENT_OTHER_JOIN, opChange.getUsername(), opChange.getPid(), table.getId(), packet3));
                    }
                }
            }

            for (int i = 0; i < players.size(); i++) {
                players.get(i).setIsStart(true);
                players.get(i).setIsDeclared(false);
            }

            if (players.size() < 1) {
                this.ownerId = 0;
                players.clear();
                table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.STATED, 1);
            } else {
                removeBotIfExist(table, serviceContract);
                table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.START_GAME, 0);
                table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.STATED, 0);
            }

            int tablebot = 0;
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUsertype() < 10) {
                    tablebot = 1;
                }
            }
            table.getAttributeAccessor().setIntAttribute(TableLobbyAttribute.TABLE_BOT, tablebot);

            GameObjectAction goa = new GameObjectAction(table.getId());
            LocalEvt le = new LocalEvt();
            le.setEvt(EVT.OBJECT_AUTO_START);
            le.setPid(players.get(0).getPid());
            goa.setAttachment(le);

            table.getScheduler().scheduleAction(goa, TimeAction.START_COUNTDOWN.getValue());
            table.getAttributeAccessor().setDateAttribute(TableLobbyAttribute.COUNTDOWN_START, new Date());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    private List<LogIFRSPlayer> listlogIFRSPlayer = new ArrayList<>();
    private LogTable Logtable = new LogTable();

    public LogTable getLogtable() {
        return Logtable;
    }

    public void setLogtable(LogTable logtable) {
        Logtable = logtable;
    }

    private void writeLog(Table table) {
        try {
            for (int i = 0; i < players.size(); i++) {
                for (String[] logGame : getLogtable().getLogGame()) {
                    if (logGame[0].equals(players.get(i).getUsername())) {
                        logGame[2] = players.get(i).getAG().toString();
                        break;
                    }
                }

                for (int j = 0; j < listlogIFRSPlayer.size(); j++) {
                    if (listlogIFRSPlayer.get(j).getUserid() == players.get(i).getUserid()) {
                        listlogIFRSPlayer.get(j).setGoldtransfer(players.get(i).getAG().intValue() - listlogIFRSPlayer.get(j).getGold());
                        listlogIFRSPlayer.get(j).setGold(players.get(i).getAG().intValue());
                        break;
                    }
                }
            }
            Logtable.setSumMarkEnd(getSumMark());
            ActivatorImpl.addLogTable(Logtable);
            ActivatorImpl.addLogIFRSTable(listlogIFRSPlayer);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private long Hesovip(long mark, int vip) {
        switch (vip) {
            case 10:
                return mark * 97 / 100;
            case 9:
                return mark * 96 / 100;
            case 8:
                return mark * 95 / 100;
            case 6:
            case 5:
            case 7:
                return mark * 94 / 100;
            case 4:
            case 3:
            case 2:
            case 1:
            case 0:
                return mark * 93 / 100;
            default:
                return mark * 93 / 100;
        }
    }

    /*
    *    
     */
    public void playTimeout(Table table, int playerId, TurnStatus turnStatus, int cardId) {
        synchronized (lock) {
            try {
                /// kiem tra trang thai turn ma player lua chon
                if (null != turnStatus) {
                    switch (turnStatus) {
                        case TAKECARD:
                            takeCardDeck(table, playerId, true);
                            break;
                        case DISCARD:
                            disCard(table, playerId, null, true);
                            break;
                        case DECLARE:
                            declare(table, playerId, null, true);
                            break;

                        case CONFIRM_DECLARE:
                            cancelConfirmDeclare(table, playerId);
                        case BOT_TAKE_CARD_PLACE:
                            ///
                            takeCardPlace(table, playerId, cardId, true);
                            break;
                        case BOT_NOTIDECLARE:
                            notiDeclare(table, playerId, cardId);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    private void confirmRoom(int playerId, int roomId, int tableId, Integer oldroom, int mark, ServiceContract serviceContract) {
        serviceContract.confirmSelectRoom_Only(playerId, roomId, tableId, mark);
    }

    private int getNumberStraightRequired() {
        return 5 - players.size();
    }

}
