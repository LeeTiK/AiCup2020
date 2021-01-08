package strategy;

import model.Player;
import model.PlayerView;

public class Final {


    final static boolean RELEASE = true;


    final static boolean TEST = false;
    final static boolean OFF_WAR = false;


    final static boolean debug = false;
    final static boolean debugERROR = true;
    final static boolean debugRelease = true;
    public final static boolean debugGraphic = false;

    ////////////////////////////РЕЖИМЫ РАБОТЫ/////////////////////////////////
    public static boolean FOG_OF_WAR_MAP = true;
    public static boolean A_STAR = true;
    public static boolean GLOBAL_DODGE = true;
    public static boolean SPECIAL_ATTACK_ENEMY_BUILDER = true;
    public static boolean MINIMAL_SAFETY = false;

    public static boolean SPECIAL_RUSH = false;
    public final static boolean CHECK_POSITION_NEXT_TIK = true;

    public final static boolean ADD_CALCULATE_TWO_COUNTER = true;
    public final static boolean CALCULATE_DISTRICT_REGION = true;

    // элементы игры
    public final static boolean BUILD_TURRET = false;
    public final static boolean BUILD_TURRET_SPECIAL = false;
    public final static boolean BUILD_TURRET_SPECIAL_V2 = true;
    public final static boolean BUILD_TURRET_SPECIAL_V2_ALL = true;

    public final static boolean GHOST_BUILDING = false;
    public final static boolean DANGER_RESOURCE = true;

    //////////////УСТАРЕВШИЕ
    public static boolean PLAYER_AREA_CALCULATE = false;
    public static boolean A_STAR_BLOCK_ALL_BUILD_UNIT = false;
    public static boolean A_STAR_CHECK_FIRST_NODE_BLOCK = false;
    public final static boolean A_NEED_MOVE = false;
    public final static boolean A_NEED_MOVE_V2 = false;


    //////////DEBUG MAP GRAPHIC//////////////

    public final static boolean COORDINATE = true;
    public final static boolean INFO_UNIT = true;
    public final static boolean PLAYER_AREA = false;
    public final static boolean PLAYER_AREA_TWO = false;
    public final static boolean DANGER_AREA = false;
    public final static boolean DANGER_CONTOUR_AREA = false;
    public final static boolean SAFETY_AREA = false;
    public final static boolean DANGER_AND_SAFETY_AREA_TEXT = true;
    public final static boolean BIOM_RESOURCE = false;
    public final static boolean SAFETY_CONTOUR = false;
    public final static boolean ATTACK_RANGE_REGION = false;
    public final static boolean DISTRICT_REGION = false;
    public final static boolean HP_NO_ACTIVE = false;
    public final static boolean NEXT_TIK_POSITION = true;
    public final static boolean ENEMY_ATTACK = true;
    public final static boolean INFO_RECOURCE = false;
    public final static boolean INFO_FOG_OF_WAR_MAP = true;


    public final static boolean CHECK_SEARCH_WAVE_BUILDER = false;
    public final static boolean CHECK_SEARCH_WAVE_START_END = false;

    public final static boolean CHECK_SEARCH_PATH_RANGER = false;
    public final static boolean CHECK_SEARCH_PATH_ASTAR = false;

    static public Player getMyPlayer(PlayerView playerView) {
        int myID = playerView.getMyId();
        for (int i = 0; i < playerView.getPlayers().length; i++) {
            if (playerView.getPlayers()[i].getId() == myID) return playerView.getPlayers()[i];
        }

        return null;
    }


    public static void DEBUG(String TAG, String text) {
        if (debug) {
            System.out.println(FinalConstant.getCurrentTik() + " " + TAG + " " + text);
        }
    }

    public static void DEBUGERROR(String text) {
        if (debugERROR) {
            System.out.println("ERROR " + text);
        }
    }

    public static void DEBUGRelease(String TAG, String text) {
        if (debugRelease) {
            System.out.println(TAG + " " + text);
        }
    }
}
