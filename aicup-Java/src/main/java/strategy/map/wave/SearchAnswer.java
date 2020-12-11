package strategy.map.wave;

import model.Vec2Int;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchAnswer {
    Vec2Int start;
    Vec2Int end;

    int cost;

    LinkedList<Vec2Int> path;

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

    public void setPath(LinkedList<Vec2Int> path) {
        this.path = path;
    }

    public LinkedList<Vec2Int> getPath() {
        return path;
    }

    @Override
    public String toString(){
        return "start: " + start.toString() + " end: " + end.toString() + " cost: " + cost;
    }
}
