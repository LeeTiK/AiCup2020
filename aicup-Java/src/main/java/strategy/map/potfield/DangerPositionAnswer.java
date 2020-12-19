package strategy.map.potfield;


public class DangerPositionAnswer {

    Field current = null;
    Field currentNoDanger = null;
    Field currentNoDangerUnitPosition = null;
    Field currentContour = null;
    Field currentSafety = null;
    Field currentSafetyUnitPosition = null;
    Field currentOnlySafety = null;
    Field currentSafetyOnlyUnitPosition = null;

    int minDanger;
    int minCounterDanger;
    int minCounterDangerUnitPosition;
    int maxCounterOnlyUnitDanger;
    int maxSafety;
    int maxCounterDanger;
    int maxEmptyPositionNoDager;
    int maxEmptyPositionSafety;

    boolean currentCounterDanger;


    public DangerPositionAnswer(){
        clear();
    }

    void clear(){
        current = null;
        currentNoDanger = null;
        currentNoDangerUnitPosition = null;
        currentContour = null;
        currentSafety = null;
        currentSafetyUnitPosition = null;
        currentOnlySafety = null;
        currentSafetyOnlyUnitPosition = null;

        minDanger = 0xFFFF;
        minCounterDanger = 0xFFFF;
        minCounterDangerUnitPosition = 0xFFFF;
        maxCounterOnlyUnitDanger = 0;
        maxSafety = 0;
        maxCounterDanger = 0;
        maxEmptyPositionNoDager = 0;
        maxEmptyPositionSafety = 0;

        currentCounterDanger = false;
    }

    /*@Override
    public String toString(){
       // return current.toString() + " "
    }*/
}
