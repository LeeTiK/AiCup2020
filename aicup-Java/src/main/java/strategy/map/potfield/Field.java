package strategy.map.potfield;

import model.Vec2Int;

public class Field {
    Vec2Int mPosition;

    int sum;

    int danger;
    int playerArea;
    int playerAreaTwo;

    int cost;
    int district;
    int districtResource;
    boolean block;

    public Field(Vec2Int vec2Int){
        this.mPosition = vec2Int;

        clear();
    }

    public int getX() {
        return mPosition.getX();
    }

    public int getY() {
        return mPosition.getY();
    }

    public void clear(){
        sum = 0;
        danger = 0;
        playerArea = 0;
        playerAreaTwo = 0;

        cost =0;
        district = -1;
        districtResource = -1;
        block = false;
    }


    public int getSum() {
        return sum;
    }

    public Vec2Int getPosition() {
        return mPosition;
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

    public void setDanger(int danger) {
        this.danger = danger;
    }

    public int getDanger() {
        return danger;
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
    public boolean equals(Object obj){
        if (obj.getClass()!=Field.class) return false;
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
}
