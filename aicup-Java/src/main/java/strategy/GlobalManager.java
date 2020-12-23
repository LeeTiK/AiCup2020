package strategy;

import model.*;
import strategy.map.potfield.MapPotField;
import strategy.map.wave.SearchAnswer;
import strategy.map.wave.WaveSearchModule;

import java.util.ArrayList;
import java.util.HashMap;

import static strategy.Final.*;

public class GlobalManager {
    // менеджер отвечает за экономическую состовляющую
    EconomicManager mEconomicManager;
    // менеджер отвечающий за бой / защиту
    WarManager mWarManager;

    // количество от
    GlobalStatistic mGlobalStatistic;

    GlobalMap mGlobalMap;
    MapPotField mMapPotField;
    static WaveSearchModule waveSearchModule;

    MoveManager mMoveManager;

    long startTime;
    long timeParsing;

    long timeAllStrategy = 0;
    long timeParsingAll = 0;

    HashMap<Integer, model.EntityAction> mActionHashMap;


    public GlobalManager() {
        mEconomicManager = new EconomicManager();
        mWarManager = new WarManager();
        mGlobalStatistic = new GlobalStatistic();
        mGlobalMap = new GlobalMap();
        waveSearchModule = new WaveSearchModule(mGlobalMap);

        mMoveManager = new MoveManager();

        timeAllStrategy = 0;
        timeParsing = 0;

        mMapPotField = new MapPotField(80);

        mActionHashMap = new HashMap<>();
    }


    public Action update(PlayerView playerView, DebugInterface debugInterface, long timeStart) {
        if (timeStart!=0)
        {
            timeParsingAll+=(System.nanoTime()-timeStart);
        }
        if (timeParsing!=0)
        {
            timeParsingAll+=(System.nanoTime()-timeParsing);
        }
        startTime = System.nanoTime();

        mActionHashMap.clear();
        // глобальная статистика и информация о мире


        mGlobalStatistic.updateInfo(playerView, this);

        if (FinalConstant.isFogOfWar())
        {
            A_STAR = true;
        }

        mGlobalMap.update(getGlobalStatistic(),mMapPotField);
        mMapPotField.update(this);
        waveSearchModule.updateMap(mMapPotField.getMapPotField());

        mMoveManager.update(mGlobalMap,mMapPotField,debugInterface,mActionHashMap);


        debugGraphic(playerView,debugInterface);
        ////////////////////////STATISTIC/////////////////////////////

        Final.DEBUG("", "Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap() + " ID:" + getGlobalStatistic().getMyPlayer().getId() + " timeStrategy: " + timeAllStrategy / 1000000 + " timeParsing: " + timeParsingAll/1000000);

        if (Final.debugRelease) {
            if (FinalConstant.getCurrentTik() == 0) {
                Final.DEBUGRelease("", "Tik: " + FinalConstant.getCurrentTik() + " ID:" + getGlobalStatistic().getMyPlayer().getId());
            }

            if (FinalConstant.getCurrentTik() % 50 == 0) {

                Final.DEBUGRelease("", "Tik: " + FinalConstant.getCurrentTik() + " resource: " + getGlobalMap().getResourceMap() + " timeStrategy: " + timeAllStrategy / 1000000 + " timeParsing: " + timeParsingAll/1000000);
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

        ArrayList<MyEntity> arrayListAllunit = getGlobalStatistic().getMyPlayer().getUnitArrayList();

        for (int i = 0; i < arrayListAllunit.size(); i++){
            arrayListAllunit.get(i).setUpdate(false);
        }



       mWarManager.update(playerView, this,debugInterface,mActionHashMap);

        mEconomicManager.update(playerView, this,debugInterface,mActionHashMap);

        mWarManager.updateWarBuilder(this,mActionHashMap);

        // проверяем всех юнитов чтобы уступили дорогу
        if (A_NEED_MOVE)
        {
            mMoveManager.calculateDodgeUnits(playerView,this,mActionHashMap);
        }

        timeAllStrategy += System.nanoTime() - startTime;

        if (Final.debug)
        {
            for ( Integer key : mActionHashMap.keySet() ) {
                boolean check = false;
                Entity entity = null;
                for (int i=0; i<playerView.getEntities().length; i++)
                {
                    if (key==playerView.getEntities()[i].getId()){
                        entity = playerView.getEntities()[i];
                    }
                    if (playerView.getEntities()[i].getPlayerId()==null) continue;
                    if (key==playerView.getEntities()[i].getId() && playerView.getEntities()[i].getPlayerId()==FinalConstant.getMyID()){
                        check = true;
                        break;
                    }


                }

                if (!check)
                {

                    Final.DEBUG("JOPA!!!!", "id: " + key + " entity: " + entity);
                }
            }
        }

        //f (Final.)

        debugGraphicV2(playerView,debugInterface);

        timeParsing = System.nanoTime();

        return new Action(mActionHashMap);
    }

    private void debugGraphic(PlayerView playerView, DebugInterface debugInterface){
        if (Final.debugGraphic)
        {
            mGlobalMap.debugUpdate(playerView, debugInterface);
            mMapPotField.debugUpdate(playerView, debugInterface);

            if (HP_NO_ACTIVE)
            {
                MyPlayer myPlayer = mGlobalStatistic.getMyPlayer();

                ArrayList<MyEntity> myEntities = myPlayer.getBuildingArrayList();

                for (int i = 0; i < myEntities.size(); i++) {
                    FinalGraphic.sendText(debugInterface, new Vec2Float(myEntities.get(i).getPosition().getX() * 1.0f, myEntities.get(i).getPosition().getY() * 1.0f + 0.2f), 11, "" +
                            myEntities.get(i).getHealth());
                }
            }

            if (false)
            {
                Vec2Int start = Vec2Int.createVector(10,10);
                for (int i=0; i<GlobalMap.rangerTwoContourArray.length; i++)
                {
                    FinalGraphic.sendSquare(debugInterface, start.add(GlobalMap.rangerTwoContourArray[i][0],GlobalMap.rangerTwoContourArray[i][1]), 1, FinalGraphic.COLOR_BLACK);
                }
            }


            if (Final.CHECK_SEARCH_WAVE_BUILDER)
            {
                MyEntity builderPos = mGlobalStatistic.getMyPlayer().getBuilderBase();

                Vec2Int vec2Int = builderPos.getPosition();

                ArrayList<Vec2Int> arrayList = mGlobalMap.getCoordAround(vec2Int,5,true);

                waveSearchModule.updateMap(mMapPotField.getMapPotField());

                SearchAnswer searchAnswer = waveSearchModule.waveSearchNeedEntity(arrayList,50, EntityType.RESOURCE);

                waveSearchModule.debugUpdate(playerView,debugInterface);

                if (searchAnswer == null) {
                    int k =0;
                }
                else {
                    FinalGraphic.sendSquare(debugInterface, searchAnswer.getStart(), 1, FinalGraphic.COLOR_BLUE);
                    FinalGraphic.sendSquare(debugInterface,searchAnswer.getEnd(), 1, FinalGraphic.COLOR_GREEN);
                }

            }
        }

    }

    private void debugGraphicV2(PlayerView playerView, DebugInterface debugInterface){
        if (Final.debugGraphic)
        {
            if (NEXT_TIK_POSITION)
            {
                for (int i=0; i<getGlobalMap().getMapNextTick().length; i++)
                {
                    for (int j=0; j<getGlobalMap().getMapNextTick().length; j++)
                    {
                        MyEntity entity = getGlobalMap().getMapNextTick()[i][j];
                        if (entity.getPlayerId()==null) continue;
                        if (entity.getPlayerId()!=FinalConstant.getMyID()) continue;

                        if (entity.getEntityType()==EntityType.BUILDER_UNIT || entity.getEntityType()==EntityType.RANGED_UNIT || entity.getEntityType()==EntityType.MELEE_UNIT)
                        {
                            Vec2Int vec2Int = Vec2Int.createVector(i,j);
                            FinalGraphic.sendSquare(debugInterface, vec2Int, 1, FinalGraphic.COLOR_BLACK_TWO);
                            FinalGraphic.sendText(debugInterface, new Vec2Float(vec2Int.getX() * 1.0f+0.5f, vec2Int.getY() * 1.0f + 0.4f), 11, "" +
                                    entity.getId());
                        }
                    }
                }
            }


            if (INFO_UNIT) {
                MyPlayer myPlayer = mGlobalStatistic.getMyPlayer();

                ArrayList<MyEntity> myEntities = myPlayer.getUnitArrayList();

                for (int i = 0; i < myEntities.size(); i++) {
                    Vec2Float vec2Float = myEntities.get(i).getPosition().getVec2Float();
                    vec2Float.setY(vec2Float.getY()+ 0.20f);
                    FinalGraphic.sendText(debugInterface, vec2Float, 11, myEntities.get(i).toString());
                    vec2Float.setY(vec2Float.getY()- 0.10f);
                    FinalGraphic.sendText(debugInterface, vec2Float, 11, myEntities.get(i).toStringV2());
                }
            }

            if (INFO_RECOURCE) {
                for (int i=0; i<getGlobalMap().getMapNextTick().length; i++)
                {
                    for (int j=0; j<getGlobalMap().getMapNextTick().length; j++)
                    {
                        MyEntity entity = getGlobalMap().getMapNextTick()[i][j];

                        if (entity.getEntityType()==EntityType.RESOURCE && entity.getTargetEntity()!=null)
                        {
                            Vec2Float vec2Float = entity.getPosition().getVec2Float();
                            vec2Float.setY(vec2Float.getY()+ 0.50f);
                            vec2Float.setX(vec2Float.getX()+ 0.50f);
                            FinalGraphic.sendText(debugInterface, vec2Float, 11, ""+entity.getTargetEntity().getId());
                        }
                    }
                }
            }
        }

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
       // mGlobalMap.debugUpdate(playerView, debugInterface);
      //  mMapPotField.debugUpdate(playerView, debugInterface);


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

    public static WaveSearchModule getWaveSearchModule() {
        return waveSearchModule;
    }

    public MoveManager getMoveManager() {
        return mMoveManager;
    }
}
