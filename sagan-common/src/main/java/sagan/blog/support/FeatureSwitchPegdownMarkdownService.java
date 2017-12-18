package sagan.blog.support;

import sagan.RendererProperties;
import sagan.feature.FeatureProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A {@link MarkdownService} that switches implementation according to whether a feature
 * is enabled or not.
 */
@Service
@Qualifier("pegdown")
public class FeatureSwitchPegdownMarkdownService implements MarkdownService {

    private static final Log logger = LogFactory.getLog(BlogService.class);

    private final PegdownMarkdownService pegdown;

    private final RemoteMarkdownService remote;

    private final FeatureProperties features;

    public FeatureSwitchPegdownMarkdownService(PegdownMarkdownService pegdown, RendererProperties remote,
                                               FeatureProperties features, RestTemplate builder) {
        this.pegdown = pegdown;
        this.remote = new RemoteMarkdownService(builder, remote.getUri(), MediaType.valueOf("text/markdown"));
        this.features = features;
    }

    @Override
    public String renderToHtml(String markdownSource) {
        try {
            if (features.isEnabled()) {
                return remote.renderToHtml(markdownSource);
            }
        } catch (Exception e) {
            logger.warn("Cannot render remote: " + e);
        }
        return pegdown.renderToHtml(markdownSource);
    }

}
