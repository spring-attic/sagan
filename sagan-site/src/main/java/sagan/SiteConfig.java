package sagan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import sagan.guides.support.GettingStartedGuides;
import sagan.support.cache.CachedRestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.h2.server.web.WebServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;

import com.google.common.cache.CacheBuilder;

/**
 * Main configuration resource for the Sagan web application. The use of @ComponentScan
 * here ensures that other @Configuration classes such as {@link MvcConfig} and
 * {@link SecurityConfig} are included as well.
 *
 * @see SiteMain#main(String[])
 */
@EnableAutoConfiguration(exclude=SocialWebAutoConfiguration.class)
@Configuration
@EnableCaching(proxyTargetClass = true)
@ComponentScan
@EntityScan
@EnableJpaRepositories
public class SiteConfig {

    public static final String REWRITE_FILTER_NAME = "rewriteFilter";
    public static final String REWRITE_FILTER_CONF_PATH = "urlrewrite.xml";

    @Bean
    public HealthIndicator healthIndicator(DataSource dataSource) {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource tcDataSource =
                    (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
            return new AbstractHealthIndicator() {
                @Override
                protected void doHealthCheck(Builder healthBuilder) throws Exception {
                    healthBuilder.up().withDetail("active", tcDataSource.getActive())
                            .withDetail("max_active", tcDataSource.getMaxActive())
                            .withDetail("idle", tcDataSource.getIdle())
                            .withDetail("max_idle", tcDataSource.getMaxIdle())
                            .withDetail("min_idle", tcDataSource.getMinIdle())
                            .withDetail("wait_count", tcDataSource.getWaitCount())
                            .withDetail("max_wait", tcDataSource.getMaxWait());
                }
            };
        }
        return null;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    public FilterRegistrationBean rewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName(REWRITE_FILTER_NAME);
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", REWRITE_FILTER_CONF_PATH);
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("statusPath", "/redirect");
        reg.addInitParameter("statusEnabledOnHosts", "*");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    @Bean
    @Profile(SaganProfiles.STANDALONE)
    public ServletRegistrationBean h2Console() {
        ServletRegistrationBean reg = new ServletRegistrationBean(new WebServlet(), "/console/*");
        reg.setLoadOnStartup(1);
        return reg;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public CacheManager cacheManager(@Value(CachedRestClient.CACHE_TTL) Long cacheNetworkTimeToLive,
                                     @Value(DatabaseConfig.CACHE_TTL) Long cacheDatabaseTimeToLive,
                                     @Value(GettingStartedGuides.CACHE_TTL) Long cacheGuideTimeToLive) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<ConcurrentMapCache> cacheList = new ArrayList<>();
        cacheList.add(createConcurrentMapCache(cacheNetworkTimeToLive, CachedRestClient.CACHE_NAME, -1));
        cacheList.add(createConcurrentMapCache(cacheDatabaseTimeToLive, DatabaseConfig.CACHE_NAME, 50));
        cacheList.add(createConcurrentMapCache(cacheGuideTimeToLive, GettingStartedGuides.CACHE_NAME, 100));
        cacheManager.setCaches(cacheList);
        return cacheManager;
    }

    private ConcurrentMapCache createConcurrentMapCache(Long timeToLive, String name, long cacheSize) {
        CacheBuilder<Object, Object> cacheBuilder =
                CacheBuilder.newBuilder().expireAfterWrite(timeToLive, TimeUnit.SECONDS);

        if (cacheSize >= 0) {
            cacheBuilder.maximumSize(cacheSize);
        }
        ConcurrentMap<Object, Object> map = cacheBuilder.build().asMap();
        return new ConcurrentMapCache(name, map, false);
    }
}
