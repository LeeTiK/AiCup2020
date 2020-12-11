package strategy.map.wave;

import model.Vec2Int;

public class SearchAnswer {
    Vec2Int start;
    Vec2Int end;

    int cost;

    public SearchAnswer()
    {

    }

    public void setEnd(Vec2Int end) {
        this.end = end;
    }

    public void setStart(Vec2Int start) {
        this.start = start;
    }

    public Vec2Int getEnd() {
        return end;
    }

    public Vec2Int getStart() {
        return start;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString(){
        return "start: " + start.toString() + " end: " + end.toString() + " cost: " + cost;
    }
}
