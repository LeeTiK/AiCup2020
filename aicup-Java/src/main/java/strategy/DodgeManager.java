package strategy;

import model.*;
import strategy.map.potfield.DodgePositionAnswer;
import strategy.map.potfield.MapPotField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DodgeManager {

    GlobalManager mGlobalManager;

    ArrayList<MyEntity> unitDodgeArrayList;

    public DodgeManager(){
        unitDodgeArrayList = new ArrayList<>();
    }


    public void update(GlobalManager globalManager,HashMap<Integer, EntityAction> actionHashMap) {
        MyPlayer myPlayer = globalManager.getGlobalStatistic().getMyPlayer();

        ArrayList<MyEntity> builderUnitArrayList = myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT);

        unitDodgeArrayList.clear();

        for (int i = 0; i < builderUnitArrayList.size(); i++) {
            MyEntity builderUnit = builderUnitArrayList.get(i);

            MoveAction m = null;
            BuildAction b = null;
            AttackAction a = null;
            RepairAction r = null;

            EntityAction entityAction = actionHashMap.get(builderUnit.getId());
            if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

            ArrayList<MyEntity> resource = globalManager.getGlobalMap().getEntityMap(builderUnit.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);

            // увороты от всех по ПП
            DodgePositionAnswer dodgePositionAnswer = globalManager.getMapPotField().getDodgePositionBuild(builderUnit,resource.size()>0,false);

            if (dodgePositionAnswer != null) {

                builderUnit.setDodgePositionAnswer(dodgePositionAnswer);
                unitDodgeArrayList.add(builderUnit);

                /*
                builderUnit.setDodge(true);
                builderUnit.setUpdate(true);
                builderUnit.setDodgePositionAnswer(dodgePositionAnswer);

                myPlayer.addCountBuildDodge();

                myPlayer.getUnitDodgeArrayList().add(builderUnit);

                actionHashMap.put(builderUnit.getId(), entityAction);*/
            }
        }
    }

    void calculateDodgeUnits(ArrayList<MyEntity> unitDodgeArrayList, GlobalManager globalManager, HashMap<Integer,EntityAction> actionHashMap){
        MyPlayer player = globalManager.getGlobalStatistic().getMyPlayer();
        GlobalMap globalMap = globalManager.getGlobalMap();

        Collections.sort(unitDodgeArrayList, mMyEntityComparator);

        ArrayList<MyEntity> unitDodgeNextArrayList = new ArrayList<>();

        for (int i=0; i<unitDodgeArrayList.size(); i++)
        {
            MyEntity entity = unitDodgeArrayList.get(i);

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

                            if (globalMap.checkEmpty(globalMap.getMapNextTick(),vec2Int))
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

                                if (globalMap.checkEmpty(globalMap.getMapNextTick(), vec2Int)) {
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

                        MyEntity entity1 = globalMap.setPositionNextTick(entity,vec2IntDodge);
                        if (entity1.getEntityType()==EntityType.BUILDER_UNIT || entity1.getEntityType()==EntityType.RANGED_UNIT)
                        {
                            unitDodgeNextArrayList.add(entity1);
                        }

                        actionHashMap.put(entity.getId(), entityAction);
                    }
                    break;
            }
        }
    }


    private void dodge(MyEntity entity, GlobalManager globalManager, HashMap<Integer,EntityAction> actionHashMap ){
        MapPotField mapPotField = globalManager.getMapPotField();
        GlobalMap globalMap = globalManager.getGlobalMap();

        MyPlayer player = globalManager.getGlobalStatistic().getMyPlayer();

        MoveAction m = null;
        BuildAction b = null;
        AttackAction a = null;
        RepairAction r = null;

        EntityAction entityAction = actionHashMap.get(entity.getId());
        if (entityAction == null) entityAction = new EntityAction(null, null, null, null);

        //    ArrayList<MyEntity> resource = globalManager.getGlobalMap().getEntityMap(entity.getPosition(),GlobalMap.aroundArray,FinalConstant.getMyID(),-1,false,EntityType.RESOURCE);
        entity.setDodgePositionAnswer(mapPotField.calculateDodgePosition(entity,true));

        Vec2Int vec2IntDodge  =null;
        // увороты от всех по ПП
        if (entity.getDodgePositionAnswer().getSafetyArrayList().size()>0)
        {
            for (int j=0; j<entity.getDodgePositionAnswer().getSafetyArrayList().size(); j++)
            {
                Vec2Int vec2Int = entity.getDodgePositionAnswer().getSafetyArrayList().get(j).getPosition();

                if (globalMap.checkEmpty(globalMap.getMapNextTick(),vec2Int))
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

                    if (globalMap.checkEmpty(globalMap.getMapNextTick(), vec2Int)) {
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

            MyEntity entity1 = globalMap.setPositionNextTick(entity,vec2IntDodge);

            actionHashMap.put(entity.getId(), entityAction);

            if (entity1!=null)
            {
                dodge(entity1,globalManager,actionHashMap);
            }
        }
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
