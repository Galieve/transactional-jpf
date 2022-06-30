package benchmarks;

import database.TRDatabase;

public abstract class Procedure {
    protected final TRDatabase db;

    public Procedure(TRDatabase db) {
        this.db = db;
    }
}
