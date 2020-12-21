package strategy;

import model.*;
import strategy.map.potfield.MapPotField;
import strategy.map.wave.SearchAnswer;

import java.util.ArrayList;

import static strategy.GlobalManager.waveSearchModule;

public class GlobalMap {
    // класс карты
    MyEntity[][] map = null;
    MyEntity[][] mapNextTick = null;

    ArrayList<MyEntity> allEntityUnits;
    ArrayList<MyEntity> allEntityResource;

    //массив в котором просишь подвинуться юниту если тебе очень нужно, он обязан это исполнить если это не

    AreaPlayer mAreaPlayer;

    long resourceMap;

    final static MyEntity empty = new MyEntity(-1, null, EntityType.Empty, null, 0, false);
    ;

    final public static byte[][] aroundArray = new byte[][]{{-1, 0}, {0, -1}, {0, 1}, {1, 0},};

    final public static byte[][] aroundAndContourArray = new byte[][]{
            {-2, 0},
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -2}, {0, -1}, {0, 1}, {0, 2},
            {1, -1}, {1, 0}, {1, 1},
            {2, 0},
    };

    final public static byte[][] aroundContourArray = new byte[][]{
            {-2, 0},
            {-1, -1}, {-1, 1},
            {0, -2}, {0, 2},
            {1, -1}, {1, 1},
            {2, 0},
    };

    final public static byte[][] rangerArray = new byte[][]{
            {-5, 0},
            {-4, 1}, {-4, 0}, {-4, -1},
            {-3, 2}, {-3, 1}, {-3, 0}, {-3, -1}, {-3, -2},
            {-2, 3}, {-2, 2}, {-2, 1}, {-2, 0}, {-2, -1}, {-2, -2}, {-2, -3},
            {-1, 4}, {-1, 3}, {-1, 2}, {-1, 1}, {-1, 0}, {-1, -1}, {-1, -2}, {-1, -3}, {-1, -4},
            {0, 5}, {0, 4}, {0, 3}, {0, 2}, {0, 1}, {0, 0}, {0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5},
            {1, 4}, {1, 3}, {1, 2}, {1, 1}, {1, 0}, {1, -1}, {1, -2}, {1, -3}, {1, -4},
            {2, 3}, {2, 2}, {2, 1}, {2, 0}, {2, -1}, {2, -2}, {2, -3},
            {3, 2}, {3, 1}, {3, 0}, {3, -1}, {3, -2},
            {4, 1}, {4, 0}, {4, -1},
            {5, 0},
    };

    final public static byte[][] rangerAndContourArray = new byte[][]{
            {-6, 0},
            {-5, 1}, {-5, 0}, {-5, -1},
            {-4, 2}, {-4, 1}, {-4, 0}, {-4, -1}, {-4, -2},
            {-3, 3}, {-3, 2}, {-3, 1}, {-3, 0}, {-3, -1}, {-3, -2}, {-3, -3},
            {-2, 4}, {-2, 3}, {-2, 2}, {-2, 1}, {-2, 0}, {-2, -1}, {-2, -2}, {-2, -3}, {-2, -4},
            {-1, 5}, {-1, 4}, {-1, 3}, {-1, 2}, {-1, 1}, {-1, 0}, {-1, -1}, {-1, -2}, {-1, -3}, {-1, -4}, {-1, -5},
            {0, 6}, {0, 5}, {0, 4}, {0, 3}, {0, 2}, {0, 1}, {0, 0}, {0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5}, {0, -6},
            {1, 5}, {1, 4}, {1, 3}, {1, 2}, {1, 1}, {1, 0}, {1, -1}, {1, -2}, {1, -3}, {1, -4}, {1, -5},
            {2, 4}, {2, 3}, {2, 2}, {2, 1}, {2, 0}, {2, -1}, {2, -2}, {2, -3}, {2, -4},
            {3, 3}, {3, 2}, {3, 1}, {3, 0}, {3, -1}, {3, -2}, {3, -3},
            {4, 2}, {4, 1}, {4, 0}, {4, -1}, {4, -2},
            {5, 1}, {5, 0}, {5, -1},
            {6, 0},
    };

    final public static byte[][] rangerContourArray = new byte[][]{
            {-6, 0},
            {-5, 1}, {-5, -1},
            {-4, 2}, {-4, -2},
            {-3, 3}, {-3, -3},
            {-2, 4}, {-2, -4},
            {-1, 5}, {-1, -5},
            {0, 6}, {0, -6},
            {1, 5}, {1, -5},
            {2, 4}, {2, -4},
            {3, 3}, {3, -3},
            {4, 2}, {4, -2},
            {5, 1}, {5, -1},
            {6, 0},
    };

    final public static byte[][] rangerTwoContourArray = new byte[][]{
            {-7, 0},
            {-6, 1}, {-6, -1},
            {-5, 2}, {-5, -2},
            {-4, 3}, {-4, -3},
            {-3, 4}, {-3, -4},
            {-2, 5}, {-2, -5},
            {-1, 6}, {-1, -6},
            {0, 7}, {0, -7},
            {1, 6}, {1, -6},
            {2, 5}, {2, -5},
            {3, 4}, {3, -4},
            {4, 3}, {4, -3},
            {5, 2}, {5, -2},
            {6, 1}, {6, -1},
            {7, 0},
    };

    final public static byte[][] turretArray = new byte[][]{
            {-5, 1}, {-5, 0},
            {-4, 2}, {-4, 1}, {-4, 0}, {-4, -1},
            {-3, 3}, {-3, 2}, {-3, 1}, {-3, 0}, {-3, -1}, {-3, -2},
            {-2, 4}, {-2, 3}, {-2, 2}, {-2, 1}, {-2, 0}, {-2, -1}, {-2, -2}, {-2, -3},
            {-1, 5}, {-1, 4}, {-1, 3}, {-1, 2}, {-1, 1}, {-1, 0}, {-1, -1}, {-1, -2}, {-1, -3}, {-1, -4},
            {0, 6}, {0, 5}, {0, 4}, {0, 3}, {0, 2}, {0, 1}, {0, 0}, {0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5},
            {1, 6}, {1, 5}, {1, 4}, {1, 3}, {1, 2}, {1, 1}, {1, 0}, {1, -1}, {1, -2}, {1, -3}, {1, -4}, {1, -5},
            {2, 5}, {2, 4}, {2, 3}, {2, 2}, {2, 1}, {2, 0}, {2, -1}, {2, -2}, {2, -3}, {2, -4},
            {3, 4}, {3, 3}, {3, 2}, {3, 1}, {3, 0}, {3, -1}, {3, -2}, {3, -3},
            {4, 3}, {4, 2}, {4, 1}, {4, 0}, {4, -1}, {4, -2},
            {5, 2}, {5, 1}, {5, 0}, {5, -1},
            {6, 1}, {6, 0},
    };

    final public static byte[][] turretAndContourArray = new byte[][]{
            {-6, 1}, {-6, 0},
            {-5, 2}, {-5, 1}, {-5, 0}, {-5, -1},
            {-4, 3}, {-4, 2}, {-4, 1}, {-4, 0}, {-4, -1}, {-4, -2},
            {-3, 4}, {-3, 3}, {-3, 2}, {-3, 1}, {-3, 0}, {-3, -1}, {-3, -2}, {-3, -3},
            {-2, 5}, {-2, 4}, {-2, 3}, {-2, 2}, {-2, 1}, {-2, 0}, {-2, -1}, {-2, -2}, {-2, -3}, {-2, -4},
            {-1, 6}, {-1, 5}, {-1, 4}, {-1, 3}, {-1, 2}, {-1, 1}, {-1, 0}, {-1, -1}, {-1, -2}, {-1, -3}, {-1, -4}, {-1, -5},
            {0, 7}, {0, 6}, {0, 5}, {0, 4}, {0, 3}, {0, 2}, {0, 1}, {0, 0}, {0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5}, {0, -6},
            {1, 7}, {1, 6}, {1, 5}, {1, 4}, {1, 3}, {1, 2}, {1, 1}, {1, 0}, {1, -1}, {1, -2}, {1, -3}, {1, -4}, {1, -5}, {1, -6},
            {2, 6}, {2, 5}, {2, 4}, {2, 3}, {2, 2}, {2, 1}, {2, 0}, {2, -1}, {2, -2}, {2, -3}, {2, -4}, {2, -5},
            {3, 5}, {3, 4}, {3, 3}, {3, 2}, {3, 1}, {3, 0}, {3, -1}, {3, -2}, {3, -3}, {3, -4},
            {4, 4}, {4, 3}, {4, 2}, {4, 1}, {4, 0}, {4, -1}, {4, -2}, {4, -3},
            {5, 3}, {5, 2}, {5, 1}, {5, 0}, {5, -1}, {5, -2},
            {6, 2}, {6, 1}, {6, 0}, {6, -1},
            {7, 1}, {7, 0},
    };

    final public static byte[][] turretContourArray = new byte[][]{
            {-6, 1}, {-6, 0},
            {-5, 2}, {-5, -1},
            {-4, 3}, {-4, -2},
            {-3, 4}, {-3, -3},
            {-2, 5}, {-2, -4},
            {-1, 6}, {-1, -5},
            {0, 7}, {0, -6},
            {1, 7}, {1, -6},
            {2, 6}, {2, -5},
            {3, 5}, {3, -4},
            {4, 4}, {4, -3},
            {5, 3}, {5, -2},
            {6, 2}, {6, -1},
            {7, 1}, {7, 0},
    };

    final public static byte[][] housePositionFogOfWar = new byte[][]{
            {2, 2}, {6, 2}, {10, 2}, {14, 2}, {18, 2}, {22, 2}, {26, 2},
                    {2, 22}, {2, 26},
            {2, 6}, {6, 6}, {10, 6}, {14, 6}, {18, 6}, {22, 6}, {26, 6},
            {2, 10}, {6, 10}, {10, 10}, {14, 10}, {18,10}, {22, 10}, {26, 10},
            {2, 14}, {6, 14}, {10, 14}, {14, 14}, {18,14}, {22, 14}, {26, 14},
            {2, 18}, {6, 18}, {10, 18}, {14, 18}, {18,18}, {22, 18}, {26, 18},
            {2, 22}, {6, 22}, {10, 22}, {14, 22}, {18,22}, {22, 22},
            {2, 26}, {6, 26}, {10, 26}, {14, 26}, {18,26},
    };

    final public static byte[][] housePosition = new byte[][]{
            {0, 0}, {0, 3},{0, 6},{0, 9},{0, 12},{0, 15},{0, 18}, {0, 21},
            {4, 0}, {7, 0},{10, 0},{13, 0}, {16, 0},{19, 0},{22, 0},{25, 0},
           /* {1, 3},{1, 6},{1, 9},{1, 12},{1, 15},{1, 18}, {1, 21},
            {4, 1}, {7, 1},{10, 1},{13, 1}, {16, 1},{19, 1},{22, 1},{25, 1},*/

        //    {11, 4},  {11, 8}, {11, 12}, {11, 16},
        //    {4, 11}, {16, 11},

          //  {17, 4},  {17, 8}, {17, 12}, {17, 16},
           // {4, 17},  {8, 17}, {12, 17},

         //   {21, 4},{21, 8},{21, 12}, {21, 16},
         //   {4, 21},{8, 21},{12, 21},{16, 21},

        //    {25, 4}, {25, 8},//25, 12},// {25, 16},
        //    {4, 25}, {8, 25}, //{12, 25}, //{16, 25},

          //  33, 4

    };


    final public static byte[][] housePositionV3 = new byte[][]{
       //     {0, 0}, {0, 3},{0, 6},{0, 9},{0, 12},{0, 15},{0, 18}, {0, 21},
      //      {4, 0}, {7, 0},{10, 0},{13, 0}, {16, 0},{19, 0},{22, 0},{25, 0},

            {11, 4},  {11, 8}, {11, 12}, {11, 16},
            {4, 11}, {16, 11},

            {17, 4},  {17, 8}, {17, 11}, {17, 16},
            {4, 17},  {8, 17}, {11, 17},

    };

    final public static byte[][] housePositionV2 = new byte[][]{
          //  {0, 0}, {0, 3},{0, 6},{0, 9},{0, 12},{0, 15},{0, 18}, {0, 21},
          //  {4, 0}, {7, 0},{10, 0},{13, 0}, {16, 0},{19, 0},{22, 0},{25, 0},

          //  {11, 4},  {11, 8}, {11, 12}, {11, 16},
          //  {4, 11}, {16, 11},

            {21, 4},{21, 8},{21, 12}, {21, 16},
            {4, 21},{8, 21},{12, 21},{16, 21},

            //    {25, 4}, {25, 8},//25, 12},// {25, 16},
            //    {4, 25}, {8, 25}, //{12, 25}, //{16, 25},

            //  33, 4

    };

    final public static byte[][] basePosition = new byte[][]{
              {5, 5}, {15, 5},{5, 15},{11, 5},{5, 11},{0, 15},{0, 18}, {0, 21},
              {7, 5}, {5, 7}, {15, 4},{4, 15},{4, 11}, {11, 4},
    };

    final public static byte[][] basePositionV2 = new byte[][]{
            {2, 13}, {3, 13},{4, 13},{6, 13},{7, 13},{8, 13},{9, 13},{10, 13},{11, 13},{12, 13},{13, 13},{14, 13},{15, 13},{16, 13},
            {13, 2}, {13, 3}, {13, 4}, {13, 6}, {13, 7}, {13, 8}, {13, 9}, {13, 10}, {13, 11}, {13, 12},{13, 13},{13, 14},{13, 15},
            {2, 14}, {3, 14},{4, 14},{5, 14},{6, 14},{7, 14},{8, 14},{9, 14},{10, 14},{11, 14},{12, 14},{13, 14},{14, 14},{15, 14},{16, 14},
            {14, 2}, {14, 3}, {14, 4}, {14, 5}, {14, 6}, {14, 7}, {14, 8}, {14, 9}, {14, 10}, {14, 11}, {14, 12},{14, 13},{14, 14},{14, 15},
            {10, 10},{11, 2},{11, 3},{11, 4},{11, 6},{11, 7},{11, 8},{11, 9},{11, 10},
            {0, 10},{0, 11},{0, 12},{0, 6},
            {0, 15}, {15, 0}, {0, 0},
    };

    public GlobalMap() {

    }

    public void update(GlobalStatistic globalStatistic) {
        if (map == null) {
            map = new MyEntity[FinalConstant.getMapSize()][FinalConstant.getMapSize()];
            mapNextTick = new MyEntity[FinalConstant.getMapSize()][FinalConstant.getMapSize()];
        }

        updateMap(globalStatistic);

        mAreaPlayer = getPlayerArea(FinalConstant.getMyID());
    }

    public AreaPlayer getPlayerArea(int playerID) {

        int xMax = 0;
        int yMax = 0;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j <= i; j++) {
                Entity entity = map[i][j];
                if (entity.getEntityType() != EntityType.RANGED_UNIT &&
                        entity.getEntityType() != EntityType.MELEE_UNIT &&
                        entity.getEntityType() != EntityType.RESOURCE &&
                        entity.getEntityType() != EntityType.Empty
                ) {
                    if (entity.getPlayerId() == playerID) {
                        if (xMax < i) {
                            xMax = i;
                        }

                        if (yMax < j) {
                            yMax = j;
                        }
                    }
                }

                entity = map[j][i];
                if (entity.getEntityType() != EntityType.RANGED_UNIT &&
                        entity.getEntityType() != EntityType.MELEE_UNIT &&
                        entity.getEntityType() != EntityType.RESOURCE &&
                        entity.getEntityType() != EntityType.Empty
                ) {
                    if (entity.getPlayerId() == playerID) {
                        if (xMax < j) {
                            xMax = j;
                        }

                        if (yMax < i) {
                            yMax = i;
                        }
                    }
                }

            }
        }

        return new AreaPlayer(Vec2Int.createVector(0, 0), xMax + 4, yMax + 4);

    }

    private void updateMap(GlobalStatistic globalStatistic) {
        clearMap();

        allEntityUnits = globalStatistic.getMyEntityArrayList();
        allEntityResource = globalStatistic.getResourceArrayList();

        for (int i = 0; i < allEntityUnits.size(); i++) {
            MyEntity entity = allEntityUnits.get(i);

            addEntity(entity);
            //  playerView.getEntityProperties().get()
        }

        for (int i = 0; i < allEntityResource.size(); i++) {
            MyEntity entity = allEntityResource.get(i);

            resourceMap += entity.getHealth();

            if (checkCoord(entity.getPosition())
            ) {
                map[entity.getPosition().getX()][entity.getPosition().getY()] = entity;
                mapNextTick[entity.getPosition().getX()][entity.getPosition().getY()] = entity;
            }
            //  playerView.getEntityProperties().get()
        }

        if (FinalConstant.isFogOfWar())
        {
            updateMapFogOfWar(globalStatistic);
        }
    }

    private void addEntity(MyEntity entity){
        if (checkCoord(entity.getPosition())
        ) {
            map[entity.getPosition().getX()][entity.getPosition().getY()] = entity;
            mapNextTick[entity.getPosition().getX()][entity.getPosition().getY()] = entity;

            switch (entity.getEntityType()) {

                case HOUSE:
                case BUILDER_BASE:
                case MELEE_BASE:
                case RANGED_BASE:
                case TURRET:
                    EntityProperties entityProperties = FinalConstant.getEntityProperties(entity.getEntityType());
                    for (int j = 0; j < entityProperties.getSize(); j++) {
                        for (int k = 0; k < entityProperties.getSize(); k++) {
                            map[entity.getPosition().getX() + j][entity.getPosition().getY() + k] = entity;
                            mapNextTick[entity.getPosition().getX() + j][entity.getPosition().getY() + k] = entity;
                        }
                    }
                    break;
            }

        }
    }

    private void updateMapFogOfWar(GlobalStatistic globalStatistic) {
        if (globalStatistic.getPlayers().size()==2)
        {
            if (FinalConstant.getCurrentTik()<500)
            {
                MyEntity entity = new MyEntity(10000, globalStatistic.getLeftPlyer().getId(), EntityType.BUILDER_BASE, Vec2Int.createVector(70,70), 100, true);
                addEntity(entity);
            }
        }

        if (globalStatistic.getPlayers().size()==4)
        {
            if (FinalConstant.getCurrentTik()<500)
            {
                MyEntity entity = new MyEntity(10000, globalStatistic.getLeftPlyer().getId(), EntityType.BUILDER_BASE, Vec2Int.createVector(5,70), 100, true);
                addEntity(entity);

                MyEntity entity1 = new MyEntity(10001, globalStatistic.getRightPlyer().getId(), EntityType.BUILDER_BASE, Vec2Int.createVector(70,5), 100, true);
                addEntity(entity1);
            }
        }
    }

    private void clearMap() {
        // очищаем ресурсы
        resourceMap = 0;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = empty;
                mapNextTick[i][j] = empty;
            }
        }
    }
    public ArrayList<Vec2Int> getCoordAround(Vec2Int start, int size, boolean checkEmpty){
        return getCoordAround(start,size,checkEmpty,-1,-1,null);
    }

    public ArrayList<Vec2Int> getCoordAround(Vec2Int start, int size, boolean checkEmpty, int ID){
        return getCoordAround(start,size,checkEmpty,ID,-1,null);
    }

    public ArrayList<Vec2Int> getCoordAround(Vec2Int start, int size, boolean checkEmpty, int myID, int district, MapPotField mapPotField) {
        ArrayList<Vec2Int> arrayList = new ArrayList<>();


        Vec2Int vec2Int = start;

        for (int x = size - 1; x >= 0; x--) {
            Vec2Int vec2Int1 = vec2Int.add(x, size);

            if (!checkCoord(vec2Int1)) continue;

            boolean check = false;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    check = true;
                }
                else {
                    check = false;
                }

                if (mapPotField!=null)
                {
                    if (mapPotField.getMapPotField(vec2Int1).getDistrict()!=district) {
                        check=false;
                    }
                }
            }

            if (myID!=-1)
            {
                if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()!=null)
                {
                    if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()==myID){
                        return new ArrayList<>();
                    }
                }
            }

            if (check)
            {
                arrayList.add(vec2Int1);
            }
        }


        for (int y = size - 1; y >= 0; y--) {
            Vec2Int vec2Int1 = vec2Int.add(size, y);

            if (!checkCoord(vec2Int1)) continue;

            boolean check = false;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    check = true;
                }
                else {
                    check = false;
                }

                if (mapPotField!=null)
                {
                    if (mapPotField.getMapPotField(vec2Int1).getDistrict()!=district) {
                        check=false;
                    }
                }
            }

            if (myID!=-1)
            {
                if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()!=null)
                {
                    if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()==myID){
                        return new ArrayList<>();
                    }
                }
            }

            if (check)
            {
                arrayList.add(vec2Int1);
            }
        }

        for (int x = size - 1; x >= 0; x--) {
            Vec2Int vec2Int1 = vec2Int.add(x, -1);

            if (!checkCoord(vec2Int1)) continue;

            boolean check = false;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    check = true;
                }
                else {
                    check = false;
                }

                if (mapPotField!=null)
                {
                    if (mapPotField.getMapPotField(vec2Int1).getDistrict()!=district) {
                        check=false;
                    }
                }
            }

            if (myID!=-1)
            {
                if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()!=null)
                {
                    if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()==myID){
                        return new ArrayList<>();
                    }
                }
            }

            if (check)
            {
                arrayList.add(vec2Int1);
            }
        }

        for (int y = size - 1; y >= 0; y--) {
            Vec2Int vec2Int1 = vec2Int.add(-1, y);

            if (!checkCoord(vec2Int1)) continue;

            boolean check = false;

            if (checkEmpty) {
                if (checkEmpty(vec2Int1)) {
                    check = true;
                }
                else {
                    check = false;
                }

                if (mapPotField!=null)
                {
                    if (mapPotField.getMapPotField(vec2Int1).getDistrict()!=district) {
                        check=false;
                    }
                }
            }

            if (myID!=-1)
            {
                if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()!=null)
                {
                    if (map[vec2Int1.getX()][vec2Int1.getY()].getPlayerId()==myID){
                        return new ArrayList<>();
                    }
                }
            }

            if (check)
            {
                arrayList.add(vec2Int1);
            }
        }

        return arrayList;
    }




    public MyEntity getNearest(Vec2Int position, EntityType entityType, boolean checkEmpty, int myID, MapPotField mapPotField) {
        double minDis = 0xFFFF;
        MyEntity current = null;

        ArrayList<MyEntity> allEntity;

        if (entityType==EntityType.RESOURCE)
        {
            allEntity = allEntityResource;
        }
        else {
            allEntity = allEntityUnits;
        }

        for (int i = 0; i < allEntity.size(); i++) {
            if (allEntity.get(i).getEntityType() != entityType) continue;

            if (entityType==EntityType.RESOURCE && allEntity.get(i).getTargetEntity()!=null) continue;

            if (mapPotField!=null)
            {
                Vec2Int pos = allEntity.get(i).getPosition();
                if (mapPotField.getMapPotField()[pos.getX()][pos.getY()].getSumDanger()>0) {
                    continue;
                }
             }

            double dis = position.distance(allEntity.get(i).getPosition());
            if (dis < minDis) {

                ArrayList<Vec2Int> arrayList = getCoordAround(allEntity.get(i).getPosition(), 1, true, myID, mapPotField.getMapPotField(position).getDistrict(),mapPotField);
                if (arrayList.size() == 0 && checkEmpty) continue;

                if (mapPotField!=null) {
                    boolean check = false;
                    for (int j = 0; j < arrayList.size(); j++) {
                        if (mapPotField.getMapPotField()[arrayList.get(j).getX()][arrayList.get(j).getY()].getSumDanger()==0) {
                            check = true;
                        }
                    }
                    if (!check) continue;
                }

                current = allEntity.get(i);
                minDis = dis;
            }
        }
        return current;
    }

    public Vec2Int getNearestCoord(Vec2Int position, ArrayList<Vec2Int> arrayList) {
        double minDis = 0xFFFF;
        Vec2Int current = null;
        for (int i = 0; i < arrayList.size(); i++) {
            double dis = position.distance(arrayList.get(i));
            if (dis < minDis) {
                current = arrayList.get(i);
                minDis = dis;
            }
        }
        return current;
    }

    public Vec2Int getPositionBuildUnitPriorite(Entity building, MapPotField mapPotField) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition().copy();
        Vec2Int vec2IntCurrent = null;
        double minDis = 0xFFFF;

        for (int x = entityProperties.getSize() - 1; x >= 0; x--) {
            if (checkEmpty(vec2Int.add(x, entityProperties.getSize()))) {
                Vec2Int vec2Int1 = vec2Int.add(x, entityProperties.getSize());
                MyEntity vecReseurse = getNearest(vec2Int1, EntityType.RESOURCE,true,FinalConstant.getMyID(),mapPotField);
                if (vecReseurse==null) continue;
                double dis = vecReseurse.getPosition().distance(vec2Int1);
                if (dis < minDis) {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }


        for (int y = entityProperties.getSize() - 1; y >= 0; y--) {
            if (checkEmpty(vec2Int.add(entityProperties.getSize(), y))) {
                Vec2Int vec2Int1 = vec2Int.add(entityProperties.getSize(), y);
                MyEntity vecReseurse = getNearest(vec2Int1, EntityType.RESOURCE,true,FinalConstant.getMyID(),mapPotField);
                if (vecReseurse==null) continue;
                double dis = vecReseurse.getPosition().distance(vec2Int1);
                if (dis < minDis) {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }

        for (int x = entityProperties.getSize() - 1; x >= 0; x--) {
            if (checkEmpty(vec2Int.add(x, -1))) {
                Vec2Int vec2Int1 = vec2Int.add(x, -1);
                MyEntity vecReseurse = getNearest(vec2Int1, EntityType.RESOURCE,true,FinalConstant.getMyID(),mapPotField);
                if (vecReseurse==null) continue;
                double dis = vecReseurse.getPosition().distance(vec2Int1);
                if (dis < minDis) {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }

        for (int y = entityProperties.getSize() - 1; y >= 0; y--) {
            if (checkEmpty(vec2Int.add(-1, y))) {
                Vec2Int vec2Int1 = vec2Int.add(-1, y);
                MyEntity vecReseurse = getNearest(vec2Int1, EntityType.RESOURCE,true,FinalConstant.getMyID(),mapPotField);
                if (vecReseurse==null) continue;
                double dis = vecReseurse.getPosition().distance(vec2Int1);
                if (dis < minDis) {
                    vec2IntCurrent = vec2Int1;
                    minDis = dis;
                }
            }
        }

        if (vec2IntCurrent == null) return Vec2Int.createVector(0, 0);

        return vec2IntCurrent;

    }

    public Vec2Int getPositionBuildUnitPrioriteV2(Entity building) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition();

        ArrayList<Vec2Int> arrayList = getCoordAround(vec2Int,entityProperties.getSize(),true);

        SearchAnswer searchAnswer = waveSearchModule.waveSearchNeedEntity(arrayList,50,EntityType.RESOURCE);

        if (searchAnswer == null) return Vec2Int.createVector(0, 0);

        return searchAnswer.getStart();

    }

    public Vec2Int getPositionBuildUnit(Entity building) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition().copy();

        for (int x = entityProperties.getSize() - 1; x >= 0; x--) {
            if (checkEmpty(vec2Int.add(x, entityProperties.getSize()))) {
                return vec2Int.add(x, entityProperties.getSize());
            }
        }


        for (int y = entityProperties.getSize() - 1; y >= 0; y--) {
            if (checkEmpty(vec2Int.add(entityProperties.getSize(), y))) {
                return vec2Int.add(entityProperties.getSize(), y);
            }
        }

        for (int x = entityProperties.getSize() - 1; x >= 0; x--) {
            if (checkEmpty(vec2Int.add(x, -1))) {
                return vec2Int.add(x, -1);
            }
        }

        for (int y = entityProperties.getSize() - 1; y >= 0; y--) {
            if (checkEmpty(vec2Int.add(-1, y))) {
                return vec2Int.add(-1, y);
            }
        }

        return vec2Int;

    }



    public Vec2Int getPositionBuildUnitAttack(Entity building, ArrayList<MyEntity> enemyUnits) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(building);

        Vec2Int vec2Int = building.getPosition().copy();

        ArrayList<Vec2Int> arrayList = getCoordAround(vec2Int,entityProperties.getSize(),true);

        if (arrayList.size()==0) return Vec2Int.createVector(0,0);

        if (enemyUnits.size()==0) return arrayList.get(0);

        Vec2Int current = null;
        double midDis = 0xFFFF;

        for (int i=0; i<arrayList.size(); i++)
        {
            for (int j=0; j<enemyUnits.size(); j++)
            {
                double dis = arrayList.get(i).distance(enemyUnits.get(j).getPosition());
                if (dis<midDis && dis>2)
                {
                    current = arrayList.get(i);
                    midDis = dis;
                }
            }
        }

        return current;
    }

    // функция показывает точку для создания здания
    public Vec2Int getPositionBuildHouse(EntityProperties entityProperties) {
        int size = entityProperties.getSize();

        Vec2Int vec2IntRight = Vec2Int.createVector(0, 0);
        Vec2Int vec2IntLeft = Vec2Int.createVector(4, 0);
        int iter = 6;
        while (!checkEmptyAndAround(vec2IntLeft, entityProperties) && !checkEmptyAndAround(vec2IntRight, entityProperties)) {
            vec2IntRight = vec2IntRight.add(0, size);
            vec2IntLeft = vec2IntLeft.add(size, 0);
            iter--;
            if (iter < 0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft, entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight, entityProperties)) return vec2IntRight;

        vec2IntRight = Vec2Int.createVector(11, 4);
        vec2IntLeft = Vec2Int.createVector(4, 11);


        iter = 1;
        while (!checkEmptyAndAround(vec2IntLeft, entityProperties) && !checkEmptyAndAround(vec2IntRight, entityProperties)) {
            vec2IntRight = vec2IntRight.add(0, size);
            vec2IntLeft = vec2IntLeft.add(size, 0);
            iter--;
            if (iter < 0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft, entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight, entityProperties)) return vec2IntRight;

        vec2IntRight = Vec2Int.createVector(21, 4);
        vec2IntLeft = Vec2Int.createVector(4, 21);


        iter = 3;
        while (!checkEmptyAndAround(vec2IntLeft, entityProperties) && !checkEmptyAndAround(vec2IntRight, entityProperties)) {
            vec2IntRight = vec2IntRight.add(0, size + 1);
            vec2IntLeft = vec2IntLeft.add(size + 1, 0);
            iter--;
            if (iter < 0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft, entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight, entityProperties)) return vec2IntRight;

        vec2IntRight = Vec2Int.createVector(25, 4);
        vec2IntLeft = Vec2Int.createVector(4, 25);


        iter = 3;
        while (!checkEmptyAndAround(vec2IntLeft, entityProperties) && !checkEmptyAndAround(vec2IntRight, entityProperties)) {
            vec2IntRight = vec2IntRight.add(0, size + 1);
            vec2IntLeft = vec2IntLeft.add(size + 1, 0);
            iter--;
            if (iter < 0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft, entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight, entityProperties)) return vec2IntRight;

        vec2IntRight = Vec2Int.createVector(29, 4);
        vec2IntLeft = Vec2Int.createVector(4, 29);


        iter = 5;
        while (!checkEmptyAndAround(vec2IntLeft, entityProperties) && !checkEmptyAndAround(vec2IntRight, entityProperties)) {
            vec2IntRight = vec2IntRight.add(0, size + 1);
            vec2IntLeft = vec2IntLeft.add(size + 1, 0);
            iter--;
            if (iter < 0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft, entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight, entityProperties)) return vec2IntRight;

        vec2IntRight = Vec2Int.createVector(33, 4);
        vec2IntLeft = Vec2Int.createVector(4, 33);


        iter = 6;
        while (!checkEmptyAndAround(vec2IntLeft, entityProperties) && !checkEmptyAndAround(vec2IntRight, entityProperties)) {
            vec2IntRight = vec2IntRight.add(0, size + 1);
            vec2IntLeft = vec2IntLeft.add(size + 1, 0);
            iter--;
            if (iter < 0) break;
        }

        if (checkEmptyAndAround(vec2IntLeft, entityProperties)) return vec2IntLeft;
        if (checkEmptyAndAround(vec2IntRight, entityProperties)) return vec2IntRight;

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
    public Vec2Int getMinPositionBuilding(Vec2Int positionUnit, Vec2Int positionBuild, EntityProperties entityProperties) {
        ArrayList arrayList = getCoordAround(positionBuild, entityProperties.getSize(), true);

        Vec2Int vec2Int = getNearestCoord(positionUnit, arrayList);

        return vec2Int;
    }

    // функция для проверки можно ли поставить здания с безопасным уклоном
    public boolean checkSafeСreationBuilding(Vec2Int vec2Int, EntityProperties entityProperties, MapPotField mapPotField)
    {
        // проверка на пустоту под здания, и сразу на опасность(не ставить здания в зоне удара вражеский юнитов)
        if (!mapPotField.checkSafety(vec2Int, entityProperties.getSize())) return false;

        // проверка возможности подхода к зданию
        ArrayList arrayList = getCoordAround(vec2Int, entityProperties.getSize(), true);
        if (arrayList.size() == 0) return false;

        return true;
    }

    public boolean checkEmptyAndAround(Vec2Int vec2Int, EntityProperties entityProperties) {
        if (!checkEmpty(vec2Int, entityProperties.getSize())) return false;

        ArrayList arrayList = getCoordAround(vec2Int, entityProperties.getSize(), true);
        if (arrayList.size() == 0) return false;
        else return true;
    }

    public boolean checkEmpty(MyEntity[][] map,Vec2Int vec2Int) {
        return checkEmpty(map, vec2Int, 1, 1);
    }

    public boolean checkEmpty(MyEntity[][] map,Vec2Int vec2Int, int size) {
        return checkEmpty(map, vec2Int, size, size);
    }

    public boolean checkEmpty(Vec2Int vec2Int) {
        return checkEmpty(getMap(), vec2Int, 1, 1);
    }

    public boolean checkEmpty(Vec2Int vec2Int, int size) {
        return checkEmpty(getMap(), vec2Int, size, size);
    }

    public boolean checkEmpty(MyEntity[][] map,Vec2Int vec2Int, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!checkCoord(vec2Int.getX() + x, vec2Int.getY() + y)) return false;
                if (map[vec2Int.getX() + x][vec2Int.getY() + y].getEntityType() != EntityType.Empty) return false;
            }
        }

        return true;
    }

    public Vec2Int getNearestPlayer(ArrayList<MyPlayer> arrayList) {
        return Vec2Int.createVector(0, 0);
    }

    public Vec2Int getNearestPlayer(int myID) {
        Vec2Int vec2Int = Vec2Int.createVector(0, 0);
        double minDis = 0xFFFFF;
        Vec2Int currentPos = vec2Int;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId() == myID) continue;

                double dis = map[i][j].getPosition().distance(vec2Int);

                if (dis < minDis) {
                    minDis = dis;
                    currentPos = map[i][j].getPosition();
                }
            }
        }
        return currentPos;
    }

    public MyEntity getNearestPlayer(Vec2Int vec2Int, int myID){
        return getNearestPlayer(vec2Int,myID,-1,null,false,false);
    }

    public MyEntity getNearestPlayer(Vec2Int vec2Int, int myID, int enemyID){
        return getNearestPlayer(vec2Int,myID,enemyID,null,false,false);
    }

    public MyEntity getNearestPlayer(Vec2Int vec2Int, int myID, int enemyID,boolean counterAttack){
        return getNearestPlayer(vec2Int,myID,enemyID,null,false,counterAttack);
    }

    public MyEntity getNearestPlayer(Vec2Int vec2Int, int myID,  EntityType entityType){
        return getNearestPlayer(vec2Int,myID,-1,entityType,false,false);
    }

    public MyEntity getMyUnitPlayer(Vec2Int vec2Int, EntityType entityType){
        return getNearestPlayer(vec2Int,-1,FinalConstant.getMyID(),entityType,false,false);
    }

    public MyEntity getNearestPlayer(Vec2Int vec2Int, int myID, int enemyID, EntityType entityType, boolean update, boolean counterAttack) {
        double minDis = 0xFFFFF;
        MyEntity current = null;

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId() == myID) continue;

                if (enemyID==FinalConstant.getMyID() && i==vec2Int.getX() && j == vec2Int.getY()) continue;

                if (enemyID != -1 && map[i][j].getPlayerId() != enemyID) continue;

                if (update) {
                    if (map[i][j].isUpdate() || map[i][j].isDodge())
                    {
                        continue;
                    }
                }

               // if (map[i][j].getTargetEntity() != null) continue;

                if (entityType!=null)
                {
                    if (entityType==EntityType.ATTACK_ENTITY)
                    {
                        if (map[i][j].getEntityType() != EntityType.RANGED_UNIT && map[i][j].getEntityType() != EntityType.TURRET && map[i][j].getEntityType() != EntityType.MELEE_UNIT){
                            int k=0;
                            //System.out.println("EBLAN: " + map[i][j].getEntityType() );
                            continue;
                        }

                    }
                    else {
                        if (entityType==EntityType.NO_ATTACK_ENTITY)
                        {
                            if (map[i][j].getEntityType() != EntityType.BUILDER_UNIT &&
                                    map[i][j].getEntityType() != EntityType.BUILDER_BASE &&
                                    map[i][j].getEntityType() != EntityType.RANGED_BASE &&
                                    map[i][j].getEntityType() != EntityType.MELEE_BASE &&
                                    map[i][j].getEntityType() != EntityType.HOUSE
                            ){
                                int k=0;
                                //System.out.println("EBLAN: " + map[i][j].getEntityType() );
                                continue;
                            }
                        }
                        else {
                            if (map[i][j].getEntityType() != entityType) continue;
                        }
                    }
                }

                if (counterAttack)
                {
                    if (map[i][j].getEntityType()==EntityType.RANGED_UNIT || map[i][j].getEntityType()==EntityType.BUILDER_UNIT)
                    {
                        if (map[i][j].getCountAttackingUnit()>=WarManager.counterAttack)
                        {
                            continue;
                        }
                    }

                    if (map[i][j].getEntityType()==EntityType.MELEE_UNIT)
                    {
                        if (map[i][j].getCountAttackingUnit()>=WarManager.counterAttack*2)
                        {
                            continue;
                        }
                    }
                }

                double dis = map[i][j].getPosition().distance(vec2Int);

                if (dis < minDis) {
                    minDis = dis;
                    current = map[i][j];
                }
            }
        }
        return current;
    }


    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        //  debugInterface.
        if (map != null) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (Final.COORDINATE) {
                        FinalGraphic.sendText(debugInterface, Vec2Int.createVector(i, j), 10, "(" + i + "," + j + ")");
                    }
                }
            }
        }

        // FinalGraphic.sendSquare(debugInterface,Vec2Int.createVector(0,0),getAreaPlayer().width, FinalGraphic.COLOR_WHITE);
    }

    //проверка для отхода крестьян от опасности
    public Vec2Int checkDangerBuildUnit(Vec2Int position, MyPlayer player, int radius, EntityType entityType) {
        ArrayList<MyEntity> arrayList = getEntityMap(position, radius, player.getId(), true, false, true, entityType, false, false);

        if (arrayList.size() == 0) return null;

        int sizeMax = arrayList.size();

        if (sizeMax==0) return null;

        byte[][] bytes = new byte[][]{
                {-1, 0}, {0, -1},{0, 0}, {0, 1}, {1, 0},
        };

        Vec2Int current = null;
        int maxEmpty = 0;

     //   boolean init = false;
        MyEntity positionMyUnit = getMyUnitPlayer(position,EntityType.ATTACK_ENTITY);


        for (int i = 0; i < bytes.length; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            if (!checkEmpty(getMapNextTick(),newPosition)) continue;

            int sizeEmptyPosition =  getCoordAround(newPosition, 1, true).size();

            ArrayList<MyEntity> arrayList1 = getEntityMap(newPosition, radius, player.getId(), true, false, true, entityType, false, false);

            if (arrayList1.size() < sizeMax) {
                sizeMax = arrayList1.size();
                current = newPosition;
                maxEmpty = sizeEmptyPosition;
            }
            else {
                if (arrayList1.size() == sizeMax)
                {
                    if (maxEmpty<sizeEmptyPosition)
                    {
                        sizeMax = arrayList1.size();
                        current = newPosition;
                        maxEmpty = sizeEmptyPosition;
                    }
                    else {
                        if (current!=null &&
                                positionMyUnit!=null &&
                                newPosition.distance(positionMyUnit.getPosition()) < current.distance(positionMyUnit.getPosition()) &&
                                maxEmpty==sizeEmptyPosition)
                        {
                            sizeMax = arrayList1.size();
                            current = newPosition;
                            maxEmpty = sizeEmptyPosition;
                        }
                    }
                }
            }
            /*
            if (arrayList1.size() <= sizeMax || init && arrayList1.size() <= sizeMax) {
                if (init && positionMyUnit != null) {
                    if (sizeMax>arrayList1.size() || newPosition.distance(positionMyUnit.getPosition()) < current.distance(positionMyUnit.getPosition())) {
                        sizeMax = arrayList1.size();
                        current = newPosition;
                    }
                } else {
                    sizeMax = arrayList1.size();
                    current = newPosition;
                    init = true;
                    maxEmpty = sizeEmptyPosition;
                }
            }*/

        }

        if (sizeMax != arrayList.size()) return current;

        if (arrayList.size()>0)
        {
            return position;
        }

        return null;
    }

    public boolean checkDangerBuilding(Vec2Int position, EntityType entityType) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(entityType);


        return true;
    }

    // список юнитов в квардрате с центром position
    public ArrayList<MyEntity> getEntityMap(Vec2Int position, int size, int playerID, boolean onlyEnemy, boolean onlyPlayer, boolean onlyUnit, EntityType entityType, boolean squareRadius, boolean turret) {

        if (turret)
        {
            return getEntityMap(position,turretArray,playerID,-1,onlyUnit,entityType);
        }

        ArrayList<MyEntity> arrayList = new ArrayList<>();

        for (int x = -size; x <= size; x++) {
            if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

            int sizeY = size - Math.abs(x);

            if (squareRadius) {
                sizeY = size;
            }

            for (int y = -sizeY; y <= sizeY; y++) {
                if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                MyEntity entity = map[x + position.getX()][y + position.getY()];
                if (((entity.getEntityType() == EntityType.RANGED_UNIT || entity.getEntityType() == EntityType.MELEE_UNIT) || !onlyUnit) &&

                        (entity.getEntityType() != EntityType.Empty && entity.getEntityType() != EntityType.RESOURCE)
                ) {
                    if (entityType != EntityType.ALL && entity.getEntityType() != entityType) continue;
                    if (entity.getPlayerId() == null) continue;


                    if (entity.getPlayerId() != playerID || onlyEnemy == false) {
                        if (onlyPlayer && entity.getPlayerId() == playerID) {
                            arrayList.add(entity);
                        } else {
                            arrayList.add(entity);
                        }
                    }
                }
            }
        }

        return arrayList;
    }

    // список юнитов в позициях в bytes с центром position
    public ArrayList<MyEntity> getEntityMap(Vec2Int position, byte[][] bytes, int myID, int enemyID, boolean onlyUnit, EntityType entityType) {
        ArrayList<MyEntity> arrayList = new ArrayList<>();

        for (int i=0; i<bytes.length; i++)
        {
            int x = position.getX() + bytes[i][0];
            int y = position.getY() + bytes[i][1];

            if (!checkCoord(x,y)) continue;

            MyEntity entity = map[x][y];
            if ((entity.getEntityType() == EntityType.RANGED_UNIT || entity.getEntityType() == EntityType.MELEE_UNIT) || !onlyUnit)
            {
                if (entityType != EntityType.ALL && entity.getEntityType() != entityType) continue;

                if (entity.getEntityType()==entityType && (entityType==EntityType.RESOURCE || entityType==EntityType.Empty)) {
                    arrayList.add(entity);
                    continue;
                }

                if (entity.getPlayerId() == null) continue;

                if (entity.getPlayerId()==myID) continue;

                if (entity.getPlayerId() == enemyID || enemyID==-1) {
                    arrayList.add(entity);
                }
            }
        }


        return arrayList;
    }

    //проверяем рабочих возле турели!
    public MyEntity getBuilderUnitNearTurret(MyEntity entity) {
        if (entity.getEntityType() != EntityType.TURRET) return null;

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);
        ArrayList<Vec2Int> arrayList = getCoordAround(entity.getPosition(), entityProperties.getSize(), false);

        for (int i = 0; i < arrayList.size(); i++) {
            Vec2Int vec2Int1 = arrayList.get(i);

            MyEntity myEntity = map[vec2Int1.getX()][vec2Int1.getY()];

            if (myEntity.getPlayerId() == entity.getPlayerId() && myEntity.getEntityType() == EntityType.BUILDER_UNIT)
                return myEntity;
        }

        return null;
    }

    public MyEntity[][] getMap() {
        return map;
    }

    public MyEntity getMap(Vec2Int vec2Int) {
        if (!checkCoord(vec2Int)) return null;

        return map[vec2Int.getX()][vec2Int.getY()];
    }

    public AreaPlayer getAreaPlayer() {
        return mAreaPlayer;
    }


    public static boolean checkCoord(int x, int y, int sizeObject) {
        if (x < 0 + sizeObject || x >= FinalConstant.getMapSize() - sizeObject || y < 0 + sizeObject || y >= FinalConstant.getMapSize() + sizeObject)
            return false;
        return true;
    }

    public static boolean checkCoord(int x, int y) {
        return checkCoord(x, y, 0);
    }

    public static boolean checkCoord(Vec2Int vec2Int, int size) {
        return checkCoord(vec2Int.getX(), vec2Int.getY(), size);
    }

    public static boolean checkCoord(Vec2Int vec2Int) {
        return checkCoord(vec2Int.getX(), vec2Int.getY(), 0);
    }

    public long getResourceMap() {
        return resourceMap;
    }

    public MyEntity getMinDisToEntity(Vec2Int position, MyPlayer myPlayer, EntityType entityTypeNeed) {
        double minDis = 0xFFFFF;
        MyEntity current = null;

        ArrayList<MyEntity> arrayList = myPlayer.getEntityArrayList(entityTypeNeed);

        for (int i = 0; i < arrayList.size(); i++) {

            MyEntity builderUnit = arrayList.get(i);

            double dis = arrayList.get(i).getPosition().distance(position);
            if (dis < minDis) {
                current = arrayList.get(i);
                minDis = dis;
            }
        }

        if (current != null) {
            return current;
        }

        return null;
    }

    public MyEntity[][] getMapNextTick() {
        return mapNextTick;
    }

    public MyEntity getMoveMyUnit(Vec2Int position) {
        byte[][] bytes = new byte[][]{
                {-1, 0}, {0, -1}, {0, 1}, {1, 0},
        };

        for (int i = 0; i < 4; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            if (!checkCoord(newPosition)) continue;
            if (checkEmpty(newPosition)) continue;

            MyEntity entity = map[newPosition.getX()][newPosition.getY()];

            if (entity.getPlayerId() == null) continue;

            switch (entity.getEntityType()) {
                case RANGED_UNIT:
                case MELEE_UNIT:
                    // case BUILDER_UNIT:
                    if (entity.isMove() && !entity.isRotation()) return entity;
                    break;
                case BUILDER_UNIT:
                    if (entity.isDodge()) return entity;
            }
        }

        return null;
    }

    public static byte[][] getRadiusUnit(EntityType entityType) {
        switch (entityType) {
            case RANGED_UNIT:
                return rangerArray;
            case MELEE_UNIT:
                return aroundArray;
            case TURRET:
                return turretArray;
            case BUILDER_UNIT:
                return aroundArray;
        }

        return null;
    }

    public static byte[][] getRadiusContourUnit(EntityType entityType) {
        switch (entityType) {
            case RANGED_UNIT:
                return rangerContourArray;
            case MELEE_UNIT:
                return aroundContourArray;
            case TURRET:
                return turretContourArray;
            case BUILDER_UNIT:
                return aroundContourArray;
        }

        return null;
    }


    //TODO исправить подсчет турелей
    public int checkMyTurret(Vec2Int vec2Int, int size) {
        float count = 0;
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                if (!checkCoord(x + vec2Int.getX(), y + vec2Int.getY())) continue;

                if (map[x + vec2Int.getX()][y + vec2Int.getY()].getPlayerId() == null) continue;

                if (map[x + vec2Int.getX()][y + vec2Int.getY()].getEntityType() == EntityType.TURRET && map[x + vec2Int.getX()][y + vec2Int.getY()].getPlayerId() == FinalConstant.getMyID()) {

                    count++;
                }

            }
        }

        return (int) Math.ceil(count / 4);
    }

    public Vec2Int getPositionTurret(Vec2Int positionArea, int depth) {


        return null;
    }


    public boolean checkCreateTurret(Vec2Int vec2Int) {
        if (!checkEmpty(vec2Int, 2)) return false;

        if (checkMyTurret(vec2Int, 4) < 2) return true;

        return false;
    }

    public int getAroundEntity(Vec2Int position, EntityType entityType){
        int x = position.getX();
        int y = position.getY();

        byte[][] array = GlobalMap.aroundArray;

        int count = 0;

        for (int i=0; i<array.length; i++) {
            int x1 = x + array[i][0];
            int y1 = y + array[i][1];

            if (!checkCoord(x1,y1)) continue;

            if (map[x1][y1].getEntityType()==entityType  && map[x1][y1].getTargetEntity()==null)
            {
                count++;
            }
        }

        return count;
    }

    // список юнитов в позициях в bytes с центром position
    public ArrayList<MyEntity> getEntityMapResourceSpecial(Vec2Int position) {
        ArrayList<MyEntity> arrayList = new ArrayList<>();

        byte[][] bytes = aroundArray;

        for (int i=0; i<bytes.length; i++)
        {
            int x = position.getX() + bytes[i][0];
            int y = position.getY() + bytes[i][1];

            if (!checkCoord(x,y)) continue;

            MyEntity entity = map[x][y];

            if (entity.getEntityType()==EntityType.RESOURCE && entity.getTargetEntity()==null) {
                arrayList.add(entity);
                continue;
            }
        }

        return arrayList;
    }


    public MyEntity setPositionNextTick(MyEntity myEntity, Vec2Int position, Vec2Int positionTwo) {
        MyEntity entity = getMapNextTick()[position.getX()][position.getY()];

        if (entity.getEntityType()==EntityType.Empty){
            Final.DEBUG("NEED_MOVE", "ERROR CURRENT EMPTY " + position.toString());
            return null;
        }

        MyEntity entityTwo = getMapNextTick()[positionTwo.getX()][positionTwo.getY()];

        if (entityTwo.getId()==entity.getId()){
            Final.DEBUG("NEED_MOVE", "ERROR ID Entity two position" + position.toString());
            return null;
        }

        if (myEntity.getId()!=entity.getId())
        {
            Final.DEBUG("NEED_MOVE", "ERROR ID Entity one position " + position.toString() + " ID: " + entity.getId() );
            return setPositionNextTick(myEntity,positionTwo);
        }

        if (entityTwo.getEntityType()!=EntityType.Empty && entityTwo.getEntityType()!=EntityType.RESOURCE)
        {
            if (entityTwo.getEntityType()==EntityType.BUILDER_UNIT){
                entityTwo.setNeedMove(true);
            }
            getMapNextTick()[positionTwo.getX()][positionTwo.getY()] = entity;
            return entityTwo;
        }
        else {
            getMapNextTick()[position.getX()][position.getY()] = entityTwo;
            getMapNextTick()[positionTwo.getX()][positionTwo.getY()] = entity;
        }
        /*
        getMapNextTick()[position.getX()][position.getY()] = entityTwo;

       // if (!entity.isInterfereMove()) {
        getMapNextTick()[positionTwo.getX()][positionTwo.getY()] = entity;
      //  }
      */
        return null;
    }

    public MyEntity setPositionNextTick(MyEntity entity, Vec2Int positionTwo) {


        MyEntity entityTwo = getMapNextTick()[positionTwo.getX()][positionTwo.getY()];

        if (entityTwo.getId()==entity.getId()){
            Final.DEBUG("NEED_MOVE", "V2 ERROR ID Entity " + positionTwo.toString());
            return null;
        }

        if (entityTwo.getEntityType()!=EntityType.Empty){
            Final.DEBUG("NEED_MOVE", "V2 ERROR NO EMPTY " + entityTwo.toString());
            return null;
        }


        if (entityTwo.getEntityType()!=EntityType.Empty && entityTwo.getEntityType()!=EntityType.RESOURCE)
        {
            if (entityTwo.getEntityType()==EntityType.BUILDER_UNIT){
                entityTwo.setNeedMove(true);
            }
            getMapNextTick()[positionTwo.getX()][positionTwo.getY()] = entity;
            return entityTwo;
        }
        else {
            getMapNextTick()[positionTwo.getX()][positionTwo.getY()] = entity;
        }

        return null;
    }

    public void checkNextPositionUnit(MyEntity entity) {

        if (entity.isInitNextTickPosition()) return;

        for (int i=0; i<aroundArray.length; i++)
        {
            int x = entity.getPosition().getX()+aroundArray[i][0];
            int y =  entity.getPosition().getY()+aroundArray[i][1];

            if (!checkCoord(x,y)) continue;

            Vec2Int vec2Int = Vec2Int.createVector(x,y);

            if (checkEmpty(Vec2Int.createVector(x,y))){
                entity.setPositionNextTick(vec2Int);
            }
            else {
               /* if (map[x][y].getEntityType()!=EntityType.RESOURCE)
                {
                    if (map[x][y].getPlayerId()==entity.getPlayerId())
                    {
                        if (map[x][y].getEntityType()==EntityType.RANGED_UNIT || map[x][y].getEntityType()==EntityType.MELEE_UNIT || map[x][y].getEntityType()==EntityType.BUILDER_UNIT)
                        {
                            checkNextPositionUnit(map[x][y]);

                        }
                    }
                }*/
            }
        }

        entity.setInitNextTickPosition(true);
    }

    public ArrayList<Vec2Int> getPositionBuildHouseV2(EntityProperties entityProperties,MapPotField mapPotField, boolean baseCreate) {
        ArrayList<Vec2Int> arrayList = new ArrayList<>();

        if (FinalConstant.isFogOfWar())
        {

            for (int i=0; i<housePositionFogOfWar.length; i++)
            {
                Vec2Int vec2Int = Vec2Int.createVector(housePositionFogOfWar[i][0],housePositionFogOfWar[i][1]);


                if (checkSafeСreationBuilding(vec2Int, entityProperties,mapPotField)){
                    if (baseCreate==false && checkPositionCreateBaseFogOfWar())
                    {
                        
                    }
                    arrayList.add(vec2Int.add(1,1));
                }
            }
            
            return arrayList;
        }
        
        for (int i=0; i<housePosition.length; i++)
        {
            Vec2Int vec2Int = Vec2Int.createVector(housePosition[i][0],housePosition[i][1]);


            if (checkSafeСreationBuilding(vec2Int, entityProperties,mapPotField)){
                arrayList.add(vec2Int.add(1,1));
            }
        }

        if (baseCreate)
        {
            for (int i=0; i<housePositionV3.length; i++)
            {
                Vec2Int vec2Int = Vec2Int.createVector(housePositionV3[i][0],housePositionV3[i][1]);


                if (checkSafeСreationBuilding(vec2Int, entityProperties,mapPotField)){
                    arrayList.add(vec2Int.add(1,1));
                }
            }

        if (arrayList.size()==0)
        {
            for (int i=0; i<housePositionV2.length; i++)
            {
                Vec2Int vec2Int = Vec2Int.createVector(housePositionV2[i][0],housePositionV2[i][1]);


                if (checkSafeСreationBuilding(vec2Int, entityProperties,mapPotField)){
                    arrayList.add(vec2Int.add(1,1));
                }
            }
        }
        }

        return arrayList;
    }

    private boolean checkPositionCreateBaseFogOfWar() {
        return true;
    }

    public ArrayList<Vec2Int> getPositionBuildBase(EntityProperties entityProperties,MapPotField mapPotField) {
        ArrayList<Vec2Int> arrayList = new ArrayList<>();


        if (FinalConstant.isFogOfWar())
        {
            for (int i=0; i<basePositionV2.length; i++)
            {
                Vec2Int vec2Int = Vec2Int.createVector(basePositionV2[i][0],basePositionV2[i][1]);

                if (checkSafeСreationBuilding(vec2Int, entityProperties,mapPotField)){
                    arrayList.add(vec2Int.add(1,1));
                }
            }

            return arrayList;
        }


        for (int i=0; i<basePosition.length; i++)
        {
            Vec2Int vec2Int = Vec2Int.createVector(basePosition[i][0],basePosition[i][1]);


            if (checkSafeСreationBuilding(vec2Int, entityProperties,mapPotField)){
                arrayList.add(vec2Int.add(1,1));
            }
        }

        return arrayList;
    }

    public boolean checkUnit(MyEntity[][] mapNextTick, Vec2Int newPosition) {
        if (mapNextTick[newPosition.getX()][newPosition.getY()].getEntityType()==EntityType.BUILDER_UNIT ||
                mapNextTick[newPosition.getX()][newPosition.getY()].getEntityType()==EntityType.RANGED_UNIT ||
                mapNextTick[newPosition.getX()][newPosition.getY()].getEntityType()==EntityType.MELEE_UNIT) return true;
        else return false;
    }

    public ArrayList<MyEntity> getAllEntityResource() {
        return allEntityResource;
    }

    public ArrayList<MyEntity> getAllEntityUnits() {
        return allEntityUnits;
    }

    public boolean getSpecialCheckBuilderTask(Vec2Int vector) {

        byte[][] bytes = aroundArray;

        for (int i=0; i<bytes.length; i++)
        {
            int x = vector.getX() + bytes[i][0];
            int y = vector.getY() + bytes[i][1];

            if (!checkCoord(x,y)) continue;

            MyEntity entity = map[x][y];

            if (entity.getEntityType()==EntityType.RESOURCE) {
                return true;
            }

            switch (entity.getEntityType()){
                case WALL:
                case HOUSE:
                case BUILDER_BASE:
                case MELEE_BASE:
                case RANGED_BASE:
                case TURRET:
                    if (entity.getHealth()<FinalConstant.getEntityProperties(entity).getMaxHealth()) return true;
                    break;
                case RESOURCE:
                    return true;
            }
        }

        return false;
    }

    public Vec2Int getPositionToResourceSpecial(Vec2Int position, Vec2Int positionResource){
        int x = positionResource.getX();
        int y = positionResource.getY();

        byte[][] array = GlobalMap.aroundArray;

        int count = 0;

        Vec2Int current = null;
        double minDis = 0xFFFF;

        for (int i=0; i<array.length; i++) {
            Vec2Int vec2Int = Vec2Int.createVector(x + array[i][0],y + array[i][1]);

            if (!checkEmpty(mapNextTick,vec2Int)) continue;

            double dis = position.distance(vec2Int);
            if (dis<minDis)
            {
                minDis = dis;
                current = vec2Int;
            }
        }

        return current;
    }

    public Vec2Int getSpecialPositionAttackBuilding(MyEntity myUnit, MyEntity enemy) {
        ArrayList<Vec2Int> arrayList = getCoordAround(enemy.getPosition(),FinalConstant.getEntityProperties(enemy).getSize(),true);

        Vec2Int position = myUnit.getPosition();
        Vec2Int current = null;
        double minDis = 0xFFFF;


        for (int i=0; i<arrayList.size(); i++) {
            Vec2Int vec2Int = arrayList.get(i);
           // if (!checkEmpty(map,vec2Int)) continue;

            double dis = position.distance(vec2Int);
            if (dis<minDis)
            {
                minDis = dis;
                current = vec2Int;
            }
        }
        return current;
    }
}
