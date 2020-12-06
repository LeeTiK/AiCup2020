package strategy.map.potfield;

import model.*;
import strategy.*;

import java.util.ArrayList;

public class MapPotField {

    Field[][] mMapPotField;
    int size;

    BiomResourceMap mBiomResourceMap;

    GlobalMap mGlobalMap;

    Vec2Int leftPosition;
    Vec2Int rightPosition;

    public MapPotField(int size)
    {
        this.size = size;
        mMapPotField = new Field[size][size];
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {
                mMapPotField[i][j] = new Field(new Vec2Int(i,j));
            }
        }
        mBiomResourceMap = new BiomResourceMap();



    }

    public void update(GlobalManager globalManager){
        mGlobalMap = globalManager.getGlobalMap();
        mBiomResourceMap.clear();
        leftPosition = null;
        rightPosition = null;

        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyEntity[][] map = mGlobalMap.getMap();

        clearField();

        int districtResource=0;

        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {
                if (map[i][j].getEntityType()==EntityType.RESOURCE)
                {
                    int resourceDirect = resourceDirect(mMapPotField[i][j]);
                    if (resourceDirect==-1){
                        mMapPotField[i][j].setDistrictResource(0);
                    }
                }

                if (map[i][j].getPlayerId()==null) continue;

                // отмечаем свои владения
                if (map[i][j].getPlayerId()== FinalConstant.getMyID()) {

                    addPlayerArea(map[i][j], new Vec2Int(i, j), map);
                }

                // отмечаем вражеские войска
            }
        }

    //    globalMap.getMap()

    }

    private int resourceDirect(Field field)
    {
        if (field.getX() -1 > 0){
           if (mMapPotField[field.getX()-1][field.getY()].getDistrictResource()!=-1) return mMapPotField[field.getX()-1][field.getY()].getDistrictResource();
        }

        if (1 + field.getX() < FinalConstant.getMapSize()){
            if (mMapPotField[field.getX()+1][field.getY()].getDistrictResource()!=-1) return mMapPotField[field.getX()+1][field.getY()].getDistrictResource();
        }

        if (field.getY() -1 > 0){
            if (mMapPotField[field.getX()][field.getY()-1].getDistrictResource()!=-1) return mMapPotField[field.getX()][field.getY()-1].getDistrictResource();
        }

        if (1 + field.getY() < FinalConstant.getMapSize()){
            if (mMapPotField[field.getX()][field.getY()+1].getDistrictResource()!=-1) return mMapPotField[field.getX()][field.getY()+1].getDistrictResource();
        }

        return -1;
    }

    private void clearField() {
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
            {
                mMapPotField[i][j].clear();
            }
        }
    }


    private void addPlayerArea(MyEntity myEntity, Vec2Int position, MyEntity[][] map) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(myEntity);

        switch (myEntity.getEntityType())
        {

            case WALL:
                break;
            case HOUSE:
            case RANGED_BASE:
            case BUILDER_BASE:
            case BUILDER_UNIT:
            case MELEE_BASE:
            case TURRET:
                int size = 7;
               /*
                for (int x=-size; x<size; x++) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = -sizeY; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX()+x][position.getY() + y];

                       // if (entity.getEntityType()==EntityType.RESOURCE) break;


                   //     if (entity!=null)
                        int sum = 2*size - (Math.abs(x) +Math.abs(y))/2;
                     //   int sum = 5;
                     //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                }*/

                boolean checkYNUll = false;
                for (int x=0; x>=-size; x--) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = 0; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX()+x][position.getY() + y];

                         if (checkYNUll){
                             break;
                         }

                         if (entity.getEntityType()==EntityType.RESOURCE) {
                             if (y==0){
                                 checkYNUll = true;
                             }
                             break;
                         }


                        //     if (entity!=null)
                        int sum = 2*size - (Math.abs(x) +Math.abs(y))/2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                    for (int y = -1; y >=-sizeY; y--) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX()+x][position.getY() + y];

                         if (entity.getEntityType()==EntityType.RESOURCE) break;


                        //     if (entity!=null)
                        int sum = 2*size - (Math.abs(x) +Math.abs(y))/2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                }
                checkYNUll = false;
                for (int x=0; x<=size; x++) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = 0; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX()+x][position.getY() + y];

                        if (checkYNUll){
                            break;
                        }

                        if (entity.getEntityType()==EntityType.RESOURCE) {
                            if (y==0){
                                checkYNUll = true;
                            }
                            break;
                        }

                        //     if (entity!=null)
                        int sum = 2*size - (Math.abs(x) +Math.abs(y))/2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                    for (int y = -1; y >=-sizeY; y--) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX()+x][position.getY() + y];

                        if (entity.getEntityType()==EntityType.RESOURCE) break;


                        //     if (entity!=null)
                        int sum = 2*size - (Math.abs(x) +Math.abs(y))/2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                }

                size = 10;
                for (int x=-size; x<size; x++) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = -sizeY; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX()+x][position.getY() + y];

                        // if (entity.getEntityType()==EntityType.RESOURCE) break;


                        //     if (entity!=null)
                        int sum = 2*size - (Math.abs(x) +Math.abs(y))/2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerAreaTwo(sum);
                    }
                }
                break;
            case ALL:
                break;
            case Empty:
                break;
        }
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        //  debugInterface.
        if (getMapPotField()!=null)
        {
            for (int i=0; i<size; i++)
            {
                for (int j=0; j<size; j++)
                {
                    if (getMapPotField()[i][j].getPlayerArea()<=0) continue;

                    if (Final.PLAYER_AREA)
                    {
                       // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                        FinalGraphic.sendSquare(debugInterface,new Vec2Int(i,j),1, FinalGraphic.getColorDinamic(getMapPotField()[i][j].getPlayerArea(),30));
                    }
                }
            }


            if (Final.PLAYER_AREA)
            {
                if (getPositionDefencePlayerArea(0)!=null) {
                    // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                    FinalGraphic.sendSquare(debugInterface, getPositionDefencePlayerArea(0), 1, FinalGraphic.COLOR_BLACK);
                    FinalGraphic.sendSquare(debugInterface, getPositionDefencePlayerArea(1), 1, FinalGraphic.COLOR_BLACK);
                }
            }
        }



        // FinalGraphic.sendSquare(debugInterface,new Vec2Int(0,0),getAreaPlayer().width, FinalGraphic.COLOR_WHITE);
    }

    public Field[][] getMapPotField() {
        return mMapPotField;
    }

    public Vec2Int getNearestPlayerIntoPlayerArea(Vec2Int vec2Int, int myID){
        double minDis = 0xFFFFF;
        Vec2Int currentPos = null;

        MyEntity[][] map = mGlobalMap.getMap();

        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<map[i].length; j++)
            {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId()==myID) continue;

                if (mMapPotField[i][j].getPlayerAreaTwo()<=0) continue;

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

    public boolean checkAttackBase(int myID, GlobalStatistic globalStatistic){

        MyEntity[][] map = mGlobalMap.getMap();

        ArrayList<Integer> integers = new ArrayList<>();

        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<map[i].length; j++)
            {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId()==myID) continue;

                if (mMapPotField[i][j].getPlayerArea()<=0) continue;

                boolean check = false;
                for (int k=0; k<integers.size(); k++)
                {
                    if (integers.get(k)==map[i][j].getPlayerId()) {
                        check = true;
                        break;
                    }
                }
                if (!check) integers.add(map[i][j].getPlayerId());

            }
        }

        MyPlayer myPlayer = globalStatistic.getMyPlayer();

        for (int k=0; k<integers.size(); k++)
        {
            MyPlayer enemy = globalStatistic.getPlayer(integers.get(k));
            if (enemy.getEntityArrayList(EntityType.RANGED_UNIT).size() +
                    enemy.getEntityArrayList(EntityType.MELEE_UNIT).size() >
                    myPlayer.getEntityArrayList(EntityType.RANGED_UNIT).size() +
                            enemy.getEntityArrayList(EntityType.MELEE_UNIT).size()
            ) {
               return true;
            }
        }

        return false;
    }

    public boolean checkAttackBaseTwo(int myID){

        MyEntity[][] map = mGlobalMap.getMap();

        for (int i=0; i<map.length; i++)
        {
            for (int j=0; j<map[i].length; j++)
            {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId()==myID) continue;

                if (mMapPotField[i][j].getPlayerAreaTwo()<=0) continue;

                return true;
            }
        }
        return false;
    }

    public Vec2Int getPositionDefencePlayerArea(int position){


        if (leftPosition==null)
        {
            Vec2Int max = new Vec2Int(0,0);
            if (getMapPotField()!=null)
            {
                for (int i=0; i<size; i++)
                {
                    for (int j=0; j<size; j++)
                    {
                        if (getMapPotField()[i][j].getPlayerArea()<=0) continue;

                        if (i<30){
                            if (j>max.getY())
                            {
                                max = new Vec2Int(i,j);
                                leftPosition = max;
                            }
                        }
                    }
                }
            }
        }

        if (rightPosition == null){
            Vec2Int max = new Vec2Int(0,0);
            if (getMapPotField()!=null)
            {
                for (int i=0; i<size; i++)
                {
                    for (int j=0; j<size; j++)
                    {
                        if (getMapPotField()[i][j].getPlayerArea()<=0) continue;

                        if (j<30){
                            if (i>max.getX())
                            {
                                max = new Vec2Int(i,j);
                                rightPosition=max;
                            }
                        }
                    }
                }
            }
        }

        switch (position)
        {
            case 0:
                return leftPosition;
            case 1:
                return rightPosition;
        }

        return null;
    }
}
