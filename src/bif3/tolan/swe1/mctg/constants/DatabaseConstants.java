package bif3.tolan.swe1.mctg.constants;

/**
 * Constants needed for Database connection
 *
 * @author Christopher Tolan
 */
public final class DatabaseConstants {
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "mctg_db";
    public static final String DB_USERNAME = "postgres";
    public static final String DB_PASSWORD = "";
    public static final boolean TRY_RESET_DB_ON_STARTUP = true;
}
