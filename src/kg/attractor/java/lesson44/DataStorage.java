package kg.attractor.java.lesson44;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {

    public static List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        try {
            String content = Files.readString(Path.of("data/books.json"));
            JSONArray array = new JSONArray(content);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                books.add(new Book(
                        obj.getInt("id"),
                        obj.getString("title"),
                        obj.getString("author"),
                        obj.getString("image"),
                        obj.getString("description"),
                        obj.getString("status")
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static List<Employee> getEmployees() {
        List<Employee> employees = new ArrayList<>();
        try {
            String content = Files.readString(Path.of("data/employees.json"));
            JSONArray array = new JSONArray(content);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                employees.add(new Employee(
                        obj.getInt("id"),
                        obj.getString("fullName")
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }
}