package country.lab.search;

import country.lab.database.GuideInfo;
import country.lab.events.Transaction;
import country.lab.report.ChronoListener;
import country.lab.events.TransactionalEvent;
import country.lab.events.WriteTransactionalEvent;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPFListenerException;
import gov.nasa.jpf.util.Pair;
import gov.nasa.jpf.vm.VM;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.TreeMap;

public class ChronoTrPolyDFSearch extends TrPolyDFSearch implements ChronoSearch{


    protected ChronoListener chronoListener;

    protected TreeMap<String, Pair<Duration, Integer>> globalDuration;

    public ChronoTrPolyDFSearch(Config config, VM vm) {
        super(config, vm);
        chronoListener = new ChronoListener(config);
        globalDuration = new TreeMap<>();
    }

    protected void addDuration(String name, Duration duration){
        globalDuration.putIfAbsent(name, new Pair<>(Duration.ZERO, 1));
        var oldDuration = globalDuration.get(name);
        globalDuration.put(name, new Pair<>(duration.plus(oldDuration._1), oldDuration._2+ 1));
    }

    @Override
    protected void backtrackWithPath(LinkedList<Transaction> guidePath, TransactionalEvent end, WriteTransactionalEvent wSwap) {
        var start = Instant.now();
        super.backtrackWithPath(guidePath, end, wSwap);
        var finish = Instant.now();
        var duration = Duration.between(start, finish);
        addDuration(database.getDatabaseBacktrackMode() + "", duration);
    }

    @Override
    protected boolean checkDatabaseConsistency() {
        var start = Instant.now();
        var cons = super.checkDatabaseConsistency();
        var finish = Instant.now();
        var duration = Duration.between(start, finish);
        addDuration("databaseConsistency", duration);
        return cons;

    }

    @Override
    protected void backtrackDatabase() {

        var start = Instant.now();
        var startMode = database.getDatabaseBacktrackMode();
        super.backtrackDatabase();
        var finish = Instant.now();
        var endMode = database.getDatabaseBacktrackMode();
        var duration = Duration.between(start, finish);

        if(startMode == GuideInfo.BacktrackTypes.NONE){
            if(endMode == GuideInfo.BacktrackTypes.READ){
                addDuration(endMode + "", duration);

                /*chronoInfo = endMode + ": "+ readableDuration(duration);
                notifyChronoGap();

                 */
            }
            else if(endMode == GuideInfo.BacktrackTypes.SWAP || endMode == GuideInfo.BacktrackTypes.RESTORE){
                addDuration("Generate "+ endMode +" path", duration);
            }

        }
    }

    @Override
    public void search() {
        super.search();
        notifyChronoGap();

    }

    protected void notifyChronoGap() {
        try {
            chronoListener.chronoInfoUpdated(this);
        } catch (Throwable t) {
            throw new JPFListenerException("exception during notifyChronoGap() notification", t);
        }
    }

    public TreeMap<String, Pair<Duration, Integer>> getGlobalDuration() {
        return globalDuration;
    }
}
