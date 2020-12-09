package strategy.map.potfield;

import strategy.Final;

import java.util.ArrayList;

public class BiomResource {

    ArrayList<Field> mFieldArrayList;

    int districtResource;
    int resourceSize;

    public BiomResource(int districtResource) {
        this.districtResource = districtResource;
        mFieldArrayList = new ArrayList<>();
    }

    public ArrayList<Field> getFieldArrayList() {
        return mFieldArrayList;
    }

    public int getDistrictResource() {
        return districtResource;
    }

    public boolean addField(Field field) {
        for (int i = 0; i < mFieldArrayList.size(); i++) {
            if (mFieldArrayList.get(i).equals(field)) {
                Final.DEBUG("BiomResource", "ОШИБКА ПОВТОРА");
                return false;
            }
        }

        mFieldArrayList.add(field);

        return true;
    }

    public void addResource(int resource) {
        resourceSize += resource;
    }

    public int getResourceSize() {
        return resourceSize;
    }
}
