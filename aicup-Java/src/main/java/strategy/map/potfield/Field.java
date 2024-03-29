package strategy.map.potfield;

import model.EntityType;
import model.Vec2Int;
import strategy.Final;
import strategy.MyEntity;

import java.util.ArrayList;

public class Field {
    Vec2Int position;
    MyEntity mMyEntity;

    int sum;

    int dangerRanger;
    int dangerMelee;
    int dangerTurret;

    int dangerContourRanger;
    int dangerContourMelee;
    int dangerContourTurret;
    int buildUnit;

    ArrayList<MyEntity> dangerArrayList;
    ArrayList<MyEntity> dangerCounterArrayList;

    int safetyContour;

    int safetyRanger;
    int safetyMelee;
    int safetyTurret;
    int safetyHouse;

    boolean attackPositionRanger;
    boolean targetAttackClosed;
    boolean repairPositionClose;

    int playerArea;
    int playerAreaTwo;

    int cost;
    int district;
    int districtResource;
    boolean block;

    boolean seeFogOfWar;
    boolean dontUpdateFogOfWar;


    public Field(Vec2Int position) {
        this.position = position;
        mMyEntity = null;
        dontUpdateFogOfWar = false;
        dangerArrayList = new ArrayList<>();
        dangerCounterArrayList = new ArrayList<>();
        clear();
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public void clear() {
        sum = 0;
        dangerRanger = 0;
        dangerMelee = 0;
        dangerTurret = 0;

        dangerContourRanger = 0;
        dangerContourMelee = 0;
        dangerContourTurret = 0;


        safetyRanger = 0;
        safetyMelee = 0;
        safetyTurret = 0;
        safetyHouse = 0;
        playerArea = 0;
        playerAreaTwo = 0;

        safetyContour = 0;

        cost = 0xFFFF;
        district = -1;
        districtResource = -1;
        block = false;
        attackPositionRanger = false;
        repairPositionClose=false;

        seeFogOfWar = false;
        buildUnit=0;
        dangerArrayList.clear();
        dangerCounterArrayList.clear();
    }


    public int getSum() {
        return sum;
    }

    public Vec2Int getPosition() {
        return position;
    }

    public MyEntity getMyEntity() {
        return mMyEntity;
    }

    public void setMyEntity(MyEntity myEntity) {
        mMyEntity = myEntity;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public void addSum(int addsum) {
        this.sum += addsum;
    }


    public void setPlayerArea(int playerArea) {
        this.playerArea = playerArea;
    }

    public void addPlayerArea(int playerArea) {
        this.playerArea += playerArea;
    }

    public int getPlayerArea() {
        return playerArea;
    }

    public void addPlayerAreaTwo(int playerArea) {
        this.playerAreaTwo += playerArea;
    }

    public int getPlayerAreaTwo() {
        return playerAreaTwo;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public int getDistrict() {
        return district;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public boolean isBlock() {
        return block;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Field.class) return false;
        Field field = (Field) obj;

        if (this.getPosition().equals(field.getPosition())) return true;

        return false;
    }

    public int getDistrictResource() {
        return districtResource;
    }

    public void setDistrictResource(int districtResource) {
        this.districtResource = districtResource;
    }

    public int getDangerMelee() {
        return dangerMelee;
    }

    public int getDangerRanger() {
        return dangerRanger;
    }

    public int getDangerTurret() {
        return dangerTurret;
    }

    public void addDangerMelee() {
        this.dangerMelee++;
    }

    public void addDangerRanger(MyEntity entity) {
        this.dangerRanger++;
        this.dangerArrayList.add(entity);
    }

    public void addDangerTurret() {
        this.dangerTurret++;
    }

    public boolean checkDanger() {
        return dangerRanger + dangerTurret + dangerMelee == 0;
    }

    public int getSumDanger() {
        return dangerRanger + dangerTurret + dangerMelee;
    }

    public int getDangerContourMelee() {
        return dangerContourMelee;
    }

    public int getDangerContourRanger() {
        return dangerContourRanger;
    }

    public int getDangerContourTurret() {
        return dangerContourTurret;
    }

    public void addDangerContourMelee() {
        this.dangerContourMelee++;
    }

    public void addDangerContourRanger(MyEntity entity) {
        this.dangerContourRanger++;
        dangerArrayList.add(entity);
    }

    public void addDangerContourTurret() {
        this.dangerContourTurret++;
    }

    public boolean checkDangerContour() {
        return dangerContourRanger + dangerContourTurret + dangerContourMelee == 0;
    }

    public int getSumDangerContour() {
        return dangerContourRanger + dangerContourTurret/4 + dangerContourMelee;
    }

    public int getSumDangerContourOnlyUnit() {
        return dangerContourRanger + dangerContourMelee;
    }

    public int getSumDangerRanger() {
        return dangerRanger + dangerTurret + dangerMelee;
    }

    public int getSumDangerMelee() {
        return dangerRanger + dangerMelee;
    }

    public int getSumDanger(EntityType entityType) {
        switch (entityType) {
            case MELEE_UNIT:
                return getSumDangerMelee();
            case RANGED_UNIT:
                return getSumDangerRanger();
        }

        return getSumDanger();
    }

    public void addSafetyMelee() {
        this.safetyMelee++;
    }

    public void addSafetyRanger() {
        this.safetyRanger++;
    }

    public void addSafetyTurret() {
        this.safetyTurret++;
    }

    public void addSafetyHouse() {
        this.safetyHouse++;
    }

    public int getSafetyMelee() {
        return safetyMelee;
    }

    public int getSafetyRanger() {
        return safetyRanger;
    }

    public int getSafetyTurret() {
        return safetyTurret;
    }

    public int getSafetyHouse() {
        return safetyHouse;
    }

    public int getSumSafaty() {
        return safetyRanger + safetyTurret + safetyMelee;
    }

    public int getSumSafatyRanger() {
        return safetyRanger + safetyTurret + safetyMelee;
    }

    public int getSumSafaty(EntityType entityType) {
        switch (entityType) {
            case MELEE_UNIT:
                return getSumSafatyRanger();
            case RANGED_UNIT:
                return getSumSafatyRanger();
        }

        return getSumSafaty();
    }

    @Override
    public String toString(){
        return "" + getPosition().toString() + " D: " + getSumDanger() + " C: " + getSumDangerContour() + " SC: " + getSafetyContour();
    }

    public int getSafetyContour() {
        return safetyContour;
    }

    public void setSafetyContour(int safetyContour) {
        if (Final.MINIMAL_SAFETY) {
            if (this.safetyContour > safetyContour
                    || (this.safetyContour == 0 && safetyContour != 0)
            ) {
                this.safetyContour = safetyContour;
            }
        }
        else {
            if (this.safetyContour < safetyContour)
            {
                this.safetyContour = safetyContour;
            }
        }
    }

    public boolean isAttackPositionRanger() {
        return attackPositionRanger;
    }

    public void setAttackPositionRanger(boolean attackPositionRanger) {
        this.attackPositionRanger = attackPositionRanger;
    }

    public boolean isTargetAttackClosed() {
        return targetAttackClosed;
    }

    public void setTargetAttackClosed(boolean targetAttackClosed) {
        this.targetAttackClosed = targetAttackClosed;
    }

    public void setRepairPositionClose(boolean repairPositionClose) {
        this.repairPositionClose = repairPositionClose;
    }

    public boolean isRepairPositionClose() {
        return repairPositionClose;
    }

    public boolean isSeeFogOfWar() {
        return seeFogOfWar;
    }

    public void setSeeFogOfWar(boolean seeFogOfWar) {
        this.seeFogOfWar = seeFogOfWar;
    }

    public void setDontUpdateFogOfWar(boolean dontUpdateFogOfWar) {
        this.dontUpdateFogOfWar = dontUpdateFogOfWar;
    }

    public boolean isDontUpdateFogOfWar() {
        return dontUpdateFogOfWar;
    }

    public void addBuildUnit() {
        this.buildUnit++;
    }

    public int getBuildUnit() {
        return buildUnit;
    }

    public ArrayList<MyEntity> getDangerArrayList() {
        return dangerArrayList;
    }

    public ArrayList<MyEntity> getDangerCounterArrayList() {
        return dangerCounterArrayList;
    }
}
