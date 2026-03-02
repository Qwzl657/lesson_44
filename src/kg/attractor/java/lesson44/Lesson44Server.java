package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Lesson44Server extends BasicServer {

    private static final Configuration freemarker = initFreeMarker();

    // ====== 45 (авторизация) ======
    private final Map<String, String> users = new HashMap<>();
    private String currentUser;

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);

        // ===== 44 =====
        registerGet("/books", this::booksHandler);
        registerGet("/book", this::bookHandler);
        registerGet("/employee", this::employeeHandler);

        // ===== 45 =====
        registerGet("/register", this::registerGet);
        registerPost("/register", this::registerPost);

        registerGet("/login", this::loginGet);
        registerPost("/login", this::loginPost);

        registerGet("/profile", this::profileGet);
    }

    // =====================================================
    // ================== FreeMarker ========================
    // =====================================================

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

    protected void renderTemplate(HttpExchange exchange, String template, Object model) {
        try {
            Template temp = freemarker.getTemplate(template);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(model, writer);
                writer.flush();
            }

            sendByteData(exchange,
                    ResponseCodes.OK,
                    ContentType.TEXT_HTML,
                    stream.toByteArray());

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    protected void registerPost(String route, kg.attractor.java.server.RouteHandler handler) {
        getRoutes().put("POST " + route, handler);
    }

    // =====================================================
    // ====================== 44 ===========================
    // =====================================================

    private void booksHandler(HttpExchange exchange) {
        List<Book> books = DataStorage.getBooks();

        Map<String, Object> model = new HashMap<>();
        model.put("books", books);

        renderTemplate(exchange, "books.ftl", model);
    }

    private void bookHandler(HttpExchange exchange) throws IOException {

        Map<String, String> params = parseQuery(exchange);
        String idStr = params.get("id");

        if (idStr == null) {
            sendText(exchange, 400, "Missing id");
            return;
        }

        int id = Integer.parseInt(idStr);

        Book foundBook = DataStorage.getBooks()
                .stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);

        if (foundBook == null) {
            sendText(exchange, 404, "Book not found");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("book", foundBook);

        renderTemplate(exchange, "book.ftl", model);
    }

    private void employeeHandler(HttpExchange exchange) throws IOException {

        Map<String, String> params = parseQuery(exchange);
        String idStr = params.get("id");

        if (idStr == null) {
            sendText(exchange, 400, "Missing id");
            return;
        }

        int id = Integer.parseInt(idStr);

        Employee foundEmployee = DataStorage.getEmployees()
                .stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);

        if (foundEmployee == null) {
            sendText(exchange, 404, "Employee not found");
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("employee", foundEmployee);

        renderTemplate(exchange, "employee.ftl", model);
    }

    // =====================================================
    // ====================== 45 ===========================
    // =====================================================

    private void registerGet(HttpExchange exchange) {
        renderTemplate(exchange, "register.ftl", new HashMap<>());
    }

    private void loginGet(HttpExchange exchange) {
        renderTemplate(exchange, "login.ftl", new HashMap<>());
    }

    private void profileGet(HttpExchange exchange) {

        Map<String, Object> model = new HashMap<>();

        if (currentUser == null) {
            model.put("user", "Некий пользователь");
        } else {
            model.put("user", currentUser);
        }

        renderTemplate(exchange, "profile.ftl", model);
    }

    private void registerPost(HttpExchange exchange) throws IOException {

        Map<String, String> data = parseBody(exchange);

        String email = data.get("email");
        String password = data.get("password");

        Map<String, Object> model = new HashMap<>();

        if (email == null || password == null) {
            model.put("error", "Fill all fields");
            renderTemplate(exchange, "register.ftl", model);
            return;
        }

        if (users.containsKey(email)) {
            model.put("error", "User already exists");
            renderTemplate(exchange, "register.ftl", model);
            return;
        }

        users.put(email, password);

        redirect(exchange, "/login");
    }

    private void loginPost(HttpExchange exchange) throws IOException {

        Map<String, String> data = parseBody(exchange);

        String email = data.get("email");
        String password = data.get("password");

        Map<String, Object> model = new HashMap<>();

        if (!users.containsKey(email)) {
            model.put("error", "User not found");
            renderTemplate(exchange, "login.ftl", model);
            return;
        }

        if (!users.get(email).equals(password)) {
            model.put("error", "Wrong password");
            renderTemplate(exchange, "login.ftl", model);
            return;
        }

        currentUser = email;

        redirect(exchange, "/profile");
    }

    // =====================================================
    // ================= ВСПОМОГАТЕЛЬНЫЕ ===================
    // =====================================================

    private Map<String, String> parseQuery(HttpExchange exchange) {

        Map<String, String> result = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();

        if (query == null) return result;

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            if (!pair.contains("=")) continue;
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                result.put(parts[0], parts[1]);
            }
        }

        return result;
    }

    private Map<String, String> parseBody(HttpExchange exchange) throws IOException {

        InputStream input = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8));

        StringBuilder raw = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            raw.append(line);
        }

        Map<String, String> result = new HashMap<>();
        String[] pairs = raw.toString().split("&");

        for (String pair : pairs) {
            if (!pair.contains("=")) continue;
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                result.put(parts[0], parts[1]);
            }
        }

        return result;
    }

    private void redirect(HttpExchange exchange, String path) throws IOException {
        exchange.getResponseHeaders().add("Location", path);
        exchange.sendResponseHeaders(303, -1);
        exchange.close();
    }

    private void sendText(HttpExchange exchange, int code, String text) throws IOException {
        exchange.sendResponseHeaders(code, text.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(text.getBytes());
        os.close();
    }
}