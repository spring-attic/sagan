package org.springframework.site.web.configuration;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.site.domain.team.SignInService;
import org.springframework.site.web.security.GithubAuthenticationSigninAdapter;
import org.springframework.site.web.security.RemoteUsernameConnectionSignUp;
import org.springframework.site.web.security.SecurityContextAuthenticationFilter;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.connect.GitHubConnectionFactory;

@Configuration
@ComponentScan(basePackages = { "org.springframework.site.domain.team",
		"org.springframework.site.web.security",
		"org.springframework.site.domain.services",
		"org.springframework.site.domain.blog" })
public class SecurityConfiguration {

	static final String SIGNIN_SUCCESS_PATH = "/signin/success";

	@Configuration
	@Order(Ordered.LOWEST_PRECEDENCE - 100)
	protected static class SigninAuthenticationConfiguration extends
			WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/signin/**")
					.addFilterBefore(authenticationFilter(),
							AnonymousAuthenticationFilter.class).anonymous();
		}

		// Not a @Bean because we explicitly do not want it added automatically by
		// Bootstrap to all requests
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
	@Order(Ordered.LOWEST_PRECEDENCE - 90)
	protected static class AdminAuthenticationConfiguration extends
			WebSecurityConfigurerAdapter {

		@Autowired
		private SignInService signInService;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
			http.requestMatchers().antMatchers("/admin/**", "/signout");
			http.logout().logoutUrl("/signout")
					.logoutSuccessUrl("/signin?signout=success");
			http.authorizeRequests().anyRequest().authenticated();
		}

		private AuthenticationEntryPoint authenticationEntryPoint() {
			LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(
					"/signin");
			return entryPoint;
		}

		@Bean
		public ProviderSignInController providerSignInController(
				GitHubConnectionFactory connectionFactory,
				ConnectionFactoryRegistry registry,
				InMemoryUsersConnectionRepository repository) {

			registry.addConnectionFactory(connectionFactory);
			repository.setConnectionSignUp(new RemoteUsernameConnectionSignUp());
			ProviderSignInController controller = new ProviderSignInController(registry,
					repository, new GithubAuthenticationSigninAdapter(
							SIGNIN_SUCCESS_PATH, this.signInService));
			controller.setSignInUrl("/signin?error=access_denied");
			return controller;
		}

		@Bean
		public ConnectionFactoryRegistry connectionFactoryRegistry() {
			return new ConnectionFactoryRegistry();
		}

		@Bean
		public InMemoryUsersConnectionRepository inMemoryUsersConnectionRepository(
				ConnectionFactoryRegistry registry) {
			return new InMemoryUsersConnectionRepository(registry);
		}

	}

}
