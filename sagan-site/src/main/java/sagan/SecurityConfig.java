package sagan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import sagan.security.GithubAuthenticationManager;

import javax.servlet.Filter;
import java.util.LinkedHashMap;

/**
 * Site-wide web security configuration.
 */
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

    @Autowired
    private ClientRegistrationRepository oauthClients;

    @Autowired(required = false)
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> responseClient =
        new DefaultAuthorizationCodeTokenResponseClient();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .addFilterAfter(githubBasicAuthFilter(), BasicAuthenticationFilter.class)
            .exceptionHandling()
                .authenticationEntryPoint(entryPoint())
                .and()
            .csrf()
                .ignoringAntMatchers("/project_metadata/*")
                .and()
            .requiresChannel()
                .requestMatchers(request -> request.getHeader("x-forwarded-port") != null).requiresSecure()
                .and()
            .authorizeRequests()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.HEAD, "/project_metadata/*").permitAll()
                .mvcMatchers(HttpMethod.GET, "/project_metadata/*").permitAll()
                .mvcMatchers("/project_metadata/**").access("hasRole('ADMIN') and hasRole('API')")
                .and()
            .oauth2Login()
                .defaultSuccessUrl("/admin/")
                .loginPage("/signin")
                    .permitAll()
                .tokenEndpoint()
                    .accessTokenResponseClient(this.responseClient)
                    .and()
                .userInfoEndpoint()
                    .userService(this.userService);
    }

    private Filter githubBasicAuthFilter() {
        GithubAuthenticationManager manager = new GithubAuthenticationManager(this.oauthClients, this.userService);
        return new BasicAuthenticationFilter(manager);
    }

    private AuthenticationEntryPoint entryPoint() {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> mapping = new LinkedHashMap<>();
        mapping.put(new AntPathRequestMatcher("/project_metadata/**"), new Http403ForbiddenEntryPoint());
        DelegatingAuthenticationEntryPoint result = new DelegatingAuthenticationEntryPoint(
                mapping);
        result.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/signin"));
        return result;
    }

}
