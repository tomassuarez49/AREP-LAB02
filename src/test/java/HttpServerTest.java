import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mycompany.lab02.HttpRequest;
import com.mycompany.lab02.HttpResponse;
import com.mycompany.lab02.HttpServer;

public class HttpServerTest {

    private static Thread serverThread;

    @BeforeAll
    public static void startServer() {
        serverThread = new Thread(() -> {
            try {
                HttpServer.main(null); // Inicia el servidor
            } catch (IOException ex) {
            }
        });
        serverThread.start();

        try {
            Thread.sleep(2000); // Esperar a que el servidor inicie
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    
    @AfterAll
    public static void stopServer() {
        serverThread.interrupt();
    }


    @Test
    public void testIndexHtmlResponse() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:32000/");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(200, response.getCode(), "El código de estado debería ser 200");
                String content = EntityUtils.toString(response.getEntity());
                assertTrue(content.contains("<title>Prueba de Servidor</title>"), "El contenido debería incluir el título de la página");
                assertTrue(content.contains("<h1>Bienvenido a la prueba del servidor web</h1>"), "El contenido debería incluir el encabezado de la página");
            }
        }
    }

    @Test
    public void testDataResponse() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:32000/data");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(200, response.getCode(), "El código de estado debería ser 200");
                String content = EntityUtils.toString(response.getEntity());
                System.out.println("Contenido recibido: " + content); // Agregar esta línea para inspección
                assertTrue(content.contains("\"message\": \"Datos recibidos correctamente\""), "El contenido debería incluir el mensaje esperado en formato JSON");
            }
        }
    }

    @Test
    public void testNotFoundResponse() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:32000/nonexistent");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(404, response.getCode(), "El código de estado debería ser 404");
                String content = EntityUtils.toString(response.getEntity());
                assertTrue(content.contains("<h1>Archivo no encontrado</h1>"), "El contenido debería indicar un error 404");
            }
        }
    }

    @Test
    public void testCssResponse() throws IOException, ParseException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:32000/styles.css");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(200, response.getCode(), "El código de estado debería ser 200");
                assertEquals("text/css", response.getEntity().getContentType(), "El Content-Type debería ser text/css");
                String content = EntityUtils.toString(response.getEntity());
                assertTrue(content.contains("body"), "El contenido del CSS debería incluir estilos básicos");
            }
        }
    }

    @Test
    public void testImageResponse() throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:32000/image.jpeg");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(200, response.getCode(), "El código de estado debería ser 200");
                assertEquals("image/jpeg", response.getEntity().getContentType(), "El Content-Type debería ser image/jpeg");
                byte[] content = EntityUtils.toByteArray(response.getEntity());
                assertTrue(content.length > 0, "El contenido de la imagen no debería estar vacío");
            }
        }
    }


    @Test
    public void testHelloRoute() {
        HttpRequest req = new HttpRequest("/hello", "name=Pedro");
        HttpResponse res = new HttpResponse();
        
        String result = HttpServer.servicios.get("/hello").apply(req, res);
        
        assertEquals("Hello Pedro", result, "La ruta /hello no devolvió la respuesta esperada.");
    }

    @Test
    public void testPiRoute() {
        HttpRequest req = new HttpRequest("/pi", null);
        HttpResponse res = new HttpResponse();
        
        String result = HttpServer.servicios.get("/pi").apply(req, res);
        
        assertEquals(String.valueOf(Math.PI), result, "La ruta /pi no devolvió el valor esperado de PI.");
    }

    @Test
    public void testSumRoute() {
        HttpRequest req = new HttpRequest("/sum", "a=10&b=20");
        HttpResponse res = new HttpResponse();
        
        String result = HttpServer.servicios.get("/sum").apply(req, res);
        
        assertEquals("{\"a\": 10, \"b\": 20, \"sum\": 30}", result, "La ruta /sum no devolvió la suma correcta.");
    }



}
