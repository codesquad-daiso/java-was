package webserver;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> queries;
    private String protocol;
    private List<Pair> requestHeaders;
    private String requestBody;

    public HttpRequest(BufferedReader in) throws IOException {
        readRequestLine(in);
        readHeaders(in);
        readBody(in);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public String getProtocol() {
        return protocol;
    }

    public List<Pair> getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public boolean isLoggedIn() {
        String cookie = getHeaderValueByName("Cookie");
        return cookie != null && cookie.contains("loggedIn=true");
    }

    private void readRequestLine(BufferedReader in) throws IOException {
        parseRequestLine(readValidRequestLine(in));
    }

    private String[] readValidRequestLine(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine != null) {
            requestLine = URLDecoder.decode(requestLine, "UTF-8");
            String[] splitRequestLine = requestLine.split(" ");
            if (splitRequestLine.length == 3) {
                return splitRequestLine;
            }
        }
        throw new BadRequestException("Invalid request line.");
    }

    private void parseRequestLine(String[] requestLine) {
        this.method = requestLine[0];
        parseUrl(requestLine[1]);
        this.protocol = requestLine[2];
    }

    private void parseUrl(String url) {
        if (url.equals("/")) {
            url = "index.html";
        }
        String[] splitUrl = url.split("\\?");
        this.path = splitUrl[0];
        if (splitUrl.length == 2) {
            String queryString = splitUrl[1];
            this.queries = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    private void readHeaders(BufferedReader in) throws IOException {
        requestHeaders = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null && !line.equals("")) {
            requestHeaders.add(HttpRequestUtils.parseHeader(line));
        }
    }

    private String getHeaderValueByName(String headerName) {
        for (Pair requestHeader : requestHeaders) {
            if (requestHeader.getKey().equals(headerName)) {
                return requestHeader.getValue();
            }
        }
        return null;
    }

    private void readBody(BufferedReader in) throws IOException {
        String headerValue = getHeaderValueByName("Content-Length");
        if (headerValue != null) {
            int contentLength = Integer.parseInt(headerValue);
            char[] requestBody = new char[contentLength];
            in.read(requestBody, 0, contentLength);
            this.requestBody = URLDecoder.decode(new String(requestBody), "UTF-8");
        }
    }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
}
