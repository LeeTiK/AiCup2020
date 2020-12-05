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

        ////////////////////////STATISTIC/////////////////////////////

        Final.DEBUG("","Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap()+ " ID:" + getGlobalStatistic().getMyPlayer().getId());

        if (Final.debugRelease)
        {
            if (FinalConstant.getCurrentTik()%50==0) {
                Final.DEBUGRelease("","Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap());
                logInfo();
            }
        }

        ////////////////////////


        if (Final.TEST) {
            if (getGlobalStatistic().getMyPlayer().getBuildingArrayList().size() > 5) {
                HashMap<Integer, model.EntityAction> hashMap1 = new HashMap<>();


                return new Action(hashMap1);
            }
        }


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


    private void logInfo() {
        for (int i=0; i<getGlobalStatistic().getPlayers().size(); i++)
        {
            MyPlayer myPlayer = getGlobalStatistic().getPlayers().get(i);
            Final.DEBUGRelease("",myPlayer.toString());
            //   Final.DEBUGRelease(TAG,"Player: " + myPlayer.getId() + " B: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size() + " M: " + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size() + " R: " + myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + " DeadB: " + myPlayer.getCountDeadBiuld() + " DeadM: " + myPlayer.getCountDeadMelee() + " DeadR: " + myPlayer.getCountDeadRange() + " DeadH " + myPlayer.getCountDeadHouse() );

        }
    }

}
