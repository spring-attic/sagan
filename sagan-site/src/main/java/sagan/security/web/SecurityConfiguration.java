package sagan.security.web;

import sagan.team.service.SignInService;
import sagan.security.web.GithubAuthenticationSigninAdapter;
import sagan.security.web.RemoteUsernameConnectionSignUp;
import sagan.security.web.SecurityContextAuthenticationFilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.AntPathRequestMatcher;
import org.springframework.security.web.util.AnyRequestMatcher;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityConfiguration {

    static final String SIGNIN_SUCCESS_PATH = "/signin/success";

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class SigninAuthenticationConfiguration extends
            WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureHeaders(http.headers());
            http.antMatcher("/signin/**")
                    .addFilterBefore(authenticationFilter(),
                            AnonymousAuthenticationFilter.class).anonymous().and().csrf()
                    .disable();
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
            configureHeaders(http.headers());
            http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                    .and().requestMatchers().antMatchers("/admin/**", "/signout").and()
                    .addFilterAfter(new OncePerRequestFilter() {

                        // TODO this filter needs to be removed once basic auth is removed
                        @Override
                        protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response, FilterChain filterChain)
                                throws ServletException, IOException {
                            Authentication authentication = SecurityContextHolder
                                    .getContext().getAuthentication();
                            if (authentication == null
                                    || !authentication.isAuthenticated()
                                    || !(authentication.getPrincipal() instanceof Long)) {
                                throw new BadCredentialsException("Not a github user!");
                            }
                            filterChain.doFilter(request, response);
                        }
                    }, ExceptionTranslationFilter.class);
            http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
                    .logoutSuccessUrl("/").and().authorizeRequests().anyRequest()
                    .authenticated();
            if (isForceHttps()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

        private AuthenticationEntryPoint authenticationEntryPoint() {
            LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint(
                    "/signin");
            entryPoint.setForceHttps(isForceHttps());
            return entryPoint;
        }

        private boolean isForceHttps() {
            return !this.environment.acceptsProfiles(this.environment.getDefaultProfiles())
                    && !this.environment.acceptsProfiles("acceptance")
                    && !this.environment.acceptsProfiles("development");
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

    private static void configureHeaders(HeadersConfigurer<?> headers) throws Exception {
        HstsHeaderWriter writer = new HstsHeaderWriter(false);
        writer.setRequestMatcher(new AnyRequestMatcher());
        headers.contentTypeOptions().xssProtection().cacheControl()
                .addHeaderWriter(writer).frameOptions();
    }
}
