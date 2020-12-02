import model.*;

import java.util.ArrayList;

public class GlobalMap {
    // класс карты
    MyEntity[][] map = null;

    ArrayList<MyEntity> allEntity;

    AreaPlayer mAreaPlayer;

    public GlobalMap(){

    }

    public void update(GlobalStatistic globalStatistic){
        if (map == null)
        {
            map = new MyEntity[FinalConstant.getMapSize()][FinalConstant.getMapSize()];

        }

        updateMap(globalStatistic);

        mAreaPlayer= getPlayerArea(FinalConstant.getMyID());
    }

    public AreaPlayer getPlayerArea(int playerID) {

        int xMax = 0;
        int yMax = 0;
        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<=i; j++)
            {
                Entity entity = map[i][j];
                if (entity.getEntityType()!=EntityType.RANGED_UNIT &&
                        entity.getEntityType()!=EntityType.MELEE_UNIT &&
                        entity.getEntityType()!=EntityType.RESOURCE &&
                        entity.getEntityType()!=EntityType.Empty
                       )
                {
                    if (entity.getPlayerId()==playerID)
                    {
                        if (xMax<i)
                        {
                            xMax = i;
                        }

                        if (yMax<j)
                        {
                            yMax = j;
                        }
                    }
                }

                entity = map[j][i];
                if (entity.getEntityType()!=EntityType.RANGED_UNIT &&
                        entity.getEntityType()!=EntityType.MELEE_UNIT &&
                        entity.getEntityType()!=EntityType.RESOURCE &&
                        entity.getEntityType()!=EntityType.Empty
                )
                {
                    if (entity.getPlayerId()==playerID)
                    {
                        if (xMax<j)
                        {
                            xMax = j;
                        }

                        if (yMax<i)
                        {
                            yMax = i;
                        }
                    }
                }

            }
        }

        return new AreaPlayer(new Vec2Int(0,0),xMax+4,yMax+4);

    }

    private void updateMap(GlobalStatistic globalStatistic) {
        clearMap();

        allEntity = globalStatistic.getMyEntityArrayList();

        for (int i=0; i<allEntity.size(); i++)
        {
            MyEntity entity = allEntity.get(i);
            if (checkCoord(entity.getPosition())
            ) {
                map[entity.getPosition().getX()][entity.getPosition().getY()] = entity;

                switch (entity.getEntityType())
                {

                    case HOUSE:
                    case BUILDER_BASE:
                    case MELEE_BASE:
                    case RANGED_BASE:
                    case TURRET:
                        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity.getEntityType());
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
                map[i][j] = new MyEntity(-1,-1,EntityType.Empty,null,0,false);
            }
        }
    }

    public ArrayList<Vec2Int> getCoordAround(Vec2Int start, int size, boolean checkEmpty)
    {
        ArrayList<Vec2Int> arrayList = new ArrayList<>();

        Vec2Int vec2Int = start;

        for (int x=size-1; x>=0; x--)
        {
            Vec2Int vec2Int1 = vec2Int.add(x, size);

            if (!checkCoord(vec2Int1)) continue;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    arrayList.add(vec2Int1);
                }
            }
            else {
                arrayList.add(vec2Int1);
            }
        }


        for (int y=size-1; y>=0; y--)
        {
            Vec2Int vec2Int1 = vec2Int.add(size, y);

            if (!checkCoord(vec2Int1)) continue;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    arrayList.add(vec2Int1);
                }
            }
            else {
                arrayList.add(vec2Int1);
            }
        }

        for (int x=size-1; x>=0; x--)
        {
            Vec2Int vec2Int1 = vec2Int.add(x, -1);

            if (!checkCoord(vec2Int1)) continue;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    arrayList.add(vec2Int1);
                }
            }
            else {
                arrayList.add(vec2Int1);
            }
        }

        for (int y=size-1; y>=0; y--)
        {
            Vec2Int vec2Int1 = vec2Int.add(-1, y);

            if (!checkCoord(vec2Int1)) continue;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    arrayList.add(vec2Int1);
                }
            }
            else {
                arrayList.add(vec2Int1);
            }
        }

        return arrayList;
    }


    public Vec2Int getNearest(Vec2Int position, EntityType entityType)
    {
        double minDis = 0xFFFF;
        Vec2Int current = new Vec2Int(0,0);
        for (int i=0; i<allEntity.size(); i++)
        {
            if (allEntity.get(i).getEntityType()!=entityType) continue;
            double dis = position.distance(allEntity.get(i).getPosition());
            if (dis<minDis)
            {
                current = allEntity.get(i).getPosition();
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

    public Vec2Int getPositionBuildUnitPriorite(Entity building)
    {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(building);

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

    public Vec2Int getPositionBuildUnit(Entity building)
    {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(building);

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
        int iter = 6;
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
                       // FinalGraphic.sendSquare(debugInterface,new Vec2Int(i,j),1, FinalGraphic.COLOR_BLACK);
                    }
                }
            }
        }

        FinalGraphic.sendSquare(debugInterface,new Vec2Int(0,0),getAreaPlayer().width, FinalGraphic.COLOR_WHITE);
    }

    //проверка для отхода крестьян от опасности
    public Vec2Int checkDangerBuildUnit(Vec2Int position, int playerID){
        ArrayList<MyEntity> arrayList = getEntityMap(position,6,playerID,true,true);

        if (arrayList.size()==0) return null;

        int sizeMax = arrayList.size();

        byte[][] bytes= new byte[][]{
                {1,0},{0,1},{-1,0},{0,-1},
        };

        Vec2Int current = null;

        for (int i=0; i<4; i++)
        {
            Vec2Int newPosition = position.add(bytes[i][0],bytes[i][1]);

            if (!checkCoord(newPosition)) continue;

            ArrayList<MyEntity> arrayList1 = getEntityMap(newPosition,6,playerID,true,true);
            if (arrayList1.size()<sizeMax)
            {
                sizeMax = arrayList1.size();
                current = newPosition;
            }

        }

        if (sizeMax!=arrayList.size()) return current;

        return null;
    }

    public boolean checkDangerBuilding(Vec2Int position, EntityType entityType){
        EntityProperties entityProperties = FinalConstant.getEntityProperties(entityType);



        return true;
    }

    // список юнитов в квардрате с центром position
    public ArrayList<MyEntity> getEntityMap(Vec2Int position, int size, int playerID, boolean onelyEnemy, boolean onlyUnit){
        ArrayList<MyEntity> arrayList = new ArrayList<>();

        for (int x=-size; x<=size; x++ )
        {
            if (x+position.getX()<0 || x+position.getX()>= FinalConstant.getMapSize()) continue;

            int sizeY = size-Math.abs(x);

            for (int y=-sizeY; y<=sizeY; y++)
            {
                if (y+position.getY()<0 || y+position.getY()>= FinalConstant.getMapSize()) continue;

                MyEntity entity = map[x+position.getX()][y+position.getY()];
                if (((entity.getEntityType() == EntityType.RANGED_UNIT || entity.getEntityType() == EntityType.MELEE_UNIT) || !onlyUnit) &&

                        (entity.getEntityType() != EntityType.Empty && entity.getEntityType() != EntityType.RESOURCE)
                )
                {
                    if (entity.getPlayerId()==null) continue;

                    if (entity.getPlayerId() != playerID || onelyEnemy==false)
                    {
                        arrayList.add(entity);
                    }
                }
            }
        }

        return arrayList;
    }

    //проверяем рабочих возле турели!
    public MyEntity getBuilderUnitNearTurret(MyEntity entity){
        if (entity.getEntityType()!=EntityType.TURRET) return null;

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);
        ArrayList<Vec2Int> arrayList = getCoordAround(entity.getPosition(),entityProperties.getSize(),false);

        for (int i=0; i<arrayList.size(); i++)
        {
            Vec2Int vec2Int1 = arrayList.get(i);

            MyEntity myEntity = map[vec2Int1.getX()][vec2Int1.getY()];

            if (myEntity.getPlayerId() == entity.getPlayerId() && myEntity.getEntityType()==EntityType.BUILDER_UNIT) return myEntity;
        }

        return null;
    }

    public Entity[][] getMap() {
        return map;
    }

    public Entity getMap(Vec2Int vec2Int)
    {
        if (!checkCoord(vec2Int)) return null;

        return map[vec2Int.getX()][vec2Int.getY()];
    }

    public AreaPlayer getAreaPlayer() {
        return mAreaPlayer;
    }


    public boolean checkCoord(int x, int y, int sizeObject)
    {
        if (x<0+sizeObject || x>= FinalConstant.getMapSize()-sizeObject || y<0+sizeObject || y>= FinalConstant.getMapSize()+sizeObject) return false;
        return true;
    }

    public boolean checkCoord(int x, int y)
    {
       return checkCoord(x,y,0);
    }

    public boolean checkCoord(Vec2Int vec2Int, int size)
    {
        return checkCoord(vec2Int.getX(),vec2Int.getY(),size);
    }

    public boolean checkCoord(Vec2Int vec2Int)
    {
        return checkCoord(vec2Int.getX(),vec2Int.getY(),0);
    }

}
