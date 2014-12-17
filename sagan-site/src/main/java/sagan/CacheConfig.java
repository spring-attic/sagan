package sagan;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import sagan.blog.support.BlogService;
import sagan.guides.support.GettingStartedGuides;
import sagan.guides.support.Tutorials;
import sagan.guides.support.UnderstandingDocs;
import sagan.support.cache.CachedRestClient;
import sagan.support.cache.JsonRedisTemplate;
import sagan.support.cache.RedisCacheManager;
import sagan.team.support.TeamService;

import java.util.Arrays;
import java.util.stream.Collectors;


@Configuration
@EnableCaching(proxyTargetClass = true)
@Profile(SaganProfiles.STANDALONE)
class StandaloneCacheConfig {

    @Bean
    public CacheManager simpleCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(
                Arrays.asList(CachedRestClient.CACHE_NAME, BlogService.CACHE_NAME, TeamService.CACHE_NAME,
                        GettingStartedGuides.CACHE_NAME, UnderstandingDocs.CACHE_NAME, Tutorials.CACHE_NAME).stream()
                        .map(name -> new ConcurrentMapCache(name))
                        .collect(Collectors.toList()));
        return cacheManager;
    }

}

@Configuration
@EnableCaching(proxyTargetClass = true)
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudFoundryCacheConfig extends AbstractCloudConfig {

    @Value(CachedRestClient.CACHE_TTL)
    protected Long cacheNetworkTimeToLive;

    @Value(BlogService.CACHE_TTL)
    protected Long cacheBlogTimeToLive;

    @Value(TeamService.CACHE_TTL)
    protected Long cacheTeamTimeToLive;

    @Value(GettingStartedGuides.CACHE_TTL)
    protected Long cacheGuideTimeToLive;

    @Value(UnderstandingDocs.CACHE_TTL)
    protected Long cacheUnderstandingTimeToLive;

    @Value(Tutorials.CACHE_TTL)
    protected Long cacheTutorialTimeToLive;


    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                          ObjectMapper objectMapper) {

        RedisCacheManager cacheManager = new RedisCacheManager(redisConnectionFactory);

        // Use the default redisTemplate for caching REST calls
        cacheManager.withCache(CachedRestClient.CACHE_NAME, this.cacheNetworkTimeToLive);

        // Use

        JsonRedisTemplate blogTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper, BlogService.CACHE_TYPE);
        cacheManager.withCache(BlogService.CACHE_NAME, blogTemplate, this.cacheBlogTimeToLive);

        JsonRedisTemplate teamTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper, TeamService.CACHE_TYPE);
        cacheManager.withCache(TeamService.CACHE_NAME, teamTemplate, this.cacheTeamTimeToLive);

        JsonRedisTemplate guidesTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper, GettingStartedGuides.CACHE_TYPE);
        cacheManager.withCache(GettingStartedGuides.CACHE_NAME, guidesTemplate, this.cacheGuideTimeToLive);

        JsonRedisTemplate understandingTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper, UnderstandingDocs.CACHE_TYPE);
        cacheManager.withCache(UnderstandingDocs.CACHE_NAME, understandingTemplate, this.cacheUnderstandingTimeToLive);

        JsonRedisTemplate tutorialTemplate = new JsonRedisTemplate<>(redisConnectionFactory, objectMapper, Tutorials.CACHE_TYPE);
        cacheManager.withCache(Tutorials.CACHE_NAME, tutorialTemplate, this.cacheUnderstandingTimeToLive);

        return cacheManager;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return connectionFactory().redisConnectionFactory();
    }

}
