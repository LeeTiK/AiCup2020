import model.Entity;
import model.EntityProperties;
import model.EntityType;
import model.PlayerView;

import java.util.ArrayList;

public class GlobalStatistic {

    final static String TAG = "GlobalStatistic";

    ArrayList<MyPlayer> mMyPlayers;

    EntityProperties mEntityPropertiesWALL;
    EntityProperties mEntityPropertiesHOUSE;
    EntityProperties mEntityPropertiesBUILDER_BASE;
    EntityProperties mEntityPropertiesBUILDER_UNIT;
    EntityProperties mEntityPropertiesMELEE_BASE;
    EntityProperties mEntityPropertiesMELEE_UNIT;
    EntityProperties mEntityPropertiesRANGED_BASE;
    EntityProperties mEntityPropertiesRANGED_UNIT;
    EntityProperties mEntityPropertiesRESOURCE;
    EntityProperties mEntityPropertiesTURRET;

    int myID=0;

    static int currentTik = 0;

    public GlobalStatistic(){
        mMyPlayers = new ArrayList<>();
    }


    public void updateInfo(PlayerView playerView, DebugInterface debugInterface){
        myID = playerView.getMyId();

        mEntityPropertiesWALL = playerView.getEntityProperties().get(EntityType.WALL);
        mEntityPropertiesHOUSE = playerView.getEntityProperties().get(EntityType.HOUSE);
        mEntityPropertiesBUILDER_BASE = playerView.getEntityProperties().get(EntityType.BUILDER_BASE);
        mEntityPropertiesBUILDER_UNIT = playerView.getEntityProperties().get(EntityType.BUILDER_UNIT);
        mEntityPropertiesMELEE_BASE = playerView.getEntityProperties().get(EntityType.MELEE_BASE);
        mEntityPropertiesMELEE_UNIT = playerView.getEntityProperties().get(EntityType.MELEE_UNIT);
        mEntityPropertiesRANGED_BASE = playerView.getEntityProperties().get(EntityType.RANGED_BASE);
        mEntityPropertiesRANGED_UNIT = playerView.getEntityProperties().get(EntityType.RANGED_UNIT);
        mEntityPropertiesRESOURCE = playerView.getEntityProperties().get(EntityType.RESOURCE);
        mEntityPropertiesTURRET = playerView.getEntityProperties().get(EntityType.TURRET);

        updatePlayerInfo(playerView);
    }

    private void updatePlayerInfo(PlayerView playerView){

        currentTik = playerView.getCurrentTick();
        Final.DEBUG(TAG,"CurrentTik: " + currentTik);

        if (Final.debugRelease)
        {
            if (currentTik%100==0) logInfo();
        }


      //  playerView.isFogOfWar()

        // добавление и обновление информации о игроках
        for (int i=0; i<playerView.getPlayers().length; i++)
        {
            boolean check =true;
            for (int j=0; j<mMyPlayers.size(); j++)
            {
                if (playerView.getPlayers()[i].getId() == mMyPlayers.get(j).getId())
                {
                    mMyPlayers.get(j).update(playerView.getPlayers()[i]);
                    check = false;
                    break;
                }
            }

            if (check)
            {
                mMyPlayers.add(new MyPlayer(playerView.getPlayers()[i]));
            }
        }
        // начала обновлений
        for (int j=0; j<mMyPlayers.size(); j++)
        {
            mMyPlayers.get(j).startUpdate();
        }

        // добавление и обновление информации о юнитах
        for (int i=0; i<playerView.getEntities().length; i++)
        {
            if (playerView.getEntities()[i].getPlayerId() == null) continue;

            MyPlayer myPlayer = getPlayer(playerView.getEntities()[i].getPlayerId());

            if (myPlayer == null) continue;

            EStatus eStatus = myPlayer.updateEntity(playerView.getEntities()[i]);

            switch (eStatus)
            {
                case NEW_Entity:
                case DELETE_Entity: {
                    Final.DEBUG(TAG, "Player ID: " + myPlayer.getId() + " Event: " + eStatus + " Type: " + playerView.getEntities()[i].getEntityType());
                    break;
                }

                case UPDATE_Entity:
                    {
                        break;
                    }
                case ERROR:{
                    Final.DEBUG(TAG,"Player ID: " +myPlayer.getId() + " Event: " + eStatus);
                    break;
                }
            }
        }

        for (int j=0; j<mMyPlayers.size(); j++)
        {
            EntityType entityType = mMyPlayers.get(j).checkDelete();

            if (entityType!=null){
                Final.DEBUG(TAG,"Player ID: " +mMyPlayers.get(j).getId() + " Event: " + EStatus.DELETE_Entity + " Type: " + entityType);
            }

            while (entityType!=null)
            {
                entityType = mMyPlayers.get(j).checkDelete();

                if (entityType!=null){
                    Final.DEBUG(TAG,"Player ID: " +mMyPlayers.get(j).getId() + " Event: " + EStatus.DELETE_Entity + " Type: " + entityType);
                }
            }

            mMyPlayers.get(j).finishUpdate();
        }


    }

    private void logInfo() {
        for (int i=0; i<mMyPlayers.size(); i++)
        {
            MyPlayer myPlayer = mMyPlayers.get(i);
            Final.DEBUGRelease(TAG,"Player: " + myPlayer.getId() + " Size Builder: " + myPlayer.getBuildingArrayList().size() + " Size Unit: " + myPlayer.getUnitArrayList().size() + " Population: " + myPlayer.getPopulationCurrent() + "/" + myPlayer.getPopulationMax());
            Final.DEBUGRelease(TAG,"Player: " + myPlayer.getId() + " B: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size() + " M: " + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size() + " R: " + myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + " DeadB: " + myPlayer.getCountDeadBiuld() + " DeadM: " + myPlayer.getCountDeadMelee() + " DeadR: " + myPlayer.getCountDeadRange() + " DeadH " + myPlayer.getCountDeadHouse() );

        }
    }


    public MyPlayer getMyPlayer(){
        return getPlayer(myID);
    }

    public MyPlayer getPlayer(int id)
    {
        for (int i=0; i<mMyPlayers.size(); i++)
        {
            if (mMyPlayers.get(i).getId() == id) return mMyPlayers.get(i);
        }

        return null;
    }

    public ArrayList<MyPlayer> getPlayers() {
        return mMyPlayers;
    }


    public EntityProperties getEntityPropertiesBUILDER_BASE() {
        return mEntityPropertiesBUILDER_BASE;
    }

    public EntityProperties getEntityPropertiesBUILDER_UNIT() {
        return mEntityPropertiesBUILDER_UNIT;
    }

    public EntityProperties getEntityPropertiesHOUSE() {
        return mEntityPropertiesHOUSE;
    }

    public EntityProperties getEntityPropertiesMELEE_BASE() {
        return mEntityPropertiesMELEE_BASE;
    }

    public EntityProperties getEntityPropertiesMELEE_UNIT() {
        return mEntityPropertiesMELEE_UNIT;
    }

    public EntityProperties getEntityPropertiesRANGED_BASE() {
        return mEntityPropertiesRANGED_BASE;
    }

    public EntityProperties getEntityPropertiesRANGED_UNIT() {
        return mEntityPropertiesRANGED_UNIT;
    }

    public EntityProperties getEntityPropertiesRESOURCE() {
        return mEntityPropertiesRESOURCE;
    }

    public EntityProperties getEntityPropertiesTURRET() {
        return mEntityPropertiesTURRET;
    }

    public EntityProperties getEntityPropertiesWALL() {
        return mEntityPropertiesWALL;
    }

    public EntityProperties getEntityProperties(Entity entity)
    {
        return getEntityProperties(entity.getEntityType());
    }

    public EntityProperties getEntityProperties(EntityType entityType)
    {
        switch (entityType)
        {

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

    public static int getCurrentTik() {
        return currentTik;
    }

    public int getMyID() {
        return myID;
    }
}
