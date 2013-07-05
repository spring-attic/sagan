package org.springframework.site.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.site.security.GithubAuthenticationSigninAdapter;
import org.springframework.site.security.RemoteUsernameConnectionSignUp;
import org.springframework.site.security.SecurityContextAuthenticationFilter;
import org.springframework.site.team.SignInService;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.connect.GitHubConnectionFactory;

import javax.servlet.Filter;

@Configuration
@ComponentScan(basePackages = {"org.springframework.site.team", "org.springframework.site.security", "org.springframework.site.services", "org.springframework.site.blog"})
public class SecurityConfiguration {

	static final String SIGNIN_SUCCESS_PATH = "/signin/success";

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

}
