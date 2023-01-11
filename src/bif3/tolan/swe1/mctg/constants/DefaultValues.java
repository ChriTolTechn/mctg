package bif3.tolan.swe1.mctg.constants;

/**
 * Default Values used throughout the app
 *
 * @author Christopher Tolan
 */
public final class DefaultValues {
    public static final int NEW_USER_COINS = 20;
    public static final int PACKAGE_COST = 5;

    /**
     * While the default elo is specified as 100, there was an option to change this with a more sophisticated elo system
     * Since the projected uses elo calculation based on chess, the default elo is now 1000
     */
    public static final int ELO = 1000;

    public static final String ADMIN_USERNAME = "admin";

    public static final int BATTLE_TIMEOUT = 20000;

    public static final int DECK_SIZE = 4;

    public static final float CRIT_PROBABILITY = 0.1f;

    public static final float CRIT_DAMAGE_MULTIPLIER = 1.5f;
}
