package country.lab.search;

import gov.nasa.jpf.util.Pair;

import java.time.Duration;
import java.util.TreeMap;

public interface ChronoSearch {

    public TreeMap<String, Pair<Duration, Integer>> getGlobalDuration();

}
