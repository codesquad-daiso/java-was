package webserver;

import util.HttpRequestUtils.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {
    private String protocol;
    private String statusCode;
    private String reasonPhrase;
    private List<Pair> responseHeaders;
    private byte[] responseBody;

    public HttpResponse() {
        responseHeaders = new ArrayList<>();
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public void setResponseHeaders(List<Pair> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public void addHeader(String name, String value) {
        Pair header = new Pair(name, value);
        responseHeaders.add(header);
    }

    public void publish(DataOutputStream out) throws IOException {
        writeResponseLine(out);
        writeHeaders(out);
        if (responseBody != null) {
            out.write(responseBody);
        }
        out.flush();
    }

    private void writeResponseLine(DataOutputStream out) throws IOException {
        StringBuilder responseLineBuilder = new StringBuilder();
        responseLineBuilder.append(protocol);
        responseLineBuilder.append(" ");
        responseLineBuilder.append(statusCode);
        responseLineBuilder.append(" ");
        responseLineBuilder.append(reasonPhrase);
        responseLineBuilder.append("\r\n");
        out.writeBytes(responseLineBuilder.toString());
    }

    private void writeHeaders(DataOutputStream out) throws IOException {
        for (Pair header : responseHeaders) {
            StringBuilder headerBuilder = new StringBuilder();
            headerBuilder.append(header.getKey());
            headerBuilder.append(": ");
            headerBuilder.append(header.getValue());
            headerBuilder.append("\r\n");
            out.writeBytes(headerBuilder.toString());
        }
        out.writeBytes("\r\n");
    }
}
