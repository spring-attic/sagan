package sagan.support.github;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriTemplate;

public class StubGithubRestClient extends GitHubClient {

    private Map<String, String> responseMap = new HashMap<>();
    private Map<String, byte[]> responseMapBytes = new HashMap<>();

    public StubGithubRestClient() {
        super(null, null);
    }

    public void putResponse(String url, String response) {
        responseMap.put(url, response);
    }

    public void putResponseBytes(String url, byte[] response) {
        responseMapBytes.put(url, response);
    }

    public void clearResponses() {
        responseMap.clear();
    }

    @Override
    public String sendRequestForJson(String path, Object... uriVariables) {
        return handleRequest(path, uriVariables);
    }

    @Override
    public String sendRequestForHtml(String path, Object... uriVariables) {
        return handleRequest(path, uriVariables);
    }

    @Override
    public String sendPostRequestForHtml(String path, String body, Object... uriVariables) {
        return "<h1>HTML</h1>";
    }

    @Override
    public byte[] sendRequestForDownload(String path, Object... uriVariables) {
        String url = new UriTemplate(path).expand(uriVariables).getPath();
        if (!responseMapBytes.containsKey(url)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "no response for: " + url);
        }
        return responseMapBytes.get(url);
    }

    private String handleRequest(String path, Object[] uriVariables) {
        String url = new UriTemplate(path).expand(uriVariables).getPath();
        if (!responseMap.containsKey(url)) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "no response for: " + url);
        }
        return responseMap.get(url);
    }
}
