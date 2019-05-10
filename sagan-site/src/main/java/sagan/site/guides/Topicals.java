package sagan.site.guides;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.site.renderer.GuideContent;
import sagan.site.renderer.SaganRendererClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
public class Topicals implements GuidesRepository<Topical> {

	private static Logger logger = LoggerFactory.getLogger(Topicals.class);

	public static final String CACHE_TOPICALS = "cache.topicals";

	public static final Class<?> CACHE_TOPICALS_TYPE = GuideHeader[].class;

	public static final String CACHE_TOPICAL = "cache.topical";

	public static final Class<?> CACHE_TOPICAL_TYPE = Topical.class;

	private final SaganRendererClient client;

	@Autowired
	public Topicals(SaganRendererClient client) {
		this.client = client;
	}

	@Override
	@Cacheable(value = CACHE_TOPICALS)
	public GuideHeader[] findAll() {
		return Arrays.stream(this.client.fetchTopicalGuides())
				.map(DefaultGuideHeader::new)
				.toArray(DefaultGuideHeader[]::new);
	}

	@Override
	public Optional<GuideHeader> findGuideHeaderByName(String name) {
		DefaultGuideHeader guideHeader = new DefaultGuideHeader(this.client.fetchTopicalGuide(name));
		return Optional.of(guideHeader);
	}

	@Override
	@Cacheable(value = CACHE_TOPICAL)
	public Optional<Topical> findByName(String name) {
		DefaultGuideHeader guideHeader = new DefaultGuideHeader(this.client.fetchTopicalGuide(name));
		GuideContent guideContent = this.client.fetchTopicalGuideContent(name);
		return Optional.of(new Topical(guideHeader, guideContent));
	}

	@CacheEvict(value = CACHE_TOPICALS)
	public void evictListFromCache() {
		logger.info("Tutorials evicted from cache");
	}

	@CacheEvict(value = CACHE_TOPICAL)
	public void evictFromCache(String guide) {
		logger.info("Tutorial evicted from cache: {}", guide);
	}

}
