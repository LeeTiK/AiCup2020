package strategy;

import model.*;
import strategy.map.wave.SearchAnswer;

import java.util.ArrayList;
import java.util.HashMap;

public class WarManager {

    public static final String TAG = "strategy.WarManager";

    public static final boolean HEAL_RANGER = true;

    DebugInterface debugInterface;
    //группы юнитов
    ArrayList<MyGroupUnit> mMyGroupUnitArrayList = new ArrayList<>();

    int sizeLeft;
    int sizeRight;
    int sizeMy;


    int globalPositionDefense = 0;

    //отвечаем за атаку и защиту


    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalManager globalManager, DebugInterface debugInterface) {
        this.debugInterface = debugInterface;
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();


        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        sizeMy = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size();
        sizeLeft = globalManager.getGlobalStatistic().getLeftPlyer().getEntityArrayList(EntityType.RANGED_UNIT).size() + globalManager.getGlobalStatistic().getLeftPlyer().getEntityArrayList(EntityType.MELEE_UNIT).size();
        sizeRight = globalManager.getGlobalStatistic().getRightPlyer().getEntityArrayList(EntityType.RANGED_UNIT).size() + globalManager.getGlobalStatistic().getRightPlyer().getEntityArrayList(EntityType.MELEE_UNIT).size();

        Final.DEBUG(TAG, " sizeMy: " + sizeMy + " " + sizeLeft + " " +sizeRight);

        if (Final.OFF_WAR) return actionHashMap;

        // поиск врагов
        myPlayer.searchEnemy(globalManager.getGlobalMap());

        myPlayer.initEnemyArrayListSlow();


       /* if (FinalConstant.getCurrentTik() < 210) {
            moveUnitOld(myPlayer, globalManager, actionHashMap, 1000);
        } else {

            if (FinalConstant.getCurrentTik() < 210) {
                moveUnitOld(myPlayer, globalManager, actionHashMap, 17);
            } else {
                moveUnitOld(myPlayer, globalManager, actionHashMap, 1000);
            }
        }*/

        moveUnitV2(myPlayer,globalManager,actionHashMap);

        // сортируем по удалёности (сначала самые дальние)
        myPlayer.sortAttackUnit(false);
        // проверяем атаку юнитов
        attackUnit(myPlayer, globalManager, actionHashMap);

        // dodgeUnit(myPlayer,globalManager,actionHashMap);

        // действия для турелей
        attackTurret(myPlayer, globalManager, actionHashMap);


        //  myPlayer.sortAttackUnit(globalManager.getGlobalMap());
        // выполняем обработку движения юнитов (нужен A star)

        /*

        if (FinalConstant.getCurrentTik()<350)
        {
            defence(playerView,globalManager,actionHashMap);
        }
        else {
            actionHashMap.putAll(attack(playerView, globalManager, 1000));
        }*/


        return actionHashMap;

    }

    private void attackTurret(MyPlayer myPlayer, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);

        for (int i = 0; i < turretArrayList.size(); i++) {

            strategy.DataAttack idAttack = getTargetAttack(turretArrayList.get(i), globalManager);

            AttackAction a = null;

            if (idAttack != null) {
                a = new AttackAction(
                        idAttack.getIdEntity(),
                        new AutoAttack(
                                FinalConstant.getEntityPropertiesTURRET().getSightRange(),
                                new EntityType[]{}
                        )
                );
            } else {
                a = new AttackAction(
                        null,
                        new AutoAttack(
                                FinalConstant.getEntityPropertiesTURRET().getSightRange(),
                                new EntityType[]{}
                        )
                );
            }

            //  }

            actionHashMap.put(turretArrayList.get(i).getId(), new EntityAction(null, null, a, null));
        }
    }

    private void attackUnit(MyPlayer myPlayer, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> buildArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        //сначала милишники , потом ренджеры
        for (int i = 0; i < meleeArrayList.size(); i++) {

            DataAttack dataAttack = getTargetAttack(meleeArrayList.get(i), globalManager);

            meleeArrayList.get(i).setDataAttack(dataAttack);
            AttackAction a = null;

            if (dataAttack != null) {

                if (dataAttack.getMyEntity() != null) {
                    a = new AttackAction(
                            null,
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    meleeArrayList.get(i).getEntityAction().setAttackAction(a);
                    actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(null, null, a, null));
                } else {
                    a = new AttackAction(
                            dataAttack.getIdEntity(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    meleeArrayList.get(i).getEntityAction().setAttackAction(a);
                    actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(null, null, a, null));
                }
            }

        }

        for (int i = 0; i < rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

          //  if (range.isDodge()) continue;

            DataAttack dataAttack = getTargetAttack(rangeArrayList.get(i), globalManager);

            range.setDataAttack(dataAttack);

            AttackAction a = null;

            if (dataAttack != null) {

                if (dataAttack.getMyEntity() != null) {

                    a = null;

                    //m = new MoveAction(dataAttack.getMyEntity().getPosition(), true, true);
                } else {

                    if (range.isDodge())
                    {
                        if (dataAttack.getTargetEntity().getEntityType()==EntityType.MELEE_UNIT)
                        {
                            continue;
                        }
                    }

                    a = new AttackAction(
                            dataAttack.getIdEntity(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    range.getEntityAction().setAttackAction(a);
                    range.getEntityAction().setMoveAction(null);
                    actionHashMap.put(range.getId(), new EntityAction(null, null, a, null));
                    //  m = null;
                }
            }
        }

        if (globalManager.getGlobalMap().getResourceMap()==0)
        {
            for (int i = 0; i < buildArrayList.size(); i++) {

                DataAttack dataAttack = getTargetAttack(buildArrayList.get(i), globalManager);

                buildArrayList.get(i).setDataAttack(dataAttack);
                AttackAction a = null;

                if (dataAttack != null) {

                    if (dataAttack.getMyEntity() != null) {
                        a = new AttackAction(
                                null,
                                new AutoAttack(
                                        FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                        new EntityType[]{}
                                )
                        );
                        buildArrayList.get(i).getEntityAction().setAttackAction(a);
                        actionHashMap.put(buildArrayList.get(i).getId(), new EntityAction(null, null, a, null));
                    } else {
                        a = new AttackAction(
                                dataAttack.getIdEntity(),
                                new AutoAttack(
                                        FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                        new EntityType[]{}
                                )
                        );
                        buildArrayList.get(i).getEntityAction().setAttackAction(a);
                        actionHashMap.put(buildArrayList.get(i).getId(), new EntityAction(null, null, a, null));
                    }
                }

            }
        }
    }

    private void moveUnitV2(MyPlayer myPlayer, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap)
    {
        GlobalMap globalMap = globalManager.getGlobalMap();

        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);
        ArrayList<MyEntity> buildArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        ArrayList<MyEntity> enemyArrayList = myPlayer.getEnemyArrayList();

        Final.DEBUG(TAG, "TIK: " + FinalConstant.getCurrentTik() + " rangeArrayList SIZE: " + rangeArrayList.size());

        for (int i = 0; i < rangeArrayList.size(); i++){
            rangeArrayList.get(i).setUpdate(false);
        }
        for (int i = 0; i < meleeArrayList.size(); i++) {
            meleeArrayList.get(i).setUpdate(false);
        }

        for (int i = 0; i < rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

            EntityAction entityAction = actionHashMap.get(range.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            if (dodgeRanger(range,myPlayer,entityAction,globalManager))
            {
                range.setUpdate(true);
                range.setDangerMove(true);
                range.setDodge(true);
                range.setEnemyMinDis(null);
                actionHashMap.put(range.getId(), entityAction);
                continue;
            }

            if (HEAL_RANGER) {
                if (range.getHealth() <=5) {
                    MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(), myPlayer, EntityType.BUILDER_UNIT);

                    if (entity != null) {
                        MoveAction moveAction = globalManager.getMoveManager().getMoveActionPosition(range,entity.getPosition());
                        entityAction.setMoveAction(moveAction);
                        range.getEntityAction().setMoveAction(moveAction);
                        range.setUpdate(true);
                        range.setDodge(true);
                        range.setEnemyMinDis(null);
                        actionHashMap.put(range.getId(), entityAction);
                        continue;
                    }
                }
            }
        }

        Final.DEBUG(TAG, "TIK: " + FinalConstant.getCurrentTik() + " enemyArrayList SIZE: " + enemyArrayList.size());
        int sizeUnit = 1;
        // защищаем нашу базу
        for (int i=0; i<enemyArrayList.size(); i++)
        {
            MyEntity enemy = enemyArrayList.get(i);

            for (int j=0; j<sizeUnit; j++) {
                MyEntity myEntity = globalMap.getNearestPlayer(enemy.getPosition(), enemy.getPlayerId(), FinalConstant.getMyID(), EntityType.RANGED_UNIT, true);

                if (myEntity != null) {
                    Final.DEBUG(TAG, "TIK: " + FinalConstant.getCurrentTik() + " myEntity defence: " + myEntity.getId());

                    EntityAction entityAction = actionHashMap.get(myEntity.getId());
                    if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

                    MoveAction moveAction = globalManager.getMoveManager().getMoveActionPosition(myEntity,enemy.getPosition());
                    entityAction.setMoveAction(moveAction);

                    enemy.setTargetEntity(myEntity);

                    myEntity.setEnemyMinDis(enemy);
                    myEntity.setUpdate(true);

                    actionHashMap.put(myEntity.getId(), entityAction);
                } else {
                    Final.DEBUG(TAG, "TIK: " + FinalConstant.getCurrentTik() + " ERROR MY UNIT ");
                }
            }
        }

        //сначала милишники
        //милишники должны не бояться идти против одного лучника/ против нескольких уходить или идти на рабочих и мешать добывать
        //милишники должны уметь защищать турели своими хп, прятаться за турелью и выходить когда на турель напали
        for (int i = 0; i < meleeArrayList.size(); i++) {
            MyEntity melee = meleeArrayList.get(i);

            if (melee.isUpdate()) continue;

            EntityAction entityAction = actionHashMap.get(melee.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);


            MyEntity enemy = globalManager.getGlobalMap().getNearestPlayer(melee.getPosition(), myPlayer.getId(), -1);


            if (enemy!=null)
            {
                MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(melee.getPosition());

                Final.DEBUG(TAG, "ID:" + melee.getId() + " entity: " + entity);
                MoveAction m;
                if (entity == null) {
                    m = globalManager.getMoveManager().getMoveActionPosition(melee,enemy.getPosition());
                         //   new MoveAction(enemy.getPosition(), true, false);
                } else {
                    m = globalManager.getMoveManager().getMoveActionPosition(melee,entity.getPosition());
                         //   new MoveAction(entity.getPosition(), true, false);
                    entity.setRotation(true);
                }

                entityAction.setMoveAction(m);
                actionHashMap.put(melee.getId(), entityAction);
            }
        }

        //сначала ренджи
        //должны уметь всё xD
        // уметь доджить милишников
        // уходить на личение при 5 хп и 1 хп
        // должны выбирать лучшую позицию для атаки врага, путём волнового алгоритма
        // должен как и милишник ходить атаковать,когда на базу никто не напал,
        // упрощаем логику защиты, теперь есть список ближайщих врагов не юнита и наших зданий и рабочих, и атакуем этих юнитов
        // если базу не атакую, атакуем сами (экономика не должна спамить войнов для простоя) (ищем строителей)

        for (int i = 0; i < rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

            EntityAction entityAction = actionHashMap.get(range.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            Vec2Int vec2IntDanger = globalManager.getMapPotField().getDangerAttackRanger(range);

            if (vec2IntDanger != null && !range.isDodge()) {
                MoveAction m = globalManager.getMoveManager().getMoveActionPosition(range,vec2IntDanger);
                        //new MoveAction(vec2IntDanger, true, false);
                globalManager.getGlobalMap().setPositionNextTick(range.getPosition(),vec2IntDanger);

                entityAction.setMoveAction(m);
                range.setUpdate(true);
                range.setDangerMove(true);

                actionHashMap.put(range.getId(), entityAction);
                continue;
            }

            if (range.isUpdate()) continue;

            MyEntity enemy = globalManager.getGlobalMap().getNearestPlayer(range.getPosition(), myPlayer.getId(), -1);

            if (enemy!=null)
            {
                MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(range.getPosition());

                Final.DEBUG(TAG, "ID:" + range.getId() + " entity: " + entity);
                MoveAction m;
                if (entity == null) {
                    m = globalManager.getMoveManager().getMoveActionPosition(range,enemy.getPosition());
                //new MoveAction(enemy.getPosition(), true, true);
                } else {
                    m = globalManager.getMoveManager().getMoveActionPosition(range,entity.getPosition());
                            //new MoveAction(entity.getPosition(), true, true);
                    entity.setRotation(true);
                }

                range.setEnemyMinDis(enemy);
                entityAction.setMoveAction(m);
                actionHashMap.put(range.getId(), entityAction);
            }

        }

        for (int i = 0; i < rangeArrayList.size(); i++) {
            //просто тест
            MyEntity range = rangeArrayList.get(i);

            EntityAction entityAction = actionHashMap.get(range.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            if (range.isDodge()) continue;
            if (range.isDangerMove()) continue;

            MyEntity enemy = range.getEnemyMinDis();

            if (enemy!=null) {

               if (enemy.getEntityType() == EntityType.RANGED_UNIT) {
                    if (enemy.getPosition().distance(range.getPosition()) < 12) {
                        SearchAnswer searchAnswer = globalManager.getWaveSearchModule().searchPathRange(range.getPosition(), 12);
                        if (searchAnswer != null) {
                            Vec2Int nextMove = searchAnswer.getPath().getLast();

                            MoveAction m = globalManager.getMoveManager().getMoveActionPosition(range,nextMove);
                                    //new MoveAction(nextMove, true, false);
                            globalManager.getGlobalMap().setPositionNextTick(range.getPosition(), nextMove);

                            globalManager.getMapPotField().changeBlockPositionAttack(searchAnswer.getEnd());

                            if (Final.debugGraphic) {
                                if (Final.CHECK_SEARCH_PATH_RANGER) {
                                    for (int k = 0; k < searchAnswer.getPath().size(); k++) {
                                        FinalGraphic.sendSquare(debugInterface, searchAnswer.getPath().get(k), 1, FinalGraphic.COLOR_BLACK);
                                    }
                                }
                            }


                            entityAction.setMoveAction(m);
                            range.getEntityAction().setMoveAction(m);
                            actionHashMap.put(range.getId(), entityAction);
                            continue;
                        }
                    }
                }
            }
        }

        if (!globalManager.getGlobalStatistic().isCheckEnemyUnits() &&(FinalConstant.getCurrentTik()<240 || FinalConstant.getCurrentTik()>800))
        {
            for (int i = 0; i < rangeArrayList.size(); i++) {
                //просто тест
                MyEntity range = rangeArrayList.get(i);

                EntityAction entityAction = actionHashMap.get(range.getId());
                if (entityAction == null) entityAction = new EntityAction(null, null, null, null);


                Vec2Int vec2Int = globalManager.getGlobalStatistic().getMinDisToPlayerFogOfWar(range.getPosition());


                if (vec2Int != null) {
                    MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(range.getPosition());

                    Final.DEBUG(TAG, "ID:" + range.getId() + " entity: " + entity);
                    MoveAction m;
                    if (entity == null) {
                        m = globalManager.getMoveManager().getMoveActionPosition(range,vec2Int);
                                //new MoveAction(vec2Int, true, true);
                    } else {
                        m = globalManager.getMoveManager().getMoveActionPosition(range,entity.getPosition());
                                //new MoveAction(entity.getPosition(), true, true);
                        entity.setRotation(true);
                    }

                    entityAction.setMoveAction(m);
                    actionHashMap.put(range.getId(), entityAction);
                }
            }
        }

        if (globalManager.getGlobalMap().getResourceMap()<2000)
        {
            for (int i = 0; i < buildArrayList.size(); i++) {
                MyEntity build = buildArrayList.get(i);

                if (build.getTargetEntity()!=null) continue;

                if (build.getUnitState() == EUnitState.REPAIR || build.getUnitState() == EUnitState.BUILD)
                    continue;
               // if (build.isUpdate()) continue;

                EntityAction entityAction = actionHashMap.get(build.getId());
                if (entityAction == null) entityAction = new EntityAction(null, null, null, null);


                MyEntity enemy = globalManager.getGlobalMap().getNearestPlayer(build.getPosition(), myPlayer.getId(), -1,EntityType.NO_ATTACK_ENTITY,false);

                if (enemy!=null)
                {
                  /*  MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(build.getPosition());

                    Final.DEBUG(TAG, "ID:" + build.getId() + " entity: " + entity);
                    MoveAction m;
                    if (entity == null) */
                    MoveAction  m = globalManager.getMoveManager().getMoveActionPosition(build,enemy.getPosition());
                            //new MoveAction(enemy.getPosition(), true, false);
                   /* } else {
                        m = new MoveAction(entity.getPosition(), true, false);
                        entity.setRotation(true);
                    }*/

                    entityAction.setMoveAction(m);
                    actionHashMap.put(build.getId(), entityAction);
                }
                else {
                    Vec2Int vec2Int = globalManager.getGlobalStatistic().getMinDisToPlayerFogOfWar(build.getPosition());

                    if (vec2Int != null) {
                     /*   MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(build.getPosition());

                        Final.DEBUG(TAG, "ID:" + build.getId() + " entity: " + entity);
                        MoveAction m;
                        if (entity == null) {*/
                        MoveAction   m =   globalManager.getMoveManager().getMoveActionPosition(build,vec2Int);
                                //new MoveAction(vec2Int, true, false);


                     /*   } else {
                            m = new MoveAction(entity.getPosition(), true, false);
                            entity.setRotation(true);
                        }*/

                        entityAction.setMoveAction(m);
                        actionHashMap.put(build.getId(), entityAction);
                    }
                }
            }
        }
    }

    private boolean dodgeRanger(MyEntity range, MyPlayer myPlayer, EntityAction entityAction, GlobalManager globalManager) {

        Vec2Int vec2IntDodge = globalManager.getGlobalMap().checkDangerBuildUnit(range.getPosition(), myPlayer, 2, EntityType.MELEE_UNIT);

        if (vec2IntDodge != null) {
            MoveAction   m = globalManager.getMoveManager().getMoveActionPosition(range,vec2IntDodge);
           // MoveAction m = new MoveAction(vec2IntDodge, true, false);

            globalManager.getGlobalMap().setPositionNextTick(range.getPosition(),vec2IntDodge);

            entityAction.setAttackAction(null);

            if (vec2IntDodge.equals(range.getPosition()))
            {
                entityAction.setMoveAction(null);
                range.getEntityAction().setMoveAction(null);
            }
            else {
                entityAction.setMoveAction(m);
                range.getEntityAction().setMoveAction(m);
            }


            range.getEntityAction().setAttackAction(null);
            range.setDodge(true);

            return true;
        }
        return false;
    }



    DataAttack getTargetAttack(MyEntity entity, GlobalManager globalManager) {

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        int attackRange = entityProperties.getAttack().getAttackRange();

        // if (entity.getEntityType() == EntityType.RANGED_UNIT) attackRange++;

        ArrayList<MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(entity.getPosition(), attackRange, FinalConstant.getMyID(), true, false, false, EntityType.ALL, false, entity.getEntityType() == EntityType.TURRET);

        if (arrayList.size() == 0) return null;

        int minHPRange = 0xFFFF;
        int minHPBuild = 0xFFFF;
        int minHPMelee = 0xFFFF;
        int minHPTurret = 0xFFFF;
        int minHPBuilding = 0xFFFF;
        int minHPWall = 0xFFFF;

        MyEntity range = null;
        MyEntity buildUnit = null;
        MyEntity melee = null;
        MyEntity turret = null;
        MyEntity building = null;
        MyEntity wall = null;

        for (int i = 0; i < arrayList.size(); i++) {
            MyEntity entity1 = arrayList.get(i);

            if (entity1.getSimulationHP() <= 0) continue;

            switch (entity1.getEntityType()) {

                case WALL:
                    if (minHPWall > entity1.getSimulationHP()) {
                        wall = entity1;
                        minHPWall = entity1.getSimulationHP();
                    }
                    break;
                case HOUSE:
                case BUILDER_BASE:
                case MELEE_BASE:
                case RANGED_BASE:
                    if (minHPBuilding > entity1.getSimulationHP()) {
                        building = entity1;
                        minHPBuilding = entity1.getSimulationHP();
                    }
                    break;
                case MELEE_UNIT:
                    if (minHPMelee > entity1.getSimulationHP()) {
                        melee = entity1;
                        minHPMelee = entity1.getSimulationHP();
                    }
                    break;
                case RANGED_UNIT:
                    if (minHPRange > entity1.getSimulationHP()) {
                        range = entity1;
                        minHPRange = entity1.getSimulationHP();
                    }
                    break;
                case BUILDER_UNIT:
                    if (minHPBuild > entity1.getSimulationHP()) {
                        buildUnit = entity1;
                        minHPBuild = entity1.getSimulationHP();
                    }
                    break;
                case TURRET:
                    if (minHPTurret > entity1.getSimulationHP()) {
                        turret = entity1;
                        minHPTurret = entity1.getSimulationHP();
                    }
                    break;
            }
        }

        if (building!=null)
        {
            if (!building.isActive())
            {
                building.attackHP(entityProperties.getAttack().getDamage());
                return new DataAttack(building);
            }
        }

        if (range != null) {
            range.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(range);
        }

        if (melee != null) {
            melee.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(melee);
        }

        if (buildUnit != null) {
            buildUnit.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(buildUnit);
        }

        if (turret != null) {

            // добавить проверку на рабочих рядом

            /*MyEntity entity1 = globalManager.getGlobalMap().getBuilderUnitNearTurret(turret);

            if (entity1!=null)
            {
                DataAttack dataAttack = new DataAttack(entity1);
                dataAttack.setMyEntity(entity1);
                return dataAttack;
            }*/

            turret.attackHP(entityProperties.getAttack().getDamage());

            return new DataAttack(turret);
        }

        if (building != null) {
            building.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(building);
        }

        if (wall != null) {
            wall.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(wall);
        }

        return null;
    }

    ;

    public DebugInterface getDebugInterface() {
        return debugInterface;
    }
}
