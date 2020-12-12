package strategy.map.potfield;

import model.*;
import strategy.*;

import java.util.ArrayList;

import static strategy.GlobalMap.checkCoord;

public class MapPotField {

    Field[][] mMapPotField;
    int size;

    BiomResourceMap mBiomResourceMap;

    GlobalMap mGlobalMap;

    Vec2Int leftPosition;
    Vec2Int rightPosition;

    public MapPotField(int size) {
        this.size = size;
        mMapPotField = new Field[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mMapPotField[i][j] = new Field(new Vec2Int(i,j));
            }
        }
        mBiomResourceMap = new BiomResourceMap();


    }

    public void update(GlobalManager globalManager) {
        mGlobalMap = globalManager.getGlobalMap();
        mBiomResourceMap.clear();
        leftPosition = null;
        rightPosition = null;

        GlobalStatistic globalStatistic = globalManager.getGlobalStatistic();

        MyEntity[][] map = mGlobalMap.getMap();

        clearField(map);

        int districtResource = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (map[i][j].getEntityType() == EntityType.RESOURCE) {
                    if (mMapPotField[i][j].getDistrictResource()==-1)
                    {
                      //  addResource(i,j);
                    }
                }

                if (map[i][j].getPlayerId() == null) continue;

                // отмечаем свои владения
                if (map[i][j].getPlayerId() == FinalConstant.getMyID()) {

                    addPlayerArea(map[i][j], new Vec2Int(i, j), map);

                    addSafare(map[i][j], map);
                } else {
                    addDanger(map[i][j], map);
                }

                // отмечаем вражеские войска
            }
        }


        //    globalMap.getMap()

    }

    private void addResource(int x, int y) {
        if (mGlobalMap.getMap()[x][y].getEntityType()!=EntityType.RESOURCE) return;
        if (mMapPotField[x][y].getDistrictResource() == mBiomResourceMap.getSizeBiom()) return;

        mBiomResourceMap.addSizeBiom();

        recursiveResource(x,y);
    }

    private void recursiveResource(int x,int y){
        if (!mGlobalMap.checkCoord(x,y)) return;

        if (mGlobalMap.getMap()[x][y].getEntityType()!=EntityType.RESOURCE) return;

        if (mMapPotField[x][y].getDistrictResource()!=-1) return;

        mMapPotField[x][y].setDistrictResource(mBiomResourceMap.getSizeBiom());

        mBiomResourceMap.addBiomResource( mMapPotField[x][y],mGlobalMap.getMap()[x][y].getHealth());

        recursiveResource(x+1,y);

        recursiveResource(x-1,y);

        recursiveResource(x,y+1);

        recursiveResource(x,y-1);
    }

    private void addSafare(MyEntity entity, MyEntity[][] map) {
        if (entity.getEntityType() != EntityType.RANGED_UNIT && entity.getEntityType() != EntityType.MELEE_UNIT && entity.getEntityType() != EntityType.TURRET)
            return;

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        if (entity.getPosition() == null) {
            int k = 0;
        }

        Vec2Int position = entity.getPosition();

        EntityType entityType = entity.getEntityType();

        if (entity.getEntityType() == EntityType.MELEE_UNIT) {
            entityType = EntityType.RANGED_UNIT;
        }

        for (int i = 0; i < GlobalMap.getRadiusUnit(entityType).length; i++) {
            int x = GlobalMap.getRadiusUnit(entityType)[i][0];
            int y = GlobalMap.getRadiusUnit(entityType)[i][1];

            if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

            if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;
            if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

            switch (entity.getEntityType()) {
                case MELEE_UNIT:
                    mMapPotField[x + position.getX()][y + position.getY()].addSafetyMelee();
                    break;
                case RANGED_UNIT:
                    mMapPotField[x + position.getX()][y + position.getY()].addSafetyRanger();
                    break;
                case TURRET:
                    mMapPotField[x + position.getX()][y + position.getY()].addSafetyTurret();
                    break;
            }
        }

    }

    private void addDanger(MyEntity entity, MyEntity[][] map) {
        if (entity.getEntityType() != EntityType.RANGED_UNIT && entity.getEntityType() != EntityType.MELEE_UNIT && entity.getEntityType() != EntityType.TURRET)
            return;

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        Vec2Int position = entity.getPosition();

        // danger
        for (int i = 0; i < GlobalMap.getRadiusUnit(entity.getEntityType()).length; i++) {
            int x = GlobalMap.getRadiusUnit(entity.getEntityType())[i][0];
            int y = GlobalMap.getRadiusUnit(entity.getEntityType())[i][1];

            if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

            if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;
            if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

            switch (entity.getEntityType()) {
                case MELEE_UNIT:
                    mMapPotField[x + position.getX()][y + position.getY()].addDangerMelee();
                    break;
                case RANGED_UNIT:
                    mMapPotField[x + position.getX()][y + position.getY()].addDangerRanger();
                    break;
                case TURRET:
                    if (entity.isActive()) {
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerTurret();
                    }
                    break;
            }
        }
        int sizeMyUnitTwoCounter =0;
        if (entity.getEntityType()==EntityType.RANGED_UNIT)
        {
          //  mGlobalMap.checkNextPositionUnit(entity);

            ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(position,GlobalMap.rangerTwoContourArray,-1,FinalConstant.getMyID(),true,EntityType.RANGED_UNIT);
            sizeMyUnitTwoCounter = arrayList.size();
        }

        // counter

        //if ((entity.isUpPosition() || entity.isDownPosition() || entity.isLeftPosition() || entity.isRigthPosition()) || entity.getEntityType()!=EntityType.RANGED_UNIT) {
            for (int i = 0; i < GlobalMap.getRadiusContourUnit(entity.getEntityType()).length; i++) {
                int x = GlobalMap.getRadiusContourUnit(entity.getEntityType())[i][0];
                int y = GlobalMap.getRadiusContourUnit(entity.getEntityType())[i][1];

                if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

                if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;
                if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                switch (entity.getEntityType()) {
                    case MELEE_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourMelee();
                        break;
                    case RANGED_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourRanger();
                        mMapPotField[x + position.getX()][y + position.getY()].setSafetyContour(sizeMyUnitTwoCounter);
                        break;
                    case TURRET:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourTurret();
                        break;
                }
            }
       // }

    }

    private int resourceDirect(Field field) {
        if (field.getX() - 1 > 0) {
            if (mMapPotField[field.getX() - 1][field.getY()].getDistrictResource() != -1)
                return mMapPotField[field.getX() - 1][field.getY()].getDistrictResource();
        }

        if (1 + field.getX() < FinalConstant.getMapSize()) {
            if (mMapPotField[field.getX() + 1][field.getY()].getDistrictResource() != -1)
                return mMapPotField[field.getX() + 1][field.getY()].getDistrictResource();
        }

        if (field.getY() - 1 > 0) {
            if (mMapPotField[field.getX()][field.getY() - 1].getDistrictResource() != -1)
                return mMapPotField[field.getX()][field.getY() - 1].getDistrictResource();
        }

        if (1 + field.getY() < FinalConstant.getMapSize()) {
            if (mMapPotField[field.getX()][field.getY() + 1].getDistrictResource() != -1)
                return mMapPotField[field.getX()][field.getY() + 1].getDistrictResource();
        }

        return -1;
    }

    private void clearField(MyEntity[][] map) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mMapPotField[i][j].clear();
                mMapPotField[i][j].setMyEntity(map[i][j]);
            }
        }
    }


    private void addPlayerArea(MyEntity myEntity, Vec2Int position, MyEntity[][] map) {
        EntityProperties entityProperties = FinalConstant.getEntityProperties(myEntity);

        switch (myEntity.getEntityType()) {

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
                for (int x = 0; x >= -size; x--) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = 0; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX() + x][position.getY() + y];

                        if (checkYNUll) {
                            break;
                        }

                        if (entity.getEntityType() == EntityType.RESOURCE) {
                            if (y == 0) {
                                checkYNUll = true;
                            }
                            break;
                        }


                        //     if (entity!=null)
                        int sum = 2 * size - (Math.abs(x) + Math.abs(y)) / 2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                    for (int y = -1; y >= -sizeY; y--) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX() + x][position.getY() + y];

                        if (entity.getEntityType() == EntityType.RESOURCE) break;


                        //     if (entity!=null)
                        int sum = 2 * size - (Math.abs(x) + Math.abs(y)) / 2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                }
                checkYNUll = false;
                for (int x = 0; x <= size; x++) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = 0; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX() + x][position.getY() + y];

                        if (checkYNUll) {
                            break;
                        }

                        if (entity.getEntityType() == EntityType.RESOURCE) {
                            if (y == 0) {
                                checkYNUll = true;
                            }
                            break;
                        }

                        //     if (entity!=null)
                        int sum = 2 * size - (Math.abs(x) + Math.abs(y)) / 2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                    for (int y = -1; y >= -sizeY; y--) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX() + x][position.getY() + y];

                        if (entity.getEntityType() == EntityType.RESOURCE) break;


                        //     if (entity!=null)
                        int sum = 2 * size - (Math.abs(x) + Math.abs(y)) / 2;
                        //   int sum = 5;
                        //   System.out.println("sumAdd:  " + sum);
                        mMapPotField[x + position.getX()][y + position.getY()].addPlayerArea(sum);
                    }
                }

                size = 10;
                for (int x = -size; x < size; x++) {
                    if (x + position.getX() < 0 || x + position.getX() >= FinalConstant.getMapSize()) continue;

                    int sizeY = size - Math.abs(x);
                    for (int y = -sizeY; y <= sizeY; y++) {
                        if (y + position.getY() < 0 || y + position.getY() >= FinalConstant.getMapSize()) continue;

                        MyEntity entity = map[position.getX() + x][position.getY() + y];

                        // if (entity.getEntityType()==EntityType.RESOURCE) break;


                        //     if (entity!=null)
                        int sum = 2 * size - (Math.abs(x) + Math.abs(y)) / 2;
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
        if (getMapPotField() != null) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (Final.DANGER_AREA) {
                        if (!getMapPotField()[i][j].checkDanger()) {
                            FinalGraphic.sendSquare(debugInterface, new Vec2Int(i, j), 1, FinalGraphic.getColorDinamicRED(getMapPotField()[i][j].getSumDanger(), 10));
                        }
                    }

                    if (Final.SAFETY_AREA) {
                        if (getMapPotField()[i][j].getSumSafaty() > 0) {
                            FinalGraphic.sendSquare(debugInterface, new Vec2Int(i, j), 1, FinalGraphic.getColorDinamicBLUE(getMapPotField()[i][j].getSumSafaty(), 10));
                        }
                    }

                    if (Final.DANGER_CONTOUR_AREA) {
                        if (getMapPotField()[i][j].getSumDangerContour() > 0) {
                            FinalGraphic.sendSquare(debugInterface, new Vec2Int(i, j), 1, FinalGraphic.getColorDinamicGREEN(getMapPotField()[i][j].getSumSafaty(), 10));
                        }
                    }

                    if (Final.DANGER_AND_SAFETY_AREA_TEXT) {
                        if (getMapPotField()[i][j].getSumDanger()+getMapPotField()[i][j].getSumSafaty()+getMapPotField()[i][j].getSumDangerContour()>0) {
                            FinalGraphic.sendText(debugInterface, new Vec2Float(i * 1.0f, j * 1.0f + 0.85f), 11, "D:" +
                                    getMapPotField()[i][j].getDangerRanger() + "," + getMapPotField()[i][j].getDangerMelee() + "," + getMapPotField()[i][j].getDangerTurret());
                            FinalGraphic.sendText(debugInterface, new Vec2Float(i * 1.0f, j * 1.0f + 0.65f), 11, "C:" +
                                    getMapPotField()[i][j].getDangerContourRanger() + "," + getMapPotField()[i][j].getDangerContourMelee() + "," + getMapPotField()[i][j].getDangerContourTurret());
                            FinalGraphic.sendText(debugInterface, new Vec2Float(i * 1.0f, j * 1.0f + 0.45f), 11, "SC:" +
                                    getMapPotField()[i][j].getSafetyContour());
                        }
                    }

                    if (Final.BIOM_RESOURCE) {
                        if (getMapPotField()[i][j].getDistrictResource()>=0) {
                            FinalGraphic.sendText(debugInterface, new Vec2Float(i * 1.0f, j * 1.0f + 0.5f), 11, "" +
                                    getMapPotField()[i][j].getDistrictResource());
                        }
                    }


                    if (Final.PLAYER_AREA_TWO) {
                        if (getMapPotField()[i][j].getPlayerAreaTwo()>0) {
                            // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                            FinalGraphic.sendSquare(debugInterface, new Vec2Int(i, j), 1, FinalGraphic.getColorDinamicGREEN(getMapPotField()[i][j].getPlayerAreaTwo(), 30));
                        }
                    }

                   // if (getMapPotField()[i][j].getPlayerArea() <= 0) continue;

                    if (Final.PLAYER_AREA) {
                        if (getMapPotField()[i][j].getPlayerArea()>0) {
                            // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                            FinalGraphic.sendSquare(debugInterface, new Vec2Int(i, j), 1, FinalGraphic.getColorDinamicGREEN(getMapPotField()[i][j].getPlayerArea(), 30));
                        }
                    }


                }
            }


            if (Final.PLAYER_AREA) {
                if (getPositionDefencePlayerArea(0) != null && getPositionDefencePlayerArea(1) != null) {
                    // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                    FinalGraphic.sendSquare(debugInterface, getPositionDefencePlayerArea(0), 1, FinalGraphic.COLOR_BLACK);
                    FinalGraphic.sendSquare(debugInterface, getPositionDefencePlayerArea(1), 1, FinalGraphic.COLOR_BLACK);
                }
            }

            if (Final.PLAYER_AREA) {
                if (getPositionDefencePlayerArea(0) != null && getPositionDefencePlayerArea(1) != null) {
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

    public MyEntity getNearestPlayerIntoPlayerArea(Vec2Int vec2Int, int myID) {
        double minDis = 0xFFFFF;
        MyEntity current = null;

        MyEntity[][] map = mGlobalMap.getMap();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId() == myID) continue;

                if (mMapPotField[i][j].getPlayerAreaTwo() <= 0) continue;

                if (map[i][j].getCountAttackingUnit()>0) {
                    continue;
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

    public boolean checkAttackBase(int myID, GlobalStatistic globalStatistic) {

        MyEntity[][] map = mGlobalMap.getMap();

        ArrayList<Integer> integers = new ArrayList<>();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId() == myID) continue;

                if (mMapPotField[i][j].getPlayerArea() <= 0) continue;

                boolean check = false;
                for (int k = 0; k < integers.size(); k++) {
                    if (integers.get(k) == map[i][j].getPlayerId()) {
                        check = true;
                        break;
                    }
                }
                if (!check) integers.add(map[i][j].getPlayerId());

            }
        }


        MyPlayer myPlayer = globalStatistic.getMyPlayer();


        if (integers.size()>0) return true;

        for (int k = 0; k < integers.size(); k++) {
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

    public boolean checkAttackBaseTwo(int myID) {

        MyEntity[][] map = mGlobalMap.getMap();

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j].getEntityType() == EntityType.Empty) continue;

                if (map[i][j].getPlayerId() == null) continue;

                if (map[i][j].getPlayerId() == myID) continue;

                if (mMapPotField[i][j].getPlayerAreaTwo() <= 0) continue;

                return true;
            }
        }
        return false;
    }

    public Vec2Int getPositionDefencePlayerArea(int position) {


        if (leftPosition == null) {
            Vec2Int max = new Vec2Int(0, 0);
            if (getMapPotField() != null) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (getMapPotField()[i][j].getPlayerArea() <= 0) continue;

                        if (i < 30) {
                            if (j > max.getY()) {
                                max = new Vec2Int(i, j);
                                leftPosition = max;
                            }
                        }
                    }
                }
            }

            if (leftPosition != null) {
                leftPosition.setY(leftPosition.getY() - 5);
            }
        }

        if (rightPosition == null) {
            Vec2Int max = new Vec2Int(0, 0);
            if (getMapPotField() != null) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (getMapPotField()[i][j].getPlayerArea() <= 0) continue;

                        if (j < 30) {
                            if (i > max.getX()) {
                                max = new Vec2Int(i, j);
                                rightPosition = max;
                            }
                        }
                    }
                }
            }
            if (rightPosition != null) {
                rightPosition.setX(rightPosition.getX() - 5);
            }
        }

        switch (position) {
            case 0:
                return leftPosition;
            case 1:
                return rightPosition;
        }

        return null;
    }

    public Vec2Int getDangerAttackRanger(MyEntity entity) {

        byte[][] bytes = new byte[][]{
                {-1, 0}, {0, -1}, {0, 0},{0, 1}, {1, 0},
        };

        Vec2Int position = entity.getPosition();
        Field current = null;

        Field currentNoDanger = null;
        Field currentContour = null;
        Field currentSafety = null;

        int minDanger = 0xFFFF;
        int minCounterDanger = 0xFFFF;
        int maxCounterOnlyUnitDanger = 0;
        int maxSafety = 0;

        for (int i = 0; i < bytes.length; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            if (!mGlobalMap.checkEmpty(mGlobalMap.getMapNextTick(),newPosition)) continue;

            Field field = mMapPotField[newPosition.getX()][newPosition.getY()];

            if (field.getSumDanger(entity.getEntityType()) > 0) {
                /// проверяем контур атаки (это значит возможно на следующем ходу там появится второй юнит)
                if (field.getSumDanger(entity.getEntityType()) == minDanger){
                    if (current.getSumDangerContour()>field.getSumDangerContour()) {
                        minDanger = field.getSumDanger(entity.getEntityType());
                        current = field;
                    }
                }

                if (field.getSumDanger(entity.getEntityType()) < minDanger){
                    minDanger = field.getSumDanger(entity.getEntityType());
                    current = field;
                }
            }

            if (field.getSumDangerContourOnlyUnit()>maxCounterOnlyUnitDanger)
            {
                maxCounterOnlyUnitDanger = field.getSumDangerContourOnlyUnit();
            }

            if (field.getSumDangerContour() > 0 && field.getSumDangerContour() < minCounterDanger) {
             //   minCounterDanger = field.getSumDangerContour();
             //   currentContour = field;
            }

            if (field.getSumDanger(entity.getEntityType()) == 0
                    && field.getSumDangerContourOnlyUnit() < 2
            ) {
                currentNoDanger = field;

                if (field.getSumSafaty(entity.getEntityType()) > 0 &&
                        field.getSumSafaty(entity.getEntityType()) < maxSafety) {
                    maxSafety = field.getSumSafaty(entity.getEntityType());
                    currentSafety = field;
                }
            }


        }

        if (minDanger == 0xFFFF) {

            if (maxCounterOnlyUnitDanger>1)
            {
                if (currentNoDanger!=null) {
                    return currentNoDanger.getPosition();
                }
            }

            return null;
        }

       /* if (maxSafety>=3 && minDanger!=0xFFFF)
        {
            return currentSafety;
        }*/
     /*   Field currentPosition = mMapPotField[position.getX()][position.getY()];
        Final.DEBUG("DangerAttackRange", "T: "+FinalConstant.getCurrentTik()+"ID: " + entity.getId()
        + " MD: " + minDanger + " " + currentPosition.toString() + " CD: " + (currentNoDanger==null ? "null" : currentNoDanger.toString()));
*/
        if (minDanger > 1) {

            // надо чекнуть много ли наших рядом с турелью и только тогда заходим и выносим
            if (minDanger==4)
            {
                if (current.getDangerTurret()>=4)
                {
                    MyEntity vec2Int = mGlobalMap.getNearestPlayer(entity.getPosition(),FinalConstant.getMyID(),EntityType.TURRET);

                    ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(vec2Int.getPosition(),GlobalMap.turretAndContourArray,-1,FinalConstant.getMyID(),true,EntityType.ALL);
                    if (arrayList.size()>=5)
                    {
                        return current.getPosition();
                    }
                }
            }

            Field field = mMapPotField[position.getX()][position.getY()];

            if (field.getSumDangerContour()<=1 && field.getSumDanger()==0) return position;

           /* if (minCounterDanger==1)
            {
                return currentContour.getPosition();
            }*/

            if (currentNoDanger != null) return currentNoDanger.getPosition();

            if (currentSafety != null) return currentSafety.getPosition();

            if (mMapPotField[entity.getPosition().getX()][entity.getPosition().getY()].getSumDanger()==0) return entity.getPosition();
        }

        if (minDanger==1 && current.getSumDangerContour()>0 && current.getSumDangerContour()-current.getSafetyContour()+1>0)
        {
            Field field = mMapPotField[position.getX()][position.getY()];

            if (field.getSumDangerContour()<=1) return position;

            if (currentNoDanger!=null) {
                return currentNoDanger.getPosition();
            }
        }

        return current.getPosition();
    }

    public boolean checkPlayerArea(Vec2Int position) {
        return mMapPotField[position.getX()][position.getY()].getPlayerArea() > 0;
    }

    public boolean checkSafety(Vec2Int vec2Int) {
        return checkSafety(vec2Int, 1, 1);
    }

    public boolean checkSafety(Vec2Int vec2Int, int size) {
        return checkSafety(vec2Int, size, size);
    }

    public boolean checkSafety(Vec2Int vec2Int, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!checkCoord(vec2Int.getX() + x, vec2Int.getY() + y)) return false;
                if (mGlobalMap.getMap()[vec2Int.getX() + x][vec2Int.getY() + y].getEntityType() != EntityType.Empty) return false;
                if (mMapPotField[vec2Int.getX() + x][vec2Int.getY() + y].getSumDanger() > 0) return false;
                if (mMapPotField[vec2Int.getX() + x][vec2Int.getY() + y].getSumDangerContour() > 0) return false;
            }
        }

        return true;
    }
}
