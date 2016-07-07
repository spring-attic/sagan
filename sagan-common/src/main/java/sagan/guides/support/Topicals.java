package sagan.guides.support;

import sagan.guides.GuideMetadata;
import sagan.guides.Topical;
import sagan.projects.support.ProjectMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
public class Topicals extends PrefixDocRepository<Topical> {

    static final String REPO_PREFIX = "top-";

    public static final String CACHE_NAME = "cache.topicals";
    public static final Class<?> CACHE_TYPE = Topical.class;
    public static final String CACHE_TTL = "${cache.topicals.timetolive:0}"; // never expires

    @Autowired
    public Topicals(GuideOrganization org, ProjectMetadataService projectMetadataService) {
        super(org, projectMetadataService, REPO_PREFIX);
    }
    
    @Override
    protected Topical create(GuideMetadata metadata) {
        return new Topical(metadata);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public Topical find(String guide) {
        return super.find(guide);
    }

    @CacheEvict(value = CACHE_NAME)
    public void evictFromCache(String guide) {
        log.info("Tutorial evicted from cache: {}", guide);
    }

}
