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
package org.springframework.site.configuration;

import org.cloudfoundry.runtime.env.CloudEnvironment;
import org.cloudfoundry.runtime.env.RdbmsServiceInfo;
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator;
import org.springframework.bootstrap.SpringApplication;
import org.springframework.bootstrap.context.annotation.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.site.blog.feed.BlogPostAtomViewer;
import org.springframework.site.services.DateService;
import org.springframework.site.services.SiteUrl;

import javax.sql.DataSource;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = {"org.springframework.site", "org.springframework.search"})
@EnableScheduling
public class ApplicationConfiguration {

	public static void main(String[] args) {
		build(ApplicationConfiguration.class).run(args);
	}

	public static SpringApplication build(Class<?>... config) {
		SpringApplication application = new SpringApplication(config);
		application.setDefaultCommandLineArgs(
				"--spring.template.mode=LEGACYHTML5",
				"--spring.template.cache=false");
		return application;
	}

	@Configuration
	@Profile({"development", "staging", "production"})
	protected static class CloudFoundryDataSourceConfiguration {
		@Bean
		public DataSource dataSource() {
			CloudEnvironment cloudEnvironment = new CloudEnvironment();
			RdbmsServiceInfo serviceInfo = cloudEnvironment.getServiceInfo("sagan-db", RdbmsServiceInfo.class);
			RdbmsServiceCreator serviceCreator = new RdbmsServiceCreator();
			return serviceCreator.createService(serviceInfo);
		}
	}

	@Bean
	public BlogPostAtomViewer blogPostAtomViewer(SiteUrl siteUrl, DateService dateService){
		return new BlogPostAtomViewer(siteUrl, dateService);
	}

}
