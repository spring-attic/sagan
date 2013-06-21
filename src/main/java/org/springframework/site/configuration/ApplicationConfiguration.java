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

import java.util.Collections;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.bootstrap.SpringApplication;
import org.springframework.bootstrap.bind.RelaxedDataBinder;
import org.springframework.bootstrap.config.YamlPropertiesFactoryBean;
import org.springframework.bootstrap.context.annotation.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.HttpConfiguration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.site.documentation.DocumentationService;
import org.springframework.site.security.GithubAuthenticationSigninAdapter;
import org.springframework.site.security.RemoteUsernameConnectionSignUp;
import org.springframework.site.security.SecurityContextAuthenticationFilter;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.util.Assert;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.springframework.site")
public class ApplicationConfiguration {

	private static final Log logger = LogFactory
			.getLog(ApplicationConfiguration.class);

	static final String SIGNIN_SUCCESS_PATH = "/signin/success";

	@Autowired
	private DocumentationService documentationService;

	@Value("${github.client.id:none}")
	private String githubClientId;

	@Value("${github.client.secret:none}")
	private String githubClientSecret;

	public static void main(String[] args) {
		build().run(args);
	}

	public static SpringApplication build() {
		SpringApplication application = new SpringApplication(
				ApplicationConfiguration.class);
		application.setDefaultCommandLineArgs(
				"--spring.template.mode=LEGACYHTML5",
				"--spring.template.cache=false");
		return application;
	}

	@Bean
	public GitHubConnectionFactory gitHubConnectionFactory() {
		return new GitHubConnectionFactory(githubClientId,
				githubClientSecret);
	}

	@Bean
	public GitHub gitHubTemplate() {
		// TODO parameterize auth token
		return new GitHubTemplate("5a0e089d267693b45926d7f620d85a2eb6a85da6");
	}

	@Configuration
	@Order(Integer.MAX_VALUE - 1)
	protected static class SigninAuthenticationConfiguration extends
			WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpConfiguration http) throws Exception {
			http.antMatcher("/signin/**")
					.addFilterBefore(authenticationFilter(),
							AnonymousAuthenticationFilter.class)
					.authorizeUrls().anyRequest().anonymous();
		}

		// Not a @Bean because we explicitly do not want it added automatically by Bootstrap to all requests
		protected Filter authenticationFilter() {
			AbstractAuthenticationProcessingFilter filter = new SecurityContextAuthenticationFilter(
					SIGNIN_SUCCESS_PATH);
			return filter;
		}
	}

	@Configuration
	@Order(Integer.MAX_VALUE)
	protected static class AdminAuthenticationConfiguration extends
			WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpConfiguration http) throws Exception {
			http.exceptionHandling().authenticationEntryPoint(
					authenticationEntryPoint());
			http.logout().logoutUrl("/logout").logoutSuccessUrl("/signin?logout=success");
			http.requestMatchers().antMatchers("/admin/**", "/logout").authorizeUrls().anyRequest()
					.authenticated();
		}

		private AuthenticationEntryPoint authenticationEntryPoint() {
			// TODO: this causes an interstitial page to pop up in UI. Can we or
			// should we POST back here (e.g. with forward)?
			return new LoginUrlAuthenticationEntryPoint("/signin");
		}

		@Bean
		public ProviderSignInController providerSignInController(
				GitHubConnectionFactory connectionFactory) {
			ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
			registry.addConnectionFactory(connectionFactory);
			// TODO: do we need this at all?
			InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(
					registry);
			repository.setConnectionSignUp(new RemoteUsernameConnectionSignUp());
			ProviderSignInController controller = new ProviderSignInController(registry, repository,
					new GithubAuthenticationSigninAdapter(SIGNIN_SUCCESS_PATH));
			controller.setSignInUrl("/signin?error=access_denied");
			return controller;
		}

	}

	@PostConstruct
	public void loadDocumentationProjects() {
		bind("documentation.yml", documentationService);
	}

	public static void bind(String path,
			DocumentationService documentationService) {
		RelaxedDataBinder binder = new RelaxedDataBinder(documentationService);
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(new Resource[] { new ClassPathResource(path) });
		Properties properties = factory.getObject();
		logger.info("Binding properties: " + properties);
		properties.remove("projects");
		for (Object key : Collections.list(properties.propertyNames())) {
			if (key.toString().endsWith(("ersions"))) {
				properties.remove(key);
			}
		}
		binder.bind(new MutablePropertyValues(properties));
		Assert.state(!binder.getBindingResult().hasErrors(), "Errors binding "
				+ path + ": " + binder.getBindingResult().getAllErrors());
	}

}
