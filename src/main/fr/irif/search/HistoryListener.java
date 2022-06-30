package fr.irif.search;

import fr.irif.database.Database;
import fr.irif.events.TrEventRegister;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.search.Search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class HistoryListener extends ListenerAdapter {

    protected TrEventRegister trEventRegister;

    protected PrintWriter out;

    protected long counter;

    protected boolean actualFile = false;

    public HistoryListener(Config config) {
        trEventRegister = TrEventRegister.getEventRegister();
        //config.getEssentialInstance("out.database_model.class", DatabaseRelations.class);
        String path = config.getString("db.history.out", "histories/histories."+getDefaultFileName(config)+".out");
        counter = 0;
        if (path == null) {
            out = new PrintWriter(System.out, true);
        } else {
            File file = new File(path);
            //File file = config.getPath(path);
            try {
                out = new PrintWriter(file);
                actualFile = true;
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File not found: " + path);
            }
        }
    }

    protected String getDefaultFileName(Config config){
        var freeargs = config.getFreeArgs();
        for(var a : freeargs){
            if(a != null && !a.matches("([-+]).*")){
                return a;
            }
        }
        return null;
    }

    @Override
    public void stateProcessed(Search search) {
        var database = Database.getDatabase();
        if(search.isEndState() || database.isAssertionViolated()){
            out.println("----------------------------------- history #" +
                  counter + ": ");
            out.println(database.getDatabaseState());
            ++counter;
        }
    }

    @Override
    public void searchFinished(Search search) {
        if(actualFile)
            out.close();
        super.searchFinished(search);
    }
}
