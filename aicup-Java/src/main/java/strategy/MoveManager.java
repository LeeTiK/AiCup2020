package strategy;

import model.MoveAction;
import model.Vec2Int;
import strategy.map.astar.AStar;
import strategy.map.astar.Node;
import strategy.map.potfield.MapPotField;

import java.util.List;

// класс помогающий юнитам двигаться по собственному алгоритму поиска пути
public class MoveManager {

    GlobalMap mGlobalMap;
    MapPotField mMapPotField;

    AStar mAStar;

    DebugInterface mDebugInterface;

    public MoveManager(){
        mAStar = new AStar(80);
    }

    public void update(GlobalMap globalMap, MapPotField mapPotField,DebugInterface debugInterface){
        this.mGlobalMap = globalMap;
        this.mMapPotField = mapPotField;
        mDebugInterface = debugInterface;

        mAStar.updateMap(mGlobalMap,mapPotField);
    }


    MoveAction getMoveActionPosition(MyEntity entity, Vec2Int targetPosition){

        if (targetPosition==null) return null;

        MoveAction moveAction = new MoveAction();
        moveAction.setBreakThrough(true);
        moveAction.setFindClosestPosition(true);


        if (entity.getPosition().distance(targetPosition)<1.5) {
            moveAction.setTarget(targetPosition);

        }
        else {
            // поиск Astar интересно

            mAStar.initSearch(entity.getPosition(),targetPosition);
            List<Node> path = mAStar.findPath();

            if (path.size()>1)
            {

                if (Final.debugGraphic) {
                    if (Final.CHECK_SEARCH_PATH_ASTAR) {
                        for (int k = 0; k < path.size(); k++) {
                            FinalGraphic.sendSquare(mDebugInterface, path.get(k).getVec2Int(), 1, FinalGraphic.COLOR_GREEN);
                        }
                    }
                }

                moveAction.setTarget(path.get(1).getVec2Int());
                return moveAction;
            }

        }

        moveAction.setTarget(targetPosition);

        return moveAction;
    }
}
