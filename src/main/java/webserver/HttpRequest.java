package webserver;

import util.HttpRequestUtils.Pair;

import java.io.BufferedReader;
import java.util.List;

public class HttpRequest {
    private String method;
    private String url;
    private String protocol;
    private List<Pair> requestHeaders;
    private String requestBody;

    public HttpRequest(BufferedReader in) {
        // construct an HttpRequest object using the input stream of a connection socket.
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
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
}
