import model.*;

import java.util.ArrayList;

public class GlobalMap {
    // класс карты
    Entity[][] map = null;

    Entity[] allEntity;

    public GlobalMap(){

    }

    public void update(PlayerView playerView, GlobalStatistic globalStatistic){
        if (map == null)
        {
            map = new Entity[playerView.getMapSize()][playerView.getMapSize()];
        }

        updateMap(playerView,globalStatistic);
    }

    private void updateMap(PlayerView playerView, GlobalStatistic globalStatistic) {
        clearMap();

        allEntity = playerView.getEntities();

        for (int i=0; i<playerView.getEntities().length; i++)
        {
            Entity entity = playerView.getEntities()[i];
            if (entity.getPosition().getX()>=0 && entity.getPosition().getX() <80 &&
                    entity.getPosition().getY()>=0 && entity.getPosition().getY() <80
            ) {
                map[entity.getPosition().getX()][entity.getPosition().getY()] = entity;

                switch (entity.getEntityType())
                {

                    case HOUSE:
                    case BUILDER_BASE:
                    case MELEE_BASE:
                    case RANGED_BASE:
                    case TURRET:
                        EntityProperties entityProperties = globalStatistic.getEntityProperties(entity.getEntityType());
                        for (int j=0; j<entityProperties.getSize(); j++)
                        {
                            for (int k=0; k<entityProperties.getSize(); k++)
                            {
                                map[entity.getPosition().getX()+j][entity.getPosition().getY()+k] = entity;
                            }
                        }
                        break;
                }

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

    public ArrayList<Vec2Int> getCoordAround(Vec2Int start, int size, boolean checkEmpty)
    {
        ArrayList<Vec2Int> arrayList = new ArrayList<>();

        Vec2Int vec2Int = start;

        for (int x=size-1; x>=0; x--)
        {
            if (checkEmpty) {
                if (checkEmpty(vec2Int.add(x, size))) {
                    arrayList.add(vec2Int.add(x, size));
                }
            }
            else {
                arrayList.add(vec2Int.add(x, size));
            }
        }


        for (int y=size-1; y>=0; y--)
        {
            if (checkEmpty) {
                if (checkEmpty(vec2Int.add(size,y))) {
                    arrayList.add(vec2Int.add(size,y));
                }
            }
            else {
                arrayList.add(vec2Int.add(size,y));
            }
        }

        for (int x=size-1; x>=0; x--)
        {
            if (checkEmpty) {
                if (checkEmpty(vec2Int.add(x,-1))) {
                    arrayList.add(vec2Int.add(x,-1));
                }
            }
            else {
                arrayList.add(vec2Int.add(x,-1));
            }
        }

        for (int y=size-1; y>=0; y--)
        {
            if (checkEmpty) {
                if (checkEmpty(vec2Int.add(-1,y))) {
                    arrayList.add(vec2Int.add(-1,y));
                }
            }
            else {
                arrayList.add(vec2Int.add(-1,y));
            }
        }

        return arrayList;
    }


    public Vec2Int getNearest(Vec2Int position, EntityType entityType)
    {
        double minDis = 0xFFFF;
        Vec2Int current = new Vec2Int(0,0);
        for (int i=0; i<allEntity.length; i++)
        {
            if (allEntity[i].getEntityType()!=entityType) continue;
            double dis = position.distance(allEntity[i].getPosition());
            if (dis<minDis)
            {
                current = allEntity[i].getPosition();
                minDis = dis;
            }
        }
        return current;
    }

    public Vec2Int getNearestCoord(Vec2Int position, ArrayList<Vec2Int> arrayList)
    {
        double minDis = 0xFFFF;
        Vec2Int current = null;
        for (int i=0; i<arrayList.size(); i++)
        {
            double dis = position.distance(arrayList.get(i));
            if (dis<minDis)
            {
                current = arrayList.get(i);
                minDis = dis;
            }
        }
        return current;
    }

    public Vec2Int getPositionBuildUnitPriorite(GlobalStatistic globalStatistic, Entity building)
    {
        EntityProperties entityProperties = globalStatistic.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition().copy();
        Vec2Int vec2IntCurrent = null;
        double minDis = 0xFFFF;

        for (int x=entityProperties.getSize()-1; x>=0; x--)
        {
            if (checkEmpty(vec2Int.add(x,entityProperties.getSize())))
            {
               Vec2Int vec2Int1 = vec2Int.add(x,entityProperties.getSize());
               Vec2Int vecReseurse = getNearest(vec2Int1,EntityType.RESOURCE);
                double dis = vecReseurse.distance(vec2Int1);
                if (dis<minDis)
                {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }


        for (int y=entityProperties.getSize()-1; y>=0; y--)
        {
            if (checkEmpty(vec2Int.add(entityProperties.getSize(), y)))
            {
                Vec2Int vec2Int1 =  vec2Int.add(entityProperties.getSize(),y);
                Vec2Int vecReseurse = getNearest(vec2Int1,EntityType.RESOURCE);
                double dis = vecReseurse.distance(vec2Int1);
                if (dis<minDis)
                {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }

        for (int x=entityProperties.getSize()-1; x>=0; x--)
        {
            if (checkEmpty(vec2Int.add(x,-1)))
            {
                Vec2Int vec2Int1 = vec2Int.add(x,-1);
                Vec2Int vecReseurse = getNearest(vec2Int1,EntityType.RESOURCE);
                double dis = vecReseurse.distance(vec2Int1);
                if (dis<minDis)
                {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }

        for (int y=entityProperties.getSize()-1; y>=0; y--)
        {
            if (checkEmpty(vec2Int.add(-1, y)))
            {
                Vec2Int vec2Int1 = vec2Int.add(-1,y);
                Vec2Int vecReseurse = getNearest(vec2Int1,EntityType.RESOURCE);
                double dis = vecReseurse.distance(vec2Int1);
                if (dis<minDis)
                {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }

        if (vec2IntCurrent==null) return new Vec2Int(0,0);

        return vec2IntCurrent;

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

    // функция показывает точку для создания здания
    public Vec2Int getPositionBuildHouse(EntityProperties entityProperties)
    {
        int size = entityProperties.getSize();

        Vec2Int vec2IntRight = new Vec2Int(0,0);
        Vec2Int vec2IntLeft = new Vec2Int(4,0);
        int iter = 8;
        while (!checkEmptyAndAround(vec2IntLeft,entityProperties) && !checkEmptyAndAround(vec2IntRight,entityProperties))
        {
            vec2IntRight = vec2IntRight.add(0,size);
            vec2IntLeft = vec2IntLeft.add(size,0);
            iter--;
            if (iter<0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft,entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight,entityProperties)) return vec2IntRight;

        vec2IntRight = new Vec2Int(11,4);
        vec2IntLeft = new Vec2Int(4,11);


        iter = 1;
        while (!checkEmptyAndAround(vec2IntLeft,entityProperties) && !checkEmptyAndAround(vec2IntRight,entityProperties))
        {
            vec2IntRight = vec2IntRight.add(0,size);
            vec2IntLeft = vec2IntLeft.add(size,0);
            iter--;
            if (iter<0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft,entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight,entityProperties)) return vec2IntRight;

        vec2IntRight = new Vec2Int(21,4);
        vec2IntLeft = new Vec2Int(4,21);


        iter = 3;
        while (!checkEmptyAndAround(vec2IntLeft,entityProperties) && !checkEmptyAndAround(vec2IntRight,entityProperties))
        {
            vec2IntRight = vec2IntRight.add(0,size+1);
            vec2IntLeft = vec2IntLeft.add(size+1,0);
            iter--;
            if (iter<0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft,entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight,entityProperties)) return vec2IntRight;

        vec2IntRight = new Vec2Int(25,4);
        vec2IntLeft = new Vec2Int(4,25);


        iter = 3;
        while (!checkEmptyAndAround(vec2IntLeft,entityProperties) && !checkEmptyAndAround(vec2IntRight,entityProperties))
        {
            vec2IntRight = vec2IntRight.add(0,size+1);
            vec2IntLeft = vec2IntLeft.add(size+1,0);
            iter--;
            if (iter<0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft,entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight,entityProperties)) return vec2IntRight;

        vec2IntRight = new Vec2Int(29,4);
        vec2IntLeft = new Vec2Int(4,29);


        iter = 5;
        while (!checkEmptyAndAround(vec2IntLeft,entityProperties) && !checkEmptyAndAround(vec2IntRight,entityProperties))
        {
            vec2IntRight = vec2IntRight.add(0,size+1);
            vec2IntLeft = vec2IntLeft.add(size+1,0);
            iter--;
            if (iter<0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft,entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight,entityProperties)) return vec2IntRight;

        vec2IntRight = new Vec2Int(33,4);
        vec2IntLeft = new Vec2Int(4,33);


        iter = 6;
        while (!checkEmptyAndAround(vec2IntLeft,entityProperties) && !checkEmptyAndAround(vec2IntRight,entityProperties))
        {
            vec2IntRight = vec2IntRight.add(0,size+1);
            vec2IntLeft = vec2IntLeft.add(size+1,0);
            iter--;
            if (iter<0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft,entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight,entityProperties)) return vec2IntRight;

        return null;
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

        //return null;

    }

    // функция показывает наилушую точку для создания здания юнитом
    public Vec2Int getMinPositionBuilding(Vec2Int positionUnit, Vec2Int positionBuild,EntityProperties entityProperties)
    {
        ArrayList arrayList = getCoordAround(positionBuild,entityProperties.getSize(),true);

        Vec2Int vec2Int = getNearestCoord(positionUnit,arrayList);

        return vec2Int;
    }

    public boolean checkEmptyAndAround(Vec2Int vec2Int, EntityProperties entityProperties)
    {
        if (!checkEmpty(vec2Int,entityProperties.getSize())) return false;

        ArrayList arrayList = getCoordAround(vec2Int,entityProperties.getSize(),true);
        if (arrayList.size()==0) return false;
        else return true;
    }

    public boolean checkEmpty(Vec2Int vec2Int)
    {
       return checkEmpty(vec2Int,1,1);
    }

    public boolean checkEmpty(Vec2Int vec2Int,int size)
    {
        return checkEmpty(vec2Int,size,size);
    }

    public boolean checkEmpty(Vec2Int vec2Int,int width, int height)
    {
        if (vec2Int.getX()<0 || vec2Int.getX()>=80 || vec2Int.getY()<0 || vec2Int.getY()>=80) return false;
        for (int x=0; x<width; x++)
        {
            for (int y=0; y<height; y++)
            {
                if (map[vec2Int.getX() + x][vec2Int.getY() + y].getEntityType()!=EntityType.Empty) return false;
            }
        }

        return true;
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


    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
      //  debugInterface.
        if (map!=null)
        {
            for (int i=0; i<map.length; i++)
            {
                for (int j=0; j<map[i].length; j++)
                {
                    if (map[i][j].getEntityType()!=EntityType.Empty){
                        FinalGraphic.sendSquare(debugInterface,new Vec2Int(i,j),1, FinalGraphic.COLOR_BLACK);
                    }
                }
            }
        }
    }
}
