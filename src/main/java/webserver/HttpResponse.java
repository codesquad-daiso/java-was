package webserver;

import util.HttpRequestUtils.Pair;

import java.util.List;

public class HttpResponse {
    private String protocol;
    private String statusCode;
    private String reasonPhrase;
    private List<Pair> responseHeaders;
    private String responseBody;

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

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String publish() {
        // combine parts of a httpResponse object into a properly formatted HTTP response.
        return null;
    }
}
