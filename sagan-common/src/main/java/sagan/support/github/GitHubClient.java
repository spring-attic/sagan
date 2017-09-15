package sagan.support.github;

import sagan.support.cache.CachedRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class GitHubClient {

    public static final String API_URL_BASE = "https://api.github.com";

    private final GitHub gitHub;
    private final CachedRestClient restClient;

    @Autowired
    public GitHubClient(GitHub gitHub, CachedRestClient restClient) {
        this.gitHub = gitHub;
        this.restClient = restClient;
    }

    public String sendRequestForJson(String path, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        return restClient.get(gitHub.restOperations(), url, String.class);
    }

    public byte[] sendRequestForDownload(String path, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        return restClient.get(gitHub.restOperations(), url, byte[].class);
    }

    public String sendRequestForHtml(String path, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        MarkdownHtml markdownHtml = restClient.get(gitHub.restOperations(), url, MarkdownHtml.class);
        return markdownHtml.toString();
    }

    public String sendPostRequestForHtml(String path, String body, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        return restClient.post(gitHub.restOperations(), url, String.class, body);
    }

    private String resolveUrl(String path, Object[] uriVariables) {
        String expandedPath = new UriTemplate(path).expand(uriVariables).toString();
        return API_URL_BASE + expandedPath;
    }

}
