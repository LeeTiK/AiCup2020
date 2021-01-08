package strategy;

import model.*;
import strategy.map.astar.AStar;
import strategy.map.astar.Node;
import strategy.map.potfield.Field;
import strategy.map.potfield.MapPotField;

import java.util.*;

// класс помогающий юнитам двигаться по собственному алгоритму поиска пути
public class MoveManager {

    GlobalMap mGlobalMap;
    MapPotField mMapPotField;

    AStar mAStar;

    DebugInterface mDebugInterface;

    HashMap<Integer,EntityAction> actionHashMap;

    public MoveManager(){
        mAStar = new AStar(80);
    }

    public void update(GlobalMap globalMap, MapPotField mapPotField,DebugInterface debugInterface, HashMap<Integer,EntityAction> actionHashMap){
        this.mGlobalMap = globalMap;
        this.mMapPotField = mapPotField;
        mDebugInterface = debugInterface;
        this.actionHashMap = actionHashMap;

        mAStar.updateMap(mGlobalMap,mapPotField);
    }

    MoveAction getMoveActionPosition(MyEntity entity, Vec2Int targetPosition){
        return getMoveActionPosition(entity,targetPosition,true);
    }

    MoveAction getMoveActionPosition(MyEntity entity, Vec2Int targetPosition, boolean astar){
        return getMoveActionPosition(entity,targetPosition,astar,null);
    }

    MoveAction getMoveActionPosition(MyEntity entity, Vec2Int targetPosition, boolean astar, Node current ){

    /*    if (current!=null)
        {
            Final.DEBUGRelease("MOVE",FinalConstant.getCurrentTik() + " " + entity.toString() + " " + current.getVec2Int().toString());
        }*/

        if (targetPosition==null && current==null) return null;
        if (targetPosition!=null && entity.getPosition().equals(targetPosition)) return null;

        if (entity.isDangerMove())
        {
            int k =0;
        }

        MoveAction moveAction = new MoveAction();
        moveAction.setBreakThrough(true);
        moveAction.setFindClosestPosition(true);

        if (!astar && targetPosition!=null)
        {
            moveAction.setTarget(targetPosition);
            entity.getEntityAction().setMoveAction(moveAction);
            return moveAction;
        }

       /* if (targetPosition!=null) {
            Final.DEBUGRelease("CHECK_MOVE", FinalConstant.getCurrentTik() + " " + entity.getPosition().toString() +
                    " targetPosition: " + targetPosition.toString() + " DIS: " + entity.getPosition().distance(targetPosition));
        }*/

        if (targetPosition!=null && entity.getPosition().distance(targetPosition)<1.1) {

            moveAction.setTarget(targetPosition);
            MyEntity entity1 = getGlobalMap().setPositionNextTick(entity, targetPosition);
            if (entity1!=null) {
                // тут нужен додж
                if (entity1.isDangerMove())
                {
                    Final.DEBUG("ERROR ", "position is Danger MOVE " + entity.getPosition().toString() + " To: " + targetPosition.toString());
                }
               /* boolean rotation = addMoveTargetUnit(entity1, entity.getPosition());
                if (!rotation)
                {
                    entity.getEntityAction().setMoveAction(null);
                    return null;
                }*/
            }
            //mAStar.addNewBlock(targetPosition);
            entity.getEntityAction().setMoveAction(moveAction);
            return moveAction;
        }
        else {
            // поиск Astar интересно
            if (Final.A_STAR && current==null) {

                mAStar.initSearch(entity.getPosition(), targetPosition);
                List<Node> path = mAStar.findPath(entity.getEntityType());
                if (path.size()>0) {

                    if (Final.debugGraphic) {
                        if (Final.CHECK_SEARCH_PATH_ASTAR) {
                            for (int k = 0; k < path.size(); k++) {
                                FinalGraphic.sendSquare(mDebugInterface, path.get(k).getVec2Int(), 1, FinalGraphic.COLOR_GREEN_TWO);
                            }
                        }
                    }

                    current = path.get(0);

                    if (Final.DANGER_RESOURCE) {
                        for (int i = 0; i < 5; i++) {
                            if (path.size()<=i) break;
                            if (mGlobalMap.getMapNoCheck(path.get(i).getVec2Int()).getEntityType()==EntityType.RESOURCE)
                            {
                                mGlobalMap.getMapNoCheck(path.get(i).getVec2Int()).setDangerResource(true);
                            }
                        }
                    }
                }
            }

            if (current!=null) {
                if (current.getChild()!=null) {

                    moveAction.setTarget(current.getChild().getVec2Int());
                    MyEntity entity1 = getGlobalMap().setPositionNextTick(entity,current.getChild().getVec2Int());
                    if (entity1!=null) {
                        if (current.getChild().getChild()!=null) {
                            boolean rotation = false;
                            if (entity1.isDangerMove())
                            {
                                rotation = false;
                                getGlobalMap().setPositionNextTick(entity1,current.getChild().getVec2Int());
                            }
                            else {
                                rotation = addMoveTargetUnit(entity1,current.getChild());
                            }
                            if (!rotation) {
                                entity.getEntityAction().setMoveAction(null);
                                return null;
                            }
                        }
                    }

                    //mAStar.addNewBlock(path.get(1).getVec2Int());
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

    private boolean addMoveTargetUnit(MyEntity entity, Vec2Int newPosition) {
     //   if (entity.isUpdate()) return false;

        MoveAction moveAction = getMoveActionPosition(entity,newPosition,true);
        entity.setUpdate(true);

        EntityAction entityAction = new EntityAction();
        entityAction.clear();

        entityAction.setMoveAction(moveAction);

        actionHashMap.put(entity.getId(),entityAction);

        return true;
    }

    private boolean addMoveTargetUnit(MyEntity entity, Node current) {
        if (entity.isRotation()) return false;
        if (entity.isDodge()) return false;
        if (entity.isDangerMove()) return false;
        entity.setUpdate(true);
        entity.setRotation(true);

        MoveAction moveAction = getMoveActionPosition(entity,null,true,current);

        if (moveAction==null) return false;

        EntityAction entityAction = new EntityAction();
        entityAction.clear();

        entityAction.setMoveAction(moveAction);

        actionHashMap.put(entity.getId(),entityAction);

        return true;
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

    public List<Node> findPath(Vec2Int start, Vec2Int end, EntityType type){
        mAStar.initSearch(start, end);
        List<Node> path = mAStar.findPath(type);

        return path;
    }


    public void checkPositionNextTik(GlobalManager globalManager, HashMap<Integer, EntityAction> actionHashMap) {

        if (!Final.A_STAR) return;

        globalManager.getMapPotField().calculateAttackNextTik(globalManager);

        MapPotField mapPotField = globalManager.getMapPotField();

        MyPlayer myPlayer = globalManager.getGlobalStatistic().getMyPlayer();

        ArrayList<MyEntity> rangerArrayList= myPlayer.getEntityArrayList(EntityType.RANGED_UNIT);

        for (int i=0; i<rangerArrayList.size(); i++)
        {
            MyEntity ranger = rangerArrayList.get(i);

            if (ranger.getEntityAction().getMoveAction()!=null)
            {
                Field field = mapPotField.getMapPotField()[ranger.getEntityAction().getMoveAction().getTarget().getX()][ranger.getEntityAction().getMoveAction().getTarget().getY()];
                int countDanger = field.getDangerRanger();

                if (countDanger==0) continue;

                int countDangerAttackNextTikMax =0;

                for (int j=0; j<field.getDangerArrayList().size();j++)
                {
                    if (field.getDangerArrayList().get(j).getAttackNextTik()>countDangerAttackNextTikMax)
                    {
                        countDangerAttackNextTikMax = field.getDangerArrayList().get(j).getAttackNextTik();
                    }
                }

                /*
                for (int j=0; j<field.getDangerCounterArrayList().size();j++)
                {
                    if (field.getDangerArrayList().get(j).getAttackNextTik()>countDangerAttackNextTikMax)
                    {
                        countDangerAttackNextTikMax = field.getDangerArrayList().get(j).getAttackNextTik();
                    }
                }*/

                if (countDangerAttackNextTikMax<countDanger)
                {
                    ranger.getEntityAction().setMoveAction(null);
                    EntityAction entityAction = actionHashMap.get(ranger.getId());
                    if (entityAction == null) entityAction = new EntityAction(null, null, null, null);
                    entityAction.setMoveAction(null);
                //    Final.DEBUGRelease("CPNT", FinalConstant.getCurrentTik() + " " + ranger.getId() + " " + ranger.getPosition().toString() +
                //            " C: " + countDangerAttackNextTikMax + " CD: " + countDanger);
                    actionHashMap.put(ranger.getId(),entityAction);
                }
            }
        }
    }

    public AStar getAStar() {
        return mAStar;
    }
}
