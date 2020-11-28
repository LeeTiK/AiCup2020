import model.Entity;
import model.EntityType;
import model.Player;

import java.util.ArrayList;

public class MyPlayer extends Player {

   // ArrayList<MyEntity> mEntityArrayList;

    ArrayList<MyUnit> mUnitArrayList;
    ArrayList<MyEntity> mBuildingArrayList;

    int populationCurrent =0;
    int populationMax =0;

    int historyAll;

    /*WALL(0),
    HOUSE(1),
    BUILDER_BASE(2),
    BUILDER_UNIT(3),
    MELEE_BASE(4),
    MELEE_UNIT(5),
    RANGED_BASE(6),
    RANGED_UNIT(7),
    RESOURCE(8),
    TURRET(9);*/

    public MyPlayer (int id, int score, int resource){
        super(id,score,resource);
     //   mEntityArrayList = new ArrayList<>();
        mUnitArrayList = new ArrayList<>();
        mBuildingArrayList = new ArrayList<>();
    }


    public MyPlayer (Player player){
        super(player.getId(),player.getScore(),player.getResource());
     //   mEntityArrayList = new ArrayList<>();
        mUnitArrayList = new ArrayList<>();
        mBuildingArrayList = new ArrayList<>();
    }

    public void update(Player player) {
        setResource(player.getResource());
        setScore(player.getScore());
    }


    public void startUpdate(){
        for (int i=0; i<mBuildingArrayList.size(); i++)
        {
            mBuildingArrayList.get(i).setUpdate(false);
        }

        for (int i=0; i<mUnitArrayList.size(); i++)
        {
            mUnitArrayList.get(i).setUpdate(false);
        }
    }

    public EStatus updateEntity(Entity entity) {
        boolean check =true;
        // добавление и обновление информации о игроках

        switch (entity.getEntityType())
        {
            case WALL:
            case HOUSE:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE:{

                for (int j=0; j<mBuildingArrayList.size(); j++)
                {
                    if (mBuildingArrayList.get(j).getId() == entity.getId()){
                        mBuildingArrayList.get(j).update(entity);
                        check = false;
                        return EStatus.UPDATE_Entity;
                    }
                }

                if (check)
                {
                    addNewEntity(entity);
                    return EStatus.NEW_Entity;
                }
                break;
            }
            case BUILDER_UNIT:
            case MELEE_UNIT:
            case RANGED_UNIT:{
                for (int j=0; j<mUnitArrayList.size(); j++)
                {
                    if (mUnitArrayList.get(j).getId() == entity.getId()){
                        mUnitArrayList.get(j).update(entity);
                        check = false;
                        return EStatus.UPDATE_Entity;
                    }
                }

                if (check)
                {
                    addNewEntity(entity);
                    return EStatus.NEW_Entity;
                }
            }
        }

        return EStatus.ERROR;
    }

    public EntityType checkDelete(){
        //проверяем и удаляем старые юниты
        for (int i=0; i<mBuildingArrayList.size(); i++)
        {
            if (!mBuildingArrayList.get(i).isUpdate()){
                EntityType entityType = mBuildingArrayList.get(i).getEntityType();
                mBuildingArrayList.remove(i);
                return entityType;
            }
        }

        for (int i=0; i<mUnitArrayList.size(); i++)
        {
            if (!mUnitArrayList.get(i).isUpdate()){
                EntityType entityType = mUnitArrayList.get(i).getEntityType();
                mUnitArrayList.remove(i);
                return entityType;
            }
        }

        return null;
    }

    public void finishUpdate(){
        populationMax = 0;
        populationCurrent = 0;
        for (int i=0; i<mBuildingArrayList.size(); i++)
        {
            switch (mBuildingArrayList.get(i).getEntityType())
            {

                case HOUSE:
                case BUILDER_BASE:
                case MELEE_BASE:
                case RANGED_BASE:
                    populationMax+=5;
                    break;
            }
        }

        for (int i=0; i<mUnitArrayList.size(); i++)
        {
            switch (mUnitArrayList.get(i).getEntityType())
            {

                case MELEE_UNIT:
                case RANGED_UNIT:
                case BUILDER_UNIT:
                    populationCurrent+=1;
                    break;
            }
        }
    }

/*
    private void updateEntity(Entity entity){
        mEntityArrayList.add(entity);
        switch (entity.getEntityType())
        {
            case WALL -> {
            }
            case HOUSE, BUILDER_BASE, MELEE_BASE, RANGED_BASE, TURRET -> {
                mBuildingArrayList.add(entity);
            }
            case BUILDER_UNIT, RANGED_UNIT, MELEE_UNIT -> {
                mUnitArrayList.add(new MyUnit(entity));
            }
            case RESOURCE -> {
            }
        }
    }*/

    private void addNewEntity(Entity entity){
      // mEntityArrayList.add(entity);
        switch (entity.getEntityType())
        {
            case WALL:
            case HOUSE:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE:{
                mBuildingArrayList.add(new MyEntity(entity));
                break;
            }
            case BUILDER_UNIT:
            case MELEE_UNIT:
            case RANGED_UNIT:{
                mUnitArrayList.add(new MyUnit(entity));
            }
        }
    }

    public ArrayList<MyEntity> getBuildingArrayList() {
        return mBuildingArrayList;
    }

    public ArrayList<MyUnit> getUnitArrayList() {
        return mUnitArrayList;
    }


    public ArrayList<MyEntity> getEntityArrayList(EntityType entityType) {
        ArrayList<MyEntity> arrayList = new ArrayList<>();
        switch (entityType)
        {
            case WALL:
            case HOUSE:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE:{
                //mBuildingArrayList.add(new MyEntity(entity));
                for (int i=0; i<mBuildingArrayList.size();i++)
                {
                    if (mBuildingArrayList.get(i).getEntityType() == entityType) arrayList.add(mBuildingArrayList.get(i));
                }
                break;
            }
            case BUILDER_UNIT:
            case MELEE_UNIT:
            case RANGED_UNIT:{
                for (int i=0; i<mUnitArrayList.size();i++)
                {
                    if (mUnitArrayList.get(i).getEntityType() == entityType) arrayList.add(mUnitArrayList.get(i));
                }
            }
        }
        return arrayList;
    }

    @Override
    public String toString(){
     //   return
        return "";
    }

    public int getPopulationCurrent() {
        return populationCurrent;
    }

    public int getPopulationMax() {
        return populationMax;
    }
}
