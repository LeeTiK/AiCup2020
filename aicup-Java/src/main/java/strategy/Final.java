package strategy;

import model.Player;
import model.PlayerView;

public class Final {

    final static boolean TEST = false;
    final static boolean debug = true;
    final static boolean debugRelease = true;
    public final static boolean debugGraphic = true;

    final static boolean OFF_WAR = false;

    // элементы игры
    public final static boolean BUILD_TURRET = false;

    //////////DEBUG MAP GRAPHIC//////////////

    public final static boolean COORDINATE = false;
    public final static boolean INFO_UNIT = false;
    public final static boolean PLAYER_AREA = false;
    public final static boolean PLAYER_AREA_TWO = false;
    public final static boolean DANGER_AREA = false;
    public final static boolean DANGER_CONTOUR_AREA = false;
    public final static boolean SAFETY_AREA = false;
    public final static boolean DANGER_AND_SAFETY_AREA_TEXT = false;
    public final static boolean BIOM_RESOURCE = false;

    public final static boolean CHECK_SEARCH_WAVE_BUILDER = true;
    public final static boolean CHECK_SEARCH_WAVE_START_END = true;

    static public Player getMyPlayer(PlayerView playerView) {
        int myID = playerView.getMyId();
        for (int i = 0; i < playerView.getPlayers().length; i++) {
            if (playerView.getPlayers()[i].getId() == myID) return playerView.getPlayers()[i];
        }

        return null;
    }


    public static void DEBUG(String TAG, String text) {
        if (debug) {
            System.out.println(TAG + " " + text);
        }
    }

    public static void DEBUGRelease(String TAG, String text) {
        if (debugRelease) {
            System.out.println(TAG + " " + text);
        }
    }
}
