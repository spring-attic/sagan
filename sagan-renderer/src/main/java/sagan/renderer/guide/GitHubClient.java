package sagan.renderer.guide;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHub;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

@Component
public class GitHubClient {

    public static final String API_URL_BASE = "https://api.github.com";

    private final GitHub gitHub;

    @Autowired
    public GitHubClient(GitHub gitHub) {
        this.gitHub = gitHub;
    }

    public byte[] sendRequestForDownload(String path, Object... uriVariables) {
        String url = resolveUrl(path, uriVariables);
        return gitHub.restOperations().getForObject(url, byte[].class);
    }

    private String resolveUrl(String path, Object[] uriVariables) {
        String expandedPath = new UriTemplate(path).expand(uriVariables).toString();
        return API_URL_BASE + expandedPath;
    }

}
