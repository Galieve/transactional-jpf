package benchmarks.courseware;

import database.TRUtility;
import java.util.ArrayList;
import java.util.HashMap;

public class CourseWareUtility {


    public static HashMap<String, Student> readStudent(String st) {
        return TRUtility.generateHashMap(st, (s)->(new Student(s)));
    }

    public static HashMap<String, Course> readCourse(String course) {
        return TRUtility.generateHashMap(course, (s)->(new Course(s)));
    }

    public static HashMap<String, ArrayList<String>> readEnrollements(String course) {
        return TRUtility.generateHashMap(course, (s)->(TRUtility.generateArrayList(s)));
    }
}
