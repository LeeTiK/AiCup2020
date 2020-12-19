package strategy;

import model.*;
import strategy.map.astar.AStar;
import strategy.map.astar.Node;
import strategy.map.potfield.MapPotField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// класс помогающий юнитам двигаться по собственному алгоритму поиска пути
public class MoveManager {

    GlobalMap mGlobalMap;
    MapPotField mMapPotField;

    AStar mAStar;

    DebugInterface mDebugInterface;

    public MoveManager(){
        mAStar = new AStar(80);
    }

    public void update(GlobalMap globalMap, MapPotField mapPotField,DebugInterface debugInterface){
        this.mGlobalMap = globalMap;
        this.mMapPotField = mapPotField;
        mDebugInterface = debugInterface;

        mAStar.updateMap(mGlobalMap,mapPotField);
    }

    MoveAction getMoveActionPosition(MyEntity entity, Vec2Int targetPosition){
        return getMoveActionPosition(entity,targetPosition,true);
    }

    MoveAction getMoveActionPosition(MyEntity entity, Vec2Int targetPosition, boolean astar){

        if (targetPosition==null) return null;
        if (entity.getPosition().equals(targetPosition)) return null;

        MoveAction moveAction = new MoveAction();
        moveAction.setBreakThrough(true);
        moveAction.setFindClosestPosition(true);

        if (!astar)
        {
            moveAction.setTarget(targetPosition);
            return moveAction;
        }

        if (entity.getPosition().distance(targetPosition)<1.5) {
            moveAction.setTarget(targetPosition);
            getGlobalMap().setPositionNextTick(entity.getPosition(),targetPosition);
        }
        else {
            // поиск Astar интересно
            if (Final.A_STAR) {

                mAStar.initSearch(entity.getPosition(), targetPosition);
                List<Node> path = mAStar.findPath();

                if (path.size() > 1) {

                    if (Final.debugGraphic) {
                        if (Final.CHECK_SEARCH_PATH_ASTAR) {
                            for (int k = 0; k < path.size(); k++) {
                                FinalGraphic.sendSquare(mDebugInterface, path.get(k).getVec2Int(), 1, FinalGraphic.COLOR_GREEN_TWO);
                            }
                        }
                    }

                    moveAction.setTarget(path.get(1).getVec2Int());
                    getGlobalMap().setPositionNextTick(entity.getPosition(),path.get(1).getVec2Int());

                    return moveAction;
                } else {
                    Final.DEBUG("JOPKA: ", "" + entity.getPosition().toString() + " target: " + targetPosition.toString());
                }
            }
        }

        moveAction.setTarget(targetPosition);

        return moveAction;
    }



    void checkNeedMoveUnit(PlayerView playerView, GlobalManager globalManager, HashMap<Integer,EntityAction> actionHashMap){
        MyPlayer player = globalManager.getGlobalStatistic().getMyPlayer();

        ArrayList<MyEntity> arrayList = player.getUnitArrayList();

        for (int i=0; i<arrayList.size(); i++)
        {
            MyEntity entity = arrayList.get(i);

            if (!entity.isNeedMove()) continue;

            Final.DEBUG("NEED_MOVE", entity.getEntityType() + " " + entity.getPosition().toString());

            switch (entity.getEntityType())
            {
                case BUILDER_UNIT:
                    MoveAction m = null;
                    BuildAction b = null;
                    AttackAction a = null;
                    RepairAction r = null;

                    EntityAction entityAction = actionHashMap.get(entity.getId());
                    if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

                //    ArrayList<MyEntity> resource = globalManager.getGlobalMap().getEntityMap(entity.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);

                    // увороты от всех по ПП
                    Vec2Int vec2IntDodge = globalManager.getMapPotField().getDangerPositionBuild(entity,false,true);

                    if (vec2IntDodge != null) {
                        m = //globalManager.getMoveManager().getMoveActionPosition(entity,vec2IntDodge);
                        new MoveAction(vec2IntDodge, true, true);
                        //  globalManager.getGlobalMap().setPositionNextTick(builderUnit.getPosition(),vec2IntDodge);

                        entityAction.setAttackAction(null);
                        entityAction.setMoveAction(m);
                        entityAction.setRepairAction(null);

                        entity.getEntityAction().setAttackAction(null);
                        entity.getEntityAction().setMoveAction(m);
                        entity.setDodge(true);
                        player.addCountBuildDodge();

                        actionHashMap.put(entity.getId(), entityAction);
                    }
                    break;
            }
        }
    }

    public GlobalMap getGlobalMap() {
        return mGlobalMap;
    }

    public MapPotField getMapPotField() {
        return mMapPotField;
    }
}
