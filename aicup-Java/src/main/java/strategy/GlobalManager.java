package strategy;

import model.Action;
import model.PlayerView;
import model.Vec2Float;
import strategy.map.potfield.MapPotField;

import java.util.ArrayList;
import java.util.HashMap;

import static strategy.Final.INFO_UNIT;

public class GlobalManager {
    // менеджер отвечает за экономическую состовляющую
    EconomicManager mEconomicManager;
    // менеджер отвечающий за бой / защиту
    WarManager mWarManager;

    // количество от
    GlobalStatistic mGlobalStatistic;

    GlobalMap mGlobalMap;
    MapPotField mMapPotField;

    long startTime;

    long allTime = 0;



    public GlobalManager(){
        mEconomicManager = new EconomicManager();
        mWarManager = new WarManager();
        mGlobalStatistic = new GlobalStatistic();
        mGlobalMap = new GlobalMap();

        allTime = 0;

        mMapPotField = new MapPotField(80);
    }


    public Action update(PlayerView playerView, DebugInterface debugInterface){
        startTime = System.nanoTime();

        HashMap<Integer, model.EntityAction> hashMap = new HashMap<>();
        // глобальная статистика и информация о мире


        mGlobalStatistic.updateInfo(playerView,this);
        mGlobalMap.update(getGlobalStatistic());
        mMapPotField.update(this);

        ////////////////////////STATISTIC/////////////////////////////

        Final.DEBUG("","Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap()+ " ID:" + getGlobalStatistic().getMyPlayer().getId() +" Alltime: " + allTime/1000000);

        if (Final.debugRelease)
        {
            if (FinalConstant.getCurrentTik()==1) {
                Final.DEBUGRelease("", "Tik: " + FinalConstant.getCurrentTik() +  " ID:" + getGlobalStatistic().getMyPlayer().getId());
            }

            if (FinalConstant.getCurrentTik()%50==0) {

                Final.DEBUGRelease("","Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap() +" Alltime: " + allTime/1000000);
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
        hashMap.putAll(mWarManager.update(playerView,this));

        hashMap.putAll(mEconomicManager.update(playerView,this));


        allTime +=System.nanoTime()-startTime;

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
        mMapPotField.debugUpdate(playerView,debugInterface);

        if (INFO_UNIT) {
            MyPlayer myPlayer = mGlobalStatistic.getMyPlayer();

            ArrayList<MyEntity> myEntities = myPlayer.getUnitArrayList();

            for (int i = 0; i < myEntities.size(); i++) {
                Vec2Float vec2Float = myEntities.get(i).getPosition().getVec2Float();
                vec2Float.setY(vec2Float.getY()+0.5f);
                FinalGraphic.sendText(debugInterface,vec2Float,15,myEntities.get(i).toString());
            }
        }
    }


    private void logInfo() {
        for (int i=0; i<getGlobalStatistic().getPlayers().size(); i++)
        {
            MyPlayer myPlayer = getGlobalStatistic().getPlayers().get(i);
            Final.DEBUGRelease("",myPlayer.toString());
            //   strategy.Final.DEBUGRelease(TAG,"Player: " + myPlayer.getId() + " B: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size() + " M: " + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size() + " R: " + myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + " DeadB: " + myPlayer.getCountDeadBiuld() + " DeadM: " + myPlayer.getCountDeadMelee() + " DeadR: " + myPlayer.getCountDeadRange() + " DeadH " + myPlayer.getCountDeadHouse() );

        }
    }

    public MapPotField getMapPotField() {
        return mMapPotField;
    }
}