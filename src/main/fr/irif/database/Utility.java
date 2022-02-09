package fr.irif.database;

import java.util.ArrayList;

public class Utility {
    public static <T> ArrayList<ArrayList<T>> deepCopy(ArrayList<ArrayList<T>> mat){
        ArrayList<ArrayList<T>> newMat = new ArrayList<>();
        for(ArrayList<T> a: mat){
            newMat.add(new ArrayList<>(a));
        }
        return newMat;
    }


}
