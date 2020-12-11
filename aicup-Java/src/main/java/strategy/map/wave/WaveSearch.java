package strategy.map.wave;

import model.*;
import strategy.*;
import strategy.map.potfield.Field;

import java.util.ArrayList;

public class WaveSearch {
    Field[][] map;

    int size;

    public WaveSearch(int size) {
        this.size = size;
        map = new Field[size][size];
    }

    public void initMap(MyEntity[][] maps) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                map[x][y] = new Field();
                map[x][y].setMyEntity(maps[x][y]);
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
        else return false;

        if (x == end.getX() && end.getY() == y) return true;


        if (wave(x + 1, y, cost + 1, end)) return true;

        if (wave(x, y + 1, cost + 1, end)) return true;

        if (wave(x - 1, y, cost + 1, end)) return true;

        if (wave(x, y - 1, cost + 1, end)) return true;

        //map[x][y] = size;
        return false;
    }

    public SearchAnswer waveSearchNeedEntity(ArrayList<Vec2Int> startPoints, int maxCount, EntityType needEntity) {

        int count = 1;

        long timeStart = System.nanoTime();

        ArrayList<Vec2Int> points = new ArrayList<>(startPoints.size());
        for (int i=0; i<startPoints.size(); i++)
        {
            points.add(startPoints.get(i));
            map[points.get(i).getX()][points.get(i).getY()].setCost(0);
        }


        ArrayList<Vec2Int> nextStep = new ArrayList<>();

        boolean checkComplite = false;

        while (count<maxCount)
        {
            int startSizeArray = points.size();

            for (int i=0;i<points.size(); i++)
            {
                boolean check = waveOneСross(nextStep,points.get(i),count,needEntity);
                if (check)
                {
                    checkComplite =true;
                    break;
                   // return nextStep.get(nextStep.size()-1);
                }
            }

            if (checkComplite) break;

            ArrayList<Vec2Int> a = points;
            points = nextStep;
            nextStep = a;
            nextStep.clear();

            count++;
        }
        //map[x][y] = size;
        if (checkComplite)
        {
            Vec2Int end = nextStep.get(nextStep.size()-1);
            int cost = map[end.getX()][end.getY()].getCost();
            int cost1 = cost;
            Vec2Int start = end;
            while (cost>0) {
                byte[][] array = GlobalMap.aroundArray;

                cost--;

                for (int i = 0; i < array.length; i++) {
                    int x = start.getX() + array[i][0];
                    int y = start.getY() + array[i][1];

                    if (x < 0 || x >= size || y < 0 || y >= size) continue;

                    if (map[x][y].getCost()==cost)
                    {
                        start = new Vec2Int(x,y);
                        break;
                    }
                }
            }

            SearchAnswer searchAnswer = new SearchAnswer();
            searchAnswer.setEnd(end);
            searchAnswer.setStart(start);
            searchAnswer.setCost(cost1);

            Final.DEBUG("WaveSearch", "time: " +(System.nanoTime()-timeStart) +" " + searchAnswer.toString());

            return searchAnswer;
        }

        Final.DEBUG("WaveSearch", "time: " +(System.nanoTime()-timeStart) +" " + "BAD ");
        return null;
    }

    boolean waveOneСross(ArrayList<Vec2Int> nextSteps, Vec2Int vec2Int, int cost, EntityType needEntity) {

        int x = vec2Int.getX();
        int y = vec2Int.getY();

        byte[][] array = GlobalMap.aroundArray;

        for (int i=0; i<array.length; i++)
        {
            int x1 = x + array[i][0];
            int y1 = y + array[i][1];

            EResultSearch result = waveOnePoint(x1, y1, cost, needEntity);

           switch (result)
           {
               case SUCCES:
                   nextSteps.add(new Vec2Int(x1,y1));
                   return true;
               case PATH:
                   nextSteps.add(new Vec2Int(x1,y1));
                   break;
           }
        }

        return false;
    }

    EResultSearch waveOnePoint(int x,int y, int cost, EntityType needEntity) {

        if (x < 0 || x >= size || y < 0 || y >= size) return EResultSearch.BLOCK;

        if (getMap()[x][y].getCost()>cost) {
            getMap()[x][y].setCost(cost);
        }
        else {
            return EResultSearch.BLOCK;
        }

        if (getMap()[x][y].getMyEntity().getEntityType()==needEntity) {
            if (getMap()[x][y].getMyEntity().getTargetEntity()==null){
                return EResultSearch.SUCCES;
            }
            else {
                getMap()[x][y].setCost(0xFFFF);
                return EResultSearch.BLOCK;
            }
        }

        if (getMap()[x][y].getMyEntity().getEntityType()!=EntityType.Empty) {
            getMap()[x][y].setCost(0xFFFF);
            return EResultSearch.BLOCK;
        }

        return EResultSearch.PATH;
    }


    public Field[][] getMap() {
        return map;
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        //  debugInterface.
        if (getMap() != null) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                   if ( getMap()[x][y].getCost()!=0xFFFF) {
                       FinalGraphic.sendText(debugInterface, new Vec2Float(x * 1.0f, y * 1.0f + 0.5f), 11, "" +
                               getMap()[x][y].getCost());
                   }
                }
            }
        }
    }
}
