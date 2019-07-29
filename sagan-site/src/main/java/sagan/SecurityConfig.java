package sagan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Site-wide web security configuration.
 */
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {
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
