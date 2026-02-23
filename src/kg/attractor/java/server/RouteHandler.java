package kg.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpExchange exchange) throws IOException;
}
