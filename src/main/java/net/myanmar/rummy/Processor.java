package net.myanmar.rummy;

import com.athena.services.api.ServiceContract;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.table.Table;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.myanmar.rummy.event.EVT;
import net.myanmar.rummy.log.PlayerAction;
import net.myanmar.rummy.logic.RummyBoard;

import net.myanmar.rummy.vo.LocalEvt;
import net.myanmar.rummy.logic.votransfer.Packet;
import net.myanmar.rummy.utils.GameUtil;

public class Processor implements GameProcessor {

    private static final Logger LOGGER = Logger.getLogger("RUMMY_PROCESSOR");
    private final JsonParser parser = new JsonParser();

    private final ServiceContract serviceContract;

    public Processor(GameContext context) {
        this.serviceContract = context.getServices().getServiceInstance(ServiceContract.class);
    }

    @Override
    public void handle(GameDataAction action, Table table) {
        try {
            String message = new String(action.getData().array());
            LOGGER.info("tableId:" + table.getId() + "Player:  " + action.getPlayerId() + " Incoming message: " + message);
            JsonObject je = (JsonObject) parser.parse(message);
            RummyBoard board = (RummyBoard) table.getGameState().getState();

            if (je.get("evt") != null) {
                String evt = je.get("evt").getAsString();

                switch (evt) {

                    case EVT.DATA_READY_TABLE:
                        board.readyTable(table, action.getPlayerId());
                        break;
                    case EVT.DATA_START_GAME:
                        board.startGame(table, action.getPlayerId());
                        break;
                    case EVT.DATA_TAKE_CARD_DECK:
                        board.takeCardDeck(table, action.getPlayerId(), false);
                        break;
                    case EVT.DATA_DISCARD:
                        int[] is = GameUtil.gson.fromJson(je.get("C").getAsJsonArray(), int[].class);
                        board.disCard(table, action.getPlayerId(), is, false);
                        break;
                    case EVT.DATA_TAKE_CARD_PLACES:
                        int icard1 = je.get("C").getAsInt();
                        board.takeCardPlace(table, action.getPlayerId(), icard1, false);
                        break;
                    case EVT.DATA_NOTICE_DECLARE:
                        int icard2 = je.get("C").getAsInt();
                        board.notiDeclare(table, action.getPlayerId(), icard2);
                        break;
                    case EVT.DATA_PLAYER_DECLARE:
                        List<List<Double>> doubles = GameUtil.gson.fromJson(je.get("Arr").getAsJsonArray(), ArrayList.class);

                        List<List<Integer>> lsphom = new ArrayList<>();
                        for (List<Double> aDouble : doubles) {
                            List<Integer> integers = new ArrayList<>();
                            for (Double double1 : aDouble) {
                                integers.add(double1.intValue());
                            }
                            lsphom.add(integers);
                        }

                        board.declare(table, action.getPlayerId(), lsphom, false);
                        break;
                    case EVT.DATA_FOLD_CARD:
                        board.foldCard(table, action.getPlayerId(), false);
                        break;
                    case EVT.DATA_HACK:
//                        board.Hacking(this.game.getServiceContract(), action.getPlayerId());
                        break;
                    case EVT.DATA_AMUVIP:
                        board.serviceUpAG(table, action.getPlayerId(), je.get("ag").getAsLong(), serviceContract);
                        break;
                    case EVT.DATA_AGIAP:
                        board.serviceUpAGIAP(table, action.getPlayerId(), serviceContract);
                        break;

                    case EVT.DATA_CHAT_TABLE:
                        board.chatTable(table, action);
                        break;
                    case EVT.DATA_KICK_TABLE:
                        int pid = board.kickTable(je.get("Name").getAsString(), action.getPlayerId());
                        if (pid != 0) {
                            LeaveAction la = new LeaveAction(pid, table.getId());
                            table.getScheduler().scheduleAction(la, 0);

                            Packet packet = new net.myanmar.rummy.logic.votransfer.Packet("0", GameUtil.strKick_Err1_TH);
                            table.getNotifier().notifyPlayer(pid, GameUtil.toDataAction(pid, table.getId(),
                                    packet));

                            LOGGER.info(new PlayerAction(EVT.DATA_KICK_TABLE, "", pid, table.getId(), packet));

                        } else {
                            Packet packet = new net.myanmar.rummy.logic.votransfer.Packet("0", GameUtil.strKick_Err2_TH);
                            table.getNotifier().notifyPlayer(pid, GameUtil.toDataAction(pid, table.getId(),
                                    packet));

                            LOGGER.info(new PlayerAction(EVT.DATA_KICK_TABLE, "", pid, table.getId(), packet));
                        }
                        break;
                    ///====> SEND GAME DATA ====> {"evt":"autoExit"}
                    case EVT.DATA_AUTO_EXIT:
                        board.autoExit(table, action.getPlayerId());
                        break;
                    case EVT.DATA_UVIP:
                        board.UVipTable(table, action.getPlayerId(), je.get("ag").getAsInt());
                        break;
                    case EVT.DATA_TIP_DEALER:
                        board.tipDealer(table, action.getPlayerId(), serviceContract);
                        break;

                    case EVT.DATA_CONFIRM_DECLARE:

                        boolean b = je.get("confirm").getAsBoolean();
                        board.cancelConfirmDeclare(table, action.getPlayerId());
                        break;
                    default:
                        break;
                }

            } else {

            }
        } catch (JsonSyntaxException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void handle(GameObjectAction action, Table table) {
        try {
            RummyBoard board = (RummyBoard) table.getGameState().getState();
            LocalEvt le = (LocalEvt) action.getAttachment();
            LOGGER.info(" - tableId:" + table.getId() + " - " + le.getEvt());

            switch (le.getEvt()) {
                case EVT.OBJECT_FINISH:
                    board.finishGame(table, serviceContract);
                    break;

                case EVT.OBJECT_AUTO_START:
                    board.startGame(table, le.getPid());
                    break;
                case EVT.OBJECT_AUTO_READY:
                    board.autoReadyTable(table);
                    break;
                case EVT.OBJECT_TAKE_CARD_DECK:
                    board.takeCardDeck(table, le.getPid(), true);
                    break;
                case EVT.OBJECT_DISCARD:
//                    int icard = le.getIcard();
//                    board.disCard(table, le.getPid(), icard, true);
                    break;
                case EVT.OBJECT_TAKE_CARD_PLACES:
//                    int icard1 = le.getIcard();
////				table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(action.getPlayerId(), table.getId(), new Packet("ancard 0", "" + icard1)));
//                    board.takeCardPlace(table, le.getPid(), icard1, false);
                    break;
                case EVT.OBJECT_FOLD_CARD:
                    board.foldCard(table, le.getPid(), true);
                    break;
                case EVT.OBJECT_TIMEOUT:
                    board.playTimeout(table, le.getPid(), le.getTurnStatus(), le.getCardId());
                    break;
                case EVT.OBJECT_PLAYER_DISCONNECT:
                    board.playerDisconnected(table, le.getPid());
                    break;
                case EVT.OBJECT_TURN_DISCONNECT:
                    if (null != le.getTurnStatus()) {
                        switch (le.getTurnStatus()) {
                            case TAKECARD:
                                board.takeCardDeck(table, le.getPid(), true);
                                break;
                            case DISCARD:
                                board.disCard(table, le.getPid(), null, true);
                                break;
                            case DECLARE:
                                board.declare(table, le.getPid(), null, true);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case EVT.OBJECT_BOT_SHOT:
                    board.botShot(table, le.getPid(), true, serviceContract, le.getTurnStatus());
                    break;
                case EVT.OBJECT_NOTICE_DECLARE:
//                    int icard2 = le.getIcard();
//                    board.NotiDeclare(table, le.getPid(), icard2);
                    break;
                case EVT.OBJECT_PLAYER_DECLARE:
//                    List<List<Double>> lsphom = le.getArrCard();
//                    board.Declare(table, le.getPid(), lsphom, true);
                    break;
                case EVT.OBJECT_REMOVE_BOT:
//                    board.removeBot(table, this.game.getServiceContract());
//                    BOT HANDLE
                    break;
                case EVT.OBJECT_CHECK_GET_BOT:
                    board.startTimeOutGetBot(table);
                    break;
                case EVT.OBJECT_GET_BOT:
                    serviceContract.getUserGameBot(board.getMark(), (short) table.getMetaData().getGameId(), table.getId(), 0);
                    break;
                case EVT.OBJECT_READY_BOT:
//                    board.readyBot(table, le.getPid());
//                    BOT HANDLE
                    break;

                default:
                    break;
            }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
