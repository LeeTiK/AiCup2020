package strategy;

import model.*;
import strategy.map.astar.AStar;
import strategy.map.astar.Node;
import strategy.map.potfield.MapPotField;

import java.util.*;

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
            entity.getEntityAction().setMoveAction(moveAction);
            return moveAction;
        }

        if (entity.getPosition().distance(targetPosition)<1.5) {
            moveAction.setTarget(targetPosition);
            getGlobalMap().setPositionNextTick(entity, entity.getPosition(),targetPosition);
            entity.getEntityAction().setMoveAction(moveAction);
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
                    getGlobalMap().setPositionNextTick(entity,entity.getPosition(),path.get(1).getVec2Int());
                    entity.getEntityAction().setMoveAction(moveAction);

                    return moveAction;
                } else {
                    Final.DEBUG("JOPKA: ", "" + entity.getPosition().toString() + " target: " + targetPosition.toString());
                }
            }
        }

        moveAction.setTarget(targetPosition);
        entity.getEntityAction().setMoveAction(moveAction);

        return moveAction;
    }

    void calculateDodgeUnits(PlayerView playerView, GlobalManager globalManager, HashMap<Integer,EntityAction> actionHashMap){
        MyPlayer player = globalManager.getGlobalStatistic().getMyPlayer();

        ArrayList<MyEntity> arrayList = player.getUnitDodgeArrayList();

        Collections.sort(arrayList, new Comparator<MyEntity>() {
            public int compare(MyEntity a, MyEntity b) {
                if (a.getDodgePositionAnswer().getSafetyArrayList().size()+
                        a.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size() >
                        b.getDodgePositionAnswer().getSafetyArrayList().size()+
                        b.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()) return 1;
                if (a.getDodgePositionAnswer().getSafetyArrayList().size()+
                        a.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size() <
                        b.getDodgePositionAnswer().getSafetyArrayList().size()+
                                b.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()) return -1;
                return 0;
            }
        });


        for (int i=0; i<arrayList.size(); i++)
        {
            MyEntity entity = arrayList.get(i);

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
                    Vec2Int vec2IntDodge  =null;
                    // увороты от всех по ПП
                    if (entity.getDodgePositionAnswer().getSafetyArrayList().size()>0)
                    {
                        for (int j=0; j<entity.getDodgePositionAnswer().getSafetyArrayList().size(); j++)
                        {
                            Vec2Int vec2Int = entity.getDodgePositionAnswer().getSafetyArrayList().get(j).getPosition();

                            if (getGlobalMap().checkEmpty(getGlobalMap().getMapNextTick(),vec2Int))
                            {
                                vec2IntDodge = vec2Int;
                            }
                        }
                    }

                    if (vec2IntDodge==null) {
                        if (entity.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()>0)
                        {
                            vec2IntDodge = entity.getDodgePositionAnswer().getSafetyPositionUnitArrayList().get(0).getPosition();
                        }
                        else {
                            Final.DEBUG("NEED_MOVE", "BAD_NEED_MOVE: " + entity.getPosition().toString());
                        }
                    }
                    if (vec2IntDodge==null) {
                        if (entity.getDodgePositionAnswer().getSafetyCounterArrayList().size() > 0) {
                            for (int j = 0; j < entity.getDodgePositionAnswer().getSafetyCounterArrayList().size(); j++) {
                                Vec2Int vec2Int = entity.getDodgePositionAnswer().getSafetyCounterArrayList().get(j).getPosition();

                                if (getGlobalMap().checkEmpty(getGlobalMap().getMapNextTick(), vec2Int)) {
                                    vec2IntDodge = vec2Int;
                                }
                            }
                        }
                    }


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

                        getGlobalMap().setPositionNextTick(entity,entity.getPosition(),vec2IntDodge);

                        actionHashMap.put(entity.getId(), entityAction);
                    }
                    break;
            }
        }

        ArrayList<MyEntity> needDodge = new ArrayList<>();

        ArrayList<MyEntity> arrayListUnit = player.getUnitArrayList();

        for (int i=0; i<arrayListUnit.size(); i++) {
            MyEntity entity = arrayListUnit.get(i);

            if (!entity.isNeedMove()) continue;

            if (entity.getEntityAction().getMoveAction()!=null) {
                if (entity.getEntityAction().getMoveAction().getTarget().equals(entity.getPosition()))
                {
                    continue;
                }
                else {
                    if (getGlobalMap().checkEmpty(entity.getEntityAction().getMoveAction().getTarget()))
                    {
                        continue;
                    }
                }
            }

            switch (entity.getEntityType())
            {
                case BUILDER_UNIT:
                    entity.setDodgePositionAnswer(getMapPotField().calculateDodgePosition(entity,true));
                    needDodge.add(entity);
                   // dodge(entity,globalManager,actionHashMap);
                    break;
            }
        }

        Collections.sort(needDodge, new Comparator<MyEntity>() {
            public int compare(MyEntity a, MyEntity b) {
                if (a.getDodgePositionAnswer().getSafetyArrayList().size()+
                        a.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size() >
                        b.getDodgePositionAnswer().getSafetyArrayList().size()+
                                b.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()) return 1;
                if (a.getDodgePositionAnswer().getSafetyArrayList().size()+
                        a.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size() <
                        b.getDodgePositionAnswer().getSafetyArrayList().size()+
                                b.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()) return -1;
                return 0;
            }
        });

        for (int i=0; i<needDodge.size(); i++) {
            MyEntity entity = needDodge.get(i);

            dodge(entity,globalManager,actionHashMap);
        }
    }

    private void dodge(MyEntity entity, GlobalManager globalManager, HashMap<Integer,EntityAction> actionHashMap ){
        MyPlayer player = globalManager.getGlobalStatistic().getMyPlayer();

        MoveAction m = null;
        BuildAction b = null;
        AttackAction a = null;
        RepairAction r = null;

        EntityAction entityAction = actionHashMap.get(entity.getId());
        if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

        //    ArrayList<MyEntity> resource = globalManager.getGlobalMap().getEntityMap(entity.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);
        entity.setDodgePositionAnswer(getMapPotField().calculateDodgePosition(entity,true));

        Vec2Int vec2IntDodge  =null;
        // увороты от всех по ПП
        if (entity.getDodgePositionAnswer().getSafetyArrayList().size()>0)
        {
            for (int j=0; j<entity.getDodgePositionAnswer().getSafetyArrayList().size(); j++)
            {
                Vec2Int vec2Int = entity.getDodgePositionAnswer().getSafetyArrayList().get(j).getPosition();

                if (getGlobalMap().checkEmpty(getGlobalMap().getMapNextTick(),vec2Int))
                {
                    vec2IntDodge = vec2Int;
                }
            }
        }

        if (vec2IntDodge==null) {
            if (entity.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()>0)
            {
                vec2IntDodge = entity.getDodgePositionAnswer().getSafetyPositionUnitArrayList().get(0).getPosition();
            }
            else {
                Final.DEBUG("NEED_MOVE", "BAD_NEED_MOVE: " + entity.getPosition().toString());
            }
        }
        if (vec2IntDodge==null) {
            if (entity.getDodgePositionAnswer().getSafetyCounterArrayList().size() > 0) {
                for (int j = 0; j < entity.getDodgePositionAnswer().getSafetyCounterArrayList().size(); j++) {
                    Vec2Int vec2Int = entity.getDodgePositionAnswer().getSafetyCounterArrayList().get(j).getPosition();

                    if (getGlobalMap().checkEmpty(getGlobalMap().getMapNextTick(), vec2Int)) {
                        vec2IntDodge = vec2Int;
                    }
                }
            }
        }


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

            MyEntity entity1 = getGlobalMap().setPositionNextTick(entity,vec2IntDodge);

            actionHashMap.put(entity.getId(), entityAction);

            if (entity1!=null)
            {
                dodge(entity1,globalManager,actionHashMap);
            }
        }
    }

    public GlobalMap getGlobalMap() {
        return mGlobalMap;
    }

    public MapPotField getMapPotField() {
        return mMapPotField;
    }

    static final public Comparator<MyEntity> mMyEntityComparator =  new Comparator<MyEntity>() {
        public int compare(MyEntity a, MyEntity b) {
            if (a.getDodgePositionAnswer().getSafetyArrayList().size()==0 &&
                    b.getDodgePositionAnswer().getSafetyArrayList().size()!=0) return 1;

            if (a.getDodgePositionAnswer().getSafetyArrayList().size() +
                    a.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size() >
                    b.getDodgePositionAnswer().getSafetyArrayList().size() +
                            b.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()) return 1;
            if (a.getDodgePositionAnswer().getSafetyArrayList().size() +
                    a.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size() <
                    b.getDodgePositionAnswer().getSafetyArrayList().size() +
                            b.getDodgePositionAnswer().getSafetyPositionUnitArrayList().size()) return -1;
            return 0;
        }
    };
}
