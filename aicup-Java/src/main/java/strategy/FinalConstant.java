package strategy;

import model.Entity;
import model.EntityProperties;
import model.EntityType;

public class FinalConstant {

    public static boolean fogOfWar;
    static EntityProperties mEntityPropertiesWALL;
    static EntityProperties mEntityPropertiesHOUSE;
    static EntityProperties mEntityPropertiesBUILDER_BASE;
    static EntityProperties mEntityPropertiesBUILDER_UNIT;
    static EntityProperties mEntityPropertiesMELEE_BASE;
    static EntityProperties mEntityPropertiesMELEE_UNIT;
    static EntityProperties mEntityPropertiesRANGED_BASE;
    static EntityProperties mEntityPropertiesRANGED_UNIT;
    static EntityProperties mEntityPropertiesRESOURCE;
    static EntityProperties mEntityPropertiesTURRET;

    static public int myID = 0;
    static public int currentTik = 0;

    static public int mapSize;

    public static int getMyID() {
        return myID;
    }

    public static int getCurrentTik() {
        return currentTik;
    }

    public static boolean isFogOfWar(){
        return fogOfWar;
    }


    public static void initConstantMap() {

    }


    static public EntityProperties getEntityPropertiesBUILDER_BASE() {
        return mEntityPropertiesBUILDER_BASE;
    }

    static public EntityProperties getEntityPropertiesBUILDER_UNIT() {
        return mEntityPropertiesBUILDER_UNIT;
    }

    static public EntityProperties getEntityPropertiesHOUSE() {
        return mEntityPropertiesHOUSE;
    }

    static public EntityProperties getEntityPropertiesMELEE_BASE() {
        return mEntityPropertiesMELEE_BASE;
    }

    static public EntityProperties getEntityPropertiesMELEE_UNIT() {
        return mEntityPropertiesMELEE_UNIT;
    }

    static public EntityProperties getEntityPropertiesRANGED_BASE() {
        return mEntityPropertiesRANGED_BASE;
    }

    static public EntityProperties getEntityPropertiesRANGED_UNIT() {
        return mEntityPropertiesRANGED_UNIT;
    }

    static public EntityProperties getEntityPropertiesRESOURCE() {
        return mEntityPropertiesRESOURCE;
    }

    static public EntityProperties getEntityPropertiesTURRET() {
        return mEntityPropertiesTURRET;
    }

    static public EntityProperties getEntityPropertiesWALL() {
        return mEntityPropertiesWALL;
    }

    static public EntityProperties getEntityProperties(Entity entity) {
        return getEntityProperties(entity.getEntityType());
    }

    static public EntityProperties getEntityProperties(EntityType entityType) {
        switch (entityType) {

            case WALL:
                return getEntityPropertiesWALL();
            case HOUSE:
                return getEntityPropertiesHOUSE();
            case BUILDER_BASE:
                return getEntityPropertiesBUILDER_BASE();
            case BUILDER_UNIT:
                return getEntityPropertiesRANGED_UNIT();
            case MELEE_BASE:
                return getEntityPropertiesMELEE_BASE();
            case MELEE_UNIT:
                return getEntityPropertiesMELEE_UNIT();
            case RANGED_BASE:
                return getEntityPropertiesRANGED_BASE();
            case RANGED_UNIT:
                return getEntityPropertiesRANGED_UNIT();
            case RESOURCE:
                return getEntityPropertiesRESOURCE();
            case TURRET:
                return getEntityPropertiesTURRET();
            case Empty:
                break;
        }
        return null;
    }

    public static int getMapSize() {
        return mapSize;
    }
}

