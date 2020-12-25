package strategy.map.potfield;

import model.Vec2Int;
import strategy.Final;
import strategy.MyEntity;

public class AttackRangeAnswer {

    Field attackPosition;
    Field defencePosition;
    Field defencePositionUnit;

    MyEntity myEntityUnit;

    int maxDanger;
    int maxCounterDanger;

    public AttackRangeAnswer(){
        clear();
    }

    public void clear(){
        attackPosition = null;
        defencePosition = null;
        defencePositionUnit = null;
        myEntityUnit = null;
        maxDanger = 0;
        maxCounterDanger = 0;
    }

    public int getMaxDanger() {
        return maxDanger;
    }

    public int getMaxCounterDanger() {
        return maxCounterDanger;
    }

    public Field getAttackPosition() {
        return attackPosition;
    }

    public Field getDefencePosition() {
        return defencePosition;
    }

    public Field getDefencePositionUnit() {
        return defencePositionUnit;
    }

    public MyEntity getMyEntityUnit() {
        return myEntityUnit;
    }
}



