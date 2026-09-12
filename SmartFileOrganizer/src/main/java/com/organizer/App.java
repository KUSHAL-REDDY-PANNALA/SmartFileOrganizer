package com.organizer;

import com.google.gson.Gson;
import com.organizer.core.ScannerService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/scan", new ScanHandler());
        
        server.setExecutor(null);
        System.out.println("Server started on http://localhost:8080");
        server.start();
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            File file = new File("src/main/resources/public/index.html");
            byte[] response = Files.readAllBytes(file.toPath());
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    static class ScanHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                // Hardcoded to scan user directory for safety, can be dynamically passed
                String pathToScan = "D:/Downloads";
                ScannerService scanner = new ScannerService();
                scanner.scanDirectory(pathToScan);
                
                Map<String, Object> results = scanner.getResults();
                String jsonResponse = new Gson().toJson(results);

                t.getResponseHeaders().set("Content-Type", "application/json");
                t.sendResponseHeaders(200, jsonResponse.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(jsonResponse.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}