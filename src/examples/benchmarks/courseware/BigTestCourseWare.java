package benchmarks.courseware;

import benchmarks.MainUtility;
import benchmarks.Worker;
import database.APIDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class BigTestCourseWare {

    /**
     * ID Generator. Given a database table name, returns a function that given some data
     * (String representation) returns its ID.
     * @param type
     * @return
     */
    private static Function<String, String> idGenerator(String type){

        switch (type) {
            case CourseWare.COURSE:
            case CourseWare.ENROLLMENTS:
            case CourseWare.STUDENT:
                return (s)->{
                    var sl = s.split(";");
                    return sl[0];
                };
            default:
                return null;

        }

    }

    private static int populateDB(String ... args){

        APIDatabase dbMain = APIDatabase.getDatabase();

        HashMap<String, String> tableInfo = new HashMap<>();
        HashSet<String> arraySet = new HashSet<>();


        tableInfo.put(CourseWare.COURSE, "{}");
        tableInfo.put(CourseWare.ENROLLMENTS, "{}");
        tableInfo.put(CourseWare.STUDENT, "{}");

        //warehouse, district, history, item, stock are not array
        arraySet.add(CourseWare.ENROLLMENTS);

        int ret = args.length - 1;


        for(int i = 0; i +1 < args.length && ret == args.length - 1; i+=2){
            switch (args[i]){

                case "-s":
                    tableInfo.put(CourseWare.STUDENT,  args[i+1]);
                    break;
                case "-c":
                    tableInfo.put(CourseWare.COURSE,  args[i+1]);
                    break;
                case "-e":
                    tableInfo.put(CourseWare.ENROLLMENTS, args[i+1]);
                    break;
                default:
                    ret = i;
                    break;

            }
        }
        new MainUtility(dbMain).initTransaction(tableInfo, arraySet, (s)->(idGenerator(s)));

        return ret;


    }

    public static void main(String [] args) {


        var idx = populateDB(args);


        var ses = CourseWare.getInstance();

        try {
            var threads = new ArrayList<Thread>();
            for(int i = idx; i < args.length; ++i){
                threads.add(new Thread(new Worker<>(ses, args[i])));
            }
            for(var t : threads){
                t.start();
            }
            for(var t : threads){
                t.join();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
