package kg.attractor.java.lesson44;

public class Book {
    private int id;
    private String title;
    private String author;
    private String image;
    private String description;
    private String status;

    public Book(int id, String title, String author, String image, String description, String status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.image = image;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}