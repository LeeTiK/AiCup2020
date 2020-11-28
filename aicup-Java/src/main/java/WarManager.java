import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class WarManager {

    public static final String TAG = "WarManager";

    public HashMap<Integer, EntityAction> update(PlayerView playerView, GlobalStatistic globalStatistic) {
        HashMap<Integer, EntityAction> actionHashMap = new HashMap<>();

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        ArrayList<MyEntity> arrayList1 = myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);

        for (int i=0; i<arrayList1.size(); i++) {
            MoveAction m = new MoveAction(new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1), true, true);
            AttackAction a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            globalStatistic.getEntityPropertiesRANGED_UNIT().getSightRange(),
                            new EntityType[]{}
                    )
            );

            actionHashMap.put(arrayList1.get(i).getId(), new EntityAction(m, null, a, null));
        }

        ArrayList<MyEntity> arrayList2 = myPlayer.getEntityArrayList(EntityType.MELEE_UNIT);

        for (int i=0; i<arrayList2.size(); i++) {
            MoveAction m = new MoveAction(new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1), true, true);
            AttackAction a = new AttackAction(
                    //Arrays.stream(playerView.getEntities()).filter(e -> myId.equals(e.getEntityType()) & e.getEntityType() == EntityType.MELEE_BASE).findAny().get().getId(),
                    null,
                    new AutoAttack(
                            globalStatistic.getEntityPropertiesRANGED_UNIT().getSightRange(),
                            new EntityType[]{}
                    )
            );

            actionHashMap.put(arrayList2.get(i).getId(), new EntityAction(m, null, a, null));
        }

        return actionHashMap;

    }

}
