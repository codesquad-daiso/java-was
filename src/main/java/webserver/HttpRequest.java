package webserver;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpRequest {
    private String method;
    private String url;
    private String protocol;
    private List<Pair> requestHeaders;
    private String requestBody;

    public HttpRequest(BufferedReader in) {
        requestHeaders = new ArrayList<>();
        readRequestLine(in);
        readHeaders(in);
        readBody(in);
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

    private void readRequestLine(BufferedReader in) {
        try {
            String[] requestLine = in.readLine().split(" ");
            this.method = requestLine[0];
            String url = requestLine[1];
            if (url.equals("/")) {
                url = "index.html";
            }
            this.url = url;
            this.protocol = requestLine[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHeaders(BufferedReader in) {
        try {
            String line = "";
            while (!(line = in.readLine()).equals("")) { // \r\n is removed
                requestHeaders.add(HttpRequestUtils.parseHeader(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readBody(BufferedReader in) {
        if (!method.equals("GET")) { // or other HTTP methods that don't have a body
            int contentLength = getContentLength();
            char[] requestBody = new char[contentLength];
            try {
                int numCharsRead = in.read(requestBody, 0, contentLength);
                // TODO: exception handling - numCharsRead must be equal to contentLength
                this.requestBody = Arrays.toString(requestBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getContentLength() {
        for (Pair requestHeader : requestHeaders) {
            if (requestHeader.getKey().equals("Content-Length")) {
                return Integer.parseInt(requestHeader.getValue());
            }
        }
        return -1; // TODO: exception handling
    }
}
