import model.Action;
import model.Entity;
import model.PlayerView;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalManager {
    // менеджер отвечает за экономическую состовляющую
    EconomicManager mEconomicManager;
    // менеджер отвечающий за бой / защиту
    WarManager mWarManager;


    // количество от
    GlobalStatistic mGlobalStatistic;

    GlobalMap mGlobalMap;



    public GlobalManager(){
        mEconomicManager = new EconomicManager();
        mWarManager = new WarManager();
        mGlobalStatistic = new GlobalStatistic();
        mGlobalMap = new GlobalMap();
    }


    public Action update(PlayerView playerView, DebugInterface debugInterface){
        HashMap<Integer, model.EntityAction> hashMap = new HashMap<>();
        // глобальная статистика и информация о мире


        mGlobalStatistic.updateInfo(playerView,this);
        mGlobalMap.update(getGlobalStatistic());

        /////////////////////////////////////////////////////

        hashMap.putAll(mEconomicManager.update(playerView,this));

        hashMap.putAll(mWarManager.update(playerView,this));

        return new Action(hashMap);
    }

    public EconomicManager getEconomicManager() {
        return mEconomicManager;
    }

    public GlobalStatistic getGlobalStatistic() {
        return mGlobalStatistic;
    }

    public WarManager getWarManager() {
        return mWarManager;
    }

    public GlobalMap getGlobalMap() {
        return mGlobalMap;
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        mGlobalMap.debugUpdate(playerView,debugInterface);
    }
}
