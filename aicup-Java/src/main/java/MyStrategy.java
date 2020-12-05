import model.*;
import strategy.DebugInterface;
import strategy.Final;
import strategy.GlobalManager;

public class MyStrategy {
    GlobalManager mGlobalManager;

    public Action getAction(PlayerView playerView, DebugInterface debugInterface) {

        if (mGlobalManager==null)
        {
            mGlobalManager = new GlobalManager();
        }


        //debugInterface.getState().
      //  playerView.get

       // mGlobalManager.update(playerView,debugInterface);
        return mGlobalManager.update(playerView,debugInterface);

      //  return new Action(new HashMap<>());
    }
    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        debugInterface.send(new DebugCommand.Clear());
        debugInterface.getState();

        if (!Final.debugGraphic) return;

        if (mGlobalManager!=null) {
            mGlobalManager.debugUpdate(playerView, debugInterface);
        }
    }
}