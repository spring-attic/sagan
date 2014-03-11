package sagan.blog.support;

import sagan.util.service.github.GitHubClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class DefaultMarkdownService implements MarkdownService {

    private final GitHubClient gitHub;

    @Autowired
    public DefaultMarkdownService(GitHubClient gitHub) {
        this.gitHub = gitHub;
    }

    /**
     * Process the given markdown through GitHub's Markdown Rendering API. See
     * http://developer.github.com/v3/markdown
     */
    @Override
    public String renderToHtml(String markdownSource) {
        return gitHub.sendPostRequestForHtml("/markdown/raw", markdownSource);
    }

}
