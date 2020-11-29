import model.*;

import java.util.ArrayList;

public class GlobalMap {
    // класс карты
    Entity[][] map = null;

    public GlobalMap(){

    }

    public void update(PlayerView playerView){
        if (map == null)
        {
            map = new Entity[playerView.getMapSize()][playerView.getMapSize()];
        }

        updateMap(playerView);
    }

    private void updateMap(PlayerView playerView) {
        clearMap();

        for (int i=0; i<playerView.getEntities().length; i++)
        {
            Entity entity = playerView.getEntities()[i];
            if (entity.getPosition().getX()>=0 && entity.getPosition().getX() <80 &&
                    entity.getPosition().getY()>=0 && entity.getPosition().getY() <80
            ) {
                map[entity.getPosition().getX()][entity.getPosition().getY()] = entity;
            }
          //  playerView.getEntityProperties().get()
        }
    }

    private void clearMap(){
        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<map[i].length; j++)
            {
                map[i][j] = new Entity(-1,-1,EntityType.Empty,null,0,false);
            }
        }
    }

    public Vec2Int getPositionBuildUnit(GlobalStatistic globalStatistic, Entity building)
    {
        EntityProperties entityProperties = globalStatistic.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition().copy();

        for (int x=entityProperties.getSize()-1; x>=0; x--)
        {
            if (checkEmpty(vec2Int.add(x,entityProperties.getSize())))
            {
               return vec2Int.add(x,entityProperties.getSize());
            }
        }


        for (int y=entityProperties.getSize()-1; y>=0; y--)
        {
            if (checkEmpty(vec2Int.add(entityProperties.getSize(), y)))
            {
               return vec2Int.add(entityProperties.getSize(),y);
            }
        }

        for (int x=entityProperties.getSize()-1; x>=0; x--)
        {
            if (checkEmpty(vec2Int.add(x,-1)))
            {
                return vec2Int.add(x,-1);
            }
        }

        for (int y=entityProperties.getSize()-1; y>=0; y--)
        {
            if (checkEmpty(vec2Int.add(-1, y)))
            {
                   return vec2Int.add(-1,y);
            }
        }

        return vec2Int;

    }

    public Vec2Int getPositionBuildHouse(GlobalStatistic globalStatistic)
    {
      /*  EntityProperties entityProperties = globalStatistic.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition().copy();

        for (int x=entityProperties.getSize()-1; x>=0; x--)
        {
            if (checkEmpty(vec2Int.add(x,entityProperties.getSize())))
            {
                return vec2Int.add(x,entityProperties.getSize());
            }
        }

        for (int y=entityProperties.getSize()-1; y>=0; y--)
        {
            if (checkEmpty(vec2Int.add(entityProperties.getSize(), y)))
            {
                return vec2Int.add(entityProperties.getSize(),y);
            }
        }*/

        return null;

    }

    public boolean checkEmpty(Vec2Int vec2Int)
    {
        if (vec2Int.getX()<0 || vec2Int.getX()>=80 || vec2Int.getY()<=0 || vec2Int.getY()>=80) return false;
        return map[vec2Int.getX()][vec2Int.getY()].getEntityType()==EntityType.Empty;
    }

    public Vec2Int getNearestPlayer(ArrayList<MyPlayer> arrayList){
        return new Vec2Int(0,0);
    }

    public Vec2Int getNearestPlayer(int myID){
        Vec2Int vec2Int = new Vec2Int(0,0);
        double minDis = 0xFFFFF;
        Vec2Int currentPos = vec2Int;

        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<map[i].length; j++)
            {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId()==myID) continue;

                double dis = map[i][j].getPosition().distance(vec2Int);

                if (dis<minDis)
                {
                    minDis = dis;
                    currentPos = map[i][j].getPosition();
                }
            }
        }
        return currentPos;
    }

    public Vec2Int getNearestPlayer(Vec2Int vec2Int, int myID){
        double minDis = 0xFFFFF;
        Vec2Int currentPos = vec2Int;

        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<map[i].length; j++)
            {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId()==myID) continue;

                double dis = map[i][j].getPosition().distance(vec2Int);

                if (dis<minDis)
                {
                    minDis = dis;
                    currentPos = map[i][j].getPosition();
                }
            }
        }
        return currentPos;
    }


}
