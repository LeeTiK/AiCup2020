package strategy;

import model.Vec2Int;

public class DataAttack {

    MyEntity targetEntity;

    MyEntity mMyEntity;
    Vec2Int position;


    public DataAttack(MyEntity targetEntity) {
        this.targetEntity = targetEntity;
        mMyEntity = null;
        position = null;
    }

    public int getIdEntity() {
        if (mMyEntity != null) return mMyEntity.getId();
        return targetEntity.getId();
    }

    public MyEntity getMyEntity() {
        return mMyEntity;
    }

    public Vec2Int getPosition() {
        return position;
    }

    public void setTargetEntity(MyEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public MyEntity getTargetEntity() {
        return targetEntity;
    }

    public void setMyEntity(MyEntity myEntity) {
        mMyEntity = myEntity;
    }

    public void setPosition(Vec2Int position) {
        this.position = position;
    }

    public void reset(int attack) {
        if (mMyEntity != null) {
            mMyEntity.attackResetHP(attack);
        } else {
            if (targetEntity != null) {
                targetEntity.attackResetHP(attack);
            }
        }
    }
}
