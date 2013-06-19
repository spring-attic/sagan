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

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.SpringApplication;
import org.springframework.bootstrap.bind.RelaxedDataBinder;
import org.springframework.bootstrap.config.YamlPropertiesFactoryBean;
import org.springframework.bootstrap.context.annotation.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.site.documentation.DocumentationService;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.util.Assert;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.springframework.site")
public class ApplicationConfiguration {
	
	private static final Log logger = LogFactory.getLog(ApplicationConfiguration.class);
	
	@Autowired
	private DocumentationService documentationService;

	public static void main(String[] args) {
		build().run(args);
	}

	public static SpringApplication build() {
		SpringApplication application = new SpringApplication(ApplicationConfiguration.class);
		application.setDefaultCommandLineArgs("--spring.template.mode=LEGACYHTML5", "--spring.template.cache=false");
		return application;
	}

	@Bean
	public GitHub gitHubTemplate() {
		// TODO parametrize auth token
		return new GitHubTemplate("5a0e089d267693b45926d7f620d85a2eb6a85da6");
	}
	
	@PostConstruct
	public void loadDocumentationProjects() {
		RelaxedDataBinder binder = new RelaxedDataBinder(documentationService);
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(new Resource[] {new ClassPathResource("documentation.yml")});
		Properties properties = factory.getObject();
		logger.info("Binding documentation properties: " + properties);
		properties.remove("projects");
		binder.bind(new MutablePropertyValues(properties));
		Assert.state(!binder.getBindingResult().hasErrors(), "Errors binding documentation" + binder.getBindingResult().getAllErrors());
	}

}
