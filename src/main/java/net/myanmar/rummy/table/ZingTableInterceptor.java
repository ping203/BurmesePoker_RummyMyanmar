/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.myanmar.rummy.table;

import com.athena.services.api.ServiceContract;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.SeatRequest;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableInterceptor;
import net.myanmar.rummy.logic.RummyBoard;
import org.apache.log4j.Logger;


/**
 *
 * @author UserXP
 */
public class ZingTableInterceptor implements TableInterceptor {

    private static final Logger LOGGER = Logger.getLogger(ZingTableInterceptor.class);

    private ServiceContract serviceContract;
    public ZingTableInterceptor(GameContext context) {
        this.serviceContract = context.getServices().getServiceInstance(ServiceContract.class);
    }
    
    @Override
    public InterceptionResponse allowJoin(Table table, SeatRequest request) {
        try{
            RummyBoard board = (RummyBoard)table.getGameState().getState();
            return board.allowJoin(table,request, serviceContract);
        }catch(Exception ex){
            LOGGER.error(ex.getMessage(), ex);
            return new InterceptionResponse(false, -2);
        }
    }

    @Override
    public InterceptionResponse allowReservation(Table table, SeatRequest request) {
        return new InterceptionResponse(true, 0);
    }

    @Override
    public InterceptionResponse allowLeave(Table table, int playerId) {
    	try{
            RummyBoard board = (RummyBoard)table.getGameState().getState();
            return board.allowLeave(table,playerId);
        }catch(Exception ex){
            LOGGER.error(ex.getMessage(), ex);
            return new InterceptionResponse(false, -2);
        }
    }
}
