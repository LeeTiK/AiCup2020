import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class WarManager {

    public static final String TAG = "WarManager";

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

        if (FinalConstant.getCurrentTik()<300)
        {
            actionHashMap = attack(playerView,globalManager,20);
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

        ArrayList<MyPlayer> arrayList = globalStatistic.getPlayers();
        MyPlayer targetPlayerAttack;

        ArrayList<MyEntity> turretArrayList = myPlayer.getEntityArrayList(EntityType.TURRET);


        for (int i=0; i<rangeArrayList.size(); i++) {
            Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(rangeArrayList.get(i).getPosition(),playerView.getMyId());

            Final.DEBUG(TAG,"distance: " + vec2Int.distance(rangeArrayList.get(i).getPosition()));

            MoveAction m = null;
            if (vec2Int!=null)
            {
                 if (vec2Int.distance(rangeArrayList.get(i).getPosition())<dis) {
                     m = new MoveAction(vec2Int, true, true);
                 }
                 else {
                     if (turretArrayList.size()>0)
                     {
                         m = new MoveAction(turretArrayList.get(0).getPosition(), true, false);
                     }
                 }
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
            Vec2Int vec2Int = globalManager.getGlobalMap().getNearestPlayer(meleeArrayList.get(i).getPosition(),playerView.getMyId());

            Final.DEBUG(TAG,"distance: " + vec2Int.distance(meleeArrayList.get(i).getPosition()));

            MoveAction m = null;
            if (vec2Int!=null)
            {
                if (vec2Int.distance(meleeArrayList.get(i).getPosition())<dis) {
                    m = new MoveAction(vec2Int, true, true);
                }
            }


            AttackAction a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            FinalConstant.getEntityPropertiesRANGED_UNIT().getSightRange(),
                            new EntityType[]{}
                    )
            );

            actionHashMap.put(meleeArrayList.get(i).getId(), new EntityAction(m, null, a, null));
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


    int getTargetAttack(MyEntity entity, GlobalManager globalManager)
    {

        return 0;
    };
}
