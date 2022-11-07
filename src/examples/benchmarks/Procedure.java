package benchmarks;

import database.APIDatabase;

public abstract class Procedure {
    protected final APIDatabase db;

    public Procedure(APIDatabase db) {
        this.db = db;
    }
}
