package strategy.map.potfield;

import model.*;
import strategy.*;

import java.util.ArrayList;

import static strategy.GlobalMap.checkCoord;

public class MapPotField {

    final static public String TAG = "MapPotField";

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
                mMapPotField[i][j] = new Field(Vec2Int.createVector(i,j));
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

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mMapPotField[i][j].setMyEntity(map[i][j]);
            }
        }

        int district= 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (map[i][j].getEntityType() == EntityType.RESOURCE) {
                    if (mMapPotField[i][j].getDistrictResource()==-1)
                    {
                     //   addResource(i,j);
                    }
                }

                if (map[i][j].getEntityType() == EntityType.Empty) {
                    if (mMapPotField[i][j].getDistrict()==-1)
                    {
                        addDistrict(i,j,district);
                        district++;
                    }
                }

                if (map[i][j].getPlayerId() == null) continue;

                // отмечаем свои владения
                if (map[i][j].getPlayerId() == FinalConstant.getMyID()) {

                    if (Final.PLAYER_AREA_CALCULATE){
                        addPlayerArea(map[i][j], Vec2Int.createVector(i, j), map);
                    }

                    addSafare(map[i][j], map);
                } else {
                    addDanger(map[i][j], map);

                    if (Final.BUILD_TURRET_SPECIAL_V2 || Final.BUILD_TURRET_SPECIAL_V2_ALL)
                    {
                        addBuildUnit(map[i][j], map);
                    }
                }

                // отмечаем вражеские войска
            }
        }


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (map[i][j].getPlayerId() == null) continue;
                // отмечаем свои владения
                if (map[i][j].getPlayerId()!= FinalConstant.getMyID()) {
                    addSafetyContour(map[i][j], map);
                }

                // отмечаем вражеские войска
            }
        }


        //    globalMap.getMap()

    }

    private void addBuildUnit(MyEntity entity, MyEntity[][] map) {
        if (entity.getEntityType() != EntityType.BUILDER_UNIT)
            return;

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        Vec2Int position = entity.getPosition();

        byte[][] bytes = GlobalMap.sightRangeUnit;

        // danger
        for (int i = 0; i < bytes.length; i++) {
            int x = bytes[i][0];
            int y = bytes[i][1];

            if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

            mMapPotField[x + position.getX()][y + position.getY()].addBuildUnit();
        }
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

    private void addDistrict(int x, int y, int district) {
        if (mGlobalMap.getMap()[x][y].getEntityType()==EntityType.RESOURCE) return;
        if (mMapPotField[x][y].getDistrict() != -1) return;

        recursiveDistrict(x,y,district);
    }

    private void recursiveDistrict(int x,int y, int district){
        if (!mGlobalMap.checkCoord(x,y)) return;

        if (mGlobalMap.getMap()[x][y].getEntityType()==EntityType.RESOURCE) return;
        if (mGlobalMap.getMap()[x][y].getEntityType()==EntityType.HOUSE ||
                mGlobalMap.getMap()[x][y].getEntityType()==EntityType.BUILDER_BASE ||
                mGlobalMap.getMap()[x][y].getEntityType()==EntityType.RANGED_BASE ||
                mGlobalMap.getMap()[x][y].getEntityType()==EntityType.MELEE_BASE ||
                mGlobalMap.getMap()[x][y].getEntityType()==EntityType.TURRET
        ) return;

        if (mMapPotField[x][y].getDistrict()!=-1) return;

        mMapPotField[x][y].setDistrict(district);

        recursiveDistrict(x+1,y,district);

        recursiveDistrict(x-1,y,district);

        recursiveDistrict(x,y+1,district);

        recursiveDistrict(x,y-1,district);
    }

    private void addSafare(MyEntity entity, MyEntity[][] map) {
        if (entity.getEntityType() != EntityType.TURRET && entity.getEntityType() != EntityType.HOUSE)
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


        byte[][] bytes = GlobalMap.getRadiusUnit(entityType);
        for (int i = 0; i < bytes.length; i++) {
            int x = bytes[i][0];
            int y = bytes[i][1];

            if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

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
                case HOUSE:
                    mMapPotField[x + position.getX()][y + position.getY()].addSafetyHouse();
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
      /*  int sizeMyUnitTwoCounter =0;
        if (entity.getEntityType()==EntityType.RANGED_UNIT)
        {
          //  mGlobalMap.checkNextPositionUnit(entity);

            ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(position,GlobalMap.rangerTwoContourArray,-1,FinalConstant.getMyID(),true,EntityType.RANGED_UNIT,true);
            sizeMyUnitTwoCounter = arrayList.size();
        }*/

        // counter

        //if ((entity.isUpPosition() || entity.isDownPosition() || entity.isLeftPosition() || entity.isRigthPosition()) || entity.getEntityType()!=EntityType.RANGED_UNIT) {



          /*  for (int i = 0; i < GlobalMap.getRadiusContourUnit(entity.getEntityType()).length; i++) {
                int x = GlobalMap.getRadiusContourUnit(entity.getEntityType())[i][0];
                int y = GlobalMap.getRadiusContourUnit(entity.getEntityType())[i][1];

                if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

                switch (entity.getEntityType()) {
                    case MELEE_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourMelee();
                        break;
                    case RANGED_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourRanger();
                   //     mMapPotField[x + position.getX()][y + position.getY()].setSafetyContour(sizeMyUnitTwoCounter);
                        break;
                    case TURRET:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourTurret();
                        break;
                }
            }*/
       // }

        /// массив заменяем на проверку 4 граней и 4 точек
        // сначала проверяем есть ли у юнита припятсвия с  4 сторон
        boolean left =true;
        boolean rigth = true;
        boolean up = true;
        boolean down = true;

        Vec2Int vec2IntLeft = entity.getPosition().add(-1,0);
        Vec2Int vec2IntRigth = entity.getPosition().add(1,0);
        Vec2Int vec2IntUp = entity.getPosition().add(0,1);
        Vec2Int vec2IntDown = entity.getPosition().add(0,-1);
        if (mGlobalMap.checkCoord(vec2IntLeft)){
            if (mGlobalMap.getMap()[vec2IntLeft.getX()][vec2IntLeft.getY()].getEntityType()==EntityType.RESOURCE ||
                    mGlobalMap.getMap()[vec2IntLeft.getX()][vec2IntLeft.getY()].getEntityType()==EntityType.HOUSE ||
                    mGlobalMap.getMap()[vec2IntLeft.getX()][vec2IntLeft.getY()].getEntityType()==EntityType.RANGED_BASE ||
                    mGlobalMap.getMap()[vec2IntLeft.getX()][vec2IntLeft.getY()].getEntityType()==EntityType.BUILDER_BASE ||
                    mGlobalMap.getMap()[vec2IntLeft.getX()][vec2IntLeft.getY()].getEntityType()==EntityType.WALL ||
                    mGlobalMap.getMap()[vec2IntLeft.getX()][vec2IntLeft.getY()].getEntityType()==EntityType.MELEE_BASE
            )
            {
                left = false;
            }
        }

        if (mGlobalMap.checkCoord(vec2IntRigth)){
            if (mGlobalMap.getMap()[vec2IntRigth.getX()][vec2IntRigth.getY()].getEntityType()==EntityType.RESOURCE ||
                    mGlobalMap.getMap()[vec2IntRigth.getX()][vec2IntRigth.getY()].getEntityType()==EntityType.HOUSE ||
                    mGlobalMap.getMap()[vec2IntRigth.getX()][vec2IntRigth.getY()].getEntityType()==EntityType.RANGED_BASE ||
                    mGlobalMap.getMap()[vec2IntRigth.getX()][vec2IntRigth.getY()].getEntityType()==EntityType.BUILDER_BASE ||
                    mGlobalMap.getMap()[vec2IntRigth.getX()][vec2IntRigth.getY()].getEntityType()==EntityType.WALL ||
                    mGlobalMap.getMap()[vec2IntRigth.getX()][vec2IntRigth.getY()].getEntityType()==EntityType.MELEE_BASE
            )
            {
                rigth = false;
            }
        }

        if (mGlobalMap.checkCoord(vec2IntUp)){
            if (mGlobalMap.getMap()[vec2IntUp.getX()][vec2IntUp.getY()].getEntityType()==EntityType.RESOURCE ||
                    mGlobalMap.getMap()[vec2IntUp.getX()][vec2IntUp.getY()].getEntityType()==EntityType.HOUSE ||
                    mGlobalMap.getMap()[vec2IntUp.getX()][vec2IntUp.getY()].getEntityType()==EntityType.RANGED_BASE ||
                    mGlobalMap.getMap()[vec2IntUp.getX()][vec2IntUp.getY()].getEntityType()==EntityType.BUILDER_BASE ||
                    mGlobalMap.getMap()[vec2IntUp.getX()][vec2IntUp.getY()].getEntityType()==EntityType.WALL ||
                    mGlobalMap.getMap()[vec2IntUp.getX()][vec2IntUp.getY()].getEntityType()==EntityType.MELEE_BASE
            )
            {
                up = false;
            }
        }

        if (mGlobalMap.checkCoord(vec2IntDown)){
            if (mGlobalMap.getMap()[vec2IntDown.getX()][vec2IntDown.getY()].getEntityType()==EntityType.RESOURCE ||
                    mGlobalMap.getMap()[vec2IntDown.getX()][vec2IntDown.getY()].getEntityType()==EntityType.HOUSE ||
                    mGlobalMap.getMap()[vec2IntDown.getX()][vec2IntDown.getY()].getEntityType()==EntityType.RANGED_BASE ||
                    mGlobalMap.getMap()[vec2IntDown.getX()][vec2IntDown.getY()].getEntityType()==EntityType.BUILDER_BASE ||
                    mGlobalMap.getMap()[vec2IntDown.getX()][vec2IntDown.getY()].getEntityType()==EntityType.WALL ||
                    mGlobalMap.getMap()[vec2IntDown.getX()][vec2IntDown.getY()].getEntityType()==EntityType.MELEE_BASE
            )
            {
                down = false;
            }
        }

       /* if (left && rigth && down && up)
        {*/
            for (int i = 0; i < GlobalMap.getRadiusContourUnit(entity.getEntityType()).length; i++) {
                int x = GlobalMap.getRadiusContourUnit(entity.getEntityType())[i][0];
                int y = GlobalMap.getRadiusContourUnit(entity.getEntityType())[i][1];

                if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

                switch (entity.getEntityType()) {
                    case MELEE_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourMelee();
                        break;
                    case RANGED_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourRanger();
                        //     mMapPotField[x + position.getX()][y + position.getY()].setSafetyContour(sizeMyUnitTwoCounter);
                        break;
                    case TURRET:
                        mMapPotField[x + position.getX()][y + position.getY()].addDangerContourTurret();
                        break;
                }
            }
       /* }
        else {




        }*/



        // добавляю клетки для от куда можно атаковать ренджам
        for (int i = 0; i < GlobalMap.getRadiusUnit(EntityType.RANGED_UNIT).length; i++) {
            int x = GlobalMap.getRadiusUnit(EntityType.RANGED_UNIT)[i][0];
            int y = GlobalMap.getRadiusUnit(EntityType.RANGED_UNIT)[i][1];

            if (!checkCoord(position.getX() + x, position.getY() + y)) continue;
            if ( mMapPotField[x + position.getX()][y + position.getY()].getMyEntity().getEntityType()==EntityType.RESOURCE) continue;

            switch (entity.getEntityType()) {
                case MELEE_UNIT:
                    mMapPotField[x + position.getX()][y + position.getY()].setAttackPositionRanger(true);
                    break;
                case RANGED_UNIT:
                    mMapPotField[x + position.getX()][y + position.getY()].setAttackPositionRanger(true);
                    break;
                case TURRET:
                    if (entity.isActive()) {
                        mMapPotField[x + position.getX()][y + position.getY()].setAttackPositionRanger(true);
                    }
                    break;
            }
        }
    }

    private void addSafetyContour(MyEntity entity, MyEntity[][] map) {
        if (entity.getEntityType() != EntityType.RANGED_UNIT)
            return;

        EntityProperties entityProperties = FinalConstant.getEntityProperties(entity);

        Vec2Int position = entity.getPosition();


        int sizeMyUnitTwoCounter =0;
      /*  if (entity.getEntityType()==EntityType.RANGED_UNIT)
        {
            //  mGlobalMap.checkNextPositionUnit(entity);

            ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(position,GlobalMap.rangerTwoContourArray,-1,FinalConstant.getMyID(),true,EntityType.RANGED_UNIT,true);
            sizeMyUnitTwoCounter = arrayList.size();
        }

        // counter

        if (sizeMyUnitTwoCounter>0) {
            for (int i = 0; i < GlobalMap.rangerContourArray.length; i++) {
                int x = GlobalMap.rangerContourArray[i][0];
                int y =GlobalMap.rangerContourArray[i][1];

                if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

                switch (entity.getEntityType()) {
                    case MELEE_UNIT:
                        break;
                    case RANGED_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].setSafetyContour(sizeMyUnitTwoCounter);
                        break;
                    case TURRET:
                        break;
                }
            }
        }*/

        sizeMyUnitTwoCounter =0;
        if (entity.getEntityType()==EntityType.RANGED_UNIT)
        {
            //  mGlobalMap.checkNextPositionUnit(entity);

            ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(position,GlobalMap.rangerContourArray,-1,FinalConstant.getMyID(),true,EntityType.RANGED_UNIT,true);
            sizeMyUnitTwoCounter = arrayList.size();
        }

        if (sizeMyUnitTwoCounter>0) {
            for (int i = 0; i < GlobalMap.rangerDamageContourArray.length; i++) {
                int x = GlobalMap.rangerDamageContourArray[i][0];
                int y = GlobalMap.rangerDamageContourArray[i][1];

                if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

                switch (entity.getEntityType()) {
                    case MELEE_UNIT:
                        break;
                    case RANGED_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].setSafetyContour(sizeMyUnitTwoCounter);
                        break;
                    case TURRET:
                        break;
                }
            }

            for (int i = 0; i < GlobalMap.rangerContourArray.length; i++) {
                int x = GlobalMap.rangerContourArray[i][0];
                int y = GlobalMap.rangerContourArray[i][1];

                if (!checkCoord(position.getX() + x, position.getY() + y)) continue;

                switch (entity.getEntityType()) {
                    case MELEE_UNIT:
                        break;
                    case RANGED_UNIT:
                        mMapPotField[x + position.getX()][y + position.getY()].setSafetyContour(sizeMyUnitTwoCounter);
                        break;
                    case TURRET:
                        break;
                }
            }
        }
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

    public void clearField() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                mMapPotField[i][j].clear();
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
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.getColorDinamicRED(getMapPotField()[i][j].getSumDanger(), 10));
                        }
                    }

                    if (Final.SAFETY_AREA) {
                        if (getMapPotField()[i][j].getSumSafaty() > 0) {
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.getColorDinamicBLUE(getMapPotField()[i][j].getSumSafaty(), 10));
                        }
                    }

                    if (Final.DANGER_CONTOUR_AREA) {
                        if (getMapPotField()[i][j].getSumDangerContour() > 0) {
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.getColorDinamicGREEN(getMapPotField()[i][j].getSumSafaty(), 10));
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
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.getColorDinamicGREEN(getMapPotField()[i][j].getPlayerAreaTwo(), 30));
                        }
                    }

                   // if (getMapPotField()[i][j].getPlayerArea() <= 0) continue;

                    if (Final.PLAYER_AREA) {
                        if (getMapPotField()[i][j].getPlayerArea()>0) {
                            // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.getColorDinamicGREEN(getMapPotField()[i][j].getPlayerArea(), 30));
                        }
                    }

                    if (Final.SAFETY_CONTOUR) {
                        if (getMapPotField()[i][j].getSafetyContour()>1) {
                            // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.COLOR_BLUE);
                        }
                    }

                    if (Final.ATTACK_RANGE_REGION) {
                        if (getMapPotField()[i][j].isAttackPositionRanger()) {
                            // FinalGraphic.sendText(debugInterface,new Vec2Float(i+0.5f,j+0.5f),20,"("+getMapPotField()[i][j].getPlayerArea()+")");
                            FinalGraphic.sendSquare(debugInterface, Vec2Int.createVector(i, j), 1, FinalGraphic.COLOR_RED);
                        }
                    }

                    if (Final.DISTRICT_REGION) {
                        if (getMapPotField()[i][j].getDistrict()>=0) {
                            FinalGraphic.sendText(debugInterface, new Vec2Float(i * 1.0f, j * 1.0f + 0.5f), 11, "" +
                                    getMapPotField()[i][j].getDistrict());
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


        // FinalGraphic.sendSquare(debugInterface,Vec2Int.createVector(0,0),getAreaPlayer().width, FinalGraphic.COLOR_WHITE);
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
            Vec2Int max = Vec2Int.createVector(0, 0);
            if (getMapPotField() != null) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (getMapPotField()[i][j].getPlayerArea() <= 0) continue;

                        if (i < 30) {
                            if (j > max.getY()) {
                                max = Vec2Int.createVector(i, j);
                                leftPosition = max;
                            }
                        }
                    }
                }
            }

            if (leftPosition != null) {
                leftPosition = leftPosition.subtract(0,- 5);
            }
        }

        if (rightPosition == null) {
            Vec2Int max = Vec2Int.createVector(0, 0);
            if (getMapPotField() != null) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (getMapPotField()[i][j].getPlayerArea() <= 0) continue;

                        if (j < 30) {
                            if (i > max.getX()) {
                                max = Vec2Int.createVector(i, j);
                                rightPosition = max;
                            }
                        }
                    }
                }
            }
            if (rightPosition != null) {
                rightPosition = rightPosition.subtract(0, - 5);
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

        byte[][] bytes = bytesOne;

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
                minCounterDanger = field.getSumDangerContour();
                currentContour = field;
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

            if (currentContour!=null)
            {
                if (currentContour.getSumDanger()==0 && currentContour.getSumDangerContour()==1)
                {
                    return currentContour.getPosition();
                }
            }

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
        if (current.getSumDanger()>0 || current.getSumDangerContour()>0 && current.getSumDanger()+current.getSumDangerContour()-current.getSafetyContour()+1>0)
        {
            Field field = mMapPotField[position.getX()][position.getY()];

            return current.getPosition();
        }


        if (minDanger > 1) {

            // надо чекнуть много ли наших рядом с турелью и только тогда заходим и выносим
            if (minDanger==4)
            {
                if (current.getDangerTurret()>=4)
                {
                    MyEntity vec2Int = mGlobalMap.getNearestPlayer(entity.getPosition(),FinalConstant.getMyID(),EntityType.TURRET);

                    ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(vec2Int.getPosition(),GlobalMap.turretAndContourArray,-1,FinalConstant.getMyID(),true,EntityType.ALL,false);
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

    public Vec2Int getDangerAttackRangerV2(MyEntity entity) {

        byte[][] bytes = bytesOne;

        Vec2Int position = entity.getPosition();

        Field currentPosition = mMapPotField[position.getX()][position.getY()];

        Field attackPosition = null;
        Field defencePosition = null;
        Field sleepPosition = null;

        int maxDanger = 0;
        int maxCounterDanger = 0;
        int maxCounterOnlyUnitDanger = 0;

        boolean unitPosition;
        boolean emptyPosition;

        for (int i = 0; i < bytes.length; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            unitPosition = false;
            emptyPosition = false;

            if (!mGlobalMap.checkCoord(newPosition)) continue;
            // if (mGlobalMap.getMap(newPosition).getEntityType()==EntityType.RESOURCE) continue;
            if (mGlobalMap.checkUnit(mGlobalMap.getMapNextTick(), newPosition)) {
                unitPosition = true;
            }

            if (mGlobalMap.checkEmpty(mGlobalMap.getMapNextTick(), newPosition)) {
                emptyPosition = true;
            }

            if (newPosition.equals(position)) {
                unitPosition = false;
                emptyPosition = true;
            }

            Field field = mMapPotField[newPosition.getX()][newPosition.getY()];

            if (field.getSumDanger(entity.getEntityType())>maxDanger) {
                maxDanger = field.getSumDanger(entity.getEntityType());
            }

            if (field.getSumDangerContour()>maxCounterDanger) {
               maxCounterDanger = field.getSumDangerContour();
            }

            if (emptyPosition) {
                if (field.getSumDanger() == 1 && field.getSumDangerContour() == 0) {
                    attackPosition = field;
                }

                if (field.getSumDanger() == 0 && field.getSumDangerContour() == 1) {
                    attackPosition = field;
                }

                if (field.getSumDanger() + field.getSumDangerContour() > 0 &&
                        field.getSumDanger() + field.getSumDangerContour() - field.getSafetyContour() <= 0
                ) {
                    attackPosition = field;
                }

                if (currentPosition.getSumDanger() + currentPosition.getSumDangerContour() - currentPosition.getSafetyContour() >
                        field.getSumDanger() + field.getSumDangerContour() - field.getSafetyContour() && currentPosition.getSumDanger()>=field.getSumDanger()) {
                    if (defencePosition==null){
                        defencePosition = field;
                    }
                    else {
                        if (defencePosition.getSumDanger() + defencePosition.getSumDangerContour() - defencePosition.getSafetyContour() >
                                field.getSumDanger() + field.getSumDangerContour() - field.getSafetyContour()  && defencePosition.getSumDanger()>=field.getSumDanger()){
                            defencePosition = field;
                        }
                    }
                }
            }
        }

        Final.DEBUG("DangeAttack: ", FinalConstant.getCurrentTik() + " " + entity.getId() + " " +
                currentPosition.getPosition().toString() + " " +
                (attackPosition!=null?attackPosition.toString():"") + " " +
                (defencePosition!=null?defencePosition.toString():"" + " " +
                (currentPosition.getSumDanger()+currentPosition.getSumDangerContour()-currentPosition.getSafetyContour())
                )
        );

        if (attackPosition!=null) {
            return attackPosition.getPosition();
        }

        if (currentPosition.getSumDanger()+currentPosition.getSumDangerContour()-currentPosition.getSafetyContour()>0 && defencePosition!=null)
        {
            return defencePosition.getPosition();
        }

        if (maxDanger>0 || maxCounterDanger>0)
        {
            return position;
        }

        return null;
    }


    PositionAnswer mPositionAnswer = new PositionAnswer();

    public PositionAnswer getDangerAttackRangerV3(MyEntity entity, boolean needMove) {

        mPositionAnswer.clear();

        AttackRangeAnswer attackRangeAnswer = calculateAttackRange(entity,needMove);

        Field currentPosition = mMapPotField[entity.getPosition().getX()][entity.getPosition().getY()];

        Final.DEBUG("DangeAttack: ", FinalConstant.getCurrentTik() + " " + entity.getId() + " " +
                currentPosition.getPosition().toString() + " " + " NM: " + needMove + " " +
                (attackRangeAnswer.attackPosition!=null?attackRangeAnswer.attackPosition.toString():"") + " " +
                (attackRangeAnswer.defencePosition!=null?attackRangeAnswer.defencePosition.toString():"") + " " +
                (attackRangeAnswer.defencePositionUnit!=null?attackRangeAnswer.defencePositionUnit.toString():"") + " " +
                (currentPosition.getSumDanger()+currentPosition.getSumDangerContour()-currentPosition.getSafetyContour())
        );

        if (!needMove){
            if (attackRangeAnswer.attackPosition!=null) {
                return mPositionAnswer.init(attackRangeAnswer.attackPosition.getPosition());
            }

            if (currentPosition.getSumDanger()+currentPosition.getSumDangerContour()-currentPosition.getSafetyContour()>0 && attackRangeAnswer.defencePosition!=null)
            {
                return mPositionAnswer.init(attackRangeAnswer.defencePosition.getPosition());
            }

            if (attackRangeAnswer.maxDanger>0 || attackRangeAnswer.maxCounterDanger>0)
            {

                if (attackRangeAnswer.defencePosition!=null)
                {
                    return mPositionAnswer.init(attackRangeAnswer.defencePosition.getPosition());
                }
                else {
                    if (attackRangeAnswer.defencePositionUnit!=null)
                    {
                        return mPositionAnswer.init(attackRangeAnswer.defencePositionUnit.getPosition(),attackRangeAnswer.myEntityUnit);
                    }
                    else {
                        return mPositionAnswer.init(entity.getPosition());
                    }
                }
                }
        }

        if (needMove)
        {
            if (attackRangeAnswer.defencePosition!=null)
            {
                return mPositionAnswer.init(attackRangeAnswer.defencePosition.getPosition());
            }

            if (attackRangeAnswer.defencePositionUnit!=null)
            {
                return mPositionAnswer.init(attackRangeAnswer.defencePositionUnit.getPosition(),attackRangeAnswer.myEntityUnit);
            }
        }

        return null;
    }

    public AttackRangeAnswer calculateAttackRange(MyEntity entity, boolean needMove)
    {
        AttackRangeAnswer attackRangeAnswer = new AttackRangeAnswer();

        byte[][] bytes = bytesOne;

        Vec2Int position = entity.getPosition();

        Field currentPosition = mMapPotField[position.getX()][position.getY()];

        boolean unitPosition;
        boolean emptyPosition;

        for (int i = 0; i < bytes.length; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            unitPosition = false;
            emptyPosition = false;

            if (!mGlobalMap.checkCoord(newPosition)) continue;
            // if (mGlobalMap.getMap(newPosition).getEntityType()==EntityType.RESOURCE) continue;
            if (mGlobalMap.checkUnit(mGlobalMap.getMapNextTick(), newPosition)) {
                unitPosition = true;
            }

            if (mGlobalMap.checkEmpty(mGlobalMap.getMapNextTick(), newPosition)) {
                emptyPosition = true;
            }

            if (newPosition.equals(position)) {
                unitPosition = false;
                emptyPosition = true;
            }


            Field field = mMapPotField[newPosition.getX()][newPosition.getY()];

            if (field.getSumDanger(entity.getEntityType())>attackRangeAnswer.maxDanger) {
                attackRangeAnswer.maxDanger = field.getSumDanger(entity.getEntityType());
            }

            if (field.getSumDangerContour()>attackRangeAnswer.maxCounterDanger) {
                attackRangeAnswer.maxCounterDanger = field.getSumDangerContour();
            }

            if (!emptyPosition && !unitPosition) continue;

            if (newPosition.equals(entity.getPosition()) && needMove) continue;

            if (emptyPosition) {
                if (field.getSumDanger() == 1 && field.getSumDangerContour() == 0) {
                    attackRangeAnswer.attackPosition = field;
                }

                if (field.getSumDanger() == 0 && field.getSumDangerContour() == 1) {
                    attackRangeAnswer.attackPosition = field;
                }

                if (field.getSumDanger() + field.getSumDangerContour() > 0 &&
                        field.getSumDanger() + field.getSumDangerContour() - field.getSafetyContour() <= 0
                ) {
                    attackRangeAnswer.attackPosition = field;
                }

                if (field.getSumDanger()>=4)
                {
                    if (field.getDangerTurret()>=4)
                    {
                        MyEntity vec2Int = mGlobalMap.getNearestPlayer(entity.getPosition(),FinalConstant.getMyID(),EntityType.TURRET);

                        ArrayList<MyEntity> arrayList = mGlobalMap.getEntityMap(vec2Int.getPosition(),GlobalMap.turretAndContourArray,-1,FinalConstant.getMyID(),true,EntityType.ALL,false);
                        if (arrayList.size()>=5)
                        {
                            attackRangeAnswer.attackPosition = field;
                        }
                    }
                }

                if (attackRangeAnswer.defencePosition==null)
                {
                    attackRangeAnswer.defencePosition = field;
                }

                if (attackRangeAnswer.defencePosition.getSumDanger()>field.getSumDanger())
                {
                    attackRangeAnswer.defencePosition = field;
                }
                else
                {
                    if (attackRangeAnswer.defencePosition.getSumDanger()==field.getSumDanger())
                    {
                        if (attackRangeAnswer.defencePosition.getSumDanger() + attackRangeAnswer.defencePosition.getSumDangerContour() - attackRangeAnswer.defencePosition.getSafetyContour() >
                                field.getSumDanger() + field.getSumDangerContour() - field.getSafetyContour()){
                            attackRangeAnswer.defencePosition = field;
                        }
                    }
                }
            }

            if (unitPosition)
            {
                if (attackRangeAnswer.defencePositionUnit==null)
                {
                    attackRangeAnswer.defencePositionUnit = field;
                }

                if (attackRangeAnswer.defencePositionUnit.getSumDanger()>field.getSumDanger())
                {
                    attackRangeAnswer.defencePositionUnit = field;
                }
                else
                {
                    if (attackRangeAnswer.defencePositionUnit.getSumDanger()==field.getSumDanger())
                    {
                        if (attackRangeAnswer.defencePositionUnit.getSumDanger() + attackRangeAnswer.defencePositionUnit.getSumDangerContour() - attackRangeAnswer.defencePositionUnit.getSafetyContour() >
                                field.getSumDanger() + field.getSumDangerContour() - field.getSafetyContour()){
                            attackRangeAnswer.defencePositionUnit = field;
                        }
                    }
                }
            }
        }

        if (attackRangeAnswer.maxDanger==0){
            if (attackRangeAnswer.defencePosition.getSumDanger() + attackRangeAnswer.defencePosition.getSumDangerContour() - attackRangeAnswer.defencePosition.getSafetyContour() >
                    currentPosition.getSumDanger() + currentPosition.getSumDangerContour() - currentPosition.getSafetyContour()){
                attackRangeAnswer.defencePosition = currentPosition;
            }
        }

        return attackRangeAnswer;
    }

    public Vec2Int getDangerPositionBuild(MyEntity entity, boolean resource, boolean needMove) {

        DangerPositionAnswer dangerPositionAnswer = calculateDangerPosition(entity,needMove);

        if (needMove)
        {
            if (dangerPositionAnswer.currentOnlySafety != null) {
                Final.DEBUG("NEED_MOVE", "pos: " + entity.getPosition().toString() + " currentOnlySafety " + dangerPositionAnswer.currentOnlySafety.toString());
                return dangerPositionAnswer.currentOnlySafety.getPosition();
            }
            else {
                if (dangerPositionAnswer.currentSafetyOnlyUnitPosition != null) {
                    Final.DEBUG("NEED_MOVE", "pos: " + entity.getPosition().toString() + " currentSafetyOnlyUnitPosition " + dangerPositionAnswer.currentSafetyOnlyUnitPosition.toString());
                    return dangerPositionAnswer.currentSafetyOnlyUnitPosition.getPosition();
                }
            }
            Final.DEBUG("NEED_MOVE", "pos: " + entity.getPosition().toString() + " BAD POSITION ");

            return null;
        }

        // никакой опасности рядом нет!!!
        if (dangerPositionAnswer.minDanger == 0xFFFF && dangerPositionAnswer.maxCounterDanger==0) {
            return null;
        }
        else {
           //  dangerPositionAnswer = calculateDangerPosition(entity);
            if (dangerPositionAnswer.maxCounterDanger>0 && dangerPositionAnswer.minDanger== 0xFFFF && !dangerPositionAnswer.currentCounterDanger)
            {
                if (resource)
                {
                    Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " resource  ");
                    return entity.getPosition();
                }
                else {
                    if (dangerPositionAnswer.currentSafety!=null || needMove) {
                        Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " no danger: currentSafety " + dangerPositionAnswer.currentSafety.toString() );
                        return dangerPositionAnswer.currentSafety.getPosition();
                    }
                    else {
                        if (dangerPositionAnswer.currentNoDanger!=null) {
                            Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " no danger: currentNodanger " + dangerPositionAnswer.currentNoDanger.toString() );
                            return dangerPositionAnswer.currentNoDanger.getPosition();
                        }
                    }
                }
            }
            else {
          //      Vec2Int vec2Int = getDangerPositionBuild(entity,resource);

                    if (dangerPositionAnswer.currentSafety != null) {
                        Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentSafety " + dangerPositionAnswer.currentSafety.toString());
                        return dangerPositionAnswer.currentSafety.getPosition();
                    } else {
                        if (dangerPositionAnswer.currentNoDanger != null) {
                            Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentNoDanger " + dangerPositionAnswer.currentNoDanger.toString());
                            return dangerPositionAnswer.currentNoDanger.getPosition();
                        } else {
                            if (dangerPositionAnswer.currentSafetyUnitPosition != null) {
                                Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentSafetyUnitPosition");
                                return dangerPositionAnswer.currentSafetyUnitPosition.getPosition();
                            } else {
                                if (dangerPositionAnswer.currentNoDangerUnitPosition != null) {
                                    Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentNoDangerUnitPosition");
                                    return dangerPositionAnswer.currentNoDangerUnitPosition.getPosition();
                                } else {
                                    // проблема, геймовер(надо просить кого-то подвинуться)

                                    Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " NO room to move");
                                }
                            }
                        }
                    }
            }
        }

        return null;
    }

    public DodgePositionAnswer getDodgePositionBuild(MyEntity entity, boolean resource, boolean needMove) {

        DodgePositionAnswer dodgePositionAnswer = calculateDodgePosition(entity,needMove);

        if (needMove) return dodgePositionAnswer;

        if (dodgePositionAnswer.maxDanger==0 && dodgePositionAnswer.getMaxCounterDanger()==0) return null;

        if (dodgePositionAnswer.maxCounterDanger>0 && dodgePositionAnswer.maxDanger== 0 && !dodgePositionAnswer.currentSafety) {
            if (resource) {
                Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " resource  ");
                return null;
            }
        }

        return dodgePositionAnswer;
      /*  // никакой опасности рядом нет!!!
        if (dodgePositionAnswer.maxDanger == 0 && dodgePositionAnswer.maxCounterDanger==0) {
            return null;
        }
        else {
            //  dangerPositionAnswer = calculateDangerPosition(entity);
            if (dangerPositionAnswer.maxCounterDanger>0 && dangerPositionAnswer.minDanger== 0xFFFF && !dangerPositionAnswer.currentCounterDanger)
            {
                if (resource)
                {
                    Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " resource  ");
                    return null;
                }
                else {
                    if (dangerPositionAnswer.currentSafety!=null || needMove) {
                        Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " no danger: currentSafety " + dangerPositionAnswer.currentSafety.toString() );
                        return dangerPositionAnswer.currentSafety.getPosition();
                    }
                    else {
                        if (dangerPositionAnswer.currentNoDanger!=null) {
                            Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " no danger: currentNodanger " + dangerPositionAnswer.currentNoDanger.toString() );
                            return dangerPositionAnswer.currentNoDanger.getPosition();
                        }
                    }
                }
            }
            else {
                //      Vec2Int vec2Int = getDangerPositionBuild(entity,resource);

                if (dangerPositionAnswer.currentSafety != null) {
                    Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentSafety " + dangerPositionAnswer.currentSafety.toString());
                    return dangerPositionAnswer.currentSafety.getPosition();
                } else {
                    if (dangerPositionAnswer.currentNoDanger != null) {
                        Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentNoDanger " + dangerPositionAnswer.currentNoDanger.toString());
                        return dangerPositionAnswer.currentNoDanger.getPosition();
                    } else {
                        if (dangerPositionAnswer.currentSafetyUnitPosition != null) {
                            Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentSafetyUnitPosition");
                            return dangerPositionAnswer.currentSafetyUnitPosition.getPosition();
                        } else {
                            if (dangerPositionAnswer.currentNoDangerUnitPosition != null) {
                                Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " currentNoDangerUnitPosition");
                                return dangerPositionAnswer.currentNoDangerUnitPosition.getPosition();
                            } else {
                                // проблема, геймовер(надо просить кого-то подвинуться)

                                Final.DEBUG("TAG", "pos: " + entity.getPosition().toString() + " NO room to move");
                            }
                        }
                    }
                }
            }
        }*/
    }

    byte[][] bytesOne = new byte[][]{
            {0, -1},{0, 1}, {0, 0}, {1, 0},  {-1, 0},
    };

    byte[][] bytesNeedMove = new byte[][]{
            {0, -1},{0, 1}, {1, 0},  {-1, 0},
    };

    public DangerPositionAnswer calculateDangerPosition(MyEntity entity,boolean needMove)
    {
        DangerPositionAnswer dangerPositionAnswer = new DangerPositionAnswer();

        byte[][] bytes = bytesOne;
        if (needMove)
        {
            bytes = bytesNeedMove;
        }

        Vec2Int position = entity.getPosition();

        boolean unitPosition;
        boolean emptyPosition;

        if (getMapPotField(position).getSumDangerContour()+getMapPotField(position).getSumDanger()>0) {
            dangerPositionAnswer.currentCounterDanger = true;
        }

        for (int i = 0; i < bytes.length; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            unitPosition =false;
            emptyPosition=false;

            if (!mGlobalMap.checkCoord(newPosition)) continue;
            // if (mGlobalMap.getMap(newPosition).getEntityType()==EntityType.RESOURCE) continue;
            if (mGlobalMap.checkUnit(mGlobalMap.getMapNextTick(),newPosition))
            {
                unitPosition = true;
            }

            if (mGlobalMap.checkEmpty(mGlobalMap.getMapNextTick(),newPosition))
            {
                emptyPosition = true;
            }

            if (newPosition.equals(position))
            {
                unitPosition = false;
                emptyPosition = true;
            }

            Field field = mMapPotField[newPosition.getX()][newPosition.getY()];

            int sizeEmptyPosition =  mGlobalMap.getCoordAround(newPosition, 1, true).size();

            if (field.getSumDanger(entity.getEntityType()) > 0) {
                /// проверяем контур атаки (это значит возможно на следующем ходу там появится второй юнит)
                if (field.getSumDanger(entity.getEntityType()) == dangerPositionAnswer.minDanger){
                    if (dangerPositionAnswer.current!=null && dangerPositionAnswer.current.getSumDangerContour()>field.getSumDangerContour()) {
                        dangerPositionAnswer.minDanger = field.getSumDanger(entity.getEntityType());
                        if (emptyPosition)
                        {
                            dangerPositionAnswer.current = field;
                        }
                    }
                }

                if (field.getSumDanger(entity.getEntityType()) < dangerPositionAnswer.minDanger){
                    dangerPositionAnswer.minDanger = field.getSumDanger(entity.getEntityType());

                    if (emptyPosition)
                    {
                        dangerPositionAnswer.current = field;
                    }
                }
            }

            if (field.getSumDangerContourOnlyUnit()>dangerPositionAnswer.maxCounterOnlyUnitDanger)
            {
                dangerPositionAnswer.maxCounterOnlyUnitDanger = field.getSumDangerContourOnlyUnit();
            }

            if (field.getSumDangerContour() > 0 && field.getSumDangerContour() > dangerPositionAnswer.maxCounterDanger) {
                dangerPositionAnswer.maxCounterDanger = field.getSumDangerContour();
            }

            if (field.getSumDanger(entity.getEntityType()) == 0
            ) {
                if (unitPosition)
                {
                    dangerPositionAnswer.currentNoDangerUnitPosition = field;
                }
                else {
                    if (dangerPositionAnswer.maxEmptyPositionNoDager<sizeEmptyPosition && emptyPosition) {
                        dangerPositionAnswer.currentNoDanger = field;
                        dangerPositionAnswer.maxEmptyPositionNoDager = sizeEmptyPosition;
                    }
                }

                if (unitPosition)
                {
                    if (field.getSumDangerContour() < dangerPositionAnswer.minCounterDangerUnitPosition) {
                        dangerPositionAnswer.currentSafetyUnitPosition = field;
                        dangerPositionAnswer.minCounterDangerUnitPosition = field.getSumDangerContour();
                    }
                }
                else {
                    if (field.getSumDangerContour() < dangerPositionAnswer.minCounterDanger && emptyPosition) {
                        dangerPositionAnswer.currentSafety = field;
                        dangerPositionAnswer.minCounterDanger = field.getSumDangerContour();
                        dangerPositionAnswer.maxEmptyPositionSafety = sizeEmptyPosition;
                    }
                    else {
                        if (field.getSumDangerContour() == dangerPositionAnswer.minCounterDanger) {
                            if (dangerPositionAnswer.maxEmptyPositionSafety < sizeEmptyPosition && emptyPosition) {
                                dangerPositionAnswer.currentSafety = field;
                                dangerPositionAnswer.minCounterDanger = field.getSumDangerContour();
                                dangerPositionAnswer.maxEmptyPositionSafety = sizeEmptyPosition;
                            }
                        }
                    }
                }
            }


            if (field.getSumDanger(entity.getEntityType()) == 0 && field.getSumDangerContour()==0)
            {
                if (unitPosition)
                {
                    dangerPositionAnswer.currentSafetyOnlyUnitPosition = field;
                }
                else {
                    dangerPositionAnswer.currentOnlySafety = field;
                }
            }
        }


        return dangerPositionAnswer;
    }

    public DodgePositionAnswer calculateDodgePosition(MyEntity entity,boolean needMove)
    {
        DodgePositionAnswer dodgePositionAnswer = new DodgePositionAnswer();

        byte[][] bytes = bytesOne;
        if (needMove)
        {
            bytes = bytesNeedMove;
        }

        Vec2Int position = entity.getPosition();

        boolean unitPosition;
        boolean emptyPosition;

        if (getMapPotField(position).getSumDangerContour()+getMapPotField(position).getSumDanger()>0) {
            dodgePositionAnswer.currentSafety = true;
        }

        for (int i = 0; i < bytes.length; i++) {
            Vec2Int newPosition = position.add(bytes[i][0], bytes[i][1]);

            unitPosition =false;
            emptyPosition=false;

            if (!mGlobalMap.checkCoord(newPosition)) continue;
            // if (mGlobalMap.getMap(newPosition).getEntityType()==EntityType.RESOURCE) continue;
            if (mGlobalMap.checkUnit(mGlobalMap.getMapNextTick(),newPosition))
            {
                unitPosition = true;
            }

            if (mGlobalMap.checkEmpty(mGlobalMap.getMapNextTick(),newPosition))
            {
                emptyPosition = true;
            }

            if (newPosition.equals(position))
            {
                unitPosition = false;
                emptyPosition = true;
            }

            Field field = mMapPotField[newPosition.getX()][newPosition.getY()];


            if (field.getSumDanger(entity.getEntityType())>dodgePositionAnswer.maxDanger) {
                dodgePositionAnswer.maxDanger = field.getSumDanger(entity.getEntityType());
            }

            if (field.getSumDangerContour()>dodgePositionAnswer.maxCounterDanger) {
                dodgePositionAnswer.maxCounterDanger = field.getSumDangerContour();
            }

            if (!unitPosition && !emptyPosition) continue;


            int sizeEmptyPosition =  mGlobalMap.getCoordAround(newPosition, 1, true).size();


            if (field.getSumDanger(entity.getEntityType()) == 0 && field.getSumDangerContour()==0)
            {
                if (unitPosition)
                {
                    dodgePositionAnswer.getSafetyPositionUnitArrayList().add(field);
                }
                else {
                    dodgePositionAnswer.getSafetyArrayList().add(field);
                }
            }

            if (field.getSumDanger(entity.getEntityType()) == 0 && field.getSumDangerContour()>0)
            {
                if (unitPosition)
                {
                    dodgePositionAnswer.getSafetyCounterArrayList().add(field);
                }
                else {
                    dodgePositionAnswer.getSafetyCounterArrayList().add(field);
                }
            }
        }


        return dodgePositionAnswer;
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
        int offset = 4;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!checkCoord(vec2Int.getX() + x, vec2Int.getY() + y)) return false;
                if (mGlobalMap.getMap()[vec2Int.getX() + x][vec2Int.getY() + y].getEntityType() != EntityType.Empty ) return false;
            }
        }

        for (int x = -offset; x < width+offset; x++) {
            for (int y = -offset; y < height+offset; y++) {
                if (!checkCoord(vec2Int.getX() + x, vec2Int.getY() + y)) continue;
                if (mMapPotField[vec2Int.getX() + x][vec2Int.getY() + y].getSumDanger() > 0) return false;
                if (mMapPotField[vec2Int.getX() + x][vec2Int.getY() + y].getSumDangerContour() > 0) return false;
            }
        }

        return true;
    }

    public void changeBlockPositionAttack(Vec2Int endMove) {
        getMapPotField()[endMove.getX()][endMove.getY()].setTargetAttackClosed(true);
    }

    public Field getMapPotField(Vec2Int position) {
        return mMapPotField[position.getX()][position.getY()];
    }
}
