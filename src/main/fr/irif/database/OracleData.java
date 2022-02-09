package fr.irif.database;

import gov.nasa.jpf.util.Pair;

public class OracleData implements Comparable<OracleData>{
    Pair<Integer, Integer> info;

    public OracleData(Integer trid, Integer id) {

        info = new Pair<>(trid, id);
    }

    public Pair<Integer, Integer> getInfo() {
        return info;
    }

    public void setInfo(Pair<Integer, Integer> info) {
        this.info = info;
    }

    @Override
    public int compareTo(OracleData o) {
        if(info._1 < o.info._1) return -1;
        else if(info._1 > o.info._1) return 1;
        else return info._2 - o.info._2;
    }
}