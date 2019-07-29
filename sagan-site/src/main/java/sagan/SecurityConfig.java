package sagan;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.client.RestOperations;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Site-wide web security configuration.
 */
@Configuration
class SecurityConfig {

    private static final String IS_MEMBER_URL = "https://api.github.com/teams/{team}/members/{user}";

    private static final String USER_URL = "https://api.github.com/user";

    static final String SIGNIN_SUCCESS_PATH = "/signin/success";

    @Configuration
    @Order(-10)
    protected static class SigninAuthenticationConfig extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/lib/**", "/css/**", "/font-custom/**", "/img/**", "/500", "/404");
        }

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
    @Order(0)
    protected static class ApiAuthenticationConfig extends WebSecurityConfigurerAdapter implements
            EnvironmentAware {

        private Environment environment;

        @Value("${github.team.id}")
        private String gitHubTeamId;

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
            return new BasicAuthenticationFilter(basicAuthenticationManager());
        }

        private AuthenticationManager basicAuthenticationManager() {
            RestOperations rest = new RestTemplateBuilder().build();
            return authentication -> {
                HttpHeaders headers = new HttpHeaders();
                headers.add(
                        HttpHeaders.AUTHORIZATION, "Bearer "
                                + authentication.getName());
                @SuppressWarnings("rawtypes")
                ResponseEntity<Map> userResponse = rest.exchange(USER_URL, HttpMethod.GET, new HttpEntity<>(headers),
                        Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> user = userResponse.getBody();
                @SuppressWarnings("rawtypes")
                ResponseEntity<Map> response = rest.exchange(IS_MEMBER_URL, HttpMethod.GET, new HttpEntity<>(headers),
                        Map.class, gitHubTeamId, user.get("login"));
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new BadCredentialsException("Wrong team");
                }
                return new UsernamePasswordAuthenticationToken(user.get("login"),
                        authentication.getName());
            };
        }

        private boolean isForceHttps() {
            return !environment.acceptsProfiles(Profiles.of(SaganProfiles.STANDALONE));
        }

    }

    @Configuration
    @Order(10)
    protected static class AdminAuthenticationConfig extends WebSecurityConfigurerAdapter implements
            EnvironmentAware {

        private Environment environment;

        @Value("${github.team.id}")
        private String gitHubTeamId;

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureHeaders(http.headers());
            http.requestMatchers().antMatchers("/login/**", "/admin/**", "/signout").and()
                    .anonymous().disable()
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
            http.oauth2Login().loginPage("/signin");
            http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/signout"))
                    .logoutSuccessUrl("/").and().authorizeRequests().anyRequest()
                    .authenticated();
            if (isForceHttps()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

        @Bean
        public OAuth2UserService<OAuth2UserRequest, OAuth2User> authenticationProvider() {
            RestOperations rest = new RestTemplateBuilder().build();
            return new DefaultOAuth2UserService() {
                @Override
                public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                    OAuth2User user = super.loadUser(userRequest);
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(
                            HttpHeaders.AUTHORIZATION, "Bearer "
                                    + userRequest.getAccessToken().getTokenValue());
                    @SuppressWarnings("rawtypes")
                    ResponseEntity<Map> response = rest.exchange(IS_MEMBER_URL, HttpMethod.GET, new HttpEntity<>(
                            headers), Map.class, gitHubTeamId,
                            user.getName());
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new BadCredentialsException("Wrong team");
                    }
                    return user;
                }
            };
        }

        private boolean isForceHttps() {
            return !environment.acceptsProfiles(Profiles.of(SaganProfiles.STANDALONE));
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

}
