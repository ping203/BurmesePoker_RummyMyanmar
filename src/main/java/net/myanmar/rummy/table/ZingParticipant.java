/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.table;

import java.util.Date;

import net.myanmar.rummy.vo.UserGame;
import com.cubeia.firebase.api.action.JoinRequestAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.game.GameDefinition;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.activator.RequestCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyPath;
import net.myanmar.rummy.config.Config;

import net.myanmar.rummy.logic.Player;
import net.myanmar.rummy.logic.RummyBoard;
import net.myanmar.rummy.table.attribute.TableLobbyAttribute;
import net.myanmar.rummy.utils.GameUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author UserXP
 */
public class ZingParticipant implements RequestCreationParticipant {

    private static final Logger LOGGER = Logger.getLogger(ZingParticipant.class);
    
    //private final Game game;
    //private final BacayBoard board;
    private final int pid;
    private final Attribute[] atts;
    private final UserGame uinfo;
    private final ActivatorContext activatorContext;

    public ZingParticipant(int pid, Attribute[] att, UserGame uinfo, ActivatorContext context) {
        this.pid = pid;
        this.atts = att;
        this.uinfo = uinfo;
        this.activatorContext = context;
    }

    @Override
    public void tableCreated(Table table, LobbyTableAttributeAccessor acc) {
        try {
            RummyBoard board = new RummyBoard();
            board.setBoard(this.pid, this.atts, activatorContext, table);
            Player opnew = new Player(uinfo);
            board.getPlayers().add(opnew);
            table.getGameState().setState(board);
            acc.setIntAttribute(TableLobbyAttribute.STATED, 0);
            acc.setDateAttribute(TableLobbyAttribute.LAST_CONNECT, new Date());
            acc.setIntAttribute(TableLobbyAttribute.START_GAME, 0);
//            acc.setStringAttribute(TableLobbyAttribute.NAME, "Table " + table.getId());
//            acc.setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_NAME, GameUtil.gson.toJson(board.getArrName()));
            acc.setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_ID, GameUtil.gson.toJson(board.getArrId()));
//            acc.setStringAttribute(TableLobbyAttribute.ARRAY_PLAYER_GOLD, GameUtil.gson.toJson(board.getArrAG()));
            acc.setIntAttribute(TableLobbyAttribute.MARK, board.getMark());
//        acc.setIntAttribute("T", board.getTableType());
            acc.setIntAttribute(TableLobbyAttribute.CHIP, Config.getBoundGold(board.getMark()));
            acc.setIntAttribute(TableLobbyAttribute.VIP, board.getMinVip());
            acc.setIntAttribute(TableLobbyAttribute.PLAYER, RummyBoard.MAX_PLAYER);
//        acc.setIntAttribute("D", board.getDiamond()) ; //Ban Diamond
            acc.setIntAttribute(TableLobbyAttribute.TABLE_BOT, 0);
            acc.setIntAttribute(TableLobbyAttribute.DECLARE, 0);
            JoinRequestAction join = new JoinRequestAction(pid, table.getId(), 0, opnew.getUsername());
            table.getScheduler().scheduleAction(join, 0);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    @Override
    public LobbyPath getLobbyPathForTable(Table table) {

//        if (uinfo.getRoomId() == 0) {
//            uinfo.setRoomId(1);
//        }
//        String lobby = uinfo.getLevelId() + "/" + uinfo.getRoomId() + "/";
        // System.out.println("Roomid =" + uinfo.getRoomId()+ "-lobby:"+ lobby);
        String lobby = "test";
        LOGGER.info(this.atts);
        return new LobbyPath(table.getMetaData().getGameId(), lobby, table.getId());
        
    }

    @Override
    public String getTableName(GameDefinition def, Table table) {
        return "Poker[" + table.getId() + "]";
    }

    @Override
    public boolean reserveSeatsForInvitees() {
        return true;
    }

    @Override
    public int[] modifyInvitees(int[] invitees) {
        return invitees;
    }
}
