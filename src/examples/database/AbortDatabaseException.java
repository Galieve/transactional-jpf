package database;

    public class AbortDatabaseException extends Exception{

    public AbortDatabaseException(String message) {
        super(message);
    }

    public AbortDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
