package sagan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.searchbox.client.JestClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import sagan.search.support.SearchService;
import sagan.support.health.ElasticsearchHealthIndicator;

import javax.sql.DataSource;

/**
 * The entry point for the Sagan web application.
 * <p>
 * Main configuration resource for the Sagan web application. The use of @ComponentScan
 * here ensures that other @Configuration classes such as {@link MvcConfig} and
 * {@link SecurityConfig} are included as well.
 */
@SpringBootApplication(exclude = SocialWebAutoConfiguration.class)
@EntityScan
@EnableJpaRepositories
public class SiteApplication {

    public static final String REWRITE_FILTER_NAME = "rewriteFilter";
    public static final String REWRITE_FILTER_CONF_PATH = "urlrewrite.xml";


    public static void main(String[] args) {
        new SaganApplication(SiteApplication.class).run(args);
    }


    @Bean
    public HealthIndicator dataSourceHealth(DataSource dataSource) {
        if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource tcDataSource =
                    (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
            return new AbstractHealthIndicator() {
                @Override
                protected void doHealthCheck(Health.Builder healthBuilder) throws Exception {
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
    public ElasticsearchHealthIndicator elasticsearch(JestClient jestClient, SearchService searchService) {
        return new ElasticsearchHealthIndicator(jestClient, searchService.getIndexName());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));
    }

    @Bean
    @Primary
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

}
