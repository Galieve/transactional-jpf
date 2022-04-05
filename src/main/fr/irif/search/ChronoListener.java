package fr.irif.search;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.ListenerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;

public class ChronoListener extends ListenerAdapter {

    protected PrintWriter out;

    public ChronoListener(Config config){
        String path = config.getString("db.chronolistener.out", "chrono_reports.out");
        File file = new File(path);
        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found.");
        }

    }

    protected String readableDuration(Duration duration){
        var s = duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
        return s.substring(0, s.length() - 1);
    }

    public void chronoInfoUpdated(ChronoTrDFSearch search){
        for(var v : search.getGlobalDuration().entrySet()){
            var durInt = v.getValue();
            out.println(v.getKey() + ": "+readableDuration(durInt._1));
            out.println("Number of: " + v.getKey() + ": "+durInt._2);
        }
        out.close();
    }
}
