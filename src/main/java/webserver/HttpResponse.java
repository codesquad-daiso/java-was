package webserver;

import util.HttpRequestUtils.Pair;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HttpResponse {
    private String responseLine;
    private List<Pair> responseHeaders;
    private byte[] responseBody;

    public HttpResponse() {
        responseHeaders = new ArrayList<>();
    }

    public void setResponseLine(String responseLine) {
        this.responseLine = responseLine;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public void addHeader(String name, String value) {
        Pair header = new Pair(name, value);
        responseHeaders.add(header);
    }

    public void publishTo(DataOutputStream out) throws IOException {
        writeResponseLine(out);
        writeHeaders(out);
        writeBody(out);
        out.flush();
    }

    private void writeResponseLine(DataOutputStream out) throws IOException {
        out.writeBytes(responseLine + "\r\n");
    }

    private void writeHeaders(DataOutputStream out) throws IOException {
        for (Pair header : responseHeaders) {
            StringJoiner headerJoiner = new StringJoiner(": ", "", "\r\n");
            headerJoiner.add(header.getKey());
            headerJoiner.add(header.getValue());
            out.writeBytes(headerJoiner.toString());
        }
        out.writeBytes("\r\n");
    }

    private void writeBody(DataOutputStream out) throws IOException {
        if (responseBody != null) {
            out.write(responseBody);
        }
    }
}
