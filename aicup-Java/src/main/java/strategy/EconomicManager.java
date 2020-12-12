package strategy;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class EconomicManager {

    public static final String TAG = "strategy.EconomicManager";

    // количество рабочих
    int sizeBuildUnit;
    // количество золота
    int sizeGold;
    // количество жилых мест
    int countMaxUnit;

    DebugInterface debugInterface;

    public EconomicManager() {

    }

    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalManager globalManager,DebugInterface debugInterface) {
        this.debugInterface = debugInterface;

        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();
        updateInfo(globalStatistic);
        MyPlayer myPlayer = globalManager.getGlobalStatistic().getMyPlayer();

        //всё что связанно с починкой
        repairBuilder(myPlayer, playerView, globalManager, actionHashMap);

        //всё что связанно с новые зданиями
        buildBuilder(myPlayer, playerView, globalManager, actionHashMap);


        /* ДОБЫЧА ресурсов
        вызывается метод несколько, массив делится на 2 части
        1. тут в отсортированном порядке рабочие у которых рядом есть ресурсы, от меньшего до большего
        2. после того как вверхние обработаются, оставшихся рабочих сортируем по ближайщему свободному ресурсу
         */

        //добыча ресурсов
        resurceBuilder(myPlayer, playerView, globalManager, actionHashMap);

        //увороты
        dodgeBuilder(myPlayer, playerView, globalManager, actionHashMap);

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
          // Final.DEBUG(TAG, "BUILD_UNIT: " + myEntities.get(i).getId() + " " + myEntities.get(i).getUnitState());
        }

        Final.DEBUG(TAG, "BUILD CB:" + countBuilder + " CR: " + countRepear);

        return actionHashMap;
    }

    //ресурсы
    private HashMap resurceBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        ArrayList<MyEntity> builderUnitArrayListOne = myPlayer.getBuildingUnitNearResources(globalManager.getGlobalMap());

        Final.DEBUG(TAG, "BUILDER_UNITSIZE: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size());
        Final.DEBUG(TAG, "BUILDER_UNIT_ONE SIZE: " + builderUnitArrayListOne.size());

        for (int i = 0; i < builderUnitArrayListOne.size(); i++) {
            MyEntity entity = builderUnitArrayListOne.get(i);

            ArrayList<MyEntity> resource;

            resource = globalManager.getGlobalMap().getEntityMapResourceSpecial(entity.getPosition());

            if (resource.size() > 0)
            {
                if (entity.getUnitState() == EUnitState.RESURCE || entity.getUnitState() == EUnitState.EMPTY) {
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

        ArrayList<MyEntity> builderUnitArrayListTwo = myPlayer.getBuildingUnitNearResourcesOther(globalManager.getGlobalMap());

        Final.DEBUG(TAG, "BUILDER_UNIT_TWO SIZE: " + builderUnitArrayListTwo.size());

        for (int i = 0; i < builderUnitArrayListTwo.size(); i++) {
            MyEntity builder = builderUnitArrayListTwo.get(i);

            ArrayList<MyEntity> resource;

            resource = globalManager.getGlobalMap().getEntityMapResourceSpecial(builder.getPosition());

            if (resource.size()>0){
                Final.DEBUG("ERROR",  "resource>0");
            }

            MyEntity resourceMidDis = globalManager.getGlobalMap().getNearest(builder.getPosition(), EntityType.RESOURCE,true, -1);

            if (resourceMidDis!=null)
            {
                if (builder.getUnitState() == EUnitState.RESURCE || builder.getUnitState() == EUnitState.EMPTY) {
                    builder.setDataTaskUnit(new DataTaskUnit(EUnitState.RESURCE));


                    MoveAction m = new MoveAction(resourceMidDis.getPosition(),true,true);
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    a = null;
                    resourceMidDis.setTargetEntity(builder);
                    builder.setTargetEntity(resourceMidDis);
                    // strategy.Final.DEBUG(TAG, "arrayList.get(i).getId() " + builderUnitArrayList.get(i).getId() + " " +builderUnitArrayList.get(i).getPosition().toString());

                    actionHashMap.put(builder.getId(), new EntityAction(m, b, a, r));
                } else {
                    resourceMidDis.setTargetEntity(builder);
                    continue;
                }
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
                globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodgeMelee);

                entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);


                builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodge(true);

                actionHashMap.put(builderUnit.getId(), entityAction);
                continue;
            }

            // увороты от лучников
            Vec2Int vec2IntDodgeRange = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnit.getPosition(), myPlayer, 6, EntityType.RANGED_UNIT);

            if (vec2IntDodgeRange != null) {
                m = new MoveAction(vec2IntDodgeRange, true, true);
                entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);
                globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodgeRange);

                builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodge(true);

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
                globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodgeRangeTwo);

               // builderUnit.getEntityAction().setAttackAction(null);
                builderUnit.getEntityAction().setMoveAction(m);
                builderUnit.setDodge(true);

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

    // новые постройки
    private HashMap buildBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {

        // достраиваем дома
        ArrayList<MyEntity> arrayList = myPlayer.getEntityArrayList(EntityType.HOUSE);

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        boolean checkCreate = false;

        if (myPlayer.getResource() > myPlayer.getCost(EntityType.HOUSE) - 5 &&
                ((myPlayer.getPopulationCurrent() * 1.2 >= myPlayer.getPopulationMax() && myPlayer.getResource() > myPlayer.getCost(EntityType.HOUSE) * 2) || myPlayer.getPopulationMax() < 80)
                && (myPlayer.getPopulationMax() < 180)
                && (myPlayer.getCountBuildDontCreate(EntityType.HOUSE) < 3 || myPlayer.getResource() > 400 && myPlayer.getCountBuildDontCreate(EntityType.HOUSE) < 4)
        ) {
            Vec2Int positionBuildHouse = globalManager.getGlobalMap().getPositionBuildHouse(FinalConstant.getEntityProperties(EntityType.HOUSE));

            if (positionBuildHouse == null) {
                // positionBuildHouse = new Vec2Int();
                for (int i = 0; i < builderUnitArrayList.size(); i++) {
                    MyEntity entity = builderUnitArrayList.get(i);

                    if (entity.getUnitState() == EUnitState.REPAIR) continue;

                    if (entity.getPosition().getX() - 1 < 0 || entity.getPosition().getX() - 1 > 80 - 3) continue;
                    if (entity.getPosition().getY() < 0 || entity.getPosition().getY() > 80 - 3) continue;
                    BuildAction b = new BuildAction(
                            EntityType.HOUSE, new Vec2Int(
                            entity.getPosition().getX() - 1,
                            entity.getPosition().getY()
                    )
                    );

                    MoveAction m = new MoveAction(new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1), true, false);

                    AttackAction a = null;

                    entity.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                    entity.getDataTaskUnit().setEntityType(EntityType.HOUSE);

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
                        m = new MoveAction(vec2Int1, true, false);

                        b = new BuildAction(EntityType.HOUSE, positionBuildHouse);
                        checkCreate = true;
                        a = null;

                        current.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                        current.getDataTaskUnit().setEntityType(EntityType.HOUSE);

                        Final.DEBUG(TAG, "VECTOR BUILD: " + positionBuildHouse.toString() + " currentP: " + current.getPosition());

                        actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));
                    }
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

                Vec2Int vec2Int = new Vec2Int(positionTurret[j][0], positionTurret[j][1]);

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
                            m = new MoveAction(vec2Int1, true, false);

                            b = new BuildAction(EntityType.TURRET, vec2Int);
                            checkCreate = true;
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


        if (myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.RANGED_BASE).getCost() && myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()<1 )
        {


            for (int i = 0; i < builderUnitArrayList.size(); i++) {

                MyEntity builderUnit = builderUnitArrayList.get(i);


                if (builderUnit.getUnitState() == EUnitState.REPAIR || builderUnit.getUnitState() == EUnitState.BUILD)
                    continue;

                if (builderUnit.getPosition().getX()<70 && builderUnit.getPosition().getY()<75) {
                    BuildAction b = new BuildAction(
                            EntityType.RANGED_BASE, new Vec2Int(
                            builderUnit.getPosition().getX() + 1,
                            builderUnit.getPosition().getY()
                    )
                    );

                    builderUnit.setDataTaskUnit(new DataTaskUnit(EUnitState.BUILD));
                    builderUnit.getDataTaskUnit().setEntityType(EntityType.RANGED_BASE);

                    Final.DEBUG(TAG, "VECTOR BUILD: " + builderUnit.toString() + " currentP: " + builderUnit.getPosition());

                    EntityAction entityAction = actionHashMap.get(builderUnit.getId());
                    if (entityAction==null) entityAction = new EntityAction(null,null,null,null);

                    entityAction.setBuildAction(b);

                    actionHashMap.put(builderUnit.getId(),entityAction);
                }
            }

        }
/*
        if (myPlayer.getResource()>strategy.FinalConstant.getEntityProperties(EntityType.MELEE_BASE).getCost()*4 && myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size()<1 &&  myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0)
        {
            if (b==null) {
                if (builderUnitArrayList.get(i).getPosition().getX()<70 && builderUnitArrayList.get(i).getPosition().getY()<75) {
                    b = new BuildAction(
                            EntityType.RANGED_BASE, new Vec2Int(
                            entity.getPosition().getX() + 1,
                            entity.getPosition().getY()
                    )
                    );
                }
            }
        }*/


        return actionHashMap;
    }

    // чиним здания и юнитов
    private HashMap repairBuilder(MyPlayer myPlayer, PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        ArrayList<MyEntity> buildingArrayList = myPlayer.getBuildingArrayList();
        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        for (int j = 0; j < builderUnitArrayList.size(); j++) {
            MyEntity entity = builderUnitArrayList.get(j);
            RepairAction r = getNearbyBuildNeedHeal(entity.getPosition(), globalManager);

            if (r == null) {

                if (entity.getDataTaskUnit().getUnitState() == EUnitState.REPAIR || entity.getDataTaskUnit().getUnitState() == EUnitState.BUILD) {
                    entity.getDataTaskUnit().clear();
                }

                continue;
            }

            EntityAction action = actionHashMap.get(entity);

            if (action == null) action = new EntityAction(null, null, null, null);
            action.setRepairAction(r);

            entity.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));

            actionHashMap.put(entity.getId(), action);
        }


        for (int i = 0; i < buildingArrayList.size(); i++) {
            MyEntity myEntityBuilding = buildingArrayList.get(i);
            EntityProperties entityProperties = FinalConstant.getEntityProperties(myEntityBuilding.getEntityType());

            if (myEntityBuilding.getHealth() >= entityProperties.getMaxHealth()) continue;

            MyEntity builderUnit = null;
            MyEntity currentUnit = null;
            MyEntity currentUnitTwo = null;

            double minDis = 0xFFFFF;
            double minDisTwo = 0xFFFFF;

            for (int j = 0; j < builderUnitArrayList.size(); j++) {
                builderUnit = builderUnitArrayList.get(j);

                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(builderUnit.getPosition(), myEntityBuilding.getPosition(), FinalConstant.getEntityProperties(myEntityBuilding.getEntityType()));

                if (vec2Int1 == null) break;


                double dis = builderUnit.getPosition().distance(vec2Int1);

                if (dis < minDisTwo) {
                    if (dis < minDis) {
                        if (currentUnit != null) {
                            currentUnitTwo = currentUnit;
                            minDisTwo = minDis;
                        }
                        currentUnit = builderUnit;
                        minDis = dis;
                    } else {
                        currentUnitTwo = builderUnit;
                        minDisTwo = dis;
                    }
                }


                if (dis < 2) {
                    MoveAction m = null;
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    r = new RepairAction(
                            myEntityBuilding.getId()
                    );
                    //a = null;
                    m = new MoveAction(vec2Int1, true, false);

                    builderUnit.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));

                    actionHashMap.put(builderUnit.getId(), new EntityAction(m, b, a, r));
                }
            }

            if (currentUnit != null) {

                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(currentUnit.getPosition(), myEntityBuilding.getPosition(), FinalConstant.getEntityProperties(myEntityBuilding.getEntityType()));


                MoveAction m = null;
                BuildAction b = null;
                AttackAction a = null;
                RepairAction r = null;

                r = new RepairAction(
                        myEntityBuilding.getId()
                );
                //a = null;
                m = new MoveAction(vec2Int1, true, false);

                currentUnit.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));


                actionHashMap.put(currentUnit.getId(), new EntityAction(m, b, a, r));
            }

            if (currentUnitTwo != null) {
                Vec2Int vec2Int1 = globalManager.getGlobalMap().getMinPositionBuilding(currentUnitTwo.getPosition(), myEntityBuilding.getPosition(), FinalConstant.getEntityProperties(myEntityBuilding.getEntityType()));


                MoveAction m = null;
                BuildAction b = null;
                AttackAction a = null;
                RepairAction r = null;

                r = new RepairAction(
                        myEntityBuilding.getId()
                );
                //a = null;
                m = new MoveAction(vec2Int1, true, false);

                //  currentUnitTwo.setEUnitState(strategy.EUnitState.REPAIR);
                currentUnitTwo.setDataTaskUnit(new DataTaskUnit(EUnitState.REPAIR));


                actionHashMap.put(currentUnitTwo.getId(), new EntityAction(m, b, a, r));
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

        // создаем новые юниты
        if ((builderUnitArrayList.size() < myPlayer.getPopulationMax() * 0.75 && builderUnitArrayList.size() < 65 || builderUnitArrayList.size() < 13)
               /* && !(globalManager.getGlobalMap().getResourceMap() < 10000 && builderUnitArrayList.size() > 15) &&
                !(globalManager.getGlobalMap().getResourceMap() < 20000 && builderUnitArrayList.size() > 25
                )*/
                && !globalManager.getMapPotField().checkAttackBase(myPlayer.getId(), globalManager.getGlobalStatistic())
            //    && ((myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size())>1 || myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()==0)

        ) {

            b = null;

            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i = 0; i < arrayList1.size(); i++) {


                Vec2Int vec2Int =  null;

                if (FinalConstant.getCurrentTik()<400) {
                    vec2Int = globalManager.getGlobalMap().getPositionBuildUnitPrioriteV2(arrayList1.get(i));
                }
                else {
                    vec2Int = globalManager.getGlobalMap().getPositionBuildUnitPriorite(arrayList1.get(i));
                }

                b = new BuildAction(EntityType.BUILDER_UNIT, vec2Int);

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, b, null, null));
            }
        } else {
            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i = 0; i < arrayList1.size(); i++) {

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }

        Final.DEBUG(TAG, "arrayList RANGED_BASE BASE: " + rangeBaseArrayList.size() + " resource: " + myPlayer.getResource());
        if (myPlayer.getResource() > myPlayer.getCost(EntityType.RANGED_UNIT) &&
                (globalManager.getMapPotField().checkAttackBaseTwo(myPlayer.getId()) || FinalConstant.getCurrentTik() > 1)
        && true) {
            for (int i = 0; i < rangeBaseArrayList.size(); i++) {
                b = new BuildAction(
                        EntityType.RANGED_UNIT, globalManager.getGlobalMap().getPositionBuildUnit(rangeBaseArrayList.get(i))
                );

                actionHashMap.put(rangeBaseArrayList.get(i).getId(), new EntityAction(null, b, null, null));
            }
        } else {
            for (int i = 0; i < rangeBaseArrayList.size(); i++) {
                actionHashMap.put(rangeBaseArrayList.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }


        if (myPlayer.getResource() > myPlayer.getCost(EntityType.MELEE_UNIT) &&
                6
                        * meleeUnitArrayList.size() < rangedUnitArrayList.size() &&
                (globalManager.getMapPotField().checkAttackBaseTwo(myPlayer.getId()) || FinalConstant.getCurrentTik() > 1)
                && meleeUnitArrayList.size()<2

      //      && myPlayer.getPopulationMax()>120
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

    private RepairAction getNearbyBuildNeedHeal(Vec2Int position, GlobalManager globalManager) {
        byte[][] bytes = new byte[][]{
                {1, 0}, {0, 1}, {-1, 0}, {0, -1},
        };

        for (int i = 0; i < 4; i++) {
            Vec2Int vec2Int = position.add(bytes[i][0], bytes[i][1]);


            Entity entity = globalManager.getGlobalMap().getMap(vec2Int);
            if (entity != null) {
                if (entity.getEntityType() == EntityType.BUILDER_BASE || entity.getEntityType() == EntityType.RANGED_BASE ||
                        entity.getEntityType() == EntityType.HOUSE || entity.getEntityType() == EntityType.TURRET
                        || entity.getEntityType() == EntityType.WALL || entity.getEntityType() == EntityType.MELEE_BASE) {
                    if (entity.getPlayerId() == FinalConstant.getMyID()) {
                        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);
                        if (entity.getHealth() < entityProperties.getMaxHealth()) {
                            return new RepairAction(entity.getId());
                        }
                    }
                }
                if (entity.getEntityType() == EntityType.RANGED_UNIT) {
                    if (entity.getHealth() <=5) {
                        Final.DEBUG(TAG, "Heal RANGER!!!");
                        return new RepairAction(entity.getId());
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
