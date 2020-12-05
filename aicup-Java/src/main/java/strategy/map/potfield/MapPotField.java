package strategy.map.potfield;

import strategy.GlobalManager;

public class MapPotField {

    Field[][] mMapPotField;


    public MapPotField(int size)
    {
        mMapPotField = new Field[size][size];
    }


    public void update(GlobalManager globalManager){

    }

    public Field[][] getMapPotField() {
        return mMapPotField;
    }
}
