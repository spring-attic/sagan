package io.spring.site.web.configuration;

import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.SignInService;
import io.spring.site.web.security.GithubAuthenticationSigninAdapter;
import io.spring.site.web.security.RemoteUsernameConnectionSignUp;
import io.spring.site.web.security.SecurityContextAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@Configuration
@ComponentScan({ "io.spring.site.domain.team", "io.spring.site.web.security",
        "io.spring.site.domain.services", "io.spring.site.domain.blog" })
public class SecurityConfiguration {

    static final String
			SIGNIN_SUCCESS_PATH = "/signin/success";

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class SigninAuthenticationConfiguration extends
            WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/signin/**")
                    .addFilterBefore(authenticationFilter(),
                            AnonymousAuthenticationFilter.class).anonymous();
            http.csrf().disable();
        }

        // Not a @Bean because we explicitly do not want it added automatically by
        // Bootstrap to all requests
        protected Filter authenticationFilter() {

            AbstractAuthenticationProcessingFilter filter = new SecurityContextAuthenticationFilter(
                    SIGNIN_SUCCESS_PATH);
            SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
            successHandler.setDefaultTargetUrl("/admin");
            filter.setAuthenticationSuccessHandler(successHandler);
            return filter;
        }
    }

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 90)
    protected static class AdminAuthenticationConfiguration extends
            WebSecurityConfigurerAdapter implements EnvironmentAware {

        @Autowired
        private SignInService signInService;

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());
            http.requestMatchers().antMatchers("/admin/**", "/signout");
			http.addFilterAfter(new OncePerRequestFilter() {
				//TODO this filter needs to be removed once basic auth is removed
				@Override
				protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof Long)) {
						throw new BadCredentialsException("Not a github user!");
					}
					filterChain.doFilter(request, response);
				}
			}, ExceptionTranslationFilter.class);
            http.logout()
					.logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
					.logoutSuccessUrl("/");
            http.authorizeRequests().anyRequest().authenticated();
            if (isForceHttps()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

		private AuthenticationEntryPoint authenticationEntryPoint() {
            LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/signin");
            entryPoint.setForceHttps(isForceHttps());
            return entryPoint;
        }

        private boolean isForceHttps() {
            return !this.environment.acceptsProfiles(this.environment
                    .getDefaultProfiles())
                    && !this.environment.acceptsProfiles("acceptance");
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

	@Profile({"prelaunch"})
    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 80)
	//TODO remove after launch, and also remove OncePerRequestFilter above
    protected static class BasicAuthenticationConfiguration extends
            WebSecurityConfigurerAdapter implements EnvironmentAware {

        private Environment environment;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers("/**");
            http.authorizeRequests().anyRequest().authenticated();
			http.httpBasic();
            if (isForceHttps()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/bootstrap/**");
			web.ignoring().antMatchers("/bootstrap-datetimepicker/**");
			web.ignoring().antMatchers("/css/**");
			web.ignoring().antMatchers("/font-awesome/**");
			web.ignoring().antMatchers("/img/**");
			web.ignoring().antMatchers("/js/**");
			web.ignoring().antMatchers("/500");
			web.ignoring().antMatchers("/404");
			web.ignoring().antMatchers("/project_metadata/**");
		}

		private boolean isForceHttps() {
            return !this.environment.acceptsProfiles(this.environment.getDefaultProfiles())
                    && !this.environment.acceptsProfiles("acceptance");
        }

    }

}
