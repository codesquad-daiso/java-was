package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class RequestHandler extends Thread {
    public static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream is = connection.getInputStream(); OutputStream os = connection.getOutputStream()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = handleRequest(request);
            response.publish(new DataOutputStream(os));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        if (request.getMethod().equals("GET")) {
            return handleGetRequest(request);
        }
        return null; // TODO: other cases
    }

    private HttpResponse handleGetRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        response.setProtocol(request.getProtocol());
        Path urlPath = new File("webapp/", request.getUrl()).toPath();
        try {
            byte[] responseBody = Files.readAllBytes(urlPath); // might throw NoSuchFileException
            response.setStatusCode("200");
            response.setReasonPhrase("OK");
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.addHeader("Content-Length", String.valueOf(responseBody.length));
            response.setResponseBody(responseBody);
        } catch (NoSuchFileException e) {
            response.setStatusCode("404");
            response.setReasonPhrase("Not Found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
