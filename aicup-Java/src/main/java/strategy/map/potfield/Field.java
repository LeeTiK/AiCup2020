package strategy.map.potfield;

import model.Vec2Int;

public class Field {
    Vec2Int mVec2Int;

    int sum;

    int danger;


    public int getSum() {
        return sum;
    }

    public Vec2Int getVec2Int() {
        return mVec2Int;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public void addSum(int addsum) {
        this.sum += addsum;
    }

    public void setVec2Int(Vec2Int vec2Int) {
        mVec2Int = vec2Int;
    }
}
