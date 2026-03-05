package kg.attractor.java.lesson44;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import kg.attractor.java.server.Utils;
import com.sun.net.httpserver.HttpExchange;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.server.RouteHandler;

import java.util.*;

public class Lesson44Server extends BasicServer {

    private final Map<String, Employee> users = new HashMap<>();
    private final Map<String, String> passwords = new HashMap<>();
    private final Map<String, String> sessions = new HashMap<>();

    private final Map<String, List<Integer>> currentBooks = new HashMap<>();
    private final Map<String, List<Integer>> pastBooks = new HashMap<>();


    private final Map<Integer, String> takenBooks = new HashMap<>();

    private final static Configuration freemarker = initFreeMarker();

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);

        registerGet("/books", this::booksHandler);
        registerGet("/book", this::bookHandler);

        registerGet("/register", this::registerGet);
        registerPost("/register", this::registerPost);

        registerGet("/login", this::loginGet);
        registerPost("/login", this::loginPost);

        registerGet("/profile", this::profileGet);

        registerGet("/giveBook", this::giveBookHandler);
        registerGet("/returnBook", this::returnBookHandler);

        registerGet("/logout", this::logoutHandler);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(dataModel, writer);
                writer.flush();
                byte[] data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    protected void registerPost(String route, RouteHandler handler) {
        getRoutes().put("POST " + route, handler);
    }



    private void registerGet(HttpExchange exchange) {
        renderTemplate(exchange, "register.ftl", new HashMap<>());
    }

    private void registerPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> data = Utils.parseUrlEncoded(raw, "&");

        String email = data.get("email");
        String name = data.get("name");
        String password = data.get("password");

        if (users.containsKey(email)) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", "User exists");
            renderTemplate(exchange, "register.ftl", model);
            return;
        }

        Employee employee = new Employee(users.size() + 1, name);

        users.put(email, employee);
        passwords.put(email, password);

        redirect303(exchange, "/login");
    }



    private void loginGet(HttpExchange exchange) {
        renderTemplate(exchange, "login.ftl", new HashMap<>());
    }

    private void loginPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> data = Utils.parseUrlEncoded(raw, "&");

        String email = data.get("email");
        String password = data.get("password");

        if (!passwords.containsKey(email) || !passwords.get(email).equals(password)) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Wrong login");
            renderTemplate(exchange, "login.ftl", model);
            return;
        }

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, email);

        exchange.getResponseHeaders()
                .add("Set-Cookie", "SESSION=" + sessionId + "; Max-Age=600; HttpOnly");

        redirect303(exchange, "/profile");
    }



    private void profileGet(HttpExchange exchange) {
        String email = getUser(exchange);

        if (email == null) {
            redirect303(exchange, "/login");
            return;
        }

        Employee employee = users.get(email);

        List<Integer> current = currentBooks.getOrDefault(email, new ArrayList<>());
        List<Integer> past = pastBooks.getOrDefault(email, new ArrayList<>());

        List<Book> allBooks = DataStorage.getBooks();

        List<Book> currentList = new ArrayList<>();
        List<Book> pastList = new ArrayList<>();

        for (Book book : allBooks) {
            if (current.contains(book.getId())) currentList.add(book);
            if (past.contains(book.getId())) pastList.add(book);
        }

        Map<String, Object> model = new HashMap<>();
        model.put("employee", employee);
        model.put("currentBooks", currentList);
        model.put("pastBooks", pastList);

        renderTemplate(exchange, "profile.ftl", model);
    }



    private void giveBookHandler(HttpExchange exchange) {

        String email = getUser(exchange);
        if (email == null) {
            redirect303(exchange, "/login");
            return;
        }

        Map<String, String> params =
                Utils.parseUrlEncoded(exchange.getRequestURI().getQuery(), "&");

        int id = Integer.parseInt(params.get("id"));


        if (takenBooks.containsKey(id)) {
            redirect303(exchange, "/books");
            return;
        }

        List<Integer> userCurrent =
                currentBooks.getOrDefault(email, new ArrayList<>());


        if (userCurrent.contains(id)) {
            redirect303(exchange, "/profile");
            return;
        }


        if (userCurrent.size() >= 2) {
            redirect303(exchange, "/profile");
            return;
        }

        userCurrent.add(id);
        currentBooks.put(email, userCurrent);

        takenBooks.put(id, email); //  отмечаем как занятую

        redirect303(exchange, "/profile");
    }



    private void returnBookHandler(HttpExchange exchange) {

        String email = getUser(exchange);
        if (email == null) {
            redirect303(exchange, "/login");
            return;
        }

        Map<String, String> params =
                Utils.parseUrlEncoded(exchange.getRequestURI().getQuery(), "&");

        int id = Integer.parseInt(params.get("id"));

        List<Integer> userCurrent =
                currentBooks.getOrDefault(email, new ArrayList<>());

        if (!userCurrent.contains(id)) {
            redirect303(exchange, "/profile");
            return;
        }

        userCurrent.remove((Integer) id);
        currentBooks.put(email, userCurrent);

        takenBooks.remove(id); //  освобождаем книгу

        List<Integer> history =
                pastBooks.getOrDefault(email, new ArrayList<>());

        history.add(id);
        pastBooks.put(email, history);

        redirect303(exchange, "/profile");
    }



    private void booksHandler(HttpExchange exchange) {
        Map<String, Object> model = new HashMap<>();
        model.put("books", DataStorage.getBooks());
        renderTemplate(exchange, "books.ftl", model);
    }

    private void bookHandler(HttpExchange exchange) {
        Map<String, String> params =
                Utils.parseUrlEncoded(exchange.getRequestURI().getQuery(), "&");

        int id = Integer.parseInt(params.get("id"));

        Book found = null;

        for (Book b : DataStorage.getBooks()) {
            if (b.getId() == id) {
                found = b;
                break;
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("book", found);

        renderTemplate(exchange, "book.ftl", model);
    }



    private void logoutHandler(HttpExchange exchange) {
        String cookie = getCookies(exchange);
        Map<String, String> cookies = Utils.parseUrlEncoded(cookie, ";");

        String sessionId = cookies.get("SESSION");

        if (sessionId != null) {
            sessions.remove(sessionId);
        }

        exchange.getResponseHeaders()
                .add("Set-Cookie", "SESSION=deleted; Max-Age=0");

        redirect303(exchange, "/login");
    }



    private String getUser(HttpExchange exchange) {
        String cookie = getCookies(exchange);
        Map<String, String> cookies = Utils.parseUrlEncoded(cookie, ";");
        String sessionId = cookies.get("SESSION");
        if (sessionId == null) return null;
        return sessions.get(sessionId);
    }

    private String getCookies(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                .getOrDefault("Cookie", List.of(""))
                .get(0);
    }

    protected String getBody(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset utf8 = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, utf8);

        try (BufferedReader reader = new BufferedReader(isr)) {
            return reader.lines().collect(Collectors.joining(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void redirect303(HttpExchange exchange, String path) {
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, 0);
            exchange.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}