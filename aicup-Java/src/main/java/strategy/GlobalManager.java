package strategy;

import model.*;
import strategy.map.potfield.MapPotField;
import strategy.map.wave.SearchAnswer;
import strategy.map.wave.WaveSearch;

import java.util.ArrayList;
import java.util.HashMap;

import static strategy.Final.INFO_UNIT;
import static strategy.Final.debugRelease;

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


    public GlobalManager() {
        mEconomicManager = new EconomicManager();
        mWarManager = new WarManager();
        mGlobalStatistic = new GlobalStatistic();
        mGlobalMap = new GlobalMap();

        allTime = 0;

        mMapPotField = new MapPotField(80);
    }


    public Action update(PlayerView playerView, DebugInterface debugInterface) {
        startTime = System.nanoTime();

        HashMap<Integer, model.EntityAction> hashMap = new HashMap<>();
        // глобальная статистика и информация о мире


        mGlobalStatistic.updateInfo(playerView, this);
        mGlobalMap.update(getGlobalStatistic());
        mMapPotField.update(this);

        ////////////////////////STATISTIC/////////////////////////////

        Final.DEBUG("", "Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap() + " ID:" + getGlobalStatistic().getMyPlayer().getId() + " Alltime: " + allTime / 1000000);

        if (Final.debugRelease) {
            if (FinalConstant.getCurrentTik() == 0) {
                Final.DEBUGRelease("", "Tik: " + FinalConstant.getCurrentTik() + " ID:" + getGlobalStatistic().getMyPlayer().getId());
            }

            if (FinalConstant.getCurrentTik() % 50 == 0) {

                Final.DEBUGRelease("", "Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap() + " Alltime: " + allTime / 1000000);
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
        hashMap.putAll(mWarManager.update(playerView, this));

        hashMap.putAll(mEconomicManager.update(playerView, this));


        allTime += System.nanoTime() - startTime;

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
        mGlobalMap.debugUpdate(playerView, debugInterface);
        mMapPotField.debugUpdate(playerView, debugInterface);

        if (INFO_UNIT) {
            MyPlayer myPlayer = mGlobalStatistic.getMyPlayer();

            ArrayList<MyEntity> myEntities = myPlayer.getUnitArrayList();

            for (int i = 0; i < myEntities.size(); i++) {
                Vec2Float vec2Float = myEntities.get(i).getPosition().getVec2Float();
                vec2Float.setY(vec2Float.getY()+ 0.45f);
                FinalGraphic.sendText(debugInterface, vec2Float, 11, myEntities.get(i).toString());
            }
        }


        if (Final.CHECK_SEARCH_WAVE_BUILDER)
        {
            MyEntity builderPos = mGlobalStatistic.getMyPlayer().getBuilderBase();

            Vec2Int vec2Int = builderPos.getPosition();

            ArrayList<Vec2Int> arrayList = mGlobalMap.getCoordAround(vec2Int,5,true);

            WaveSearch waveSearch = new WaveSearch(80);

            waveSearch.initMap(mGlobalMap.getMap());

            SearchAnswer searchAnswer = waveSearch.waveSearchNeedEntity(arrayList,50, EntityType.RESOURCE);

            waveSearch.debugUpdate(playerView,debugInterface);

            if (searchAnswer == null) {
                int k =0;
            }
            else {
                FinalGraphic.sendSquare(debugInterface, searchAnswer.getStart(), 1, FinalGraphic.COLOR_BLUE);
                FinalGraphic.sendSquare(debugInterface,searchAnswer.getEnd(), 1, FinalGraphic.COLOR_GREEN);
            }

        }
    }


    private void logInfo() {
        for (int i = 0; i < getGlobalStatistic().getPlayers().size(); i++) {
            MyPlayer myPlayer = getGlobalStatistic().getPlayers().get(i);
            Final.DEBUGRelease("", myPlayer.toString());
            //   strategy.Final.DEBUGRelease(TAG,"Player: " + myPlayer.getId() + " B: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size() + " M: " + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size() + " R: " + myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + " DeadB: " + myPlayer.getCountDeadBiuld() + " DeadM: " + myPlayer.getCountDeadMelee() + " DeadR: " + myPlayer.getCountDeadRange() + " DeadH " + myPlayer.getCountDeadHouse() );
        }
    }

    public MapPotField getMapPotField() {
        return mMapPotField;
    }
}
