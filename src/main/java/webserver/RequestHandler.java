package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import webserver.HttpRequest.BadRequestException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class RequestHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private DataOutputStream out;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        try (InputStream is = connection.getInputStream(); OutputStream os = connection.getOutputStream()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            out = new DataOutputStream(os);
            handleRequest(new HttpRequest(in));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void handleRequest(HttpRequest request) throws IOException {
        try {
            String method = request.getMethod();
            String path = request.getPath();
            if (method.equals("GET")) {
                handleGetRequest(request);
            } else if (method.equals("POST")){
                if (path.equals("/user/create")) {
                    handlePostSignup(request);
                }
            } else {
                handleNotImplemented();
            }
        } catch (BadRequestException e) {
            handleBadRequests(e);
        } catch (Exception e) {
            handleInternalServerError();
        }
    }

    private void handleGetRequest(HttpRequest request) throws IOException {
        try {
            Path path = new File("webapp/", request.getPath()).toPath();
            byte[] responseBody = Files.readAllBytes(path);
            handleOK(responseBody);
        } catch (NoSuchFileException e) {
            handleNotFound();
        }
    }

    private void handlePostSignup(HttpRequest request) throws IOException {
        User newUser = createUserWith(getValidatedKeyValuePairsFrom(request));
        DataBase.addUser(newUser);
        handleSeeOther("/");
    }

    private Map<String, String> getValidatedKeyValuePairsFrom(HttpRequest request) {
        Map<String, String> keyValuePairs = HttpRequestUtils.parseQueryString(request.getRequestBody());
        if (keyValuePairs == null) {
            throw new BadRequestException("No values for signup.");
        } else if (keyValuePairs.get("userId") == null) {
            throw new BadRequestException("User ID must not be blank.");
        }
        return keyValuePairs;
    }

    private User createUserWith(Map<String, String> queries) {
        String userId = queries.get("userId");
        String password = Optional.ofNullable(queries.get("password")).orElse("");
        String name = Optional.ofNullable(queries.get("name")).orElse("");
        String email = Optional.ofNullable(queries.get("email")).orElse("");
        return new User(userId, password, name, email);
    }

    private void handleOK(byte[] responseBody) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseLine("HTTP/1.1 200 OK");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.length));
        response.setResponseBody(responseBody);
        response.publishTo(out);
    }

    private void handleSeeOther(String location) throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseLine("HTTP/1.1 303 See Other");
        response.addHeader("Location", location);
        response.publishTo(out);
    }

    private void handleBadRequests(BadRequestException e) throws IOException {
        HttpResponse response = new HttpResponse();
        byte[] responseBody = e.getMessage().getBytes();
        response.setResponseLine("HTTP/1.1 400 Bad Request");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.length));
        response.setResponseBody(responseBody);
        response.publishTo(out);
    }

    private void handleNotFound() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseLine("HTTP/1.1 404 Not Found");
        response.publishTo(out);
    }

    private void handleInternalServerError() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseLine("HTTP/1.1 500 Internal Server Error");
        response.publishTo(out);
    }

    private void handleNotImplemented() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setResponseLine("HTTP/1.1 501 Not Implemented");
        response.publishTo(out);
    }
}
