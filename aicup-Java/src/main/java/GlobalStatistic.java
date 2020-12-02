import model.Entity;
import model.EntityProperties;
import model.EntityType;
import model.PlayerView;

import java.util.ArrayList;

public class GlobalStatistic {

    final static String TAG = "GlobalStatistic";

    ArrayList<MyPlayer> mMyPlayers;
    ArrayList<MyEntity> mMyEntityArrayList;


    public GlobalStatistic(){
        mMyPlayers = new ArrayList<>();
        mMyEntityArrayList = new ArrayList<>();
    }


    public void updateInfo(PlayerView playerView, DebugInterface debugInterface){
        if (playerView.getCurrentTick()==0)
        {
            initConstant(playerView);
        }

        FinalConstant.currentTik = playerView.getCurrentTick();

        updatePlayerInfo(playerView);
    }

    void initConstant(PlayerView playerView){
        FinalConstant.mapSize = playerView.getMapSize();

        FinalConstant.myID = playerView.getMyId();

        FinalConstant.mEntityPropertiesWALL = playerView.getEntityProperties().get(EntityType.WALL);
        FinalConstant.mEntityPropertiesHOUSE = playerView.getEntityProperties().get(EntityType.HOUSE);
        FinalConstant.mEntityPropertiesBUILDER_BASE = playerView.getEntityProperties().get(EntityType.BUILDER_BASE);
        FinalConstant.mEntityPropertiesBUILDER_UNIT = playerView.getEntityProperties().get(EntityType.BUILDER_UNIT);
        FinalConstant.mEntityPropertiesMELEE_BASE = playerView.getEntityProperties().get(EntityType.MELEE_BASE);
        FinalConstant.mEntityPropertiesMELEE_UNIT = playerView.getEntityProperties().get(EntityType.MELEE_UNIT);
        FinalConstant.mEntityPropertiesRANGED_BASE = playerView.getEntityProperties().get(EntityType.RANGED_BASE);
        FinalConstant.mEntityPropertiesRANGED_UNIT = playerView.getEntityProperties().get(EntityType.RANGED_UNIT);
        FinalConstant.mEntityPropertiesRESOURCE = playerView.getEntityProperties().get(EntityType.RESOURCE);
        FinalConstant.mEntityPropertiesTURRET = playerView.getEntityProperties().get(EntityType.TURRET);
    }

    private void updatePlayerInfo(PlayerView playerView){

        Final.DEBUG(TAG,"CurrentTik: " + FinalConstant.getCurrentTik());

        if (Final.debugRelease)
        {
            if (FinalConstant.getCurrentTik()%100==0) {
                Final.DEBUGRelease(TAG,"CurrentTik: " + FinalConstant.getCurrentTik());
                logInfo();
            }
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
        mMyEntityArrayList.clear();

        // начала обновлений
        for (int j=0; j<mMyPlayers.size(); j++)
        {
            mMyPlayers.get(j).startUpdate();
        }


        // добавление и обновление информации о юнитах
        for (int i=0; i<playerView.getEntities().length; i++)
        {
            mMyEntityArrayList.add(new MyEntity(playerView.getEntities()[i]));

            if (playerView.getEntities()[i].getPlayerId() == null) continue;

            MyPlayer myPlayer = getPlayer(playerView.getEntities()[i].getPlayerId());

            if (myPlayer == null) continue;

            EStatus eStatus = myPlayer.updateEntity(mMyEntityArrayList.get(mMyEntityArrayList.size()-1));

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

        //playerView.getPlayers()[i].getResource()

    }

    private void logInfo() {
        for (int i=0; i<mMyPlayers.size(); i++)
        {
            MyPlayer myPlayer = mMyPlayers.get(i);
            Final.DEBUGRelease(TAG,myPlayer.toString());
         //   Final.DEBUGRelease(TAG,"Player: " + myPlayer.getId() + " B: " + myPlayer.getEntityArrayList(EntityType.BUILDER_UNIT).size() + " M: " + myPlayer.getEntityArrayList(EntityType.MELEE_UNIT).size() + " R: " + myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() + " DeadB: " + myPlayer.getCountDeadBiuld() + " DeadM: " + myPlayer.getCountDeadMelee() + " DeadR: " + myPlayer.getCountDeadRange() + " DeadH " + myPlayer.getCountDeadHouse() );

        }
    }


    public MyPlayer getMyPlayer(){
        return getPlayer(FinalConstant.getMyID());
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


    public ArrayList<MyEntity> getMyEntityArrayList() {
        return mMyEntityArrayList;
    }
}
