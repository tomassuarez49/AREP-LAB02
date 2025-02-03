

package com.mycompany.lab02;

/**
 *
 * @author tomas
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static void main(String[] args) throws IOException {

        Integer port = 32000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en el puerto :" + port);


        boolean running = true;
        while (running) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleRequest(clientSocket);
            }
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

    String requestLine = in.readLine();
    if (requestLine != null && requestLine.startsWith("GET")) {
        String fileName = requestLine.split(" ")[1];
        if (fileName.equals("/")) {
            fileName = "/index.html"; // Página principal
        }

        // Verificar si la solicitud es para la ruta "/data"
        if (fileName.equals("/data")) {
            // Responder con datos en formato JSON
            String jsonResponse = "{\"message\": \"Datos recibidos correctamente\"}";

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(jsonResponse);

            return; // Salir después de responder la solicitud de JSON, evitando el procesamiento posterior
        }

        // Para cualquier otra solicitud, buscar archivos estáticos en la carpeta "webroot"
        File file = new File("webroot" + fileName); 
        if (file.exists()) {
            String contentType = getContentType(file);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println();
            sendFile(file, clientSocket.getOutputStream());
        } else {
            out.println("HTTP/1.1 404 Not Found");
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
        } else if (file.getName().endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "application/octet-stream";
        }
    }

    private static void sendFile(File file, OutputStream out) throws IOException {
        try (FileInputStream fileIn = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}

