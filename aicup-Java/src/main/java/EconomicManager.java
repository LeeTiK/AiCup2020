import model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class EconomicManager {

    public static final String TAG = "EconomicManager";

    // количество рабочих
    int sizeBuildUnit;
    // количество золота
    int sizeGold;
    // количество жилых мест
    int countMaxUnit;

    public EconomicManager(){

    }

    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalManager globalManager){
         HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();
         GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();
         updateInfo(globalStatistic);
         MyPlayer myPlayer = globalManager.getGlobalStatistic().getMyPlayer();

         actionHashMap.putAll(builder(myPlayer,playerView,globalManager));

        BuildAction b = null;

        ArrayList<MyEntity> rangeBaseArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_BASE);
        Final.DEBUG(TAG, "arrayList RANGED_BASE BASE: " + rangeBaseArrayList.size() + " resource: " + myPlayer.getResource());
        if (myPlayer.getResource()>30) {
            for (int i = 0; i < rangeBaseArrayList.size(); i++) {
                b = new BuildAction(
                        EntityType.RANGED_UNIT, globalManager.getGlobalMap().getPositionBuildUnit(rangeBaseArrayList.get(i))
                );

                actionHashMap.put(rangeBaseArrayList.get(i).getId(), new EntityAction(null, b, null, null));
            }
        }
        else {
            for (int i = 0; i < rangeBaseArrayList.size(); i++) {
                actionHashMap.put(rangeBaseArrayList.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }

        ArrayList<MyEntity> meleeBaseArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_BASE);
        ArrayList<MyEntity> meleeUnitArrayList = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> rangedUnitArrayList = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);

        if (myPlayer.getResource()>20 && 3*meleeUnitArrayList.size()<rangedUnitArrayList.size()) {
            for (int i = 0; i < meleeBaseArrayList.size(); i++) {
                b = new BuildAction(
                        EntityType.MELEE_UNIT, globalManager.getGlobalMap().getPositionBuildUnit(meleeBaseArrayList.get(i))
                );

                actionHashMap.put(meleeBaseArrayList.get(i).getId(), new EntityAction(null, b, null, null));
            }
        }
        else {
            for (int i = 0; i < meleeBaseArrayList.size(); i++) {
                actionHashMap.put(meleeBaseArrayList.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }
         Final.DEBUG(TAG, "hashMap: " + actionHashMap.size());

         return actionHashMap;
    }



    private HashMap builder(MyPlayer myPlayer,PlayerView playerView, GlobalManager globalManager ){
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();

        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        Final.DEBUG(TAG, "BUILDER_UNIT: " + builderUnitArrayList.size());

        ArrayList<MyEntity> buildingArrayList = myPlayer.getBuildingArrayList();

        for (int i=0; i<builderUnitArrayList.size(); i++)
        {
            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;

            m = new MoveAction(new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1), true, false);

            a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            FinalConstant.getEntityPropertiesBUILDER_UNIT().getSightRange(),
                            new EntityType[]{EntityType.RESOURCE}
                    )
            );


            Vec2Int vec2Int = globalManager.getGlobalMap().checkDangerBuildUnit(builderUnitArrayList.get(i).getPosition(),FinalConstant.getMyID());

            if (vec2Int!=null)
            {
                a = null;
                m = new MoveAction(vec2Int, true, false);
            }

           /* if (myPlayer.getResource()>200 && myPlayer.getPopulationCurrent()*1.2>=myPlayer.getPopulationMax() )
            {
                if (!checkCreate) {
                   // Vec2Int vec2Int2 = new Vec2Int(0, 0);
                   // Vec2Int vec2Int = new Vec2Int(2, 2);
                  //  m = new MoveAction(vec2Int, true, false);
                    b = new BuildAction(
                            EntityType.HOUSE, new Vec2Int(
                            builderUnitArrayList.get(i).getPosition().getX()+1,
                            builderUnitArrayList.get(i).getPosition().getY()
                    )
                    );
                    checkCreate = true;
                    a = null;
                }
            }*/

            if (myPlayer.getResource()>FinalConstant.getEntityProperties(EntityType.RANGED_BASE).getCost()+300 && myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()<3 )
            {
                if (builderUnitArrayList.get(i).getPosition().getX()<70 && builderUnitArrayList.get(i).getPosition().getY()<75) {
                    b = new BuildAction(
                            EntityType.RANGED_BASE, new Vec2Int(
                            builderUnitArrayList.get(i).getPosition().getX() + 1,
                            builderUnitArrayList.get(i).getPosition().getY()
                    )
                    );
                }
            }

            if (myPlayer.getResource()>FinalConstant.getEntityProperties(EntityType.MELEE_BASE).getCost()+400 && myPlayer.getEntityArrayList(EntityType.BUILDER_BASE).size()<1 &&  myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>0)
            {
                if (b==null) {
                    if (builderUnitArrayList.get(i).getPosition().getX()<70 && builderUnitArrayList.get(i).getPosition().getY()<75) {
                        b = new BuildAction(
                                EntityType.RANGED_BASE, new Vec2Int(
                                builderUnitArrayList.get(i).getPosition().getX() + 1,
                                builderUnitArrayList.get(i).getPosition().getY()
                        )
                        );
                    }
                }
            }


            r = getNearbyBuildNeedHeal(builderUnitArrayList.get(i).getPosition(),globalManager);

                    // Final.DEBUG(TAG, "arrayList.get(i).getId() " + builderUnitArrayList.get(i).getId() + " " +builderUnitArrayList.get(i).getPosition().toString());

            actionHashMap.put(builderUnitArrayList.get(i).getId(), new EntityAction(m, b, a, r));
        }

        // строим здания


        boolean checkCreate = false;

        if (myPlayer.getResource()>FinalConstant.getEntityProperties(EntityType.HOUSE).getCost()-10 && (myPlayer.getPopulationCurrent()*1.2>=myPlayer.getPopulationMax() || myPlayer.getPopulationMax()<70)
        && (myPlayer.getPopulationMax()<150 || myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()>1))

        {

            Vec2Int positionBuildHouse = globalManager.getGlobalMap().getPositionBuildHouse(FinalConstant.getEntityProperties(EntityType.HOUSE));

            if (positionBuildHouse == null)
            {
               // positionBuildHouse = new Vec2Int();
                for (int i = 0; i < builderUnitArrayList.size(); i++) {
                    if (builderUnitArrayList.get(i).getPosition().getX()-1<0 || builderUnitArrayList.get(i).getPosition().getX()-1>80-3) continue;
                    if (builderUnitArrayList.get(i).getPosition().getY()<0 || builderUnitArrayList.get(i).getPosition().getY()>80-3) continue;
                    BuildAction b = new BuildAction(
                            EntityType.HOUSE, new Vec2Int(
                            builderUnitArrayList.get(i).getPosition().getX() - 1,
                            builderUnitArrayList.get(i).getPosition().getY()
                    )
                    );

                    MoveAction m = new MoveAction(new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1), true, false);
                    AttackAction a = new AttackAction(
                            //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                            null,
                            new AutoAttack(
                                    FinalConstant.getEntityPropertiesBUILDER_UNIT().getSightRange(),
                                    new EntityType[]{EntityType.RESOURCE}
                            )
                    );

                    actionHashMap.put(builderUnitArrayList.get(i).getId(), new EntityAction(m, b, a, null));
                }

            }
            else {


                double minDis = 0xFFFFF;
                MyEntity current = null;


                for (int i = 0; i < builderUnitArrayList.size(); i++) {
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
                    m = new MoveAction(vec2Int1, true, false);

                    b = new BuildAction(EntityType.HOUSE, positionBuildHouse);
                    checkCreate = true;
                    a = null;

                    Final.DEBUG(TAG, "VECTOR BUILD: " + positionBuildHouse.toString() + " currentP: " + current.getPosition());

                    actionHashMap.put(current.getId(), new EntityAction(m, b, a, r));
                }
            }
        }

        //чиним здания
        // чиним здания
        for (int i=0; i<buildingArrayList.size(); i++)
        {
            MyEntity myEntityUnit =null;
            MyEntity myEntityBuilding = buildingArrayList.get(i);
            EntityProperties entityProperties = FinalConstant.getEntityProperties(myEntityBuilding.getEntityType());

            if (myEntityBuilding.getHealth()>=entityProperties.getMaxHealth()) continue;

            MyEntity currentUnit = null;
            double minDis = 0xFFFFF;
            for (int j=0; j<builderUnitArrayList.size(); j++)
            {
                myEntityUnit = builderUnitArrayList.get(j);
                double dis = myEntityUnit.getPosition().distance(myEntityBuilding.getPosition());
                if (dis <minDis)
                {
                    currentUnit = myEntityUnit;
                    minDis = dis;
                }
            }

            if (currentUnit!=null)
            {

                if (myEntityBuilding.getHealth()<entityProperties.getMaxHealth())
                {
                    MoveAction m = null;
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    r = new RepairAction(
                            myEntityBuilding.getId()
                    );
                    //a = null;
                    m = new MoveAction(myEntityBuilding.getPosition(), true, false);

                    actionHashMap.put(currentUnit.getId(), new EntityAction(m, b, a, r));
                }
            }
        }

        // создаем новые юниты
        if (builderUnitArrayList.size()<myPlayer.getPopulationMax()*0.75 && builderUnitArrayList.size()<70 ||builderUnitArrayList.size()<22  )
        {
            BuildAction b = null;

            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i=0; i<arrayList1.size(); i++)
            {
                b = new BuildAction(
                        EntityType.BUILDER_UNIT,globalManager.getGlobalMap().getPositionBuildUnitPriorite(arrayList1.get(i)
            /*new Vec2Int(
                        arrayList1.get(i).getPosition().getX() + globalStatistic.getEntityPropertiesBUILDER_BASE().getSize(),
                        arrayList1.get(i).getPosition().getY() + globalStatistic.getEntityPropertiesBUILDER_BASE().getSize() - 1*/
                )
                );

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, b, null, null));
            }
        }
        else {
            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i=0; i<arrayList1.size(); i++)
            {

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, null, null, null));
            }
        }

        return actionHashMap;
    }

    private RepairAction getNearbyBuildNeedHeal(Vec2Int position, GlobalManager globalManager) {
        byte[][] bytes= new byte[][]{
                {1,0},{0,1},{-1,0},{0,-1},
        };

        for (int i=0; i<4; i++) {
            Vec2Int vec2Int = position.add(bytes[i][0],bytes[i][1]);


            Entity entity = globalManager.getGlobalMap().getMap(vec2Int);
            if (entity != null) {
                if (entity.getEntityType()==EntityType.BUILDER_BASE || entity.getEntityType()==EntityType.RANGED_BASE ||
                        entity.getEntityType()==EntityType.HOUSE || entity.getEntityType()==EntityType.TURRET
                || entity.getEntityType() == EntityType.WALL || entity.getEntityType()==EntityType.MELEE_BASE) {
                    if (entity.getPlayerId() == FinalConstant.getMyID()) {
                        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);
                        if (entity.getHealth() < entityProperties.getMaxHealth()) {
                            return new RepairAction(entity.getId());
                        }
                    }
                }
            }
        }



        return null;
    }

    private void updateInfo(GlobalStatistic globalStatistic){
        MyPlayer player = globalStatistic.getMyPlayer();
        sizeGold = player.getResource();

    }
}
