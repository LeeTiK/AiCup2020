package strategy;

import model.*;
import strategy.*;
import strategy.map.astar.AStar;
import strategy.map.astar.MapInfo;
import strategy.map.astar.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class WarManager {

    public static final String TAG = "strategy.WarManager";

    public static final boolean HEAL_RANGER = true;

    //группы юнитов
    ArrayList<MyGroupUnit> mMyGroupUnitArrayList = new ArrayList<>();

    int sizeLeft;
    int sizeRight;

    //отвечаем за атаку и защиту

    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalManager globalManager) {
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();


        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);

        ArrayList<MyPlayer> arrayList = globalStatistic.getPlayers();
        MyPlayer targetPlayerAttack;

        if (Final.OFF_WAR) return actionHashMap;

        //if (globalStatistic.getCurrentTik()<30) return actionHashMap;

        //проверяем разбиты группы юнитов
       //  updateGroupUnit();

        // сортируем всех юнитов готовых на атаку, по ближайщему врагу, кто ближе тот и первый будет обрабатываться
         myPlayer.sortAttackUnit(globalManager.getGlobalMap());

        if (FinalConstant.getCurrentTik()<300)
        {
            moveUnitOld(myPlayer,globalManager,actionHashMap,12);
        }
        else {
            moveUnitOld(myPlayer,globalManager,actionHashMap,1000);
        }

        // проверяем атаку юнитов
        attackUnit(myPlayer,globalManager,actionHashMap);

       // dodgeUnit(myPlayer,globalManager,actionHashMap);

        // действия для турелей
        attackTurret(myPlayer,globalManager,actionHashMap);


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

    private HashMap<Integer, EntityAction> attack(PlayerView playerView, GlobalManager globalManager, int dis) {
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();

        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);

        for (int i=0; i<meleeArrayList.size(); i++) {
            /*strategy.MyEntity melee = meleeArrayList.get(i);
            Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(melee.getPosition(),playerView.getMyId());

            strategy.Final.DEBUG(TAG,"distance: " + vec2Int.distance(melee.getPosition()));

            MoveAction m = null;
            if (vec2Int!=null)
            {

                ArrayList<strategy.MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(melee.getPosition(),6,strategy.FinalConstant.getMyID(),false,true,true,EntityType.ALL,true);

                if (arrayList.size()<9 && vec2Int.distance(melee.getPosition())>6 && strategy.FinalConstant.getCurrentTik()<750)
                {
                    strategy.MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(melee.getPosition(),myPlayer,EntityType.TURRET);
                    if (entity!=null)
                    {
                        m = new MoveAction(entity.getPosition(), true, true);
                    }
                    else {
                        entity = globalManager.getGlobalMap().getMinDisToEntity(melee.getPosition(),myPlayer,EntityType.RANGED_BASE);
                        if (entity!=null)
                        {
                            m = new MoveAction(entity.getPosition(), true, false);
                        }
                    }
                }
                else {
                    if (vec2Int.distance(melee.getPosition()) < dis) {
                        m = new MoveAction(vec2Int, true, true);
                    } else {
                        strategy.MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(melee.getPosition(),myPlayer,EntityType.RANGED_BASE);
                        if (entity!=null)
                        {
                            m = new MoveAction(entity.getPosition(), true, true);
                        }
                        else {
                            if (turretArrayList.size() > 0) {
                                m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                            }
                        }
                    }
                }
            }*/

            Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(meleeArrayList.get(i).getPosition(),playerView.getMyId());

            MoveAction m = null;
            if (vec2Int!=null)
            {

                Final.DEBUG(TAG,"distance: " + vec2Int.distance(meleeArrayList.get(i).getPosition()));


                if (vec2Int.distance(meleeArrayList.get(i).getPosition())<dis) {
                    m = new MoveAction(vec2Int, true, true);
                }
                else {
                    if (turretArrayList.size()>0)
                    {
                        m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                    }
                }
            }


            DataAttack dataAttack = getTargetAttack(meleeArrayList.get(i),globalManager);

            AttackAction a = null;

            if (dataAttack!=null) {

                if (dataAttack.getMyEntity()!=null)
                {
                    a = null;

                            m = new MoveAction(dataAttack.getMyEntity().getPosition(), true, true);
                }
                else {
                    a = new AttackAction(
                            dataAttack.getIdEntity(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    m = null;
                }
            }
            else {
                a = new AttackAction(
                        null,
                        new AutoAttack(
                                FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                new EntityType[]{}
                        )
                );
            }

            actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(m, null, a, null));
        }



        for (int i=0; i<rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

            Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(range.getPosition(),playerView.getMyId());

            MoveAction m = null;
           /* if (vec2Int!=null)
            {

                ArrayList<strategy.MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(range.getPosition(),6,strategy.FinalConstant.getMyID(),false,true,true,EntityType.ALL,true);

                if (arrayList.size()<9 && vec2Int.distance(range.getPosition())>9 && strategy.FinalConstant.getCurrentTik()<750)
                {
                    strategy.MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(),myPlayer,EntityType.TURRET);
                    if (entity!=null)
                    {
                        m = new MoveAction(entity.getPosition(), true, false);
                    }
                    else {
                        entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(),myPlayer,EntityType.RANGED_BASE);
                        if (entity!=null)
                        {
                            m = new MoveAction(entity.getPosition(), true, false);
                        }
                    }
                }
                else {
                    if (vec2Int.distance(range.getPosition()) < dis) {
                        m = new MoveAction(vec2Int, true, true);
                    } else {
                        strategy.MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(),myPlayer,EntityType.MELEE_BASE);
                        if (entity!=null)
                        {
                            m = new MoveAction(entity.getPosition(), true, false);
                        }
                        else {
                            if (turretArrayList.size() > 0) {
                                m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                            }
                        }
                    }
                }
            }*/

            if (vec2Int!=null)
            {

                Final.DEBUG(TAG,"distance: " + vec2Int.distance(range.getPosition()));


                if (vec2Int.distance(range.getPosition())<dis) {
                    m = new MoveAction(vec2Int, true, true);
                }
                else {
                    MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(),myPlayer,EntityType.TURRET);
                    if (entity!=null)
                    {
                        m = new MoveAction(entity.getPosition(), true, false);
                    }
                    else {
                        if (turretArrayList.size() > 0) {
                            m = new MoveAction(turretArrayList.get(0).getPosition(), true, true);
                        }
                    }
                }
            }

            if (vec2Int.distance(range.getPosition())<6)
            {
                int k=0;
            }

            DataAttack dataAttack = getTargetAttack(rangeArrayList.get(i),globalManager);

            AttackAction a = null;

            if (dataAttack!=null) {

                if (dataAttack.getMyEntity()!=null)
                {

                    a = null;

                    m = new MoveAction(dataAttack.getMyEntity().getPosition(), true, true);
                }
                else {
                    a = new AttackAction(
                            dataAttack.getIdEntity(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    m = null;
                }
            }
            else {
                // идём на хил
                if (HEAL_RANGER) {
                    if (range.getHealth() == 5) {
                        MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(), myPlayer, EntityType.BUILDER_UNIT);

                        if (entity != null) {
                            m = new MoveAction(entity.getPosition(), true, true);
                        }
                    }
                }

            }

            // увороты от милишников

            Vec2Int vec2IntDodge = globalManager.getGlobalMap().checkDangerBuildUnit(range.getPosition(),myPlayer,2,EntityType.MELEE_UNIT);

            if (vec2IntDodge != null) {

                if (dataAttack!=null)
                {
                    dataAttack.reset(FinalConstant.getEntityProperties(EntityType.RANGED_UNIT).getAttack().getDamage());
                }

                a = null;
                m = new MoveAction(vec2IntDodge, true, true);
            }
            /*
            ArrayList<strategy.MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(range.getPosition(), 2, strategy.FinalConstant.getMyID(), true, true,EntityType.MELEE_UNIT);
            if (arrayList.size()!=0)
            {


                ArrayList<strategy.MyEntity> arrayListRange = globalManager.getGlobalMap().getEntityMap(range.getPosition(), 6, strategy.FinalConstant.getMyID(), true, true,EntityType.RANGED_UNIT);

            }*/

            actionHashMap.put(range.getId(), new EntityAction(m, null, a, null));
        }

        return actionHashMap;
    }

    private void defence(PlayerView playerView, GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap ) {
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);

        for (int i=0; i<meleeArrayList.size(); i++) {


            Vec2Int vec2Int = globalManager.getMapPotField().getNearestPlayerIntoPlayerArea(meleeArrayList.get(i).getPosition(),playerView.getMyId());

            MoveAction m = null;
            if (vec2Int!=null)
            {

                Final.DEBUG(TAG,"distance: " + vec2Int.distance(meleeArrayList.get(i).getPosition()));

                m = new MoveAction(vec2Int, true, false);
            }
            else {
                if (turretArrayList.size()>0)
                {
                    m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                }
            }


            DataAttack dataAttack = getTargetAttack(meleeArrayList.get(i),globalManager);

            AttackAction a = null;

            if (dataAttack!=null) {

                if (dataAttack.getMyEntity()!=null)
                {
                    a = null;

                    m = new MoveAction(dataAttack.getMyEntity().getPosition(), true, true);
                }
                else {
                    a = new AttackAction(
                            dataAttack.getIdEntity(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    m = null;
                }
            }
            else {
                a = new AttackAction(
                        null,
                        new AutoAttack(
                                FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                new EntityType[]{}
                        )
                );
            }

            actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(m, null, a, null));
        }



        for (int i=0; i<rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

            Vec2Int vec2Int = globalManager.getMapPotField().getNearestPlayerIntoPlayerArea(range.getPosition(),playerView.getMyId());

            MoveAction m = null;
            if (vec2Int!=null)
            {

                Final.DEBUG(TAG,"distance: " + vec2Int.distance(range.getPosition()));

                m = new MoveAction(vec2Int, true, false);
            }
            else {
                if (turretArrayList.size()>0)
                {
                    m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                }
            }

            if (vec2Int.distance(range.getPosition())<6)
            {
                int k=0;
            }

            DataAttack dataAttack = getTargetAttack(rangeArrayList.get(i),globalManager);

            AttackAction a = null;

            if (dataAttack!=null) {

                if (dataAttack.getMyEntity()!=null)
                {

                    a = null;

                    m = new MoveAction(dataAttack.getMyEntity().getPosition(), true, true);
                }
                else {
                    a = new AttackAction(
                            dataAttack.getIdEntity(),
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    m = null;
                }
            }
            else {
                // идём на хил
                if (HEAL_RANGER) {
                    if (range.getHealth() == 5) {
                        MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(), myPlayer, EntityType.BUILDER_UNIT);

                        if (entity != null) {
                            m = new MoveAction(entity.getPosition(), true, true);
                        }
                    }
                }

            }

            // увороты от милишников

            Vec2Int vec2IntDodge = globalManager.getGlobalMap().checkDangerBuildUnit(range.getPosition(),myPlayer,2,EntityType.MELEE_UNIT);

            if (vec2IntDodge != null) {

                if (dataAttack!=null)
                {
                    dataAttack.reset(FinalConstant.getEntityProperties(EntityType.RANGED_UNIT).getAttack().getDamage());
                }

                a = null;
                m = new MoveAction(vec2IntDodge, true, true);
            }
            /*
            ArrayList<strategy.MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(range.getPosition(), 2, strategy.FinalConstant.getMyID(), true, true,EntityType.MELEE_UNIT);
            if (arrayList.size()!=0)
            {


                ArrayList<strategy.MyEntity> arrayListRange = globalManager.getGlobalMap().getEntityMap(range.getPosition(), 6, strategy.FinalConstant.getMyID(), true, true,EntityType.RANGED_UNIT);

            }*/

            actionHashMap.put(range.getId(), new EntityAction(m, null, a, null));
        }
    }


    private void attackTurret(MyPlayer myPlayer,GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap ){
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);

        for (int i=0; i<turretArrayList.size(); i++)
        {
            //  strategy.DataAttack idAttack = getTargetAttack(turretArrayList.get(i),globalManager);

            AttackAction a = null;

            //   if (idAttack!=null) {
            a = new AttackAction(
                    null,
                    new AutoAttack(
                            FinalConstant.getEntityPropertiesTURRET().getSightRange(),
                            new EntityType[]{}
                    )
            );

            //  }

            actionHashMap.put(turretArrayList.get(i).getId(), new EntityAction(null, null, a, null));
        }
    }

    private void attackUnit(MyPlayer myPlayer,GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap ){
        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);

        //сначала милишники , потом ренджеры
        for (int i=0; i<meleeArrayList.size(); i++) {

            DataAttack dataAttack = getTargetAttack(meleeArrayList.get(i),globalManager);

            meleeArrayList.get(i).setDataAttack(dataAttack);
            AttackAction a = null;

            if (dataAttack!=null) {

                if (dataAttack.getMyEntity()!=null)
                {
                    a = new AttackAction(
                            null,
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                                    new EntityType[]{}
                            )
                    );
                    meleeArrayList.get(i).getEntityAction().setAttackAction(a);
                    actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(null, null, a, null));
                }
                else {
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

        for (int i=0; i<rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

            if (range.isDodge()) continue;

            DataAttack dataAttack = getTargetAttack(rangeArrayList.get(i),globalManager);

            range.setDataAttack(dataAttack);

            AttackAction a = null;

            if (dataAttack!=null) {

                if (dataAttack.getMyEntity()!=null)
                {

                    a = null;

                    //m = new MoveAction(dataAttack.getMyEntity().getPosition(), true, true);
                }
                else {
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
    }

    private void moveUnitOld(MyPlayer myPlayer,GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap,int dis ){
        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);

        //сначала милишники , потом ренджеры
        for (int i=0; i<meleeArrayList.size(); i++) {
            MyEntity melee = meleeArrayList.get(i);

            EntityAction entityAction = actionHashMap.get(melee.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            // dodje для милишников (если рядом нет своих лучников, то отходим или к лучникам)


            if (entityAction.getAttackAction() != null) continue;


            Vec2Int vec2IntOne = globalManager.getMapPotField().getNearestPlayerIntoPlayerArea(melee.getPosition(), myPlayer.getId());
            Vec2Int vec2IntTwo = globalManager.getGlobalMap().getNearestPlayer(melee.getPosition(), myPlayer.getId());

            MoveAction m = null;

            if (vec2IntOne != null) {
                MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(melee.getPosition());

                if (entity == null) {
                    m = new MoveAction(vec2IntOne, true, true);
                    melee.getEntityAction().setMoveAction(m);
                } else {
                    m = new MoveAction(entity.getPosition(), true, true);
                    melee.getEntityAction().setMoveAction(m);
                    entity.setRotation(true);
                }

            } else {

                if (vec2IntTwo != null) {

                    Final.DEBUG(TAG, "distance: " + vec2IntTwo.distance(melee.getPosition()));

                    if (vec2IntTwo.distance(melee.getPosition()) < dis) {

                        MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(melee.getPosition());

                        if (entity == null) {
                            m = new MoveAction(vec2IntTwo, true, true);
                            melee.getEntityAction().setMoveAction(m);
                        } else {
                            m = new MoveAction(entity.getPosition(), true, true);
                            melee.getEntityAction().setMoveAction(m);
                            entity.setRotation(true);
                        }
                    } else {
                        m = new MoveAction(globalManager.getMapPotField().getPositionDefencePlayerArea(i%1), true, true);
                        melee.getEntityAction().setMoveAction(m);
                    }
                } else {
                    m = new MoveAction(globalManager.getMapPotField().getPositionDefencePlayerArea(i%1), true, true);
                    melee.getEntityAction().setMoveAction(m);
                }
            }

            entityAction.setMoveAction(m);

            actionHashMap.put(melee.getId(), entityAction);
        }

        for (int i=0; i<rangeArrayList.size(); i++) {
            MyEntity range = rangeArrayList.get(i);

            EntityAction entityAction = actionHashMap.get(range.getId());
            if (entityAction==null) entityAction = new EntityAction(null,null,null,null);


            // увороты от милишников

            Vec2Int vec2IntDodge = globalManager.getGlobalMap().checkDangerBuildUnit(range.getPosition(),myPlayer,2,EntityType.MELEE_UNIT);

            if (vec2IntDodge != null) {
                MoveAction m = new MoveAction(vec2IntDodge, true, true);
                entityAction.setAttackAction(null);
                entityAction.setMoveAction(m);

                range.getEntityAction().setAttackAction(null);
                range.getEntityAction().setMoveAction(m);
                range.setDodge(true);

                actionHashMap.put(range.getId(), entityAction);
                return;
            }

            if (entityAction.getAttackAction()!=null ) continue;

           // Vec2Int vec2Int = globalManager.getMapPotField().getNearestPlayerIntoPlayerArea(range.getPosition(),myPlayer.getId());
         /*   Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(range.getPosition(),myPlayer.getId());

            MoveAction m = null;
            if (vec2Int!=null)
            {

                Final.DEBUG(TAG,"distance: " + vec2Int.distance(range.getPosition()));

                // поиск пути
                MapInfo mapInfo = new MapInfo(globalManager.getGlobalMap().getMapNextTick(),new Node(range.getPosition()),new Node(vec2Int));
                AStar aStar = new AStar();
                Node node = aStar.start(mapInfo);

                while (node.parent!=null)
                {
                    if (node.parent.parent==null) break;
                    node = node.parent;
                }

                Vec2Int next = node.coord;

                m = new MoveAction(next, true, false);
                entityAction.setMoveAction(m);
            }
            else {
                if (turretArrayList.size()>0)
                {
                    m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                    entityAction.setMoveAction(m);
                }
            }*/


            Vec2Int vec2IntOne = globalManager.getMapPotField().getNearestPlayerIntoPlayerArea(range.getPosition(), myPlayer.getId());
            Vec2Int vec2IntTwo = globalManager.getGlobalMap().getNearestPlayer(range.getPosition(), myPlayer.getId());

            MoveAction m = null;

            if (vec2IntOne != null) {
                MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(range.getPosition());

                if (entity == null) {
                    m = new MoveAction(vec2IntOne, true, true);
                    range.getEntityAction().setMoveAction(m);
                } else {
                    m = new MoveAction(entity.getPosition(), true, true);
                    range.getEntityAction().setMoveAction(m);
                    entity.setRotation(true);
                }

            } else {

                if (vec2IntTwo != null) {

                    Final.DEBUG(TAG, "ID:" + range.getId() + " istance: " + vec2IntTwo.distance(range.getPosition()));

                    if (vec2IntTwo.distance(range.getPosition()) < dis) {

                        MyEntity entity = globalManager.getGlobalMap().getMoveMyUnit(range.getPosition());


                        Final.DEBUG(TAG, "ID:" + range.getId() + " entity: " + entity);

                        if (entity == null) {
                            m = new MoveAction(vec2IntTwo, true, false);
                            range.getEntityAction().setMoveAction(m);
                        } else {
                            m = new MoveAction(entity.getPosition(), true, false);
                            range.getEntityAction().setMoveAction(m);
                            entity.setRotation(true);
                        }
                    } else {

                        m = new MoveAction(globalManager.getMapPotField().getPositionDefencePlayerArea(i%1), true, true);
                        range.getEntityAction().setMoveAction(m);
                    }
                } else {
                  //  globalManager.getMapPotField().getNearestPlayerIntoPlayerArea().get
                    m = new MoveAction(globalManager.getMapPotField().getPositionDefencePlayerArea(i%1), true, true);
                    range.getEntityAction().setMoveAction(m);
                }
            }



            // идём на хил
            if (HEAL_RANGER) {
                if (range.getHealth() == 5) {
                    MyEntity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(), myPlayer, EntityType.BUILDER_UNIT);

                    if (entity != null) {
                        m = new MoveAction(entity.getPosition(), true, true);
                        range.getEntityAction().setMoveAction(m); }
                }
            }


            entityAction.setMoveAction(m);


            actionHashMap.put(range.getId(), entityAction);
        }
    }


    private void dodgeUnit(MyPlayer myPlayer,GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {
        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);


    }

    DataAttack getTargetAttack(MyEntity entity, GlobalManager globalManager)
    {

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        int attackRange = entityProperties.getAttack().getAttackRange();

       // if (entity.getEntityType() == EntityType.RANGED_UNIT) attackRange++;

        ArrayList<MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(entity.getPosition(),attackRange,FinalConstant.getMyID(),true,false,false,EntityType.ALL,false);

        if (arrayList.size()==0) return null;

        int minHPRange=0xFFFF;
        int minHPBuild=0xFFFF;
        int minHPMelee=0xFFFF;
        int minHPTurret=0xFFFF;
        int minHPBuilding=0xFFFF;
        int minHPWall=0xFFFF;

        MyEntity range = null;
        MyEntity buildUnit = null;
        MyEntity melee = null;
        MyEntity turret =null;
        MyEntity building  = null;
        MyEntity wall = null;

        for (int i=0; i<arrayList.size();  i++)
        {
            MyEntity entity1 = arrayList.get(i);

            if (entity1.getSimulationHP()<=0) continue;

            switch (entity1.getEntityType()){

                case WALL:
                    if (minHPWall>entity1.getSimulationHP())
                    {
                        wall = entity1;
                        minHPWall = entity1.getSimulationHP();
                    }
                    break;
                case HOUSE:
                case BUILDER_BASE:
                case MELEE_BASE:
                case RANGED_BASE:
                    if (minHPBuilding>entity1.getSimulationHP())
                    {
                        building = entity1;
                        minHPBuilding = entity1.getSimulationHP();
                    }
                    break;
                case MELEE_UNIT:
                    if (minHPMelee>entity1.getSimulationHP())
                    {
                        melee = entity1;
                        minHPMelee = entity1.getSimulationHP();
                    }
                    break;
                case RANGED_UNIT:
                    if (minHPRange>entity1.getSimulationHP())
                    {
                        range = entity1;
                        minHPRange = entity1.getSimulationHP();
                    }
                    break;
                case BUILDER_UNIT:
                    if (minHPBuild>entity1.getSimulationHP())
                    {
                        buildUnit = entity1;
                        minHPBuild = entity1.getSimulationHP();
                    }
                    break;
                case TURRET:
                    if (minHPTurret>entity1.getSimulationHP())
                    {
                        turret = entity1;
                        minHPTurret = entity1.getSimulationHP();
                    }
                    break;
            }
        }

        if (range!=null)
        {
            range.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(range);
        }

        if (melee!=null)
        {
            melee.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(melee);
        }

        if (buildUnit!=null)
        {
            buildUnit.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack(buildUnit);
        }

        if (turret!=null)
        {

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

        if (building!=null)
        {
            building.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack( building);
        }

        if (wall!=null)
        {
            wall.attackHP(entityProperties.getAttack().getDamage());
            return new DataAttack( wall);
        }

        return null;
    };
}