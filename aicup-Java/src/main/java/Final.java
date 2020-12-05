import model.Player;
import model.PlayerView;

public class Final {

    final static boolean TEST =false;
    final static boolean debug =false;
    final static boolean debugRelease =true;
    final static boolean debugGraphic =false;

    final static boolean OFF_WAR = false;

    static public Player getMyPlayer(PlayerView playerView){
        int myID = playerView.getMyId();
        for (int i=0; i<playerView.getPlayers().length; i++)
        {
            if (playerView.getPlayers()[i].getId() == myID) return playerView.getPlayers()[i];
        }

        return null;
    }


    public static void DEBUG (String TAG, String text){
        if (debug)
        {
            System.out.println(TAG + " " + text);
        }
    }

    public static void DEBUGRelease (String TAG, String text){
        if (debugRelease)
        {
            System.out.println(TAG + " " + text);
        }
    }
}
