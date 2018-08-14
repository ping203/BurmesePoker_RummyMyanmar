package net.myanmar.rummy;

import com.cubeia.firebase.api.game.Game;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.TableInterceptorProvider;
import com.cubeia.firebase.api.game.TableListenerProvider;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableInterceptor;
import com.cubeia.firebase.api.game.table.TableListener;

import net.myanmar.rummy.table.ZingTableInterceptor;
import net.myanmar.rummy.table.ZingTableListener;
import org.apache.log4j.Logger;


public class GameImpl implements Game, TableListenerProvider,TableInterceptorProvider  {
    private static final Logger LOGGER = Logger.getLogger(GameImpl.class);
    private GameContext context;
    @Override
    public void init(GameContext con) {
        this.context = con;
    }

    @Override
    public GameProcessor getGameProcessor() {
        return new Processor(context);
    }
    
    @Override
    public TableListener getTableListener(Table table) {
        return new ZingTableListener(context);
    }
	
    @Override
    public void destroy() { }

    @Override
    public TableInterceptor getTableInterceptor(Table table) {
        return new ZingTableInterceptor(context);
    }
    
}
