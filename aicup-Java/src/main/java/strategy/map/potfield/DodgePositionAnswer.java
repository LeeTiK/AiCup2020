package strategy.map.potfield;

import java.util.ArrayList;

public class DodgePositionAnswer {

    ArrayList<Field> mSafetyArrayList;
    ArrayList<Field> mSafetyPositionUnitArrayList;

    int maxDanger;
    int maxCounterDanger;

    boolean currentSafety;

    public DodgePositionAnswer(){

        mSafetyArrayList = new ArrayList<>(4);
        mSafetyPositionUnitArrayList = new ArrayList<>(4);
        clear();
    }

    void clear(){
        mSafetyArrayList.clear();
        mSafetyPositionUnitArrayList.clear();
        maxDanger = 0;
        maxCounterDanger = 0;

        currentSafety = false;
    }
}
