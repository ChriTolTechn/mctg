package bif3.tolan.swe1.mcg.constants;

/**
 * Static class with default values
 *
 * @author Christopher Tolan
 */
public final class DefaultValues {
    public static final int DEFAULT_USER_BALANCE = 20;
    public static final int DEFAULT_PACKAGE_COST = 5;

    /**
     * While the default elo is specified as 100, there was an option to change this with a more sophisticated elo system
     * Since the projected uses elo calculation based on chess, the default elo is now 1000
     */
    public static final int DEFAULT_ELO = 1000;

    public static final String ADMIN_USERNAME = "admin";
}
