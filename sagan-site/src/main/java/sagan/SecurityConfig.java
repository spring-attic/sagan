package sagan;

import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.EnvironmentAware;
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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.client.RestOperations;

/**
 * Site-wide web security configuration.
 */
@Configuration
class SecurityConfig {

    private static final String IS_MEMBER_URL = "https://api.github.com/teams/{team}/members/{user}";

    private static final String USER_URL = "https://api.github.com/user";

    @Configuration
    @Order(-10)
    protected static class SigninAuthenticationConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            configureHeaders(http.headers());
            http.requestMatchers().antMatchers("/actuator/**").and().httpBasic().and().csrf()
                    .disable();
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
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/lib/**", "/css/**", "/font-custom/**", "/img/**", "/500", "/404");
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
    protected static class AdminAuthenticationConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .requiresChannel()
                    .requestMatchers(request -> request.getHeader("x-forwarded-port") != null).requiresSecure()
                    .and()
                .authorizeRequests()
                    .mvcMatchers("/admin/**").hasRole("ADMIN")
                    .and()
                .oauth2Login()
                    .defaultSuccessUrl("/admin/")
                    .loginPage("/signin")
                    .permitAll()
                    .userInfoEndpoint().userService(this.userService);
             }

    }

    private static void configureHeaders(HeadersConfigurer<?> headers) throws Exception {
        HstsHeaderWriter writer = new HstsHeaderWriter(false);
        writer.setRequestMatcher(AnyRequestMatcher.INSTANCE);
        headers.contentTypeOptions().and().xssProtection()
                .and().cacheControl().and().addHeaderWriter(writer).frameOptions();
    }

}
