package strategy.map.potfield;

import model.Vec2Int;
import strategy.Final;
import strategy.MyEntity;

import java.util.ArrayList;

public class AttackRangeAnswer {

    Field attackPosition;
    Field defencePosition;
    Field defencePositionUnit;


    ArrayList<Field> attackPositionArrayList;
    ArrayList<Field> defencePositionArrayList;
    ArrayList<Field> defencePositionUnitArrayList;

    MyEntity myEntityUnit;

    int maxDanger;
    int maxCounterDanger;

    public AttackRangeAnswer(){
        attackPositionArrayList = new ArrayList<>(2);
        defencePositionArrayList = new ArrayList<>(2);
        defencePositionUnitArrayList = new ArrayList<>(2);
        clear();
    }

    public void clear(){
        attackPositionArrayList.clear();
        defencePositionArrayList.clear();
        defencePositionUnitArrayList.clear();
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

    public MyEntity getMyEntityUnit() {
        return myEntityUnit;
    }

    public Field getDefencePosition() {
        return defencePosition;
    }

    public Field getAttackPosition() {
        return attackPosition;
    }

    public ArrayList<Field> getAttackPositionArrayList() {
        return attackPositionArrayList;
    }

    public ArrayList<Field> getDefencePositionArrayList() {
        return defencePositionArrayList;
    }

    public ArrayList<Field> getDefencePositionUnitArrayList() {
        return defencePositionUnitArrayList;
    }

    public Field getDefencePositionUnit() {
        return defencePositionUnit;
    }
}



