package sagan.guides.support;

import sagan.guides.GuideMetadata;
import sagan.guides.Tutorial;
import sagan.projects.support.ProjectMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
public class Tutorials extends PrefixDocRepository<Tutorial> {

    static final String REPO_PREFIX = "tut-";

    public static final String CACHE_NAME = "cache.tutorials";
    public static final Class<?> CACHE_TYPE = Tutorial.class;
    public static final String CACHE_TTL = "${cache.tutorials.timetolive:0}"; // never expires

    @Autowired
    public Tutorials(GuideOrganization org, ProjectMetadataService projectMetadataService) {
        super(org, projectMetadataService, REPO_PREFIX);
    }
    
    @Override
    protected Tutorial create(GuideMetadata metadata) {
        return new Tutorial(metadata);
    }

    @Override
    @Cacheable(value = CACHE_NAME)
    public Tutorial find(String guide) {
        return super.find(guide);
    }

    @CacheEvict(value = CACHE_NAME)
    public void evictFromCache(String guide) {
        log.info("Tutorial evicted from cache: {}", guide);
    }

}
