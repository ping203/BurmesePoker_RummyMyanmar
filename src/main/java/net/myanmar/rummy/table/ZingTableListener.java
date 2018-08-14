/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.table;

import com.athena.services.api.ServiceContract;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableListener;
import net.myanmar.rummy.event.EVT;
import net.myanmar.rummy.logic.RummyBoard;

import net.myanmar.rummy.vo.LocalEvt;
import org.apache.log4j.Logger;

/**
 *
 * @author UserXP
 */
public class ZingTableListener implements TableListener {
   private static final Logger LOGGER = Logger.getLogger(ZingTableListener.class);

   private final ServiceContract serviceContract;
    public ZingTableListener(GameContext context) {
        this.serviceContract = context.getServices().getServiceInstance(ServiceContract.class);
    }
    
    @Override
    public void playerJoined(Table table, GenericPlayer player) {
        try {
            RummyBoard board = (RummyBoard)table.getGameState().getState();
            board.playerJoin(table, player.getPlayerId(), serviceContract);
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void playerLeft(Table table, int player) { 
        try{
            RummyBoard board = (RummyBoard)table.getGameState().getState();
            board.playerLeft(table, player, serviceContract);
        }catch(Exception e ){
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void playerStatusChanged(Table table, int player, PlayerStatus status) { 
        LOGGER.debug("tableId:" + table.getId() + " player:" + player + " status:" + status);
        if(status.equals(PlayerStatus.DISCONNECTED) || status.equals(PlayerStatus.WAITING_REJOIN) || status.equals(PlayerStatus.LEAVING)){
            GameObjectAction goa = new GameObjectAction(table.getId());
            LocalEvt le = new LocalEvt();
            le.setEvt(EVT.OBJECT_PLAYER_DISCONNECT);
            le.setPid(player);
            goa.setAttachment(le);
            table.getScheduler().scheduleAction(goa, 0);
        }
    }

    @Override
    public void seatReserved(Table table, GenericPlayer player) { 
    }

    @Override
    public void watcherJoined(Table table, int player) { 
    }

    @Override
    public void watcherLeft(Table table, int player) { 
    }
}
