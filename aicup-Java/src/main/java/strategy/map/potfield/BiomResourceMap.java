package strategy.map.potfield;

import java.util.HashMap;

public class BiomResourceMap {
    HashMap<Integer, BiomResource> mBiomResourceHashMap;

    int sizeBiom = -2;

    public BiomResourceMap() {
        mBiomResourceHashMap = new HashMap<>();
        clear();
    }

    public HashMap<Integer, BiomResource> getBiomResourceHashMap() {
        return mBiomResourceHashMap;
    }

    public void addBiomResource(Field field, int sizeResource) {
        BiomResource biomResource = mBiomResourceHashMap.get(field.getDistrictResource());
        if (biomResource==null)
        {
            biomResource = new BiomResource(field.getDistrictResource());
            mBiomResourceHashMap.put(field.getDistrictResource(),biomResource);
        }

        if (biomResource.addField(field)){
            biomResource.addResource(sizeResource);
        }
    }

    public void addSizeBiom() {
        if (sizeBiom<0) sizeBiom = 0;
        else {
            this.sizeBiom++;
        }
    }

    public int getSizeBiom() {
        return sizeBiom;
    }

    public void clear() {
        mBiomResourceHashMap.clear();
        sizeBiom = -2;
    }
}
