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

    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalStatistic globalStatistic){
         HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();
         updateInfo(globalStatistic);
         MyPlayer myPlayer = globalStatistic.getMyPlayer();

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

        ArrayList<MyEntity> arrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        Final.DEBUG(TAG, "BUILDER_UNIT: " + arrayList.size());

        for (int i=0; i<arrayList.size(); i++)
        {
            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;

            m = new MoveAction(new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1), true, true);
            a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            globalStatistic.getEntityPropertiesBUILDER_UNIT().getSightRange(),
                            new EntityType[]{EntityType.RESOURCE}
                    )
            );

            Final.DEBUG(TAG, "arrayList.get(i).getId() " + arrayList.get(i).getId() + " " +arrayList.get(i).getPlayerId());

            actionHashMap.put(arrayList.get(i).getId(), new EntityAction(m, b, a, r));
        }

        if (arrayList.size()<5)
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
