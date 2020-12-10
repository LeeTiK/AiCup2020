package strategy;

import model.Entity;
import model.EntityType;
import model.PlayerView;

import java.util.ArrayList;

public class GlobalStatistic {

    final static String TAG = "strategy.GlobalStatistic";

    ArrayList<MyPlayer> mMyPlayers;
    ArrayList<MyEntity> mMyEntityArrayList;

    MyPlayer left;
    MyPlayer right;

    public GlobalStatistic() {
        mMyPlayers = new ArrayList<>();
        mMyEntityArrayList = new ArrayList<>();
    }


    public void updateInfo(PlayerView playerView, GlobalManager globalManager) {
        if (playerView.getCurrentTick() == 0) {
            initConstant(playerView);
        }

        FinalConstant.currentTik = playerView.getCurrentTick();

        updatePlayerInfo(playerView, globalManager);
    }

    void initConstant(PlayerView playerView) {
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

    private void updatePlayerInfo(PlayerView playerView, GlobalManager globalManager) {
        //  playerView.isFogOfWar()

        // добавление и обновление информации о игроках
        for (int i = 0; i < playerView.getPlayers().length; i++) {
            boolean check = true;
            for (int j = 0; j < mMyPlayers.size(); j++) {
                if (playerView.getPlayers()[i].getId() == mMyPlayers.get(j).getId()) {
                    mMyPlayers.get(j).update(playerView.getPlayers()[i]);
                    check = false;
                    break;
                }
            }

            if (check) {
                mMyPlayers.add(new MyPlayer(playerView.getPlayers()[i]));
            }
        }


        // начала обновлений
        for (int j = 0; j < mMyPlayers.size(); j++) {
            mMyPlayers.get(j).startUpdate();
        }

        for (int i = 0; i < mMyEntityArrayList.size(); i++) {
            mMyEntityArrayList.get(i).setUpdate(false);
        }


        // добавление и обновление информации о юнитах
        for (int i = 0; i < playerView.getEntities().length; i++) {

            MyEntity entity = addGlobalEntityList(playerView.getEntities()[i]);
            //    mMyEntityArrayList.add(new strategy.MyEntity(playerView.getEntities()[i]));

            if (entity.getPlayerId() == null) continue;

            MyPlayer myPlayer = getPlayer(entity.getPlayerId());

            if (myPlayer == null) continue;

            EStatus eStatus = myPlayer.updateEntity(entity);

            switch (eStatus) {
                case NEW_Entity:
                case DELETE_Entity: {
                    Final.DEBUG(TAG, "Player ID: " + myPlayer.getId() + " Event: " + eStatus + " Type: " + playerView.getEntities()[i].getEntityType());
                    break;
                }

                case UPDATE_Entity: {
                    break;
                }
                case ERROR: {
                    Final.DEBUG(TAG, "Player ID: " + myPlayer.getId() + " Event: " + eStatus);
                    break;
                }
            }
        }

        for (int j = 0; j < mMyPlayers.size(); j++) {
            MyEntity entity = GlobalMap.empty;

            while (entity != null) {
                entity = mMyPlayers.get(j).checkDelete();

                if (entity != null) {
                    Final.DEBUG(TAG, "Player ID: " + mMyPlayers.get(j).getId() + " Event: " + EStatus.DELETE_Entity + " Type: " + entity.getEntityType() );
                    if (entity.getEntityType()==EntityType.RANGED_UNIT && entity.getHealth()>5)
                    {
                        Final.DEBUG(TAG, "BAD POSITION " + FinalConstant.getCurrentTik() + " Player ID: " + mMyPlayers.get(j).getId() + " ID: " + entity.getId() );
                    }

                    if (entity.getEntityType()==EntityType.RANGED_UNIT && entity.getHealth()>5)
                    {
                        Final.DEBUG(TAG, "DEATH " + FinalConstant.getCurrentTik() + " Player ID: " + mMyPlayers.get(j).getId() + " ID: " + entity.getId() );
                    }
                }
            }

            mMyPlayers.get(j).finishUpdate();
        }

        finishGlobalList();


        if (mMyPlayers.size() > 2) {
            int myID = FinalConstant.getMyID();

            switch (myID)
            {
                case 1:
                    left = getPlayer(3);
                    right = getPlayer(4);

                    if (left.getPopulationCurrent() == 0 && left.getBuildingArrayList().size() == 0) {
                        left = getPlayer(2);
                    }

                    if (right.getPopulationCurrent() == 0 && right.getBuildingArrayList().size() == 0) {
                        right = getPlayer(2);
                    }
                    break;
                case 2:
                    left = getPlayer(4);
                    right = getPlayer(3);

                    if (left.getPopulationCurrent() == 0 && left.getBuildingArrayList().size() == 0) {
                        left = getPlayer(1);
                    }

                    if (right.getPopulationCurrent() == 0 && right.getBuildingArrayList().size() == 0) {
                        right = getPlayer(1);
                    }
                    break;
                case 3:
                    left = getPlayer(2);
                    right = getPlayer(1);

                    if (left.getPopulationCurrent() == 0 && left.getBuildingArrayList().size() == 0) {
                        left = getPlayer(4);
                    }

                    if (right.getPopulationCurrent() == 0 && right.getBuildingArrayList().size() == 0) {
                        right = getPlayer(4);
                    }
                    break;
                case 4:
                    left = getPlayer(1);
                    right = getPlayer(2);

                    if (left.getPopulationCurrent() == 0 && left.getBuildingArrayList().size() == 0) {
                        left = getPlayer(3);
                    }

                    if (right.getPopulationCurrent() == 0 && right.getBuildingArrayList().size() == 0) {
                        right = getPlayer(3);
                    }
                    break;
            }

        } else {
            int myID = FinalConstant.getMyID();

            switch (myID) {
                case 1:
                    left = getPlayer(2);
                    right = getPlayer(2);
                    break;
                case 2:
                    left = getPlayer(1);
                    right = getPlayer(1);
                    break;
            }
        }
        //playerView.getPlayers()[i].getResource()

    }

    private void finishGlobalList() {
        for (int i = 0; i < mMyEntityArrayList.size(); i++) {
            if (!mMyEntityArrayList.get(i).isUpdate()) {
                mMyEntityArrayList.remove(i);
            }
        }
    }

    private MyEntity addGlobalEntityList(Entity entity) {

        for (int i = 0; i < mMyEntityArrayList.size(); i++) {
            if (mMyEntityArrayList.get(i).getId() == entity.getId()) {
                mMyEntityArrayList.get(i).update(entity);
                return mMyEntityArrayList.get(i);
            }
        }

        mMyEntityArrayList.add(new MyEntity(entity));

        return mMyEntityArrayList.get(mMyEntityArrayList.size() - 1);
    }

    public MyPlayer getMyPlayer() {
        return getPlayer(FinalConstant.getMyID());
    }

    public MyPlayer getPlayer(int id) {
        for (int i = 0; i < mMyPlayers.size(); i++) {
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

    public MyPlayer getLeftPlyer() {
        return left;
    }

    public MyPlayer getRightPlyer() {
        return right;
    }

}
