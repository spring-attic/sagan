package io.spring.site.web.configuration;

import io.spring.site.domain.team.SignInService;
import io.spring.site.web.security.GithubAuthenticationSigninAdapter;
import io.spring.site.web.security.RemoteUsernameConnectionSignUp;
import io.spring.site.web.security.SecurityContextAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.connect.GitHubConnectionFactory;

import javax.servlet.Filter;

@Configuration
@ComponentScan({ "io.spring.site.domain.team", "io.spring.site.web.security",
        "io.spring.site.domain.services", "io.spring.site.domain.blog" })
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
            http.csrf().disable();
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

}
