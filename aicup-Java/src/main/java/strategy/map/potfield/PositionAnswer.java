package strategy.map.potfield;

import model.Vec2Int;
import strategy.MyEntity;

public class PositionAnswer {

    Vec2Int position;
    MyEntity mMyEntity;

    public PositionAnswer(){
        this.position = null;
        mMyEntity = null;
    }

    public PositionAnswer(Vec2Int position){
        this.position = position;
        mMyEntity = null;
    }

    public PositionAnswer(Vec2Int position,MyEntity entity){
        this.position = position;
        mMyEntity = entity;
    }

    public void  clear(){
        position = null;
        mMyEntity = null;
    }

    public PositionAnswer init(Vec2Int position){
        this.position = position;
        return this;
    }

    public PositionAnswer init(Vec2Int position,MyEntity entity){
        this.position = position;
        this.mMyEntity = entity;
        return this;
    }


    public MyEntity getMyEntity() {
        return mMyEntity;
    }

    public Vec2Int getPosition() {
        return position;
    }
}
