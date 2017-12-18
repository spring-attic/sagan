package sagan.guides.support;

import sagan.RendererProperties;
import sagan.feature.FeatureProperties;
import sagan.guides.DocumentContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * A {@link GuideRenderer} that switches implementation according to whether a feature is
 * enabled or not.
 */
@Service
@Primary
public class FeatureSwitchGuideRenderer implements GuideRenderer {

    private static final Log logger = LogFactory.getLog(GuideRenderer.class);

    private final AsciidoctorGuideRenderer local;

    private final RemoteGuideRenderer remote;

    private final FeatureProperties features;

    public FeatureSwitchGuideRenderer(AsciidoctorGuideRenderer local, RendererProperties remote,
                                      FeatureProperties features, RestTemplate builder) {
        this.local = local;
        this.remote = new RemoteGuideRenderer(builder, remote.getUri());
        this.features = features;
    }

    @Override
    public DocumentContent render(String path) {
        try {
            if (features.isEnabled()) {
                return remote.render(path);
            }
        } catch (Exception e) {
            logger.warn("Cannot render remote: " + e);
        }
        return local.render(path);
    }

}
