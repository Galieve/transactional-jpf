package fr.irif.database;

public class TrivialHistory extends COInductiveHistory {
    @Override
    protected boolean computeConsistency() {
        return true;
    }
}
