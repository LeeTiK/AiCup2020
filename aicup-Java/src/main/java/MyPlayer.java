import model.Entity;
import model.EntityType;
import model.Player;

import java.util.ArrayList;

public class MyPlayer extends Player {

   // ArrayList<MyEntity> mEntityArrayList;

    ArrayList<MyEntity> mUnitArrayList;
    ArrayList<MyEntity> mBuildingArrayList;

    int populationCurrent =0;
    int populationMax =0;

    int countAllMelee =0;
    int countAllRange =0;
    int countAllBiuld =0;
    int countAllHouse =0;

    int countDeadMelee =0;
    int countDeadRange =0;
    int countDeadBiuld =0;
    int countDeadHouse =0;
    int countDeadOtherBuilders =0;

    int resourceOldTik = 0;
    int resourceCurrentTik = 0;
    int resourceAllGame = 0;

    int historyAll;

    /// оптимизация массивов
    ArrayList<MyEntity> mRangerArrayList;
    ArrayList<MyEntity> mMeleeArrayList;
    ArrayList<MyEntity> mBuilderArrayList;
    ArrayList<MyEntity> mHouseArrayList;

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
        init();
    }


    public MyPlayer (Player player){
        super(player.getId(),player.getScore(),player.getResource());
        init();
    }

    private void init(){
        mUnitArrayList = new ArrayList<>();
        mBuildingArrayList = new ArrayList<>();
        resourceAllGame = 0;
        resourceOldTik = 0;
    }

    public void update(Player player) {
        setResource(player.getResource());
        setScore(player.getScore());

        resourceCurrentTik = player.getResource();
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

    public EStatus updateEntity(MyEntity entity) {
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
                switch (entityType) {

                    case HOUSE:
                        countDeadHouse++;
                        break;
                    case BUILDER_BASE:
                    case RANGED_BASE:
                    case MELEE_BASE:
                        countDeadOtherBuilders++;
                        break;
                }
                mBuildingArrayList.remove(i);
                return entityType;
            }
        }

        for (int i=0; i<mUnitArrayList.size(); i++)
        {
            if (!mUnitArrayList.get(i).isUpdate()){
                EntityType entityType = mUnitArrayList.get(i).getEntityType();
                switch (entityType)
                {

                    case BUILDER_UNIT:
                        countDeadBiuld++;
                        break;
                    case MELEE_UNIT:
                        countDeadMelee++;
                        break;
                    case RANGED_UNIT:
                        countDeadRange++;
                        break;
                }
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
                    if (mBuildingArrayList.get(i).isActive()) {
                        populationMax += 5;
                    }
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


        mRangerArrayList = getEntityArrayListSlow(EntityType.RANGED_UNIT);
        mMeleeArrayList =  getEntityArrayListSlow(EntityType.MELEE_UNIT);
        mBuilderArrayList =  getEntityArrayListSlow(EntityType.BUILDER_UNIT);
        mHouseArrayList =  getEntityArrayListSlow(EntityType.HOUSE);

        if (FinalConstant.getCurrentTik()==0) resourceCurrentTik = 0;

        addResource(resourceCurrentTik-resourceOldTik);
        resourceOldTik = getResource();
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
                mUnitArrayList.add(new MyEntity(entity));
            }
            case RESOURCE -> {
            }
        }
    }*/

    private void addNewEntity(MyEntity entity){
      // mEntityArrayList.add(entity);
        switch (entity.getEntityType())
        {
            case HOUSE: {
                resourceCurrentTik+=getCost(entity.getEntityType());
                countAllHouse++;
                mBuildingArrayList.add(entity);
                break;
            }
            case WALL:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE:{
                resourceCurrentTik+=getCost(entity.getEntityType());
                mBuildingArrayList.add(entity);
                break;
            }
            case BUILDER_UNIT:
                resourceCurrentTik+=getCost(entity.getEntityType());
                countAllBiuld++;
                mUnitArrayList.add(entity);
                break;
            case MELEE_UNIT:
                resourceCurrentTik+=getCost(entity.getEntityType());
                countAllMelee++;
                mUnitArrayList.add(entity);
                break;
            case RANGED_UNIT:{
                resourceCurrentTik+=getCost(entity.getEntityType());
                countAllRange++;
                mUnitArrayList.add(entity);
                break;
            }
        }
    }

    public ArrayList<MyEntity> getBuildingArrayList() {
        return mBuildingArrayList;
    }

    public ArrayList<MyEntity> getUnitArrayList() {
        return mUnitArrayList;
    }


    public ArrayList<MyEntity> getEntityArrayListSlow(EntityType entityType)
    {
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

    public ArrayList<MyEntity> getEntityArrayList(EntityType entityType) {

        switch (entityType)
        {
            case HOUSE:
                return mHouseArrayList;
            case BUILDER_UNIT:
                return mBuilderArrayList;
            case MELEE_UNIT:
                return mMeleeArrayList;
            case RANGED_UNIT:{
                return mRangerArrayList;
            }
        }

       return getEntityArrayListSlow(entityType);
    }

    @Override
    public String toString(){
     //   return
        ArrayList<MyEntity> buildArrayList = getEntityArrayList(EntityType.BUILDER_UNIT);
        ArrayList<MyEntity> meleeArrayList = getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> rangeArrayList = getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> houseArrayList = getEntityArrayList(EntityType.HOUSE);
        return "ID: " + getId() + " B: " + buildArrayList.size() +"/"+countDeadBiuld +"/"+countAllBiuld +
                " M: "+ meleeArrayList.size() + "/"+countDeadMelee +"/"+countAllMelee +
                " R: "+ rangeArrayList.size() + "/"+countDeadRange +"/"+countAllRange +
                " H: "+ houseArrayList.size() + "/"+countDeadHouse +"/"+countAllHouse +
                " O: " + countDeadOtherBuilders +
                " P: " + populationCurrent + "/" + populationMax +
                " Res: " + getResourceAllGame();

      //  cgetResourceAllGame
    }

    public int getPopulationCurrent() {
        return populationCurrent;
    }

    public int getPopulationMax() {
        return populationMax;
    }

    public int getCountDeadBiuld() {
        return countDeadBiuld;
    }

    public int getCountDeadMelee() {
        return countDeadMelee;
    }

    public int getCountDeadRange() {
        return countDeadRange;
    }

    public int getCountDeadHouse() {
        return countDeadHouse;
    }

    public int getCost(EntityType entityType)
    {
        switch (entityType)
        {
            case WALL:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case HOUSE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case BUILDER_BASE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case BUILDER_UNIT:
                return FinalConstant.getEntityProperties(entityType).getCost() + (getEntityArrayList(EntityType.BUILDER_UNIT)==null?0:getEntityArrayList(EntityType.RANGED_UNIT).size());
            case MELEE_BASE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case MELEE_UNIT:
                return FinalConstant.getEntityProperties(entityType).getCost()+ (getEntityArrayList(EntityType.MELEE_UNIT)==null?0:getEntityArrayList(EntityType.RANGED_UNIT).size());
            case RANGED_BASE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case RANGED_UNIT:
                return FinalConstant.getEntityProperties(entityType).getCost()+ (getEntityArrayList(EntityType.RANGED_UNIT)==null?0:getEntityArrayList(EntityType.RANGED_UNIT).size());
            case RESOURCE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case TURRET:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case Empty:
                return FinalConstant.getEntityProperties(entityType).getCost();
        }
        return 0;
    }

    private void addResource(int resourceCurrentTik)
    {
        resourceAllGame +=resourceCurrentTik;
    }

    public int getCountBuildDontCreate(EntityType entityType)
    {
        int count = 0;
        for (int i=0; i<getBuildingArrayList().size(); i++)
        {
            if (getBuildingArrayList().get(i).getEntityType()==entityType)
            {
                if (!getBuildingArrayList().get(i).isActive()) count++;
            }
        }

        return count;
    }

    public int getResourceAllGame() {
        return resourceAllGame;
    }
}
