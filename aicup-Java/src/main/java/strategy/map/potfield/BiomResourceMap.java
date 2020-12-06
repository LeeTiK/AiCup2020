package strategy.map.potfield;

import java.util.HashMap;

public class BiomResourceMap {
    HashMap<Integer,BiomResource> mBiomResourceHashMap;

    int sizeBiom =-1;

    public BiomResourceMap()
    {
        mBiomResourceHashMap = new HashMap<>();
        sizeBiom = -1;
    }

    public HashMap<Integer, BiomResource> getBiomResourceHashMap() {
        return mBiomResourceHashMap;
    }

    public void addBiomResource(Field field)
    {

    }

    public void addSizeBiom() {
        this.sizeBiom ++;
    }

    public int getSizeBiom() {
        return sizeBiom;
    }

    public void clear() {
        mBiomResourceHashMap.clear();
        sizeBiom=-1;
    }
}
