package strategy;

import model.*;
import strategy.map.astar.Node;
import strategy.map.potfield.DodgePositionAnswer;

import java.util.*;

import static strategy.Final.GLOBAL_DODGE;

public class EconomicManager {

    public static final String TAG = "strategy.EconomicManager";

    static int MAX_BUILDER_UNIT = 70;
    static int MAX_BUILDER_UNIT_ALL_GAME = 70;

    static int MAX_BUILDER_REPAIR = 3;
    static int MAX_BUILDER_REPAIR_BASE = 12;

    static int START_HOUSE_SPECIAL = 9;

    // количество рабочих
    int sizeBuildUnit;
    // количество золота
    int sizeGold;
    // количество жилых мест
    int countMaxUnit;

    int createHouse;
    int createHouseAndNoActive;
    int createTurret;
    int createBase;

    int nextResource;

    DebugInterface debugInterface;

    boolean init;
    boolean init2;

    boolean checkSpecialRush = true;

    public EconomicManager() {
        init = false;
        init2=false;
    }

    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalManager globalManager,DebugInterface debugInterface, HashMap<Integer, EntityAction> actionHashMap) {
        this.debugInterface = debugInterface;

        if (!init) {
            if (globalManager.getGlobalStatistic().getPlayers().size() == 2) {
                MAX_BUILDER_UNIT = 80;
            } else {
                MAX_BUILDER_UNIT = 71;
            }
            MAX_BUILDER_UNIT_ALL_GAME = MAX_BUILDER_UNIT+5;
            init = true;
        }
        if (FinalConstant.isFogOfWar() && !init2)
        {
            if (globalManager.getGlobalStatistic().getMyPlayer().getEntityArrayList(EntityType.RANGED_UNIT).size()>30)
            {
                if (globalManager.getGlobalStatistic().getPlayers().size() == 2) {
                    MAX_BUILDER_UNIT = 85;
                } else {
                    MAX_BUILDER_UNIT = 71;
                }
                MAX_BUILDER_UNIT_ALL_GAME = MAX_BUILDER_UNIT+5;

                init2  = true;
            }

            if (globalManager.getGlobalStatistic().getMyPlayer().getCountAllBiuld()==MAX_BUILDER_UNIT_ALL_GAME && !globalManager.getGlobalStatistic().isCheckFirstEnemyUnits())
            {
                MAX_BUILDER_UNIT+=5;
                MAX_BUILDER_UNIT_ALL_GAME=MAX_BUILDER_UNIT+5;
                init2 = true;
            }
        }

        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();
        updateInfo(globalStatistic);

        MyPlayer myPlayer = globalManager.getGlobalStatistic().getMyPlayer();


        if (!GLOBAL_DODGE) {
            //увороты
            dodgeBuilderV2(myPlayer, playerView, globalManager, actionHashMap);
        }

        if (Final.SPECIAL_RUSH && globalManager.getGlobalStatistic().getPlayers().size()==2)
        {
            specialPushBuilder(myPlayer, playerView, globalManager, actionHashMap);
           /* ArrayList<MyEntity> arrayList =
            for ()*/
        }

        //всё что связанно с починкой
        repairBuilder(myPlayer, playerView, globalManager, actionHashMap);


        createHouse = 0;
        createTurret=0;
        createHouseAndNoActive=0;

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);
        for (int i = 0; i < builderUnitArrayList.size(); i++)
        {
            MyEntity builder =  builderUnitArrayList.get(i);

            if (builder.getUnitState()==EUnitState.BUILD)
            {
                createHouse++;
                createHouseAndNoActive++;
            }
        }

        ArrayList<MyEntity> houseArrayList = myPlayer.getEntityArrayList(EntityType.HOUSE);
        for (int i=0; i<houseArrayList.size(); i++)
        {
            if (!houseArrayList.get(i).isActive())
            {
                createHouseAndNoActive++;
            }
        }

        ArrayList<MyEntity> turretArray = myPlayer.getEntityArrayList(EntityType.TURRET);
        for (int i=0; i<turretArray.size(); i++)
        {
            if (!turretArray.get(i).isActive())
            {
                createTurret++;
            }
        }

       // Final.DEBUGRelease("CREATE_HOUSE", FinalConstant.getCurrentTik() + " " + createHouseAndNoActive);

        //всё что связанно с новые зданиями
        buildBuilder(myPlayer, playerView, globalManager, actionHashMap);

        /* ДОБЫЧА ресурсов
        вызывается метод несколько, массив делится на 2 части
        1. тут в отсортированном порядке рабочие у которых рядом есть ресурсы, от меньшего до большего
        2. после того как вверхние обработаются, оставшихся рабочих сортируем по ближайщему свободному ресурсу
         */

        //добыча ресурсов
        resurceBuilder(myPlayer, playerView, globalManager, actionHashMap);

        // хил юнитов!
        healUnits(myPlayer, playerView, globalManager, actionHashMap);

        /// создаём юниты
        actionHashMap.putAll(createUnit(myPlayer, playerView, globalManager));


        ArrayList<MyEntity> myEntities = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        Final.DEBUG(TAG, "BUILD_SIZE: " + myEntities.size());

        int countBuilder = 0;
        int countRepear = 0;

        for (int i = 0; i < myEntities.size(); i++) {
            if (myEntities.get(i).getUnitState() == EUnitState.REPAIR) countRepear++;
            if (myEntities.get(i).getUnitState() == EUnitState.BUILD) countBuilder++;
             Final.DEBUG(TAG, "BUILD_UNIT: " + myEntities.get(i).getPosition().toString() + " " + myEntities.get(i).getId() + " " + myEntities.get(i).getUnitState());
        }

        Final.DEBUG(TAG, "BUILD CB:" + countBuilder + " CR: " + countRepear);

        return actionHashMap;
    }

    private void specialPushBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        if (!checkSpecialRush) return;
        if (myPlayer.getEntityArrayList(EntityType.HOUSE).size()==0) return;
        if (!myPlayer.getEntityArrayList(EntityType.HOUSE).get(0).isActive()) return;

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        Vec2Int vec2Int = Vec2Int.createVector(73,73);

        float minDis = 0xFFFF;
        MyEntity current = null;

        ArrayList<MyEntity> okeyArrayList = new ArrayList<>();

        for (int i = 0; i < builderUnitArrayList.size(); i++)
        {
            MyEntity builder =  builderUnitArrayList.get(i);

            if (builder.isOkey()) {
                okeyArrayList.add(builder);
                if (okeyArrayList.size()>=3) {
                    current = null;
                    break;
                }
                continue;
            }

            float dis = (float) vec2Int.distance(builder.getPosition());

            if (dis<minDis)
            {
                minDis = dis;
                current = builder;
            }
        }
        if (current!=null)
        {
            okeyArrayList.add(current);
        }

        for (int i=0; i<okeyArrayList.size(); i++)
        {
            current = okeyArrayList.get(i);

            current.setOkey(true);

            current.setDodge(true);
            current.setUpdate(true);

            List<Node> list = globalManager.getMoveManager().findPath(current.getPosition(),vec2Int,current.getEntityType());

            GlobalMap globalMap = globalManager.getGlobalMap();

            for (int j=0; j<list.size(); j++)
            {
                if (globalMap.getMapNoCheck(list.get(j).getVec2Int()).getEntityType()==EntityType.RESOURCE)
                {
                    checkSpecialRush = false;
                    return;
                }
            }

            MoveAction m = globalManager.getMoveManager().getMoveActionPosition(current,vec2Int);
            //new MoveAction(resourceMidDis.getPosition(),true,true);
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;

            actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));
        }
    }

    //ресурсы
    private HashMap resurceBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        ArrayList<MyEntity> builderUnitArrayListOne = myPlayer.getBuildingUnitNearResources(globalManager.getGlobalMap());

        Final.DEBUG(TAG, "BUILDER_UNITSIZE: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size());
        Final.DEBUG(TAG, "BUILDER_UNIT_ONE SIZE: " + builderUnitArrayListOne.size());

        ArrayList<MyPlayer> arrayList = globalManager.getGlobalStatistic().getPlayers();



       /* if (arrayList.size()==2)
        {
            checkTargetAttack=false;
        }*/

        for (int i = 0; i < builderUnitArrayListOne.size(); i++) {
            MyEntity entity = builderUnitArrayListOne.get(i);

            ArrayList<MyEntity> resource;

            if (entity.getDataTaskUnit().getUnitState() == EUnitState.REPAIR || entity.getDataTaskUnit().getUnitState() == EUnitState.BUILD) continue;

            boolean checkTargetAttack = true;

            if (entity.isDodge())
            {
                if (entity.getPosition().equals(entity.getDodgeNew())) {
                    checkTargetAttack = false;
                }
                else {
                    continue;
                }
            }

            if (entity.isRotation())
            {
                continue;
            }

            if (!Final.TARGET_RESOURCE)
            {
                checkTargetAttack = false;
            }

            resource = globalManager.getGlobalMap().getEntityMapResourceSpecial(entity.getPosition(),checkTargetAttack);

            if (resource.size() > 0)
            {
                if (entity.getUnitState() == EUnitState.RESURCE || entity.getUnitState() == EUnitState.EMPTY) {
                    entity.setDataTaskUnit(new DataTaskUnit(EUnitState.RESURCE));
                    MoveAction m = null;
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    a = new AttackAction(
                            //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                            resource.get(0).getId(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesBUILDER_UNIT().getSightRange(),
                                    new EntityType[]{EntityType.RESOURCE}
                            )
                    );

                    entity.setTargetEntity(resource.get(0));
                    resource.get(0).setTargetEntity(entity);
                    resource.get(0).attackHP(1);
                    // strategy.Final.DEBUG(TAG, "arrayList.get(i).getId() " + builderUnitArrayList.get(i).getId() + " " +builderUnitArrayList.get(i).getPosition().toString());

                    actionHashMap.put(entity.getId(), new EntityAction(m, b, a, r));
                } else {
                    resource.get(0).setTargetEntity(entity);
                    continue;
                }
            }
            else {
                Final.DEBUG("ERROR",  "resource==0");
            }
        }

        ArrayList<MyEntity> builderUnitArrayListTwo = myPlayer.getBuildingUnitNearResourcesOther(globalManager.getGlobalMap(),globalManager.getMapPotField());

        Final.DEBUG(TAG, "BUILDER_UNIT_TWO SIZE: " + builderUnitArrayListTwo.size());

        for (int i = 0; i < builderUnitArrayListTwo.size(); i++) {
            MyEntity builder = builderUnitArrayListTwo.get(i);

            ArrayList<MyEntity> resource;

          /*  resource = globalManager.getGlobalMap().getEntityMapResourceSpecial(builder.getPosition());

            if (resource.size()>0){
                Final.DEBUG("ERROR",  "resource>0");
            }*/
            if (builder.isUpdate()) continue;
            if (builder.isDodge()) continue;
            if (builder.getDataTaskUnit().getUnitState() == EUnitState.REPAIR || builder.getDataTaskUnit().getUnitState() == EUnitState.BUILD) continue;

            MyEntity resourceMidDis;

            if (builder.getEnemyMinDis()!=null && builder.getEnemyMinDis().getSimulationHP()>0 && (builder.getEnemyMinDis().getTargetEntity()==null || !Final.TARGET_RESOURCE) ) {
                resourceMidDis = builder.getEnemyMinDis();
            }
            else {
                resourceMidDis =globalManager.getGlobalMap().getNearest(builder.getPosition(), EntityType.RESOURCE, true, -1, globalManager.getMapPotField());
            }

            if (resourceMidDis!=null)
            {
                if (builder.getUnitState() == EUnitState.RESURCE || builder.getUnitState() == EUnitState.EMPTY) {
                    builder.setDataTaskUnit(new DataTaskUnit(EUnitState.RESURCE));


                    Vec2Int positionToResource = globalManager.getGlobalMap().getPositionToResourceSpecial(builder.getPosition(),resourceMidDis.getPosition());

                    if (positionToResource!=null) {
                        MoveAction m = globalManager.getMoveManager().getMoveActionPosition(builder, positionToResource);
                        //new MoveAction(resourceMidDis.getPosition(),true,true);
                        BuildAction b = null;
                        AttackAction a = null;
                        RepairAction r = null;

                        a = null;
                        resourceMidDis.setTargetEntity(builder);
                        builder.setTargetEntity(resourceMidDis);
                        // strategy.Final.DEBUG(TAG, "arrayList.get(i).getId() " + builderUnitArrayList.get(i).getId() + " " +builderUnitArrayList.get(i).getPosition().toString());

                        actionHashMap.put(builder.getId(), new EntityAction(m, b, a, r));
                    }
                } else {
                    resourceMidDis.setTargetEntity(builder);
                    continue;
                }
            }
            else {
                builder.getDataTaskUnit().clear();
            }
        }


        return actionHashMap;
    }

    //убегания от противников
    private HashMap dodgeBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();


        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        for (int i = 0; i < builderUnitArrayList.size(); i++) {
            MyEntity builderUnit = builderUnitArrayList.get(i);

            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;


            EntityAction entityAction = actionHashMap.get(builderUnit.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);


            // увороты от милишников
            Vec2Int vec2IntDodgeMelee = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnit.getPosition(), myPlayer, 2, EntityType.MELEE_UNIT);

            if (vec2IntDodgeMelee != null) {
                m = new MoveAction(vec2IntDodgeMelee, true, true);
               // globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodgeMelee);

                entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);


                builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodge(true);
                myPlayer.addCountBuildDodge();

                actionHashMap.put(builderUnit.getId(), entityAction);
                continue;
            }

            // увороты от лучников
            Vec2Int vec2IntDodgeRange = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnit.getPosition(), myPlayer, 6, EntityType.RANGED_UNIT);

            if (vec2IntDodgeRange != null) {
                m = new MoveAction(vec2IntDodgeRange, true, true);
                entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);
                ///globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodgeRange);

                builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodge(true);
                myPlayer.addCountBuildDodge();

                actionHashMap.put(builderUnit.getId(), entityAction);
                continue;

            }

            Vec2Int vec2IntDodgeRangeTwo = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnit.getPosition(), myPlayer, 7, EntityType.RANGED_UNIT);

            if (vec2IntDodgeRangeTwo != null) {
                m = new MoveAction(vec2IntDodgeRangeTwo, true, true);

                ArrayList<MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(builderUnit.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);

                if (arrayList.size()==0)
                {
                    entityAction.setAttackAction(null);
                    builderUnit.getEntityAction().setAttackAction(null);
                }
                else {

                }

              //  entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);
               // globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodgeRangeTwo);

               // builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodge(true);
                myPlayer.addCountBuildDodge();

                actionHashMap.put(builderUnit.getId(), entityAction);
                continue;

            }
            /*else {

                ArrayList<MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(builderUnit.getPosition(), 8, FinalConstant.getMyID(), true, false, true, EntityType.ALL, false, false);

                if (arrayList.size() != 0) {
                    boolean range = false;
                    for (int j = 0; j < arrayList.size(); j++) {
                        if (arrayList.get(j).getEntityType() == EntityType.RANGED_UNIT) {
                            range = true;
                        }
                    }

                    if (range == false) {
                        // милишники
                        arrayList = globalManager.getGlobalMap().getEntityMap(builderUnit.getPosition(), 2, FinalConstant.getMyID(), true, false, true, EntityType.ALL, false, false);
                        if (arrayList.size() != 0) {
                            m = null;
                            //a = null;
                            Vec2Int vec2IntDodge = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnit.getPosition(), myPlayer, 2, EntityType.MELEE_UNIT);

                            if (vec2IntDodge != null) {
                                EntityAction action = actionHashMap.get(builderUnit.getId());

                                if (action == null) action = new EntityAction(null, null, null, null);
                                action.setMoveAction(new MoveAction(vec2IntDodge, true, false));
                                action.setBuildAction(null);

                                actionHashMap.put(builderUnit.getId(), action);

                                builderUnit.setDodge(true);
                            }

                        }

                    } else {
                        m = null;
                        // a = null;

                        Vec2Int vec2IntDodge = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnit.getPosition(), myPlayer, 7, EntityType.RANGED_UNIT);

                        if (vec2IntDodge != null) {
                            EntityAction action = actionHashMap.get(builderUnit.getId());

                            if (action == null) action = new EntityAction(null, null, null, null);
                            action.setMoveAction(new MoveAction(vec2IntDodge, true, false));
                            action.setBuildAction(null);

                            actionHashMap.put(builderUnit.getId(), action);

                            builderUnit.setDodge(true);
                        }
                    }

                }*
            }*/


        }

        return actionHashMap;
    }

    //убегания от противниковV2
    private HashMap dodgeBuilderV2(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        for (int i = 0; i < builderUnitArrayList.size(); i++) {
            MyEntity builderUnit = builderUnitArrayList.get(i);

            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;

            EntityAction entityAction = actionHashMap.get(builderUnit.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            ArrayList<MyEntity> resource = globalManager.getGlobalMap().getEntityMap(builderUnit.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);

            // увороты от всех по ПП
            Vec2Int vec2IntDodge = globalManager.getMapPotField().getDangerPositionBuild(builderUnit,resource.size()>0,false);

            if (vec2IntDodge != null) {
                m = globalManager.getMoveManager().getMoveActionPosition(builderUnit,vec2IntDodge);
                        //new MoveAction(vec2IntDodge, true, true);
              //  globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodge);

                entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);

                builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodgeNew(vec2IntDodge);
                builderUnit.setDodge(true);
                myPlayer.addCountBuildDodge();

                actionHashMap.put(builderUnit.getId(), entityAction);
                continue;
            }
        }

        return actionHashMap;
    }

    //убегания от противниковV2
    private HashMap dodgeBuilderV3(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        for (int i = 0; i < builderUnitArrayList.size(); i++) {
            MyEntity builderUnit = builderUnitArrayList.get(i);

            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;

            EntityAction entityAction = actionHashMap.get(builderUnit.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            ArrayList<MyEntity> resource = globalManager.getGlobalMap().getEntityMap(builderUnit.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);

            // увороты от всех по ПП
            DodgePositionAnswer dodgePositionAnswer = globalManager.getMapPotField().getDodgePositionBuild(builderUnit,resource.size()>0,false);

            if (dodgePositionAnswer != null) {

                builderUnit.setDodge(true);
                builderUnit.setUpdate(true);
                builderUnit.setDodgePositionAnswer(dodgePositionAnswer);

                myPlayer.addCountBuildDodge();

                myPlayer.getUnitDodgeArrayList().add(builderUnit);

                actionHashMap.put(builderUnit.getId(), entityAction);
            }
        }

        return actionHashMap;
    }

    // новые постройки
    private HashMap buildBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {

        // достраиваем дома
        ArrayList<MyEntity> arrayList = myPlayer.getEntityArrayList(EntityType.HOUSE);

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

      //  Final.DEBUGRelease("CHECK: ", FinalConstant.getCurrentTik() + " " + checkCreateMoreOneHouse(myPlayer));

        //boolean checkCreate = false;

        if ((myPlayer.getResource() - createHouse * myPlayer.getCost(EntityType.HOUSE)) > myPlayer.getCost(EntityType.HOUSE) - builderUnitArrayList.size()
                && ((myPlayer.getPopulationCurrent() * 1.2 >= myPlayer.getPopulationMax() && myPlayer.getResource() > myPlayer.getCost(EntityType.HOUSE) * 2) || myPlayer.getPopulationMax() < 80)
                && (myPlayer.getPopulationMax() < 180)
                && (myPlayer.getCountBuildDontCreate(EntityType.HOUSE) < 3 || (myPlayer.getResource() > 50 && myPlayer.getCountBuildDontCreate(EntityType.HOUSE) < 7 && myPlayer.getPopulationMax() < 70))
        ) {
            if (globalManager.getGlobalStatistic().getPlayers().size()==2) {
                if ((myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size() > 0 &&
                        myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size() > 0) ||
                        myPlayer.getEntityArrayList(EntityType.HOUSE).size() < 5 ||
                        (myPlayer.getResource() > 550 && myPlayer.getEntityArrayList(EntityType.HOUSE).size() < 7)
                        || (globalManager.getGlobalStatistic().getPlayers().size() == 2 && myPlayer.getEntityArrayList(EntityType.HOUSE).size() < START_HOUSE_SPECIAL)
                ) {

                    //   System.out.println("size HOME: " +  myPlayer.getEntityArrayList(EntityType.HOUSE).size());
                    //  System.out.println("size RANGED_BASE: " +  myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size());
                    if (checkCreateMoreOneHouse(myPlayer)) {
                        createHouseV2(builderUnitArrayList, globalManager, actionHashMap);
                    }
                }
            } else {
                if ((myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size() > 0 &&
                        myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size() > 0) ||
                        myPlayer.getEntityArrayList(EntityType.HOUSE).size() < 5 ||
                        (myPlayer.getResource() > 550 && myPlayer.getEntityArrayList(EntityType.HOUSE).size() < 6)
                ) {

                    //   System.out.println("size HOME: " +  myPlayer.getEntityArrayList(EntityType.HOUSE).size());
                    //  System.out.println("size RANGED_BASE: " +  myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size());
                    if (checkCreateMoreOneHouse(myPlayer)) {
                        createHouseV2(builderUnitArrayList, globalManager, actionHashMap);
                    }
                }
            }
        }

        if (myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.RANGED_BASE).getCost() && myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()<1 )
        {
            createBase(builderUnitArrayList,globalManager,actionHashMap, EntityType.RANGED_BASE);
        }

        if (myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.BUILDER_BASE).getCost() && myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size()<1 )
        {
            createBase(builderUnitArrayList,globalManager,actionHashMap, EntityType.BUILDER_BASE);
        }

        if (myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.MELEE_BASE).getCost() &&
                myPlayer.getEntityArrayList(EntityType.MELEE_BASE).size()<1 &&
                myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0 &&
                myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size()>0 )
        {
          //  createBase(builderUnitArrayList,globalManager,actionHashMap, EntityType.MELEE_BASE);
        }
/*
        if (myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.MELEE_BASE).getCost()*4 && myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size()<1 &&  myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0)
        {
            if (b==null) {
                if (builderUnitArrayList.get(i).getPosition().getX()<70 && builderUnitArrayList.get(i).getPosition().getY()<75) {
                    b = new BuildAction(
                            EntityType.RANGED_BASE, Vec2Int.createVector(
                            entity.getPosition().getX() + 1,
                            entity.getPosition().getY()
                    )
                    );
                }
            }
        }*/

        if (Final.BUILD_TURRET_SPECIAL && globalManager.getGlobalStatistic().getPlayers().size()==2 && globalManager.getGlobalStatistic().isCheckFirstEnemyUnits() &&
        globalManager.getGlobalStatistic().getMyPlayer().getEntityArrayList(EntityType.RANGED_BASE).size()>0 && createTurret<3 &&
                myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.TURRET).getCost()
        )
        {
            for (int i=0; i<builderUnitArrayList.size(); i++)
            {
                MyEntity builder = builderUnitArrayList.get(i);

                if (!globalManager.getGlobalMap().getSpecialCheckBuilderTask(builder.getPosition(),true))
                {
                    continue;
                }

                if (globalManager.getGlobalMap().getSpecialCheckBuilderTaskTurretCreate(builder.getPosition())<30*11){
                    continue;
                }

                MoveAction m = null;
                BuildAction b = null;
                AttackAction a = null;
                RepairAction r = null;
                Vec2Int vec2Int1 = globalManager.getGlobalMap().checkBuildTurretSpecial(builder);
                if (vec2Int1!=null) {

                    //new MoveAction(vec2Int1, true, false);

                    b = new BuildAction(EntityType.TURRET, vec2Int1);
                    // checkCreate = true;
                    a = null;

                    builder.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                    builder.getDataTaskUnit().setEntityType(EntityType.TURRET);

                    Final.DEBUG(TAG, "VECTOR BUILD: " + builder.toString() + " currentP: " + builder.getPosition());

                    actionHashMap.put(builder.getId(), new EntityAction(m, b, a, r));
                    break;
                }
            }
        }

        if (Final.BUILD_TURRET_SPECIAL_V2 &&
                (globalManager.getGlobalStatistic().getPlayers().size()==2 || Final.BUILD_TURRET_SPECIAL_V2_ALL) &&
                        globalManager.getGlobalStatistic().isCheckFirstEnemyUnits() &&
                globalManager.getGlobalStatistic().getMyPlayer().getEntityArrayList(EntityType.RANGED_BASE).size()>0 && createTurret<3 &&
                myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.TURRET).getCost()
        )
        {
            for (int i=0; i<builderUnitArrayList.size(); i++)
            {
                MyEntity builder = builderUnitArrayList.get(i);

                if (!globalManager.getGlobalMap().getSpecialCheckBuilderTask(builder.getPosition(),true))
                {
                    continue;
                }

                if (globalManager.getGlobalMap().getSpecialCheckBuilderTaskTurretCreate(builder.getPosition())<30*10){
                    continue;
                }

                MoveAction m = null;
                BuildAction b = null;
                AttackAction a = null;
                RepairAction r = null;
                Vec2Int vec2Int1 = globalManager.getGlobalMap().checkBuildTurretSpecial(builder);
                if (vec2Int1!=null) {

                    //new MoveAction(vec2Int1, true, false);

                    b = new BuildAction(EntityType.TURRET, vec2Int1);
                    // checkCreate = true;
                    a = null;

                    builder.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                    builder.getDataTaskUnit().setEntityType(EntityType.TURRET);

                    Final.DEBUG(TAG, "VECTOR BUILD: " + builder.toString() + " currentP: " + builder.getPosition());

                    actionHashMap.put(builder.getId(), new EntityAction(m, b, a, r));
                    break;
                }
            }
        }

        if (myPlayer.getResource() > myPlayer.getCost(EntityType.TURRET) - 10 && myPlayer.getPopulationMax() >= 50 && Final.BUILD_TURRET) {
            int[][] positionTurret = {{5, 25}, {8, 25}, {11, 25}, {14, 25}, {12, 25},
                    {25,14},{25,11},{25,7},
                    {5, 31}, {8, 31}, {11, 31}, {14, 31}, {12, 31},
                    {31,14},{31,11},{31,7},
            };
            // int[][] positionTurret = new int[10][2];
            Vec2Int vec2IntLeft = globalManager.getMapPotField().getPositionDefencePlayerArea(0);
            Vec2Int vec2IntRight = globalManager.getMapPotField().getPositionDefencePlayerArea(1);

            //if (globalManager.getGlobalMap().checkMyTurret(vec2IntLeft,7))

            for (int j = 0; j < positionTurret.length; j++) {
                if (positionTurret[j][0] == 0 && positionTurret[j][1] == 0) continue;

                Vec2Int vec2Int = Vec2Int.createVector(positionTurret[j][0], positionTurret[j][1]);

                if (globalManager.getGlobalMap().checkEmpty(vec2Int, 2)) {

                    double minDis = 0xFFFFF;
                    MyEntity current = null;

                    for (int i = 0; i < builderUnitArrayList.size(); i++) {

                        MyEntity builderUnit = builderUnitArrayList.get(i);


                        if (builderUnit.getUnitState() == EUnitState.REPAIR || builderUnit.getUnitState() == EUnitState.BUILD)
                            continue;

                        double dis = builderUnitArrayList.get(i).getPosition().distance(vec2Int);
                        if (dis < minDis) {
                            current = builderUnitArrayList.get(i);
                            minDis = dis;
                        }
                    }

                    if (current != null) {
                        MoveAction m = null;
                        BuildAction b = null;
                        AttackAction a = null;
                        RepairAction r = null;
                        Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(current.getPosition(), vec2Int, FinalConstant.getEntityProperties(EntityType.TURRET));
                        if (vec2Int1!=null) {
                            m = globalManager.getMoveManager().getMoveActionPosition(current,vec2Int1);
                                    //new MoveAction(vec2Int1, true, false);

                            b = new BuildAction(EntityType.TURRET, vec2Int);
                            // checkCreate = true;
                            a = null;

                            current.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                            current.getDataTaskUnit().setEntityType(EntityType.TURRET);

                            Final.DEBUG(TAG, "VECTOR BUILD: " + vec2Int.toString() + " currentP: " + current.getPosition());

                            actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));
                        }
                    }
                }
            }

        }



        return actionHashMap;
    }

    private boolean checkCreateMoreOneHouse(MyPlayer myPlayer) {
        if (createHouseAndNoActive<1) return true;

        if ((createHouseAndNoActive)*3.5*myPlayer.getCost(EntityType.BUILDER_UNIT)<myPlayer.getResource()) return true;

        return false;
    }

    private void createBase(ArrayList<MyEntity> builderUnitArrayList, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap, EntityType entityType) {
        ArrayList<Vec2Int> positionBuildBase;

        boolean typeV2 = false;

        if (entityType==EntityType.RANGED_BASE && FinalConstant.isFogOfWar()){
            positionBuildBase = globalManager.getGlobalMap().getPositionBuildBaseV2(FinalConstant.getEntityProperties(entityType), globalManager.getMapPotField(),globalManager);
            typeV2 = true;

            if (positionBuildBase.size()==0)
            {
                typeV2 = false;
                positionBuildBase = globalManager.getGlobalMap().getPositionBuildBase(FinalConstant.getEntityProperties(entityType), globalManager.getMapPotField());
            }
        }
        else {
            positionBuildBase = globalManager.getGlobalMap().getPositionBuildBase(FinalConstant.getEntityProperties(entityType), globalManager.getMapPotField());
            typeV2 = false;
        }

        if (positionBuildBase.size()==0) {
            //Final.DEBUG(TAG," BAD POSITION CREATE HOUSE");
           // createHouse(builderUnitArrayList,globalManager,actionHashMap);
            return;
        }

     //   for (int j=0; j<positionBuildBase.size(); j++) {
          //  Final.DEBUGRelease("BASE CREATE", FinalConstant.getCurrentTik() + " positionBuildBase: " + positionBuildBase.toString());
     //   }

        MyEntity current = null;
        Vec2Int positionHouse = null;
        float minDis = 0xFFFF;

        for (int i=0; i<builderUnitArrayList.size(); i++)
        {
            MyEntity builderUnit = builderUnitArrayList.get(i);

            if (builderUnit.isUpdate()) continue;
            if (builderUnit.isDodge()) continue;

            if (builderUnit.getUnitState() == EUnitState.REPAIR || builderUnit.getUnitState() == EUnitState.BUILD)
                continue;

            for (int j=0; j<positionBuildBase.size(); j++)
            {
                if (builderUnit.getPosition().getX()+1 == positionBuildBase.get(j).getX() && builderUnit.getPosition().getY()+1 == positionBuildBase.get(j).getY()) continue;

                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(builderUnit.getPosition(), positionBuildBase.get(j), FinalConstant.getEntityProperties(EntityType.RANGED_BASE));

                if (vec2Int1 == null) break;

                List<Node> path = globalManager.getMoveManager().findPath(builderUnit.getPosition(),vec2Int1,builderUnit.getEntityType());

                double dis = path.size();
             //   double dis = builderUnit.getPosition().distance(positionBuildBase.get(j));
                if (dis<minDis)
                {
                    current = builderUnit;
                    minDis = (float) dis;
                    positionHouse = positionBuildBase.get(j);
                }
            }
        }

      //  Final.DEBUGRelease("BASE CREATE", FinalConstant.getCurrentTik() + " pos: " + positionHouse.toString() + " unit: " + current.getPosition().toString() + " minDis: " + minDis);

        if (current!=null)
        {
            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;
            if (!typeV2){
                positionHouse = positionHouse.subtract(1,1);
            }
            Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(current.getPosition(), positionHouse, FinalConstant.getEntityProperties(entityType));
            if (vec2Int1!=null) {

                //Final.DEBUGRelease("BASE CREATE", FinalConstant.getCurrentTik() + " unitPOS: " + vec2Int1.toString() );

                m = globalManager.getMoveManager().getMoveActionPosition(current,vec2Int1);
                        //new MoveAction(vec2Int1, true, false);

                b = new BuildAction(entityType, positionHouse);
                //  checkCreate = true;
                a = null;

                current.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                current.getDataTaskUnit().setEntityType(entityType);
                current.setUpdate(true);

                Final.DEBUG(TAG, "VECTOR BUILD: " + positionBuildBase.toString() + " currentP: " + current.getPosition());

                actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));
            }
            else {
                Final.DEBUG(TAG," BAD POSITION MinPositionBuilding");
            }
        }

    }

    private void createHouse(ArrayList<MyEntity>  builderUnitArrayList, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap ) {

        Vec2Int positionBuildHouse = globalManager.getGlobalMap().getPositionBuildHouse(FinalConstant.getEntityProperties(EntityType.HOUSE));

        if (positionBuildHouse == null) {
            // positionBuildHouse = Vec2Int.createVector();
            for (int i = 0; i < builderUnitArrayList.size(); i++) {
                MyEntity entity = builderUnitArrayList.get(i);

                if (entity.getUnitState() == EUnitState.REPAIR) continue;

                if (entity.getPosition().getX() - 1 < 0 || entity.getPosition().getX() - 1 > 80 - 3) continue;
                if (entity.getPosition().getY() < 0 || entity.getPosition().getY() > 80 - 3) continue;
                BuildAction b = new BuildAction(
                        EntityType.HOUSE, Vec2Int.createVector(
                        entity.getPosition().getX() - 1,
                        entity.getPosition().getY()
                )
                );

                MoveAction m = new MoveAction(Vec2Int.createVector(79, 79), true, false);

                AttackAction a = null;

                //entity.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                //entity.getDataTaskUnit().setEntityType(EntityType.HOUSE);

                actionHashMap.put(entity.getId(), new EntityAction(m, b, a, null));
            }

        } else {

            double minDis = 0xFFFFF;
            MyEntity current = null;


            for (int i = 0; i < builderUnitArrayList.size(); i++) {

                MyEntity builderUnit = builderUnitArrayList.get(i);

                if (builderUnit.getUnitState() == EUnitState.REPAIR || builderUnit.getUnitState() == EUnitState.BUILD)
                    continue;

                double dis = builderUnitArrayList.get(i).getPosition().distance(positionBuildHouse);
                if (dis < minDis) {
                    current = builderUnitArrayList.get(i);
                    minDis = dis;
                }
            }

            if (current != null) {
                MoveAction m = null;
                BuildAction b = null;
                AttackAction a = null;
                RepairAction r = null;
                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(current.getPosition(), positionBuildHouse, FinalConstant.getEntityProperties(EntityType.HOUSE));
                if (vec2Int1!=null) {
                    m = globalManager.getMoveManager().getMoveActionPosition(current,vec2Int1);
                         //   new MoveAction(vec2Int1, true, false);

                    b = new BuildAction(EntityType.HOUSE, positionBuildHouse);
                  //  checkCreate = true;
                    a = null;

                    current.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                    current.getDataTaskUnit().setEntityType(EntityType.HOUSE);

                    Final.DEBUG(TAG, "VECTOR BUILD: " + positionBuildHouse.toString() + " currentP: " + current.getPosition());

                    actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));
                }
            }
        }
    }

    private void createHouseV2(ArrayList<MyEntity>  builderUnitArrayList, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap ) {

        ArrayList<Vec2Int> positionBuildHouse = globalManager.getGlobalMap().getPositionBuildHouseV2(FinalConstant.getEntityProperties(EntityType.HOUSE),
                globalManager.getMapPotField(),
                globalManager.getGlobalStatistic().getMyPlayer().getEntityArrayList(EntityType.RANGED_BASE).size()>0);

        if (positionBuildHouse.size()==0 && globalManager.getGlobalStatistic().getMyPlayer().getEntityArrayList(EntityType.RANGED_BASE).size()>0 && !FinalConstant.isFogOfWar()) {
            Final.DEBUG(TAG," BAD POSITION CREATE HOUSE");
            createHouse(builderUnitArrayList,globalManager,actionHashMap);
            return;
        }

        MyEntity current = null;
        Vec2Int positionHouse = null;
        float minDisOne = 0xFFFF;
        float minDisTwo = 0xFFFF;
        float minDisThree= 0xFFFF;
        float sumMin= 0xFFFF;
/*
        for (int i=0; i<builderUnitArrayList.size(); i++)
        {
            MyEntity builderUnit = builderUnitArrayList.get(i);

            if (builderUnit.isUpdate()) continue;
            if (builderUnit.isDodge()) continue;

            if (builderUnit.getUnitState() == EUnitState.REPAIR || builderUnit.getUnitState() == EUnitState.BUILD)
                continue;

            for (int j=0; j<positionBuildHouse.size(); j++)
            {
                builderUnit.setMinDisToHouse();
                double dis = builderUnit.getPosition().distance(positionBuildHouse.get(j));
                if (dis<minDis)
                {
                    current = builderUnit;
                    minDis = (float) dis;
                    positionHouse = positionBuildHouse.get(j);
                }
            }
        }*/
        for (int j=0; j<positionBuildHouse.size(); j++)
        {
            Vec2Int position = positionBuildHouse.get(j);

            minDisOne = 0xFFFF;
            minDisTwo = 0xFFFF;
            minDisThree= 0xFFFF;
            MyEntity buildMinDisOne = null;

            for (int i=0; i<builderUnitArrayList.size(); i++) {
                MyEntity builderUnit = builderUnitArrayList.get(i);

                if (builderUnit.isUpdate()) continue;
                if (builderUnit.isDodge()) continue;

                if (builderUnit.getUnitState() == EUnitState.REPAIR || builderUnit.getUnitState() == EUnitState.BUILD)
                    continue;

                double dis = builderUnit.getPosition().distance(positionBuildHouse.get(j));

                if (dis < minDisOne) {
                    minDisThree  = minDisTwo;
                    minDisTwo = minDisOne;
                    minDisOne = (float) dis;
                    buildMinDisOne = builderUnit;
                }
                else {
                    if (dis < minDisTwo) {
                        minDisThree = minDisTwo;
                        minDisTwo = (float) dis;
                    } else {
                        if (dis < minDisThree) {
                            minDisThree = (float) dis;
                        }
                    }
                }
            }

            if (minDisOne+minDisTwo+minDisThree<sumMin && buildMinDisOne!=null)
            {
                current = buildMinDisOne;
                positionHouse = position;
                sumMin = minDisOne+minDisTwo+minDisThree;
            }
        }

        if (positionHouse!=null) {
            Final.DEBUG("HOUSE", FinalConstant.getCurrentTik() + " sumMin: " + sumMin + " " + positionHouse.toString() + " current: " + current.toString());
        }


        if (current!=null)
        {
            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;
            positionHouse = positionHouse.subtract(1,1);
            Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(current.getPosition(), positionHouse, FinalConstant.getEntityProperties(EntityType.HOUSE));
            if (vec2Int1!=null) {
                m = globalManager.getMoveManager().getMoveActionPosition(current,vec2Int1);
                        //new MoveAction(vec2Int1, true, false);

                b = new BuildAction(EntityType.HOUSE, positionHouse);
                //  checkCreate = true;
                a = null;

                current.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                current.getDataTaskUnit().setEntityType(EntityType.HOUSE);

                Final.DEBUG(TAG, "VECTOR BUILD: " + positionHouse.toString() + " currentP: " + current.getPosition() + " vec2Int1: " + vec2Int1);

                actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));

                // добавляем призрачную копию дома следующего хода
                //If
            }
            else {
                Final.DEBUG("HOUSE"," BAD POSITION MinPositionBuilding");
            }
        }

    }

    // чиним здания и юнитов
    private HashMap repairBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        ArrayList<MyEntity> buildingArrayList = myPlayer.getBuildingArrayList();
        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

      /*  for (int j = 0; j < builderUnitArrayList.size(); j++){
            MyEntity entity = builderUnitArrayList.get(j);
            entity.setUpdate(false);
        }*/


        for (int j = 0; j < builderUnitArrayList.size(); j++) {
            MyEntity entity = builderUnitArrayList.get(j);
            MyEntity entityRepair = getNearbyBuildNeedHeal(entity.getPosition(), globalManager);

            if (entity.isUpdate()) continue;
            if (entity.isDodge()) continue;

            if (entityRepair == null) {

                if (entity.getDataTaskUnit().getUnitState() == EUnitState.REPAIR || entity.getDataTaskUnit().getUnitState() == EUnitState.BUILD) {
                    entity.getDataTaskUnit().clear();
                }

                continue;
            }

            EntityAction action = actionHashMap.get(entity);

            if (action == null) action = new EntityAction(null, null, null, null);
            action.setRepairAction(new RepairAction(entityRepair.getId()));
            action.setMoveAction(null);

            entityRepair.addRepairCounter();

            entity.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));
            //  entity.setUpdate(true);

            actionHashMap.put(entity.getId(), action);
        }

        for (int i = 0; i < buildingArrayList.size(); i++) {
            MyEntity myEntityBuilding = buildingArrayList.get(i);
            EntityProperties entityProperties = FinalConstant.getEntityProperties(myEntityBuilding.getEntityType());


            if (myEntityBuilding.getHealth() >= entityProperties.getMaxHealth() || myEntityBuilding.getEntityType()==EntityType.MELEE_BASE) continue;


           // if (myEntityBuilding.getRepairCounter()>2 && myEntityBuilding.getEntityType()!=EntityType.RANGED_BASE) continue;

            MyEntity builderUnit = null;


            int countBuildersRepair = 0;

            /*
            for (int j = 0; j < builderUnitArrayList.size(); j++) {
                builderUnit = builderUnitArrayList.get(j);

                if (builderUnit.isDodge()) continue;

                if (builderUnit.getDataTaskUnit().getUnitState()==EUnitState.REPAIR || builderUnit.getDataTaskUnit().getUnitState() == EUnitState.BUILD) continue;

                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(builderUnit.getPosition(), myEntityBuilding.getPosition(), FinalConstant.getEntityProperties(myEntityBuilding.getEntityType()));

                if (vec2Int1 == null) break;

                double dis = builderUnit.getPosition().distance(vec2Int1);



                if (dis < 2) {
                    EntityAction action = actionHashMap.get(builderUnit);

                    if (action == null) action = new EntityAction(null, null, null, null);

                    if (action.getRepairAction()!=null) continue;

                    MoveAction m = null;
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    myEntityBuilding.addRepairCounter();

                    r = new RepairAction(
                            myEntityBuilding.getId()
                    );
                    //a = null;
                    m = globalManager.getMoveManager().getMoveActionPosition(builderUnit,vec2Int1);
                            //new MoveAction(vec2Int1, true, false);

                    builderUnit.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));

                    actionHashMap.put(builderUnit.getId(), new EntityAction(m, b, a, r));

                   // countBuildersRepair++;
                } else {
                    if (dis < 4 && myEntityBuilding.getEntityType() == EntityType.RANGED_BASE) {
                        EntityAction action = actionHashMap.get(builderUnit);

                        if (action == null) action = new EntityAction(null, null, null, null);

                        if (action.getRepairAction() != null) continue;

                        MoveAction m = null;
                        BuildAction b = null;
                        AttackAction a = null;
                        RepairAction r = null;

                        myEntityBuilding.addRepairCounter();

                        r = new RepairAction(
                                myEntityBuilding.getId()
                        );
                        //a = null;
                        m = globalManager.getMoveManager().getMoveActionPosition(builderUnit, vec2Int1);
                        //new MoveAction(vec2Int1, true, false);

                        builderUnit.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));

                        actionHashMap.put(builderUnit.getId(), new EntityAction(m, b, a, r));

                        countBuildersRepair++;
                    }
                }
            }
             */

            int needBuilderRepait = MAX_BUILDER_REPAIR;

            if (myEntityBuilding.getEntityType()==EntityType.RANGED_BASE)
            {
                needBuilderRepait=MAX_BUILDER_REPAIR_BASE;
            }

            if (myEntityBuilding.isActive())
            {
                needBuilderRepait = 2;
            }

            if (myEntityBuilding.getRepairCounter()>=needBuilderRepait) continue;

            ArrayList<MyEntity> arrayList = new ArrayList<>();


            for (int j = 0; j < builderUnitArrayList.size(); j++) {
                builderUnit = builderUnitArrayList.get(j);

                if (builderUnit.isUpdate()) continue;
                if (builderUnit.isDodge()) continue;

                if (builderUnit.getDataTaskUnit().getUnitState()==EUnitState.REPAIR || builderUnit.getDataTaskUnit().getUnitState() == EUnitState.BUILD) continue;

                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(builderUnit.getPosition(), myEntityBuilding.getPosition(), FinalConstant.getEntityProperties(myEntityBuilding.getEntityType()));

                if (vec2Int1 == null) break;

                List<Node> path = globalManager.getMoveManager().findPath(builderUnit.getPosition(),vec2Int1,builderUnit.getEntityType());

                double dis = path.size();

                if (path.size()==0) dis=0xFFFF;

                builderUnit.setMinDisToHouse((float) dis,null);

                arrayList.add(builderUnit);
            }


            Collections.sort(arrayList, new Comparator<MyEntity>() {
                public int compare(MyEntity a, MyEntity b) {
                    if (a.getMinDisToHouse() > b.getMinDisToHouse()) return 1;
                    if (a.getMinDisToHouse() < b.getMinDisToHouse()) return -1;
                    return 0;
                }
            });


            for (int j=0; j<=needBuilderRepait-myEntityBuilding.getRepairCounter(); j++)
            {
                if (arrayList.size()<=j) break;

                MyEntity currentUnit = arrayList.get(j);

                if (currentUnit != null && currentUnit.getDataTaskUnit().getUnitState()!=EUnitState.REPAIR) {

                    Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(currentUnit.getPosition(), myEntityBuilding.getPosition(), FinalConstant.getEntityProperties(myEntityBuilding.getEntityType()));

                    if (vec2Int1==null) break;

                    MoveAction m = null;
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    r = new RepairAction(
                            myEntityBuilding.getId()
                    );
                    //a = null;
                    m = globalManager.getMoveManager().getMoveActionPosition(currentUnit,vec2Int1);
                    //new MoveAction(vec2Int1, true, false);

                    currentUnit.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));

                    globalManager.getMapPotField().getMapPotFieldNoCheck(vec2Int1).setRepairPositionClose(true);

                    myEntityBuilding.addRepairCounter();

                    actionHashMap.put(currentUnit.getId(), new EntityAction(m, b, a, r));
                }
                else {
                    break;
                }
            }
        }


        return actionHashMap;
    }

    // хил юнитов, пока простой по 1 хп для лучника
    @Deprecated // ушло в починку
    private HashMap healUnits(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {


        return actionHashMap;
    }

    private HashMap createUnit(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager) {
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();
        BuildAction b = null;

        ArrayList<MyEntity> meleeBaseArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_BASE);
        ArrayList<MyEntity> meleeUnitArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> rangedUnitArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> rangeBaseArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_BASE);
        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

      /*  Final.DEBUGRelease(" WTF: ",  FinalConstant.getCurrentTik()  + " "  + (myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()==0) +
                " " + (myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0 && !myPlayer.getEntityArrayList(EntityType.RANGED_BASE).get(0).isActive()) +
                " " + (( myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()==0 ||(myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0 && !myPlayer.getEntityArrayList(EntityType.RANGED_BASE).get(0).isActive())) && builderUnitArrayList.size() < MAX_BUILDER_UNIT));
*/
        // создаем новые юниты
        if ((((builderUnitArrayList.size() < myPlayer.getPopulationMax() * 0.75 || globalManager.getGlobalStatistic().getPlayers().size()==2) && builderUnitArrayList.size() < MAX_BUILDER_UNIT)
                || builderUnitArrayList.size() < 13
                ||
                (
                        ( myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()==0 ||
                        (myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0 && !myPlayer.getEntityArrayList(EntityType.RANGED_BASE).get(0).isActive()))
                                && builderUnitArrayList.size() < MAX_BUILDER_UNIT-10)
        )
               /* && !(globalManager.getGlobalMap().getResourceMap() < 10000 && builderUnitArrayList.size() > 15) &&
                !(globalManager.getGlobalMap().getResourceMap() < 20000 && builderUnitArrayList.size() > 25
                )*/
              //  && !globalManager.getMapPotField().checkAttackBase(myPlayer.getId(), globalManager.getGlobalStatistic())
                && !checkAttackBaseV2(myPlayer,globalManager)
                && myPlayer.getResource()>=(myPlayer.getCost(EntityType.BUILDER_UNIT)-myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size())
                && MAX_BUILDER_UNIT_ALL_GAME>myPlayer.getCountAllBiuld()
            //    && ((myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size())>1 || myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()==0)

        ) {

            b = null;

            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i = 0; i < arrayList1.size(); i++) {


                Vec2Int vec2Int =  null;

                if (FinalConstant.getCurrentTik()<400) {
                    vec2Int = globalManager.getGlobalMap().getPositionBuildUnitPrioriteV2(arrayList1.get(i));
                    if (vec2Int.getX()==0)
                    {
                        vec2Int = globalManager.getGlobalMap().getPositionBuildUnitPriorite(arrayList1.get(i),globalManager.getMapPotField());
                    }
                }
                else {
                    vec2Int = globalManager.getGlobalMap().getPositionBuildUnitPriorite(arrayList1.get(i),globalManager.getMapPotField());
                }

                if (vec2Int!=null)
                {
                  //  Final.DEBUGRelease(" WTF: ",  FinalConstant.getCurrentTik()  +" vec2Int: " + vec2Int.toString());
                }

                b = new BuildAction(EntityType.BUILDER_UNIT, vec2Int);

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, b, null, null));
            }
        } else {
           // Final.DEBUGRelease(" WTF: ",  FinalConstant.getCurrentTik()  +" FAAALSSE ");



            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i = 0; i < arrayList1.size(); i++) {

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }

        Final.DEBUG(TAG, "arrayList RANGED_BASE BASE: " + rangeBaseArrayList.size() + " resource: " + myPlayer.getResource());
        if (myPlayer.getResource() >= myPlayer.getCost(EntityType.RANGED_UNIT)-myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size()
            //    && (globalManager.getMapPotField().checkAttackBaseTwo(myPlayer.getId()) || FinalConstant.getCurrentTik() > 1)
        && true
                //&& globalManager.getGlobalStatistic().isCheckFirstEnemyUnits()
        ) {
            for (int i = 0; i < rangeBaseArrayList.size(); i++) {
                b = new BuildAction(
                        EntityType.RANGED_UNIT, globalManager.getGlobalMap().getPositionBuildUnitAttack(rangeBaseArrayList.get(i),myPlayer.getEnemyArrayList())
                );

                actionHashMap.put(rangeBaseArrayList.get(i).getId(), new EntityAction(null, b, null, null));
            }
        } else {
            for (int i = 0; i < rangeBaseArrayList.size(); i++) {
                actionHashMap.put(rangeBaseArrayList.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }


        if (myPlayer.getResource() > myPlayer.getCost(EntityType.MELEE_UNIT)-myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size() &&
                6
                        * meleeUnitArrayList.size() < rangedUnitArrayList.size()
             //   && (globalManager.getMapPotField().checkAttackBaseTwo(myPlayer.getId()) || FinalConstant.getCurrentTik() > 1)
                && meleeUnitArrayList.size()<2

            && myPlayer.getPopulationCurrent()>60
        ) {
            for (int i = 0; i < meleeBaseArrayList.size(); i++) {
                b = new BuildAction(
                        EntityType.MELEE_UNIT, globalManager.getGlobalMap().getPositionBuildUnit(meleeBaseArrayList.get(i))
                );
                // b = null;

                actionHashMap.put(meleeBaseArrayList.get(i).getId(), new EntityAction(null, b, null, null));
            }
        } else {
            for (int i = 0; i < meleeBaseArrayList.size(); i++) {
                actionHashMap.put(meleeBaseArrayList.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }

        return actionHashMap;
    }

    private boolean checkAttackBaseV2(MyPlayer myPlayer,GlobalManager globalManager) {
        ArrayList<MyEntity> arrayList = myPlayer.getEnemyArrayList();
      //  Final.DEBUGRelease("CHECKATTACK", FinalConstant.getCurrentTik() + " " + arrayList.size());
        if (arrayList.size()==0  && globalManager.getGlobalStatistic().getPlayers().size()==4)
        {
            return false;
        }
        int sizeUnit = 0;

        if (arrayList.size()>0) {
            MyPlayer myPlayer1 = globalManager.getGlobalStatistic().getPlayer(arrayList.get(0).getPlayerId());


            if (myPlayer1 != null) {
                sizeUnit = myPlayer1.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size();
            }
        }

        //Final.DEBUGRelease("CHECKATTACK", FinalConstant.getCurrentTik() + " sizeUnit: " + sizeUnit);

        if (globalManager.getGlobalStatistic().getPlayers().size()==2)
        {
            MyPlayer player =globalManager.getGlobalStatistic().getLeftPlyer();
           // Final.DEBUGRelease("CHECKATTACK", FinalConstant.getCurrentTik() + " check: " +  player.getEntityArrayList(EntityType.RANGED_UNIT).size()*4);
            if ( player.getEntityArrayList(EntityType.RANGED_UNIT).size()*4>
                    myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size()) return true;
            else return false;
        }


        if (arrayList.size()*2.5>myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size()
        || sizeUnit+5 > myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size()
        ) return true;
        else return false;
    }

    private MyEntity getNearbyBuildNeedHeal(Vec2Int position, GlobalManager globalManager) {
        byte[][] bytes = new byte[][]{
                {1, 0}, {0, 1}, {-1, 0}, {0, -1},
        };

        for (int i = 0; i < 4; i++) {
            Vec2Int vec2Int = position.add(bytes[i][0], bytes[i][1]);


            MyEntity entity = globalManager.getGlobalMap().getMap(vec2Int);
            if (entity != null) {
                if (entity.getEntityType() == EntityType.BUILDER_BASE || entity.getEntityType() == EntityType.RANGED_BASE ||
                        entity.getEntityType() == EntityType.HOUSE || entity.getEntityType() == EntityType.TURRET
                        || entity.getEntityType() == EntityType.WALL) {
                    if (entity.getPlayerId() == FinalConstant.getMyID()) {
                        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);
                        if (entity.getHealth() < entityProperties.getMaxHealth()) {
                            return entity;
                        }
                    }
                }
                if (entity.getEntityType() == EntityType.RANGED_UNIT && entity.getPlayerId()==FinalConstant.getMyID()) {
                    if (entity.getHealth() <=5) {
                        Final.DEBUG(TAG, "Heal RANGER!!!");
                        return entity;
                    }
                }

                if (entity.getEntityType() == EntityType.BUILDER_UNIT  && entity.getPlayerId()==FinalConstant.getMyID()) {
                    if (entity.getHealth() <=5) {
                        Final.DEBUG(TAG, "Heal BUILDER!!!");
                        return entity;
                    }
                }

            }
        }


        return null;
    }

    private void updateInfo(GlobalStatistic globalStatistic) {
        MyPlayer player = globalStatistic.getMyPlayer();
        sizeGold = player.getResource();
    }
}
