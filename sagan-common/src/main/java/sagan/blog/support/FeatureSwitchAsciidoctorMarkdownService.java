package sagan.blog.support;

import sagan.RendererProperties;
import sagan.feature.FeatureProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * A {@link MarkdownService} that switches implementation according to whether a feature
 * is enabled or not.
 */
@Service
@Qualifier("asciidoctor")
public class FeatureSwitchAsciidoctorMarkdownService implements MarkdownService {

    private static final Log logger = LogFactory.getLog(BlogService.class);

    private final PegdownMarkdownService pegdown;

    private final RemoteMarkdownService remote;

    private final FeatureProperties features;

    public FeatureSwitchAsciidoctorMarkdownService(PegdownMarkdownService pegdown, RendererProperties remote,
                                                   FeatureProperties features, RestTemplateBuilder builder) {
        this.pegdown = pegdown;
        this.remote = new RemoteMarkdownService(builder.build(), remote.getUri(), MediaType.valueOf("text/asciidoc"));
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
