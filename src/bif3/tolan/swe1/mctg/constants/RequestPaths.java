package bif3.tolan.swe1.mctg.constants;

/**
 * Collection of Paths used in the server and workers
 *
 * @author Christopher Tolan
 */
public final class RequestPaths {
    // User Worker
    public static final String USER_WORKER_MAIN_PATH = "users";
    public static final String USER_WORKER_REGISTRATION = "";

    // Login Worker
    public static final String LOGIN_WORKER_MAIN_PATH = "sessions";
    public static final String LOGIN_WORKER_LOGIN = "";

    // Package Worker
    public static final String PACKAGES_WORKER_MAIN_PATH = "packages";
    public static final String PACKAGE_WORKER_CREATE = "";

    // Shop Worker
    public static final String SHOP_WORKER_MAIN_PATH = "transactions";
    public static final String SHOP_WORKER_BUY_PACKAGE = "packages";

    // Card Worker
    public static final String CARD_WORKER_MAIN_PATH = "cards";
    public static final String CARD_WORKER_SHOW_CARDS = "";

    // Deck Worker
    public static final String DECK_WORKER_MAIN_PATH = "deck";
    public static final String DECK_WORKER_SHOW_DECK = "";
    public static final String DECK_WORKER_CONFIGURE_DECK = "";

    // Stats Worker
    public static final String STATISTICS_WORKER_MAIN_PATH = "stats";
    public static final String STATISTICS_WORKER_GET_STATS = "";

    // Scoreboard Worker
    public static final String SCOREBOARD_WORKER_MAIN_PATH = "score";
    public static final String SCOREBOARD_WORKER_GET_SCOREBOARD = "";

    // Battle Worker
    public static final String BATTLE_WORKER_MAIN_PATH = "battles";
    public static final String BATTLE_WORKER_BATTLE = "";

    // Trade Worker
    public static final String TRADE_WORKER_MAIN_PATH = "tradings";
    public static final String TRADE_WORKER_GET_TRADES = "";
    public static final String TRADE_WORKER_ADD_TRADE = "";
}
