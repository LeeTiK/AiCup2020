import model.*;
import strategy.*;

public class MyStrategy {
    GlobalManager mGlobalManager;

    final static boolean debugSimple = false;
    static long timeStart;

    public MyStrategy(){
        timeStart = System.nanoTime();
    }

    public Action getAction(PlayerView playerView, DebugInterface debugInterface) {

        if (debugSimple)
        {
            return simpleStrategy(playerView,debugInterface);
        }


        if (mGlobalManager==null)
        {
            mGlobalManager = new GlobalManager();
        }
      //  debugInterface.getState().

       /* int size = 10;
        Vec2Int position = Vec2Int.createVector(0,0);

        for (int x = -size; x <= size; x++) {
            int sizeY = size - Math.abs(x);

            for (int y = -sizeY; y <= sizeY; y++) {
                System.out.print("{"+x+","+y+"},");
            }
            System.out.println();
        }*/

        //debugInterface.getState().
      //  playerView.get

       // mGlobalManager.update(playerView,debugInterface);
        Action action = mGlobalManager.update(playerView,debugInterface, timeStart);
        timeStart = 0;
        return action;

      //  return new Action(new HashMap<>());
    }
    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        if (!Final.debugGraphic) return;

        debugInterface.send(new DebugCommand.Clear());
        debugInterface.getState();


        if (mGlobalManager!=null) {
            mGlobalManager.debugUpdate(playerView, debugInterface);
        }
    }

    public Action simpleStrategy(PlayerView playerView, DebugInterface debugInterface){
        Action result = new Action(new java.util.HashMap<>());
        int myId = playerView.getMyId();
        for (Entity entity : playerView.getEntities()) {
            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            MoveAction moveAction = null;
            BuildAction buildAction = null;
            if (properties.isCanMove()) {
                moveAction = new MoveAction(
                        Vec2Int.createVector(playerView.getMapSize() - 1, playerView.getMapSize() - 1),
                        true,
                        true);
            } else if (properties.getBuild() != null) {
                EntityType entityType = properties.getBuild().getOptions()[0];
                int currentUnits = 0;
                for (Entity otherEntity : playerView.getEntities()) {
                    if (otherEntity.getPlayerId() != null && otherEntity.getPlayerId() == myId
                            && otherEntity.getEntityType() == entityType) {
                        currentUnits++;
                    }
                }
                if ((currentUnits + 1) * playerView.getEntityProperties().get(entityType).getPopulationUse() <= properties.getPopulationProvide()) {
                    buildAction = new BuildAction(
                            entityType,
                            Vec2Int.createVector(
                                    entity.getPosition().getX() + properties.getSize(),
                                    entity.getPosition().getY() + properties.getSize() - 1
                            )
                    );
                }
            }
            EntityType[] validAutoAttackTargets;
            if (entity.getEntityType() == EntityType.BUILDER_UNIT) {
                validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
            } else {
                validAutoAttackTargets = new EntityType[0];
            }
            result.getEntityActions().put(entity.getId(), new EntityAction(
                    moveAction,
                    buildAction,
                    new AttackAction(
                            null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets)
                    ),
                    null
            ));
        }
        return result;
    }
}