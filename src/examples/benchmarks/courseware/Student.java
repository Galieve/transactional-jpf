package benchmarks.courseware;

import database.TRUtility;

public class Student {
    
    private int id;
    private String name;
    private boolean registered;
    private String rollNumber;

    public Student(int id, String name, boolean registered, String rollNumber) {
        this.id = id;
        this.name = name;
        this.registered = registered;
        this.rollNumber = rollNumber;
    }

    public Student(String student){
        this(
                TRUtility.getValue((s)->(Integer.parseInt(s)), student, 0),
                TRUtility.getValue(student, 1),
                TRUtility.getValue((s)->(Boolean.parseBoolean(s)),student, 2),
                TRUtility.getValue(student, 3)
        );
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getRollNumber() {
        return rollNumber;
    }
}
