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

         actionHashMap.putAll(builder(myPlayer,playerView,globalStatistic));

        BuildAction b = null;

        ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.RANGED_BASE);
        Final.DEBUG(TAG, "arrayList RANGED_BASE BASE: " + arrayList1.size());
        for (int i=0; i<arrayList1.size(); i++)
        {
            b = new BuildAction(
                    EntityType.RANGED_UNIT, new Vec2Int(
                    arrayList1.get(i).getPosition().getX() + globalStatistic.getEntityPropertiesRANGED_BASE().getSize(),
                    arrayList1.get(i).getPosition().getY() + globalStatistic.getEntityPropertiesRANGED_BASE().getSize() - 1
            )
            );

            actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(null, b, null, null));
        }

         Final.DEBUG(TAG, "hashMap: " + actionHashMap.size());

         return actionHashMap;
    }



    private HashMap builder(MyPlayer myPlayer,PlayerView playerView, GlobalStatistic globalStatistic ){
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();

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

            // чиним здания

            for (int j=0; j<buildingArrayList.size(); j++)
            {
                MyEntity myEntity = buildingArrayList.get(j);
                EntityProperties entityProperties = globalStatistic.getEntityProperties(myEntity.getEntityType());
                if (myEntity.getHealth()<entityProperties.getMaxHealth())
                {
                    r = new RepairAction(
                            myEntity.getId()
                    );
                }
            }

            Final.DEBUG(TAG, "arrayList.get(i).getId() " + builderUnitArrayList.get(i).getId() + " " +builderUnitArrayList.get(i).getPosition().toString());

            actionHashMap.put(builderUnitArrayList.get(i).getId(), new EntityAction(m, b, a, r));
        }

        if (builderUnitArrayList.size()<myPlayer.getPopulationMax()*0.7 && builderUnitArrayList.size()<70)
        {
            BuildAction b = null;

            ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.BUILDER_BASE);

            for (int i=0; i<arrayList1.size(); i++)
            {
                b = new BuildAction(
                        EntityType.BUILDER_UNIT, new Vec2Int(
                        arrayList1.get(i).getPosition().getX() + globalStatistic.getEntityPropertiesBUILDER_BASE().getSize(),
                        arrayList1.get(i).getPosition().getY() + globalStatistic.getEntityPropertiesBUILDER_BASE().getSize() - 1
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
