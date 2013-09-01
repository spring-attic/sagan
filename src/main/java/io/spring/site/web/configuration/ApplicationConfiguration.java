/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.site.web.configuration;

import com.google.common.cache.CacheBuilder;
import io.spring.site.domain.projects.ProjectMetadataService;
import io.spring.site.domain.projects.ProjectMetadataYamlParser;
import io.spring.site.domain.services.DateService;
import io.spring.site.web.SiteUrl;
import io.spring.site.web.blog.feed.BlogPostAtomViewer;
import liquibase.integration.spring.SpringLiquibase;
import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.postgresql.ds.PGSimpleDataSource;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration
@Configuration
@ComponentScan({ "io.spring.site.web", "io.spring.site.domain", "io.spring.site.search" })
@EnableCaching
public class ApplicationConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

    @Configuration
    @Profile({"development", "staging", "production"})
    protected static class CloudFoundryDataSourceConfiguration {
        @Bean
        public DataSource dataSource() {
            CloudEnvironment cloudEnvironment = new CloudEnvironment();
            RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo("sagan-db",
                    RdbmsServiceInfo.class);
            RdbmsServiceCreator serviceCreator = new RdbmsServiceCreator();
            return serviceCreator.createService(serviceInfo);
        }
    }

    @Configuration
    @Profile({"local_postgres"})
    protected static class PostgresConfiguration {
        @Bean
        public DataSource dataSource() {
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setPortNumber(5432);
            dataSource.setDatabaseName("blog_import");
            dataSource.setServerName("localhost");
            return dataSource;
        }
    }

    @Bean
    public BlogPostAtomViewer blogPostAtomViewer(SiteUrl siteUrl, DateService dateService) {
        return new BlogPostAtomViewer(siteUrl, dateService);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Serializer simpleXmlSerializer() {
        return new Persister();
    }

    @Bean
    public ProjectMetadataService projectMetadataService() throws IOException {
        InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass()).getInputStream();
        return new ProjectMetadataYamlParser().createServiceFromYaml(yaml);
    }

    // http://urlrewritefilter.googlecode.com/svn/trunk/src/doc/manual/4.0/index.html#filterparams
    // Blog filter must be declared first, to ensure its rules are applied before the general rules
    @Bean
    public FilterRegistrationBean blogRewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName("mappingsRewriteFilter");
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", "urlrewrite/urlrewrite-generated.xml");
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    @Bean
    public FilterRegistrationBean rewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName("rewriteFilter");
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", "urlrewrite/urlrewrite.xml");
        reg.addInitParameter("confReloadCheckInterval", "-1");
        reg.addInitParameter("logLevel", "WARN");
        return reg;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Bean
    public SpringLiquibase springLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db-changeset.yaml");
        return liquibase;
    }

    @Bean
    public CacheManager cacheManager(@Value("${cache.timetolive:300}") Long cacheTimeToLive) {
        ConcurrentMap<Object,Object> cacheMap = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTimeToLive, TimeUnit.SECONDS)
                .build().asMap();

        ConcurrentMapCache concurrentMapCache = new ConcurrentMapCache("cache", cacheMap, false);
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(concurrentMapCache));
        return cacheManager;
    }

}
