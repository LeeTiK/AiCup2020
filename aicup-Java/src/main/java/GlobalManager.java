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



    public GlobalManager(){
        mEconomicManager = new EconomicManager();
        mWarManager = new WarManager();
        mGlobalStatistic = new GlobalStatistic();
    }


    public Action update(PlayerView playerView, DebugInterface debugInterface){
        HashMap<Integer, model.EntityAction> hashMap = new HashMap<>();
        // глобальная статистика и информация о мире
        mGlobalStatistic.updateInfo(playerView,debugInterface);

        /////////////////////////////////////////////////////

        hashMap.putAll(mEconomicManager.update(playerView,mGlobalStatistic));

        hashMap.putAll(mWarManager.update(playerView,mGlobalStatistic));

        return new Action(hashMap);
    }
}
