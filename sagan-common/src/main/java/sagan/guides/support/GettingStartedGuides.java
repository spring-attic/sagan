package sagan.guides.support;

import sagan.guides.GuideMetadata;
import sagan.guides.GettingStartedGuide;
import sagan.projects.support.ProjectMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Repository implementation providing data access services for getting started guides.
 */
@Component
public class GettingStartedGuides extends PrefixDocRepository<GettingStartedGuide> {

    static final String REPO_PREFIX = "gs-";

    public static final String CACHE_NAME = "cache.guides";
    public static final Class<?> CACHE_TYPE = GettingStartedGuide.class;
    public static final String CACHE_TTL = "${cache.guides.timetolive:0}"; // never expires

    @Autowired
    public GettingStartedGuides(GuideOrganization org, ProjectMetadataService projectMetadataService) {
        super(org, projectMetadataService, REPO_PREFIX);
    }
    
    @Override
    protected GettingStartedGuide create(GuideMetadata metadata) {
        return new GettingStartedGuide(metadata);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public GettingStartedGuide find(String guide) {
        return super.find(guide);
    }

    @CacheEvict(value = CACHE_NAME)
    public void evictFromCache(String guide) {
        log.info("Guide evicted from cache: {}", guide);
    }

}
