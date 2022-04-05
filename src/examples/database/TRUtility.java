package database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TRUtility {

    public static<T> HashMap<String, T> generateHashMap(String s, Function<String, T> build){
        HashMap<String, T> map = new HashMap<>();

        if(!s.equals("{}")){
            var array = new ArrayList<>(Arrays.asList(s.replaceAll("[{}]","").split(",")));
            for(var e: array){
                var el = e.split("=");
                map.put(el[0].trim(), build.apply(el[1].trim()));
            }
        }
        return map;
    }

    public static ArrayList<String> generateArrayList(String s){
        return generateArrayList(s, (t)->t);
    }

    public static<T> ArrayList<T> generateArrayList(String s, Function<String, T> build){
        ArrayList<T> arrayList = new ArrayList<>();

        if(!s.equals("[]")){
            var array = new ArrayList<>(Arrays.asList(s.replaceAll("[\\[\\]]","").split(",")));
            for(var e: array){
                arrayList.add(build.apply(e.trim()));
            }
        }
        return arrayList;
    }

    public static String getValue(String s, int id){
        return s.split(";")[id].trim();
    }

    public static ArrayList<String> getArrayValue(String s, int id){
        return getArrayValue(s, id, (t) -> t);
    }

    public static<T> ArrayList<T> getArrayValue(String s, int id, Function<String, T> build){
        var info = getValue(s, id);
        var array = Arrays.stream(info.split(",")).map(build).collect(Collectors.toList());
        return new ArrayList<T>(array);
    }
}
