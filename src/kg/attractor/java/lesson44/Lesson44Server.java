package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;
import kg.attractor.java.server.RouteHandler;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lesson44Server extends BasicServer {

    private final Map<String, String> users = new HashMap<>();
    private String currentUser;

    private final static Configuration freemarker = initFreeMarker();

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/sample", this::freemarkerSampleHandler);
        registerGet("/books", this::booksHandler);
        registerGet("/book", this::bookHandler);
        registerGet("/employee", this::employeeHandler);

//        registerGet("/register", this::registerGet);
//        registerPost("/register", this::registerPost);
//        registerGet("/login", this::loginGet);
//        registerPost("/login", this::loginPost);
//        registerGet("/profile", this::profileGet);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            // путь к каталогу в котором у нас хранятся шаблоны
            // это может быть совершенно другой путь, чем тот, откуда сервер берёт файлы
            // которые отправляет пользователю
            cfg.setDirectoryForTemplateLoading(new File("data"));

            // прочие стандартные настройки о них читать тут
            // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void freemarkerSampleHandler(HttpExchange exchange) {
        renderTemplate(exchange, "sample.html", getSampleDataModel());
    }
    protected void registerPost(String route, RouteHandler handler) {
        getRoutes().put("POST " + route, handler);
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            // Загружаем шаблон из файла по имени.
            // Шаблон должен находится по пути, указанном в конфигурации
            Template temp = freemarker.getTemplate(templateFile);

            // freemarker записывает преобразованный шаблон в объект класса writer
            // а наш сервер отправляет клиенту массивы байт
            // по этому нам надо сделать "мост" между этими двумя системами

            // создаём поток, который сохраняет всё, что в него будет записано в байтовый массив
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // создаём объект, который умеет писать в поток и который подходит для freemarker
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                // обрабатываем шаблон заполняя его данными из модели
                // и записываем результат в объект "записи"
                temp.process(dataModel, writer);
                writer.flush();

                // получаем байтовый поток
                var data = stream.toByteArray();

                // отправляем результат клиенту
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private SampleDataModel getSampleDataModel() {
        // возвращаем экземпляр тестовой модели-данных
        // которую freemarker будет использовать для наполнения шаблона
        return new SampleDataModel();
    }


    private void booksHandler(HttpExchange exchange) throws IOException {

        List<Book> books = DataStorage.getBooks();

        Map<String, Object> model = new HashMap<>();
        model.put("books", books);

        renderTemplate(exchange, "books.ftl", model);
    }


    private void bookHandler(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Missing id".getBytes());
            exchange.close();
            return;
        }

        int id = Integer.parseInt(query.split("=")[1]);

        Book foundBook = null;

        for (Book book : DataStorage.getBooks()) {
            if (book.getId() == id) {
                foundBook = book;
                break;
            }
        }

        if (foundBook == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().write("Book not found".getBytes());
            exchange.close();
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("book", foundBook);

        renderTemplate(exchange, "book.ftl", model);
    }


    private void employeeHandler(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Missing id".getBytes());
            exchange.close();
            return;
        }

        int id = Integer.parseInt(query.split("=")[1]);

        Employee foundEmployee = null;

        for (Employee employee : DataStorage.getEmployees()) {
            if (employee.getId() == id) {
                foundEmployee = employee;
                break;
            }
        }

        if (foundEmployee == null) {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().write("Employee not found".getBytes());
            exchange.close();
            return;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("employee", foundEmployee);

        renderTemplate(exchange, "employee.ftl", model);
    }

}
