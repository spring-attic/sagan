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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
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
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.client.TransportClientFactoryBean;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.site.blog.feed.BlogPostAtomViewer;
import org.springframework.site.documentation.DocumentationService;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.security.GithubAuthenticationSigninAdapter;
import org.springframework.site.security.RemoteUsernameConnectionSignUp;
import org.springframework.site.security.SecurityContextAuthenticationFilter;
import org.springframework.site.services.DateService;
import org.springframework.site.services.SiteUrl;
import org.springframework.site.team.SignInService;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.Filter;
import java.util.Collections;
import java.util.Properties;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "org.springframework.site")
@EnableScheduling
public class ApplicationConfiguration {

	private static final Log logger = LogFactory
			.getLog(ApplicationConfiguration.class);

	static final String SIGNIN_SUCCESS_PATH = "/signin/success";

	@Autowired
	private DocumentationService documentationService;

	@Value("${GITHUB_CLIENT_ID:${github.client.id:none}}")
	private String githubClientId;

	@Value("${GITHUB_CLIENT_SECRET:${github.client.secret:none}}")
	private String githubClientSecret;

	@Autowired
	private Client client;

	@Value("${GITHUB_ACCESS_TOKEN:${github.access.token:5a0e089d267693b45926d7f620d85a2eb6a85da6}}")
	private String accessToken;

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

	@Bean
	public TaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(10);
		return scheduler;
	}

	@Bean
	public GitHubConnectionFactory gitHubConnectionFactory() {
		GitHubConnectionFactory factory = new GitHubConnectionFactory(githubClientId,
				githubClientSecret);
		factory.setScope("user");
		return factory;
	}

	@Bean
	public GitHub gitHubTemplate() {
		return new GitHubTemplate(accessToken);
	}

	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws Exception {
		return new ElasticsearchTemplate(client);
	}

	@Configuration
	@Profile({"staging", "production"})
	protected static class ElasticSearchExternalConfiguration {

		@Value("${elasticsearch.cluster.nodes:localhost:9300}")
		private String clusterNodes = "localhost:9300";

		@Value("${elasticsearch.cluster.name:elasticsearch_pivotal}")
		private String clusterName = "elasticsearch_pivotal";

		@Bean
		public Client elasticSearchClient() throws Exception {
			TransportClientFactoryBean transportClient = new TransportClientFactoryBean();
			transportClient.setClusterNodes(clusterNodes);
			transportClient.setClusterName(clusterName);
			transportClient.setClientTransportSniff(true);
			transportClient.afterPropertiesSet();
			return transportClient.getObject();
		}
	}

	@Configuration
	@Profile({"default", "development"})
	protected static class ElasticSearchLocalConfiguration {

		@PostConstruct
		public void deleteSearchIndex() throws Exception {
			ElasticsearchTemplate elasticsearchTemplate = new ElasticsearchTemplate(elasticSearchClient());
			elasticsearchTemplate.deleteIndex(SearchEntry.class);
			elasticsearchTemplate.createIndex(SearchEntry.class);
			elasticsearchTemplate.putMapping(SearchEntry.class);
			elasticsearchTemplate.refresh(SearchEntry.class, false);
		}

		@PreDestroy
		public void closeClient() throws Exception {
			elasticSearchClient().close();
		}

		@Bean
		public Client elasticSearchClient() throws Exception {
			return nodeBuilder().local(true).node().client();
		}
	}

	@Configuration
	@Order(Integer.MAX_VALUE - 1)
	protected static class SigninAuthenticationConfiguration extends
			WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/signin/**")
					.addFilterBefore(authenticationFilter(),
							AnonymousAuthenticationFilter.class)
					.authorizeUrls().anyRequest().anonymous();
		}

		// Not a @Bean because we explicitly do not want it added automatically by Bootstrap to all requests
		protected Filter authenticationFilter() {
			AbstractAuthenticationProcessingFilter filter = new SecurityContextAuthenticationFilter(
					SIGNIN_SUCCESS_PATH);
			SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
			successHandler.setDefaultTargetUrl("/admin/blog");
			filter.setAuthenticationSuccessHandler(successHandler);
			return filter;
		}
	}

	@Configuration
	@Order(Integer.MAX_VALUE)
	protected static class AdminAuthenticationConfiguration extends
			WebSecurityConfigurerAdapter {

		@Autowired
		private SignInService signInService;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.exceptionHandling().authenticationEntryPoint(
					authenticationEntryPoint());
			http.logout().logoutUrl("/signout").logoutSuccessUrl("/signin?signout=success");
			http.authorizeUrls().antMatchers("/admin/**", "/signout").authenticated();
		}

		private AuthenticationEntryPoint authenticationEntryPoint() {
			// TODO: this causes an interstitial page to pop up in UI. Can we or
			// should we POST back here (e.g. with forward)?
			LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/signin");
			return entryPoint;
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
					new GithubAuthenticationSigninAdapter(SIGNIN_SUCCESS_PATH, signInService));
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
		factory.setResources(new Resource[]{new ClassPathResource(path)});
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

	@Bean
	public BlogPostAtomViewer blogPostAtomViewer(SiteUrl siteUrl, DateService dateService){
		return new BlogPostAtomViewer(siteUrl, dateService);
	}

	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}
}
