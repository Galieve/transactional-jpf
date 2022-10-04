package fr.irif.report;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Reporter;
import gov.nasa.jpf.report.Statistics;
import gov.nasa.jpf.vm.ClassLoaderInfo;
import gov.nasa.jpf.vm.MethodInfo;

import java.io.PrintWriter;

public class TrConsolePublisher extends ConsolePublisher {
    public TrConsolePublisher(Config conf, Reporter reporter) {
        super(conf, reporter);
        var pref = conf.getString("report.console.file-prefix");
        var model = conf.getString("db.database_isolation_level.class");
        var trueModel = conf.getOrDefault("db.database_isolation_level.class",
                conf.getString("db.database_isolation_level.class"));
        if(pref != null && model != null && trueModel != null) {
            fileName = pref + "-" + model + "+" + trueModel + ".out";
        }
        else{
            System.out.println("ERROR: "+pref + "-" + model + "+" + trueModel + ".out");
        }

    }

    // this can be used outside a publisher, to show the same info
    public static void printStatistics (PrintWriter pw, Reporter reporter){
        Statistics stat = reporter.getStatistics();
        var trStat = (TrStatistics) stat;

        pw.println("elapsed time:       " + formatHMS(reporter.getElapsedTime()));
        pw.println("states:             new=" + stat.newStates + ",visited=" + stat.visitedStates
                + ",backtracked=" + stat.backtracked + ",end=" + stat.endStates);
        pw.println("transactional:      histories="+trStat.histories + ",swaps=" + trStat.swaps
                + ",usefulSwaps="+ trStat.usefulSwaps);
        pw.println("search:             maxDepth=" + stat.maxDepth + ",constraints=" + stat.constraints);
        pw.println("choice generators:  thread=" + stat.threadCGs
                + " (signal=" + stat.signalCGs + ",lock=" + stat.monitorCGs + ",sharedRef=" + stat.sharedAccessCGs
                + ",threadApi=" + stat.threadApiCGs + ",reschedule=" + stat.breakTransitionCGs
                + "), data=" + stat.dataCGs);
        pw.println("heap:               " + "new=" + stat.nNewObjects
                + ",released=" + stat.nReleasedObjects
                + ",maxLive=" + stat.maxLiveObjects
                + ",gcCycles=" + stat.gcCycles);
        pw.println("instructions:       " + stat.insns);
        pw.println("max memory:         " + (stat.maxUsed >> 20) + "MB");

        pw.println("loaded code:        classes=" + ClassLoaderInfo.getNumberOfLoadedClasses() + ",methods="
                + MethodInfo.getNumberOfLoadedMethods());
    }

    public synchronized void printStatistics (PrintWriter pw){
        publishTopicStart( STATISTICS_TOPIC);
        printStatistics( pw, reporter);
    }

    @Override
    public void publishStatistics() {
        printStatistics(out);
    }
}
