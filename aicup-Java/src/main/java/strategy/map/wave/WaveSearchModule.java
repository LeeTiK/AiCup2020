package strategy.map.wave;

import model.*;
import strategy.*;
import strategy.map.potfield.Field;

import java.util.ArrayList;
import java.util.LinkedList;

public class WaveSearchModule {
    Field[][] map;

    int size;

    GlobalMap mGlobalMap;

    public WaveSearchModule(GlobalMap globalMap) {
        this.mGlobalMap = globalMap;
    }

    @Deprecated
    public void initMap(MyEntity[][] maps) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                map[x][y] = new Field(new Vec2Int(x,y));
                map[x][y].setMyEntity(maps[x][y]);
            }
        }
    }

    public void updateMap(Field[][] maps) {
        size= maps.length;
        map = maps;
    }

    ArrayList<Vec2Int> searchPath(Vec2Int start, Vec2Int end) {
        return null;
    }
    ;


    public SearchAnswer waveSearchNeedEntity(ArrayList<Vec2Int> startPoints, int maxCount, EntityType needEntity) {

        clearMapPath();

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

    private void clearMapPath() {
        for (int x=0; x<size; x++)
        {
            for (int y=0; y<size; y++)
            {
                map[x][y].setCost(0xFFFF);
            }
        }
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

        if (getGlobalMap().getMapNextTick()[x][y].getEntityType()!=EntityType.Empty) {
            getMap()[x][y].setCost(0xFFFF);
            return EResultSearch.BLOCK;
        }

        return EResultSearch.PATH;
    }

    public Field[][] getMap() {
        return map;
    }

    boolean waveOneСrossRange(ArrayList<Vec2Int> nextSteps, Vec2Int vec2Int, int cost) {

        int x = vec2Int.getX();
        int y = vec2Int.getY();

        byte[][] array = GlobalMap.aroundArray;

        for (int i=0; i<array.length; i++)
        {
            int x1 = x + array[i][0];
            int y1 = y + array[i][1];

            EResultSearch result = waveOnePointRange(x1, y1, cost);

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


    SearchAnswer searchPathRange(Vec2Int start1, int maxCount) {

        clearMapPath();

        int count = 1;

        long timeStart = System.nanoTime();

        ArrayList<Vec2Int> points = new ArrayList<>();
        points.add(start1);


        ArrayList<Vec2Int> nextStep = new ArrayList<>();

        boolean checkComplite = false;

        while (count<maxCount)
        {
            int startSizeArray = points.size();

            for (int i=0;i<points.size(); i++)
            {
                boolean check = waveOneСrossRange(nextStep,points.get(i),count);
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

            LinkedList<Vec2Int> linkedList = new LinkedList<>();
            linkedList.add(start);


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
                        linkedList.add(start);
                        break;
                    }
                }
            }

            SearchAnswer searchAnswer = new SearchAnswer();
            searchAnswer.setEnd(end);
            searchAnswer.setStart(start);
            searchAnswer.setCost(cost1);
            searchAnswer.setPath(linkedList);

            Final.DEBUG("WaveSearch", "time: " +(System.nanoTime()-timeStart) +" " + searchAnswer.toString() + " link: " + linkedList.size());

            return searchAnswer;
        }

        Final.DEBUG("WaveSearch", "time: " +(System.nanoTime()-timeStart) +" " + "BAD ");
        return null;
    }

    EResultSearch waveOnePointRange(int x,int y, int cost) {

        if (x < 0 || x >= size || y < 0 || y >= size) return EResultSearch.BLOCK;

        if (getMap()[x][y].getCost()>cost) {
            getMap()[x][y].setCost(cost);
        }
        else {
            return EResultSearch.BLOCK;
        }

        if (getMap()[x][y].getSumDanger()==1 && getMap()[x][y].getSumDangerContour()==0) {
            return EResultSearch.SUCCES;
        }
        else {
            if (getMap()[x][y].getSumDanger()>1 || getMap()[x][y].getSumDangerContour()>1) {
                getMap()[x][y].setCost(0xFFFF);
                return EResultSearch.BLOCK;
            }
        }

        if (getGlobalMap().getMapNextTick()[x][y].getEntityType()!=EntityType.Empty) {
            getMap()[x][y].setCost(0xFFFF);
            return EResultSearch.BLOCK;
        }

        return EResultSearch.PATH;
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

    public GlobalMap getGlobalMap() {
        return mGlobalMap;
    }
}
