package sagan;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.HstsHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import sagan.security.GithubAuthenticationManager;

/**
 * Site-wide web security configuration.
 */
@Configuration
class SecurityConfig {

    @Configuration
    @Order(0)
    protected static class ApiAuthenticationConfig extends WebSecurityConfigurerAdapter implements
            EnvironmentAware {

        @Autowired
        private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

        @Autowired
        private ClientRegistrationRepository oauthClients;

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
                    .anyRequest().hasRole("ADMIN")
                    .and()
                    .addFilterAfter(githubBasicAuthFilter(), BasicAuthenticationFilter.class)
                    .csrf().disable();
            if (isForceHttps()) {
                http.requiresChannel().anyRequest().requiresSecure();
            }
        }

        private Filter githubBasicAuthFilter() {
            GithubAuthenticationManager manager = new GithubAuthenticationManager(this.oauthClients, this.userService);
            return new BasicAuthenticationFilter(manager);
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
