import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class WarManager {

    public static final String TAG = "WarManager";

    public static final boolean HEAL_RANGER = true;

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

        if (FinalConstant.getCurrentTik()<220)
        {
            actionHashMap = attack(playerView,globalManager,22);
        }
        else {
            actionHashMap = attack(playerView,globalManager,1000);
        }



        return actionHashMap;

    }

    private HashMap<Integer, EntityAction> attack(PlayerView playerView, GlobalManager globalManager, int dis) {
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();

        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);

        for (int i=0; i<turretArrayList.size(); i++)
        {
          //  DataAttack idAttack = getTargetAttack(turretArrayList.get(i),globalManager);

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


        for (int i=0; i<meleeArrayList.size(); i++) {
            Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(meleeArrayList.get(i).getPosition(),playerView.getMyId());

            Final.DEBUG(TAG,"distance: " + vec2Int.distance(meleeArrayList.get(i).getPosition()));

            MoveAction m = null;
            if (vec2Int!=null)
            {
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

            Final.DEBUG(TAG,"distance: " + vec2Int.distance(range.getPosition()));

            MoveAction m = null;
            if (vec2Int!=null)
            {
                 if (vec2Int.distance(range.getPosition())<dis) {
                     m = new MoveAction(vec2Int, true, true);
                 }
                 else {
                     if (turretArrayList.size()>0)
                     {
                         m = new MoveAction(turretArrayList.get(0).getPosition(), true, true);
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
                        Entity entity = globalManager.getGlobalMap().getMinDisToEntity(range.getPosition(), myPlayer, EntityType.BUILDER_UNIT);

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
                m = new MoveAction(vec2IntDodge, true, false);
            }
            /*
            ArrayList<MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(range.getPosition(), 2, FinalConstant.getMyID(), true, true,EntityType.MELEE_UNIT);
            if (arrayList.size()!=0)
            {


                ArrayList<MyEntity> arrayListRange = globalManager.getGlobalMap().getEntityMap(range.getPosition(), 6, FinalConstant.getMyID(), true, true,EntityType.RANGED_UNIT);

            }*/

            actionHashMap.put(range.getId(), new EntityAction(m, null, a, null));
        }

        return actionHashMap;
    }

    private HashMap<Integer, EntityAction> defence(PlayerView playerView, GlobalManager globalManager) {
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();

      //  globalManager.getGlobalMap().
        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        ArrayList<MyEntity> rangeArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);

        ArrayList<MyPlayer> arrayList = globalStatistic.getPlayers();
        MyPlayer targetPlayerAttack;


        AreaPlayer areaPlayer = globalManager.getGlobalMap().getAreaPlayer();

        for (int i=0; i<rangeArrayList.size(); i++) {
           // Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(rangeArrayList.get(i).getPosition(),playerView.getMyId());
            Vec2Int vec2Int = null;
            if (i%2==0)
            {
                vec2Int = new Vec2Int(areaPlayer.width/2,areaPlayer.height);
            }
            else {
                vec2Int = new Vec2Int(areaPlayer.width,areaPlayer.height/2);
            }

            MoveAction m = null;
            if (vec2Int!=null)
            {
                m = new MoveAction(vec2Int, true, false);
            }
            AttackAction a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                            new EntityType[]{}
                    )
            );

            actionHashMap.put(rangeArrayList.get(i).getId(), new EntityAction(m, null, a, null));
        }

        ArrayList<MyEntity> meleeArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);

        for (int i=0; i<meleeArrayList.size(); i++) {
           // Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(rangeArrayList.get(i).getPosition(),playerView.getMyId());

            Vec2Int vec2Int = null;
            if (i%2==0)
            {
                vec2Int = new Vec2Int(areaPlayer.width/2,areaPlayer.height);
            }
            else {
                vec2Int = new Vec2Int(areaPlayer.width,areaPlayer.height/2);
            }

            MoveAction m = null;
            if (vec2Int!=null)
            {
                m = new MoveAction(vec2Int, true, false);
            }

            AttackAction a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            FinalConstant.getEntityPropertiesMELEE_UNIT().getSightRange(),
                            new EntityType[]{}
                    )
            );

            actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(m, null, a, null));
        }

        return actionHashMap;
    }


    DataAttack getTargetAttack(MyEntity entity, GlobalManager globalManager)
    {

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        int attackRange = entityProperties.getAttack().getAttackRange();

       // if (entity.getEntityType() == EntityType.RANGED_UNIT) attackRange++;

        ArrayList<MyEntity> arrayList = globalManager.getGlobalMap().getEntityMap(entity.getPosition(),attackRange,FinalConstant.getMyID(),true,false,EntityType.ALL);

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

            MyEntity entity1 = globalManager.getGlobalMap().getBuilderUnitNearTurret(turret);

            if (entity1!=null)
            {
                DataAttack dataAttack = new DataAttack(entity1);
                dataAttack.setMyEntity(entity1);
                return dataAttack;
            }

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
