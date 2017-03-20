package sagan;

import sagan.team.MemberProfile;
import sagan.team.support.SignInService;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Site-wide web security configuration.
 */
@Configuration
class SecurityConfig {

    static final String SIGNIN_SUCCESS_PATH = "/signin/success";

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 100)
    protected static class SigninAuthenticationConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureHeaders(http.headers());
            http.requestMatchers().antMatchers("/signin/**", "/blog/**").and()
                    .addFilterBefore(authenticationFilter(),
                            AnonymousAuthenticationFilter.class).anonymous().and().csrf()
                    .disable();
        }

        // Not a @Bean because we explicitly do not want it added automatically by
        // Bootstrap to all requests
        protected Filter authenticationFilter() {

            AbstractAuthenticationProcessingFilter filter =
                    new SecurityContextAuthenticationFilter(SIGNIN_SUCCESS_PATH);
            SavedRequestAwareAuthenticationSuccessHandler successHandler =
                    new SavedRequestAwareAuthenticationSuccessHandler();
            successHandler.setDefaultTargetUrl("/admin");
            filter.setAuthenticationSuccessHandler(successHandler);
            return filter;
        }
    }

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 80)
    protected static class ApiAuthenticationConfig extends WebSecurityConfigurerAdapter implements
            EnvironmentAware {

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
            http.requestMatchers().antMatchers("/project_metadata/**")
                    .and().authorizeRequests().antMatchers(HttpMethod.HEAD, "/project_metadata/*").permitAll()
                    .antMatchers(HttpMethod.GET, "/project_metadata/*").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterAfter(githubBasicAuthFilter(), BasicAuthenticationFilter.class)
                    .csrf().disable();
            if (isForceHttps()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

        private Filter githubBasicAuthFilter() {
            return new BasicAuthenticationFilter(githubAuthenticationManager());
        }

        private AuthenticationManager githubAuthenticationManager() {
            return new AuthenticationManager() {

                @Override
                public Authentication authenticate(Authentication input) throws AuthenticationException {

                    GitHubTemplate gitHub = new GitHubTemplate(input.getName());
                    GitHubUserProfile userInfo = null;
                    try {
                        userInfo = gitHub.userOperations().getUserProfile();
                    } catch (Exception e) {
                        throw new BadCredentialsException("Cannot authenticate");
                    }
                    if (!signInService.isSpringMember(userInfo.getUsername(), gitHub)) {
                        throw new BadCredentialsException("User not member of required org");
                    }

                    MemberProfile member = signInService.getOrCreateMemberProfile(new Long(userInfo.getId()), gitHub);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            member.getId(), member.getGithubUsername(),
                            AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return authentication;

                }

            };
        }

        private boolean isForceHttps() {
            return !environment.acceptsProfiles(SaganProfiles.STANDALONE);
        }

    }

    @Configuration
    @Order(Ordered.LOWEST_PRECEDENCE - 90)
    protected static class AdminAuthenticationConfig extends WebSecurityConfigurerAdapter implements
            EnvironmentAware {

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
                        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                                        FilterChain filterChain) throws ServletException, IOException {
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            if (authentication == null || !authentication.isAuthenticated()
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
            LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/signin");
            entryPoint.setForceHttps(isForceHttps());
            return entryPoint;
        }

        private boolean isForceHttps() {
            return !environment.acceptsProfiles(SaganProfiles.STANDALONE);
        }

        @Bean
        public ProviderSignInController providerSignInController(GitHubConnectionFactory connectionFactory,
                                                                 ConnectionFactoryRegistry registry,
                                                                 InMemoryUsersConnectionRepository repository) {

            registry.addConnectionFactory(connectionFactory);
            repository.setConnectionSignUp(new RemoteUsernameConnectionSignUp());
            ProviderSignInController controller =
                    new ProviderSignInController(registry, repository, new GithubAuthenticationSigninAdapter(
                            SIGNIN_SUCCESS_PATH, signInService));
            controller.setSignInUrl("/signin?error=access_denied");
            return controller;
        }

        @Bean
        public ConnectionFactoryRegistry connectionFactoryRegistry() {
            return new ConnectionFactoryRegistry();
        }

        @Bean
        public InMemoryUsersConnectionRepository inMemoryUsersConnectionRepository(ConnectionFactoryRegistry registry) {
            return new InMemoryUsersConnectionRepository(registry);
        }
    }

    private static void configureHeaders(HeadersConfigurer<?> headers) throws Exception {
        HstsHeaderWriter writer = new HstsHeaderWriter(false);
        writer.setRequestMatcher(AnyRequestMatcher.INSTANCE);
        headers.contentTypeOptions().and().xssProtection()
                .and().cacheControl().and().addHeaderWriter(writer).frameOptions();
    }

    /**
     * Thin filter for Spring Security chain that simply transfers an existing
     * {@link Authentication} from the {@link SecurityContext} if there is one. This is
     * useful when authentication actually happened in a controller, rather than in the
     * filter chain itself.
     */
    static class SecurityContextAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

        public SecurityContextAuthenticationFilter(String defaultFilterProcessesUrl) {
            super(defaultFilterProcessesUrl);
            setAuthenticationManager(authentication -> {
                // No-op authentication manager is required by base class, but
                // actually redundant here because the authentication has either
                // already happened (happy day) or not (user is not authenticated)
                throw new IllegalStateException("Unexpected call for AuthenticationManager");
            });
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                throws AuthenticationException, IOException, ServletException {
            return SecurityContextHolder.getContext().getAuthentication();
        }
    }

    /**
     * Simple {@link ConnectionSignUp} implementation that pulls user id from remote user
     * details in the Social {@link Connection}.
     */
    static class RemoteUsernameConnectionSignUp implements ConnectionSignUp {
        @Override
        public String execute(Connection<?> connection) {
            return connection.getKey().getProviderUserId() != null ? connection.getKey().getProviderUserId() : null;
        }
    }

    static class GithubAuthenticationSigninAdapter implements SignInAdapter {

        private String path;
        private final SignInService signInService;

        public GithubAuthenticationSigninAdapter(String path, SignInService signInService) {
            this.path = path;
            this.signInService = signInService;
        }

        @Override
        public String signIn(String githubId, Connection<?> connection, NativeWebRequest request) {
            GitHub gitHub = (GitHub) connection.getApi();
            String githubUsername = connection.getDisplayName();

            try {
                if (!signInService.isSpringMember(githubUsername, gitHub)) {
                    throw new BadCredentialsException("User not member of required org");
                }

                MemberProfile member = signInService.getOrCreateMemberProfile(new Long(githubId), gitHub);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        member.getId(), member.getGithubUsername(),
                        AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return path;

            } catch (RestClientException ex) {
                throw new BadCredentialsException("User not member of required org", ex);
            }
        }
    }
}
