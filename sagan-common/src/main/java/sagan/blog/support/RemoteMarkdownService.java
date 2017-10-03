package sagan.blog.support;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

/**
 * A {@link MarkdownService} based on remote rendering service.
 */
public class RemoteMarkdownService implements MarkdownService {

    private final RestTemplate template;
    private String uri;
    private MediaType type;

    public RemoteMarkdownService(RestTemplate template, String uri, MediaType type) {
        this.uri = uri;
        this.type = type;
        this.template = template;
    }

    @Override
    public String renderToHtml(String markdownSource) {
        try {
            return this.template.exchange(RequestEntity.post(new URI(this.uri + "/documents")).contentType(type).body(
                    markdownSource),
                    String.class).getBody();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot render remotely", e);
        }
    }
}
