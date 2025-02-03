

# Web Framework for REST Services and Static File Management
Tomas Suarez Piratova

Este proyecto es un framework web minimalista desarrollado en Java, diseñado para permitir a los desarrolladores construir aplicaciones web con servicios REST personalizados y gestión de archivos estáticos. El framework está diseñado para ser sencillo, eficiente y extensible, proporcionando herramientas clave para manejar solicitudes HTTP y servir contenido dinámico y estático.

## Requisitos

-   **Java Development Kit (JDK) 17 o superior**
-   **Maven 3.6.0 o superior**
-   **Un IDE como NetBeans, IntelliJ IDEA, Eclipse, etc**
-   **Un navegador web**

## Instalación

1.  **Clonar el repositorio:**
     
    `https://github.com/tomassuarez49/AREP-LAB02` 
    
2.  **Compilar el proyecto con Maven:** :
 
    
    `mvn clean install` 
    
3.  **Estructura de archivos:**

   ![image](https://github.com/user-attachments/assets/8cdd6322-c7d1-454c-9998-2eb92a81c668)


    
    

## Ejecución

1.  **Iniciar el servidor:** Ejecutar la siguiente clase de la aplicacion para iniciar la aplicación:
        
    `HttpServer.java"` 
    
2.  **Accede al servidor:** Abrir tu navegador web y visitar `http://localhost:32000`. Aquí podrás:
    
    Accede al servidor: Abre tu navegador web y visita http://localhost:32000 Podremos:

   - [http://localhost:32000/index.html](http://localhost:32000/index.html) → Página principal  
   - [http://localhost:32000/hello?name=Pedro](http://localhost:32000/hello?name=Pedro) → Respuesta `"Hello Pedro"`  
   - [http://localhost:32000/pi](http://localhost:32000/pi) → Devuelve `"3.141592653589793"`  
   - [http://localhost:32000/e](http://localhost:32000/e) → Devuelve `"2.718281828459045"`  
   - [http://localhost:32000/data](http://localhost:32000/data) → Devuelve `{"message": "Datos recibidos correctamente"}`  
   - [http://localhost:32000/sum?a=10&b=20](http://localhost:32000/sum?a=10&b=20) → Devuelve `{"a": 10, "b": 20, "sum": 30}`  

## Arquitectura

## Funcionalidades principales

- **Servidor de archivos estáticos**: 
  Sirve archivos HTML, CSS, JavaScript e imágenes desde la carpeta `webroot`.



- **Gestión de rutas no encontradas**: 
  Devuelve un error 404 para rutas no válidas.

## Personalización

- Puedes agregar tus propios archivos HTML, CSS o imágenes en la carpeta `webroot` para que sean servidos por el servidor.
- Si deseas añadir nuevas rutas o funcionalidades, modifica la clase `HttpServer.java`.

## Pruebas

El proyecto incluye un conjunto de pruebas automatizadas para garantizar el correcto funcionamiento del servidor:

1. **Ejecuta los tests con Maven**:
    ```bash
    mvn test
    ```

2. ### 🔍 **Resumen de las pruebas implementadas**

| Test | Descripción | Entrada de Ejemplo | Salida Esperada |
|------|------------|--------------------|----------------|
| **Ruta `/hello`** | Verifica que devuelve el mensaje de saludo correcto. | `/hello?name=Pedro` | `"Hello Pedro"` |
| **Ruta `/pi`** | Devuelve el valor correcto de π. | `/pi` | `"3.141592653589793"` |
| **Ruta `/e`** | Devuelve el valor correcto de `e`. | `/e` | `"2.718281828459045"` |
| **Ruta `/data`** | Responde con JSON correcto. | `/data` | `{"message": "Datos recibidos correctamente"}` |
| **Ruta `/sum`** | Suma dos números y devuelve JSON. | `/sum?a=10&b=20` | `{"a":10, "b":20, "sum":30}` |
| **Manejo de error en `/sum`** | Maneja parámetros inválidos. | `/sum?a=x&b=10` | `{"error": "Por favor proporciona los parámetros 'a' y 'b' como números enteros."}` |
| **Archivos estáticos** | Verifica que se puedan leer archivos en `webroot/`. | `/index.html` | `Contenido de index.html` |
| **Manejo de error 404** | Verifica que el servidor responde correctamente a rutas inexistentes. | `/no-existe` | `<h1>Archivo no encontrado</h1>` |

---

![image](https://github.com/user-attachments/assets/58f29079-dfdf-47a0-9972-393b85ebb9bc)


3, **Pruebas en el navegador**

![image](https://github.com/user-attachments/assets/1d2481be-2e47-4a38-aba3-f8b22d751d3a)
![image](https://github.com/user-attachments/assets/83db9444-c478-4ee5-8ce2-f414ab89a45f)
![image](https://github.com/user-attachments/assets/d47ce755-b643-4c59-9cba-2b5dcd178086)
![image](https://github.com/user-attachments/assets/cf90877c-0ed0-448e-b276-a5d0c6fba418)
![image](https://github.com/user-attachments/assets/31094eda-6128-41c9-86f3-002afbe330fd)
![image](https://github.com/user-attachments/assets/1bbb850b-cd90-4729-81d3-819d7ec0fa63)
![image](https://github.com/user-attachments/assets/f07f26d5-3e80-4e38-b3fc-72d68ec8d6d8)
![image](https://github.com/user-attachments/assets/1941c45a-dfdc-4885-830a-ce2505c78802)




## Diagrama de Arquitectura

### Secuencia
```mermaid
sequenceDiagram
    participant Cliente
    participant Servidor
    Cliente->>Servidor: GET /
    Servidor-->>Cliente: 200 OK (HTML de página principal)
    Cliente->>Servidor: GET /data
    Servidor-->>Cliente: 200 OK (JSON con mensaje)
    Cliente->>Servidor: GET /styles.css
    Servidor-->>Cliente: 200 OK (Contenido CSS)
    Cliente->>Servidor: GET /image.jpeg
    Servidor-->>Cliente: 200 OK (Imagen)
    Cliente->>Servidor: GET /pi
    Servidor-->>Cliente: 200 OK ("3.141592653589793")
    Cliente->>Servidor: GET /e
    Servidor-->>Cliente: 200 OK ("2.718281828459045")
    Cliente->>Servidor: GET /sum?a=5&b=10
    Servidor-->>Cliente: 200 OK ({"a":5, "b":10, "sum":15})
    Cliente->>Servidor: GET /no-existe
    Servidor-->>Cliente: 404 Not Found

```

## Componentes

```mermaid
graph TD
    A[Cliente] -->|HTTP| B[Servidor Web]
    B -->|Sirve archivos estáticos| C[Carpeta Webroot]
    B -->|Procesa solicitudes REST| D[HttpServer]
    D -->|Responde JSON| E[API REST /data, /sum, /pi, /e]
    B -->|Manejo de parámetros en URL| G[QueryString Handler]
    B -->|Devuelve error 404| F[Manejador de errores]
    G -->|Extrae valores| H[HttpRequest]
    D -->|Devuelve respuestas| I[HttpResponse]

```
