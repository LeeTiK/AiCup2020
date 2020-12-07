package strategy.map.wave;

import model.Vec2Int;
import strategy.MyEntity;
import strategy.map.potfield.Field;

import java.util.ArrayList;

public class WaveSearch {
    Field[][] map;

    int size;

    int block;

    public WaveSearch(int size) {
        this.size = size;
        map = new Field[size][size];
    }

    void initMap(MyEntity[][] maps) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                switch (maps[x][y].getEntityType()) {
                    case WALL:
                        break;
                    case HOUSE:
                        break;
                    case BUILDER_BASE:
                        break;
                    case BUILDER_UNIT:
                        break;
                    case MELEE_BASE:
                        break;
                    case MELEE_UNIT:
                        break;
                    case RANGED_BASE:
                        break;
                    case RANGED_UNIT:
                        break;
                    case RESOURCE:
                        break;
                    case TURRET:
                        break;
                    case ALL:
                        break;
                    case Empty:
                        break;
                }
            }
        }
    }

    ArrayList<Vec2Int> searchPath(Vec2Int start, Vec2Int end) {
        return null;
    }

    ;

    void createDistrictArea() {

    }

    boolean wave(int x, int y, int cost, Vec2Int end) {
        if (x < 0 || x >= size || y < 0 || y >= size) return false;

        if (map[x][y].isBlock()) return false;

        if (map[x][y].getCost() > cost) map[x][y].setCost(cost);

        if (x == end.getX() && end.getY() == y) return true;


        if (wave(x + 1, y, cost + 1, end)) return true;

        if (wave(x, y + 1, cost + 1, end)) return true;

        if (wave(x - 1, y, cost + 1, end)) return true;

        if (wave(x, y - 1, cost + 1, end)) return true;

        //map[x][y] = size;
        return false;
    }


    public Field[][] getMap() {
        return map;
    }
}
