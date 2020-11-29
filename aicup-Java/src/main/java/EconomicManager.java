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

        ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.RANGED_BASE);
        Final.DEBUG(TAG, "arrayList RANGED_BASE BASE: " + arrayList1.size() + " resource: " + myPlayer.getResource());
        if (myPlayer.getResource()>29) {
            for (int i = 0; i < arrayList1.size(); i++) {
                b = new BuildAction(
                        EntityType.RANGED_UNIT, globalManager.getGlobalMap().getPositionBuildUnit(globalStatistic, arrayList1.get(i))
                );

                actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, b, null, null));
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
                            globalStatistic.getEntityPropertiesBUILDER_UNIT().getSightRange(),
                            new EntityType[]{EntityType.RESOURCE}
                    )
            );

            if (myPlayer.getResource()>200 && myPlayer.getPopulationCurrent()*1.2>=myPlayer.getPopulationMax() )
            {
                b = new BuildAction(
                        EntityType.HOUSE, new Vec2Int(
                        builderUnitArrayList.get(i).getPosition().getX()+1,
                        builderUnitArrayList.get(i).getPosition().getY()
                )
                );
            }

            if (myPlayer.getResource()>1100 && myPlayer.getEntityArrayList(EntityType.RANGED_BASE).size()<2 )
            {
                b = new BuildAction(
                        EntityType.RANGED_BASE, new Vec2Int(
                        builderUnitArrayList.get(i).getPosition().getX()+1,
                        builderUnitArrayList.get(i).getPosition().getY()
                )
                );
            }



            Final.DEBUG(TAG, "arrayList.get(i).getId() " + builderUnitArrayList.get(i).getId() + " " +builderUnitArrayList.get(i).getPosition().toString());

            actionHashMap.put(builderUnitArrayList.get(i).getId(), new EntityAction(m, b, a, r));
        }

        //чиним здания
        // чиним здания
        for (int i=0; i<buildingArrayList.size(); i++)
        {
            MyEntity myEntityUnit =null;
            MyEntity myEntityBuilding = buildingArrayList.get(i);
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
                EntityProperties entityProperties = globalStatistic.getEntityProperties(myEntityBuilding.getEntityType());
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
        if (builderUnitArrayList.size()<myPlayer.getPopulationMax()*0.75 && builderUnitArrayList.size()<70)
        {
            BuildAction b = null;

            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i=0; i<arrayList1.size(); i++)
            {
                b = new BuildAction(
                        EntityType.BUILDER_UNIT,globalManager.getGlobalMap().getPositionBuildUnit(globalStatistic, arrayList1.get(i)
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

    private void updateInfo(GlobalStatistic globalStatistic){
        MyPlayer player = globalStatistic.getMyPlayer();
        sizeGold = player.getResource();

    }
}
