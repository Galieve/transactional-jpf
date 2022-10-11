package benchmarks.courseware;

import benchmarks.BenchmarkModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseWare extends BenchmarkModule {

    public static final String STUDENT = "STUDENT";
    public static final String COURSE = "COURSE";
    public static final String ENROLLMENTS = "ENROLLMENTS";

    private static CourseWare courseWareInstance;

    public static CourseWare getInstance() {
        if(courseWareInstance == null){
            courseWareInstance = new CourseWare();
        }
        return courseWareInstance;
    }

    @Override
    public HashMap<String, Class<?>[]> getAllMethods() {
        var map = new HashMap<String, Class<?>[]>();
        map.put("enroll", new Class<?>[]{int.class, int.class});
        map.put("getEnrollments", new Class<?>[]{});
        map.put("deleteCourse", new Class<?>[]{int.class});
        map.put("openCourse",new Class<?>[]{
                int.class
        });
        map.put("closeCourse", new Class<?>[]{int.class});
        return map;
    }

    public void enroll(int studentID, int courseID){
        db.begin();
        var studentTable = CourseWareUtility.readStudent(db.read(STUDENT));
        var student = studentTable.get(studentID+"");
        var courseTable = CourseWareUtility.readCourse(db.read(COURSE));
        var course = courseTable.get(courseID+"");

        if(student != null && course != null &&
                !student.isRegistered() && course.getStatus().equals("open")){

            var enrollmentsTable =
                    CourseWareUtility.readEnrollements(db.read(ENROLLMENTS));

            enrollmentsTable.putIfAbsent(courseID+"", new ArrayList<>());
            var enrollments = enrollmentsTable.get(courseID+"");
            if(enrollments.size() < course.getCapacity()){
                enrollments.add(studentID+"");

                db.write(ENROLLMENTS, enrollmentsTable.toString());
            }

        }

        db.commit();
    }

    public Map<String, ArrayList<String>> getEnrollments(){
        db.begin();
        var enrollmentsTable =
                CourseWareUtility.readEnrollements(db.read(ENROLLMENTS));
        db.commit();
        return enrollmentsTable;
    }

    public void closeCourse(int courseID){
        db.begin();
        modifyCourseStatusBody(courseID, "close");
        db.commit();
    }

    protected void modifyCourseStatusBody(int courseID, String label){
        var courseTable =
                CourseWareUtility.readCourse(db.read(COURSE));
        var course = courseTable.get(courseID+"");
        if(course != null){
            course.setStatus(label);
            db.write(COURSE, courseTable.toString());
        }
    }

    public void openCourse(int courseID){
        db.begin();
        modifyCourseStatusBody(courseID, "open");
        db.commit();
    }


    public void deleteCourse(int courseID){
        db.begin();
        modifyCourseStatusBody(courseID, "close");
        var courseTable =
                CourseWareUtility.readCourse(db.read(COURSE));
        courseTable.remove(courseID+"");
        db.write(COURSE, courseTable.toString());

        var enrollmentsTable = CourseWareUtility.readEnrollements(db.read(ENROLLMENTS));
        enrollmentsTable.remove(courseID+"");
        db.write(ENROLLMENTS, enrollmentsTable.toString());

        db.commit();
    }
}
