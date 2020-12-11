package strategy;

import model.Entity;
import model.EntityAction;
import model.EntityType;
import model.Vec2Int;

public class MyEntity extends Entity {

    boolean update = true;

    DataTaskUnit mDataTaskUnit;

    int simulationHP;

    /// добавляем всё что требуется для групп юнитов

    int width, heigth;

    DataAttack dataAttack;
    int countAttackingUnit;

    EntityAction mEntityAction;
    boolean rotation;
    boolean dodge;
    float minDisToEnemy;
    MyEntity mEnemyMinDis;

    Vec2Int oldTikPosition;
    Vec2Int oldTwoTikPosition;

    int positionDefense = -1;

    int nearResource;
    // цель этого MyEntity
    MyEntity targetEntity;

    public MyEntity(Entity entity) {
        super(entity.getId(), entity.getPlayerId(), entity.getEntityType(), entity.getPosition(), entity.getHealth(), entity.isActive());

        init();
    }

    public MyEntity(int id, Integer playerId, EntityType entityType, Vec2Int position, int health, boolean active) {
        super(id, playerId, entityType, position, health, active);

        init();
    }

    public MyEntity() {
        init();
    }

    void init() {
        mDataTaskUnit = new DataTaskUnit(EUnitState.EMPTY);
        simulationHP = getHealth();
        mEntityAction = new EntityAction(null, null, null, null);
        oldTikPosition = null;
        oldTwoTikPosition = null;
        clear();
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update;
    }

    public void clear() {
        mEntityAction.clear();
        dodge = false;
        minDisToEnemy = 0xFFFF;
        setDataAttack(null);
        rotation = false;
        countAttackingUnit = 0;
        nearResource = 0;
        targetEntity = null;
        mEnemyMinDis = null;
    }

    public void update(Entity entity) {
      /*  if (!getPosition().equals(entity.getPosition()))
        {
            System.out.println("ВОТ ТУТ НЕ РАВНО!!!!");
        }*/
        if (oldTwoTikPosition == null) {
            oldTwoTikPosition = getPosition().copy();
        } else {
            if (oldTikPosition == null) {
                oldTikPosition = getPosition().copy();
            } else {
                oldTwoTikPosition = oldTikPosition;
                oldTikPosition = getPosition().copy();
            }
        }

        setPosition(entity.getPosition());
        //  if (oldTwoTikPosition!=null)System.out.println("ID: " + getId() + " oldPos: " + oldTwoTikPosition.toString() + " newPos:  " + entity.getPosition());
        if (!getPosition().equals(oldTwoTikPosition)) {
            //  System.out.println("ВОТ ТУТ НЕ РАВНО!!!!");
        }

        setHealth(entity.getHealth());
        setActive(entity.isActive());
        simulationHP = getHealth();
        setUpdate(true);

        clear();
    }

    public int getSimulationHP() {
        return simulationHP;
    }

    public void attackHP(int attack) {
        this.simulationHP -= attack;
    }

    public void attackResetHP(int attack) {
        this.simulationHP += attack;
    }

    public DataTaskUnit getDataTaskUnit() {
        return mDataTaskUnit;
    }

    public void setDataTaskUnit(DataTaskUnit dataTaskUnit) {
        mDataTaskUnit = dataTaskUnit;
    }

    public EUnitState getUnitState() {
        return getDataTaskUnit().getUnitState();
    }

    public void move(Vec2Int vec2Int) {
        //MoveAction action = new MoveAction()
    }

    public void setMinDisToEnemy(float minDisToEnemy) {
        this.minDisToEnemy = minDisToEnemy;
    }

    public int getWidth() {
        return width;
    }

    public float getMinDisToEnemy() {
        return minDisToEnemy;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public EntityAction getEntityAction() {
        return mEntityAction;
    }

    public boolean isMove() {
        if (getEntityAction().getMoveAction() != null && oldTwoTikPosition != null && !oldTwoTikPosition.equals(getPosition())) {
            return true;
        }
        return false;
    }

    public void setDodge(boolean dodge) {
        this.dodge = dodge;
    }

    public boolean isDodge() {
        return dodge;
    }

    public void setDataAttack(DataAttack dataAttack) {
        this.dataAttack = dataAttack;
    }

    public DataAttack getDataAttack() {
        return dataAttack;
    }

    public void setRotation(boolean rotation) {
        this.rotation = rotation;
    }

    public boolean isRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        String str = "" + getId() + " ";
        str += getPosition().toString();
        if (oldTwoTikPosition != null) str += oldTwoTikPosition.toString();
        if (getEntityAction().getMoveAction() != null) {
            str += " M: " + getEntityAction().getMoveAction().getTarget().toString();
        }
        if (getEntityAction().getAttackAction() != null) {
            str += " A: T";
        }
        str += " " + isMove();
        str += " " + isRotation();
        return str;
    }

    public void setPositionDefense(int positionDefense) {
        this.positionDefense = positionDefense;
    }

    public int getPositionDefense() {
        return positionDefense;
    }

    public int getCountAttackingUnit() {
        return countAttackingUnit;
    }

    public void addCountAttackingUnit(){
        countAttackingUnit++;
    }

    public void setTargetEntity(MyEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public MyEntity getTargetEntity() {
        return targetEntity;
    }

    public int getNearResource() {
        return nearResource;
    }

    public void setNearResource(int nearResource) {
        this.nearResource = nearResource;
    }

    public void setEnemyMinDis(MyEntity enemyMinDis) {
        mEnemyMinDis = enemyMinDis;
    }

    public MyEntity getEnemyMinDis() {
        return mEnemyMinDis;
    }
}
