package fr.irif.database;

import java.util.ArrayList;

public class Utility {

    public static <T> ArrayList<ArrayList<T>> deepCopyMatrix(ArrayList<ArrayList<T>> mat){
        ArrayList<ArrayList<T>> newMat = new ArrayList<>();
        for(ArrayList<T> a: mat){
            newMat.add(new ArrayList<>(a));
        }
        return newMat;
    }

    /*public static <T, U> boolean nonEmptyIntersection(HashMap<T,U> m1, HashMap<T,U> m2){
        for(var k : m1.keySet()){
            if(m2.containsKey(k)){
        }
    }

     */






}
