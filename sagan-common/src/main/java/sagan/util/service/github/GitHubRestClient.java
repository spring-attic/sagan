package sagan.util.service.github;

import sagan.util.service.CachedRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

@Service
public class GitHubRestClient {
    private final GitHub gitHub;
    private final CachedRestClient cachedRestClient;

    @Autowired
    public GitHubRestClient(GitHub gitHub, CachedRestClient cachedRestClient) {
        this.gitHub = gitHub;
        this.cachedRestClient = cachedRestClient;
    }

    public String sendRequestForJson(String path, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        return cachedRestClient.get(gitHub.restOperations(), url, String.class);
    }

    public String sendRequestForHtml(String path, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        MarkdownHtml markdownHtml = cachedRestClient.get(gitHub.restOperations(), url, MarkdownHtml.class);
        return markdownHtml.toString();
    }

    public String sendPostRequestForHtml(String path, String body, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        return cachedRestClient.post(gitHub.restOperations(), url, String.class, body);
    }

    private String resolveUrl(String path, Object[] uriVariables) {
        String expandedPath = new UriTemplate(path).expand(uriVariables).toString();
        return GitHubService.API_URL_BASE + expandedPath;
    }

}