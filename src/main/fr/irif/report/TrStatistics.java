package fr.irif.report;

import fr.irif.database.Database;
import fr.irif.database.GuideInfo;
import fr.irif.search.TrDFSearch;
import gov.nasa.jpf.report.Statistics;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.VM;

public class TrStatistics extends Statistics {

    public int swaps = 0;
    public int usefulSwaps = 0;
    public int histories = 0;

    @Override
    public TrStatistics clone() {
        return (TrStatistics) super.clone();
    }

    @Override
    public void stateAdvanced (Search search) {
        super.stateAdvanced(search);
        if (search.isEndState() && Database.getDatabase().isTrulyConsistent()) {
            histories++;
        }
    }

    @Override
    public void stateProcessed(Search search) {
        super.stateProcessed(search);
        if(search instanceof TrDFSearch) {
            var trSearch = (TrDFSearch) search;
            var msg = trSearch.getMessage();
            //var msg = trSearch.getAndClearMessage(); //The reporter is the last listener that will use this message.
            if (msg != null && msg.equals(GuideInfo.BacktrackTypes.SWAP + " mode ended.")) {
                ++swaps;
                if (Database.getDatabase().isTrulyConsistent()) {
                    ++usefulSwaps;
                }
            }
        }

    }



}
