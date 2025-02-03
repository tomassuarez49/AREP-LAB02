package com.mycompany.lab02;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class HttpServer {

    public static Map<String, BiFunction<HttpRequest, HttpResponse, String>> servicios = new HashMap<>();
    private static String staticFilesPath = "webroot";

    public static void main(String[] args) throws IOException {
        Integer port = 32000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en el puerto: " + port);

        // Registrar rutas REST
        get("/hello", (req, res) -> "Hello " + req.getValues("name"));
        get("/pi", (req, res) -> String.valueOf(Math.PI));
        get("/e", (req, res) -> String.valueOf(Math.E));
        get("/data", (req, res) -> {return "{\"message\": \"Datos recibidos correctamente\"}";});
        get("/sum", (req, res) -> {
            try {
                // Obtén los valores    de los parámetros "a" y "b"
                int a = Integer.parseInt(req.getValues("a"));
                int b = Integer.parseInt(req.getValues("b"));
                
                // Suma los números
                int result = a + b;

                // Devuelve la respuesta en formato JSON
                return "{\"a\": " + a + ", \"b\": " + b + ", \"sum\": " + result + "}";
            } catch (NumberFormatException | NullPointerException e) {
                // Manejo de errores: parámetros inválidos o ausentes
                return "{\"error\": \"Por favor proporciona los parametros 'a' y 'b' como numeros enteros.\"}";
            }
        });




        boolean running = true;
        while (running) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleRequest(clientSocket);
            }
        }
    }

    public static void get(String route, BiFunction<HttpRequest, HttpResponse, String> handler) {
        servicios.put(route, handler);
    }   

    public static void staticfiles(String path) {
        staticFilesPath = path;
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String requestLine = in.readLine();
        if (requestLine != null && requestLine.startsWith("GET")) {
            String fileName = requestLine.split(" ")[1];
            
            if (fileName.equals("/")) {
                fileName = "/index.html"; // Redirige a index.html si no se especifica archivo
            }

            //System.out.println("Buscando archivo en: " + staticFilesPath + fileName);

            // Separar ruta y parámetros
            String[] pathAndQuery = fileName.split("\\?", 2);
            String path = pathAndQuery[0]; // Ruta sin parámetros
            String query = (pathAndQuery.length > 1) ? pathAndQuery[1] : null;

            System.out.println("Ruta solicitada: " + path);

            BiFunction<HttpRequest, HttpResponse, String> handler = servicios.get(path);
            if (handler != null) {
                HttpRequest req = new HttpRequest(path, query);
                HttpResponse res = new HttpResponse();
                String response = handler.apply(req, res);

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/html");
                out.println();
                out.println(response);
                return;
            }

            File file = new File(staticFilesPath + fileName);
            if (file.exists() && !file.isDirectory()) {
                String contentType = getContentType(file);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + contentType);
                out.println();
                sendFile(file, clientSocket.getOutputStream());
            } else {
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/html");
                out.println();
                out.println("<h1>Archivo no encontrado</h1>");
            }
        }
    }

    private static String getContentType(File file) {
        if (file.getName().endsWith(".html")) {
            return "text/html";
        } else if (file.getName().endsWith(".css")) {
            return "text/css";
        } else if (file.getName().endsWith(".js")) {
            return "application/javascript";
        } else if (file.getName().endsWith(".jpeg") || file.getName().endsWith(".jpg")) {
            return "image/jpeg";
        } else if (file.getName().endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream";
        }
    }

    private static void sendFile(File file, OutputStream out) throws IOException {
        if (!file.exists() || !file.canRead() || file.isDirectory()) {
            throw new FileNotFoundException("No se puede acceder al archivo: " + file.getAbsolutePath());
        }

        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
