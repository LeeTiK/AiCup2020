package strategy;

import model.EntityType;
import model.Player;
import model.Vec2Int;
import strategy.map.potfield.MapPotField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyPlayer extends Player {

    // ArrayList<strategy.MyEntity> mEntityArrayList;

    ArrayList<MyEntity> mUnitArrayList;
    ArrayList<MyEntity> mBuildingArrayList;

    int populationCurrent = 0;
    int populationMax = 0;

    int countAllMelee = 0;
    int countAllRange = 0;
    int countAllBiuld = 0;
    int countAllHouse = 0;

    int countDeadMelee = 0;
    int countDeadRange = 0;
    int countDeadBiuld = 0;
    int countDeadHouse = 0;
    int countDeadOtherBuilders = 0;

    int resourceOldTik = 0;
    int resourceCurrentTik = 0;
    int resourceAllGame = 0;

    int countAllBadPositionRange = 0;

    int countBuildDodge = 0;

    int historyAll;

    /// оптимизация массивов
    ArrayList<MyEntity> mRangerArrayList;
    ArrayList<MyEntity> mMeleeArrayList;
    ArrayList<MyEntity> mBuilderArrayList;
    ArrayList<MyEntity> mHouseArrayList;

    // массив ближащих врагов к базе/ строителям
    ArrayList<MyEntity> mEnemyArrayList;

    public MyPlayer(int id, int score, int resource) {
        super(id, score, resource);
        init();
    }


    public MyPlayer(Player player) {
        super(player.getId(), player.getScore(), player.getResource());
        init();
    }

    private void init() {
        mUnitArrayList = new ArrayList<>();
        mBuildingArrayList = new ArrayList<>();
        mEnemyArrayList = new ArrayList<>();
        resourceAllGame = 0;
        resourceOldTik = 0;
    }

    public void update(Player player) {
        setResource(player.getResource());
        setScore(player.getScore());

        resourceCurrentTik = player.getResource();
    }


    public void startUpdate() {
        for (int i = 0; i < mBuildingArrayList.size(); i++) {
            mBuildingArrayList.get(i).setUpdate(false);
        }

        for (int i = 0; i < mUnitArrayList.size(); i++) {
            mUnitArrayList.get(i).setUpdate(false);
        }

        mEnemyArrayList.clear();
    }

    public EStatus updateEntity(MyEntity entity) {
        boolean check = true;
        // добавление и обновление информации о игроках

        switch (entity.getEntityType()) {
            case WALL:
            case HOUSE:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE: {

                for (int j = 0; j < mBuildingArrayList.size(); j++) {
                    if (mBuildingArrayList.get(j).getId() == entity.getId()) {
                        mBuildingArrayList.get(j).update(entity);
                        check = false;
                        return EStatus.UPDATE_Entity;
                    }
                }

                if (check) {
                    addNewEntity(entity);
                    return EStatus.NEW_Entity;
                }
                break;
            }
            case BUILDER_UNIT:
            case MELEE_UNIT:
            case RANGED_UNIT: {
                for (int j = 0; j < mUnitArrayList.size(); j++) {
                    if (mUnitArrayList.get(j).getId() == entity.getId()) {
                        mUnitArrayList.get(j).update(entity);
                        check = false;
                        return EStatus.UPDATE_Entity;
                    }
                }

                if (check) {
                    addNewEntity(entity);
                    return EStatus.NEW_Entity;
                }
            }
        }

        return EStatus.ERROR;
    }

    public MyEntity checkDelete() {
        //проверяем и удаляем старые юниты
        for (int i = 0; i < mBuildingArrayList.size(); i++) {
            if (!mBuildingArrayList.get(i).isUpdate()) {
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
                MyEntity entity = mBuildingArrayList.get(i);
                mBuildingArrayList.remove(i);
                return entity;
            }
        }

        for (int i = 0; i < mUnitArrayList.size(); i++) {
            if (!mUnitArrayList.get(i).isUpdate()) {
                EntityType entityType = mUnitArrayList.get(i).getEntityType();
                switch (entityType) {

                    case BUILDER_UNIT:
                        countDeadBiuld++;
                        break;
                    case MELEE_UNIT:
                        countDeadMelee++;
                        break;
                    case RANGED_UNIT:
                        countDeadRange++;
                        if (mUnitArrayList.get(i).getHealth()>5){
                            countAllBadPositionRange++;
                        }
                        break;
                }
                MyEntity entity =mUnitArrayList .get(i);
                mUnitArrayList.remove(i);
                return entity;
            }
        }

        return null;
    }

    public void finishUpdate() {
        populationMax = 0;
        populationCurrent = 0;
        for (int i = 0; i < mBuildingArrayList.size(); i++) {
            switch (mBuildingArrayList.get(i).getEntityType()) {

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

        for (int i = 0; i < mUnitArrayList.size(); i++) {
            switch (mUnitArrayList.get(i).getEntityType()) {

                case MELEE_UNIT:
                case RANGED_UNIT:
                case BUILDER_UNIT:
                    populationCurrent += 1;
                    break;
            }
        }


        // сортируем всех юнитов атаки по ближайщему(толкьо для себя)


        mRangerArrayList = getEntityArrayListSlow(EntityType.RANGED_UNIT);
        mMeleeArrayList = getEntityArrayListSlow(EntityType.MELEE_UNIT);
        mBuilderArrayList = getEntityArrayListSlow(EntityType.BUILDER_UNIT);
        mHouseArrayList = getEntityArrayListSlow(EntityType.HOUSE);

        if (FinalConstant.getCurrentTik() == 0) resourceCurrentTik = 0;

        addResource(resourceCurrentTik - resourceOldTik);
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
                mUnitArrayList.add(new strategy.MyEntity(entity));
            }
            case RESOURCE -> {
            }
        }
    }*/

    private void addNewEntity(MyEntity entity) {
        // mEntityArrayList.add(entity);
        switch (entity.getEntityType()) {
            case HOUSE: {
                resourceCurrentTik += getCost(entity.getEntityType());
                countAllHouse++;
                mBuildingArrayList.add(entity);
                break;
            }
            case WALL:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE: {
                resourceCurrentTik += getCost(entity.getEntityType());
                mBuildingArrayList.add(entity);
                break;
            }
            case BUILDER_UNIT:
                resourceCurrentTik += getCost(entity.getEntityType());
                countAllBiuld++;
                mUnitArrayList.add(entity);
                break;
            case MELEE_UNIT:
                resourceCurrentTik += getCost(entity.getEntityType());
                countAllMelee++;
                mUnitArrayList.add(entity);
                break;
            case RANGED_UNIT: {
                resourceCurrentTik += getCost(entity.getEntityType());
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


    public ArrayList<MyEntity> getEntityArrayListSlow(EntityType entityType) {
        ArrayList<MyEntity> arrayList = new ArrayList<>();
        switch (entityType) {
            case WALL:
            case HOUSE:
            case BUILDER_BASE:
            case MELEE_BASE:
            case TURRET:
            case RANGED_BASE: {
                //mBuildingArrayList.add(new strategy.MyEntity(entity));
                for (int i = 0; i < mBuildingArrayList.size(); i++) {
                    if (mBuildingArrayList.get(i).getEntityType() == entityType)
                        arrayList.add(mBuildingArrayList.get(i));
                }
                break;
            }
            case BUILDER_UNIT:
            case MELEE_UNIT:
            case RANGED_UNIT: {
                for (int i = 0; i < mUnitArrayList.size(); i++) {
                    if (mUnitArrayList.get(i).getEntityType() == entityType) arrayList.add(mUnitArrayList.get(i));
                }
            }
        }
        return arrayList;
    }

    public ArrayList<MyEntity> getEntityArrayList(EntityType entityType) {

        switch (entityType) {
            case HOUSE:
                return mHouseArrayList;
            case BUILDER_UNIT:
                return mBuilderArrayList;
            case MELEE_UNIT:
                return mMeleeArrayList;
            case RANGED_UNIT: {
                return mRangerArrayList;
            }
        }

        return getEntityArrayListSlow(entityType);
    }

    @Override
    public String toString() {
        //   return
        ArrayList<MyEntity> buildArrayList = getEntityArrayList(EntityType.BUILDER_UNIT);
        ArrayList<MyEntity> meleeArrayList = getEntityArrayList(EntityType.MELEE_UNIT);
        ArrayList<MyEntity> rangeArrayList = getEntityArrayList(EntityType.RANGED_UNIT);
        ArrayList<MyEntity> houseArrayList = getEntityArrayList(EntityType.HOUSE);
        return "ID: " + getId() + " Res: " + getResourceAllGame() +
                " B: " + buildArrayList.size() + "/" + countDeadBiuld + "/" + countAllBiuld + "/" + countBuildDodge +
                " M: " + meleeArrayList.size() + "/" + countDeadMelee + "/" + countAllMelee +
                " R: " + rangeArrayList.size() + "/" + countDeadRange + "/" + countAllRange + "/" + countAllBadPositionRange +
                " H: " + houseArrayList.size() + "/" + countDeadHouse + "/" + countAllHouse +
                " O: " + countDeadOtherBuilders +
                " P: " + populationCurrent + "/" + populationMax;
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

    public int getCost(EntityType entityType) {
        switch (entityType) {
            case WALL:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case HOUSE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case BUILDER_BASE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case BUILDER_UNIT:
                return FinalConstant.getEntityProperties(entityType).getCost(); //+ (getEntityArrayList(EntityType.BUILDER_UNIT) == null ? 0 : getEntityArrayList(EntityType.BUILDER_UNIT).size()-1);
            case MELEE_BASE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case MELEE_UNIT:
                return FinalConstant.getEntityProperties(entityType).getCost() + (getEntityArrayList(EntityType.MELEE_UNIT) == null ? 0 : getEntityArrayList(EntityType.MELEE_UNIT).size());
            case RANGED_BASE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case RANGED_UNIT:
                return FinalConstant.getEntityProperties(entityType).getCost() + (getEntityArrayList(EntityType.RANGED_UNIT) == null ? 0 : getEntityArrayList(EntityType.RANGED_UNIT).size());
            case RESOURCE:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case TURRET:
                return FinalConstant.getEntityProperties(entityType).getCost();
            case Empty:
                return FinalConstant.getEntityProperties(entityType).getCost();
        }
        return 0;
    }

    private void addResource(int resourceCurrentTik) {
        resourceAllGame += resourceCurrentTik;
    }

    public int getCountBuildDontCreate(EntityType entityType) {
        int count = 0;
        for (int i = 0; i < getBuildingArrayList().size(); i++) {
            if (getBuildingArrayList().get(i).getEntityType() == entityType) {
                if (!getBuildingArrayList().get(i).isActive()) count++;
            }
        }

        return count;
    }

    public int getResourceAllGame() {
        return resourceAllGame;
    }

    public MyEntity getBuilderBase() {
        ArrayList<MyEntity> arrayList = getEntityArrayList(EntityType.BUILDER_BASE);
        if (arrayList.size() > 0) {
            return arrayList.get(0);
        }
        return null;
    }

    // поиск ближайщих врагов ко всем нашим зданиям и юнитам
    public void searchEnemy(GlobalMap globalMap){
        for (int i = 0; i < mUnitArrayList.size(); i++) {
            MyEntity unit = mUnitArrayList.get(i);
            if (unit.getMinDisToEnemy()==0xFFFF) {
                MyEntity enemy = globalMap.getNearestPlayer(unit.getPosition(), getId(), -1,EntityType.ATTACK_ENTITY,false);

                if (enemy != null) {
                    unit.setMinDisToEnemy((float) unit.getPosition().distance(enemy.getPosition()));
                    unit.setEnemyMinDis(enemy);
                } else {
                    unit.setMinDisToEnemy(0xFFFF);
                }
            }
        }

        for (int i = 0; i < mBuildingArrayList.size(); i++) {
            MyEntity unit = mBuildingArrayList.get(i);
            if (unit.getMinDisToEnemy()==0xFFFF) {
                MyEntity enemy = globalMap.getNearestPlayer(unit.getPosition(), getId(), -1,EntityType.ATTACK_ENTITY,false);

                if (enemy != null) {
                    unit.setMinDisToEnemy((float) unit.getPosition().distance(enemy.getPosition()));
                    unit.setEnemyMinDis(enemy);
                } else {
                    unit.setMinDisToEnemy(0xFFFF);
                }
            }
        }
    }


    // сортируем юнитов по дистанции к врагам
    public void sortAttackUnit(boolean direction) {
        // сортируем по порядку
        Collections.sort(mUnitArrayList, new Comparator<MyEntity>() {
            public int compare(MyEntity a, MyEntity b) {
                if (direction)
                {
                    if (a.getMinDisToEnemy() > b.getMinDisToEnemy()) return 1;
                    if (a.getMinDisToEnemy() < b.getMinDisToEnemy()) return -1;
                } else {
                    if (a.getMinDisToEnemy() < b.getMinDisToEnemy()) return 1;
                    if (a.getMinDisToEnemy() > b.getMinDisToEnemy()) return -1;
                }
                return 0;
            }
        });


        mRangerArrayList = getEntityArrayListSlow(EntityType.RANGED_UNIT);
        mMeleeArrayList = getEntityArrayListSlow(EntityType.MELEE_UNIT);
        mBuilderArrayList = getEntityArrayListSlow(EntityType.BUILDER_UNIT);
    }

    public ArrayList<MyEntity> getBuildingUnitNearResources(GlobalMap globalMap) {
        ArrayList<MyEntity> builder = getEntityArrayList(EntityType.BUILDER_UNIT);
        ArrayList<MyEntity> myEntityArrayList = new ArrayList<>();

        for (int i=0; i<builder.size(); i++)
        {
            int count = globalMap.getAroundEntity(builder.get(i).getPosition(),EntityType.RESOURCE);
            if (count>0){
                builder.get(i).setNearResource(count);
                myEntityArrayList.add(builder.get(i));
            }
        }

        Collections.sort(myEntityArrayList, new Comparator<MyEntity>() {
            public int compare(MyEntity a, MyEntity b) {
                if (a.getNearResource() > b.getNearResource()) return 1;
                if (a.getNearResource() < b.getNearResource()) return -1;
                return 0;
            }
        });

        return myEntityArrayList;
    }

    public ArrayList<MyEntity> getBuildingUnitNearResourcesOther(GlobalMap globalMap, MapPotField mapPotField) {
        ArrayList<MyEntity> builders = getEntityArrayList(EntityType.BUILDER_UNIT);
        ArrayList<MyEntity> myEntityArrayList = new ArrayList<>();


        for (int i=0; i<builders.size(); i++)
        {
            MyEntity builder = builders.get(i);
            if (builder.getTargetEntity()!=null) continue;

            int count = globalMap.getAroundEntity(builder.getPosition(),EntityType.RESOURCE);
            if (count==0){
                builder.setNearResource(count);
                myEntityArrayList.add(builder);

                MyEntity entity = globalMap.getNearest(builder.getPosition(), EntityType.RESOURCE,true, -1,mapPotField);

                if (entity != null) {
                    builder.setMinDisToEnemy((float) builder.getPosition().distance(entity.getPosition()));
                    builder.setEnemyMinDis(entity);
                } else {
                    builder.setMinDisToEnemy(0xFFFF);
                    builder.setEnemyMinDis(null);
                }
            }
        }

        // сортируем по порядку
        Collections.sort(myEntityArrayList, new Comparator<MyEntity>() {
            public int compare(MyEntity a, MyEntity b) {
                if (a.getMinDisToEnemy() > b.getMinDisToEnemy()) return 1;
                if (a.getMinDisToEnemy() < b.getMinDisToEnemy()) return -1;
                return 0;
            }
        });

        return myEntityArrayList;
    }

    public ArrayList<MyEntity> initEnemyArrayListSlow() {
        mEnemyArrayList.clear();

        for (int i = 0; i < mBuildingArrayList.size(); i++) {
            MyEntity building = mBuildingArrayList.get(i);
            if (building.getMinDisToEnemy()==0xFFFF) continue;

            if (building.getMinDisToEnemy()<15){
                addEnemy(building.getEnemyMinDis(),building.getMinDisToEnemy());
            }
        }

        for (int i = 0; i < mUnitArrayList.size(); i++) {
            MyEntity unit = mUnitArrayList.get(i);
            if (unit.getMinDisToEnemy()==0xFFFF) continue;
            if (unit.getEntityType()!=EntityType.BUILDER_UNIT) continue;

            if (unit.getMinDisToEnemy()<13){
                addEnemy(unit.getEnemyMinDis(),unit.getMinDisToEnemy());
            }
        }

        if (mEnemyArrayList.size()>1) {
            // сортируем от меньшего к большему
            Collections.sort(mEnemyArrayList, new Comparator<MyEntity>() {
                public int compare(MyEntity a, MyEntity b) {
                    if (a.getMinDisToEnemy() > b.getMinDisToEnemy()) return 1;
                    if (a.getMinDisToEnemy() < b.getMinDisToEnemy()) return -1;
                    return 0;
                }
            });
        }

        return mEnemyArrayList;
    }

    private void addEnemy(MyEntity entity, float dis){
        for (int i=0; i<mEnemyArrayList.size(); i++)
        {
            if (mEnemyArrayList.get(i).getId()==entity.getId()) {
                if (mEnemyArrayList.get(i).getMinDisToEnemy()>dis)
                {
                    mEnemyArrayList.get(i).setMinDisToEnemy(dis);
                }
                return;
            }
        }

        entity.setMinDisToEnemy(dis);
        mEnemyArrayList.add(entity);
    }

    public ArrayList<MyEntity> getEnemyArrayList() {
        return mEnemyArrayList;
    }

    public void addCountBuildDodge() {
        this.countBuildDodge++;
    }

    public int getCountBuildDodge() {
        return countBuildDodge;
    }

    public int getCountAllBiuld() {
        return countAllBiuld;
    }
}
