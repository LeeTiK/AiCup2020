package strategy.map.potfield;

import java.util.ArrayList;

public class DodgePositionAnswer {

    ArrayList<Field> mSafetyArrayList;
    ArrayList<Field> mSafetyPositionUnitArrayList;
    ArrayList<Field> mSafetyCounterArrayList;

    int maxDanger;
    int maxCounterDanger;

    boolean currentSafety;

    public DodgePositionAnswer(){

        mSafetyArrayList = new ArrayList<>(4);
        mSafetyPositionUnitArrayList = new ArrayList<>(4);
        mSafetyCounterArrayList = new ArrayList<>(4);
        clear();
    }

    void clear(){
        mSafetyArrayList.clear();
        mSafetyPositionUnitArrayList.clear();
        mSafetyCounterArrayList.clear();
        maxDanger = 0;
        maxCounterDanger = 0;

        currentSafety = false;
    }

    public ArrayList<Field> getSafetyArrayList() {
        return mSafetyArrayList;
    }

    public ArrayList<Field> getSafetyPositionUnitArrayList() {
        return mSafetyPositionUnitArrayList;
    }

    public int getMaxCounterDanger() {
        return maxCounterDanger;
    }

    public int getMaxDanger() {
        return maxDanger;
    }

    public ArrayList<Field> getSafetyCounterArrayList() {
        return mSafetyCounterArrayList;
    }
}
