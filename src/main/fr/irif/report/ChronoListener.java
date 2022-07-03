package fr.irif.report;

import fr.irif.search.ChronoSearch;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Duration;

public class ChronoListener extends ListenerAdapter {

    protected PrintWriter out;

    protected boolean actualFile = false;

    public ChronoListener(Config config){
        String path = config.getString("db.chronolistener.out", "chrono_reports.out");
        File file = new File(path);
        try {
            out = new PrintWriter(file);
            actualFile = true;
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

    public void chronoInfoUpdated(ChronoSearch search){
        for(var v : search.getGlobalDuration().entrySet()){
            var durInt = v.getValue();
            out.println(v.getKey() + ": "+readableDuration(durInt._1));
            out.println("Number of: " + v.getKey() + ": "+durInt._2);
        }
        out.close();
    }

    @Override
    public void searchFinished(Search search) {
        if(actualFile)
            out.close();
        super.searchFinished(search);
    }
}
