package kg.attractor.java.lesson44;

public class Employee {
    private int id;
    private String fullName;

    public Employee(int id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
}