package com.example;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {

    private static final int PORT = 8081;

    public static String getMessage() {
        return "CI/CD Pipeline is working successfully!";
    }

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT), 0
        );

        server.createContext("/", exchange -> {

            byte[] response = getMessage().getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/plain; charset=UTF-8"
            );

            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response);
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println(
                "Application started at http://localhost:" + PORT
        );
    }
}