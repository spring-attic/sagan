package sagan.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Demonstrates how to use a {@link RequestPostProcessor} to add request-building methods
 * for establishing a security context for Spring Security. While these are just examples,
 * <a href="https://jira.springsource.org/browse/SEC-2015">official support</a> for Spring
 * Security is planned.
 */
public final class SecurityRequestPostProcessors {

    public static CsrfRequestPostProcessor csrf() {
        return new CsrfRequestPostProcessor();
    }

    /**
     * Establish a security context for a user with the specified username. All details
     * are declarative and do not require that the user actually exists. This means that
     * the authorities or roles need to be specified too.
     */
    public static UserRequestPostProcessor user(Object username) {
        return new UserRequestPostProcessor(username);
    }

    /**
     * Establish a security context for a user with the specified username. The additional
     * details are obtained from the {@link UserDetailsService} declared in the
     * {@link WebApplicationContext}.
     */
    public static UserDetailsRequestPostProcessor userDetailsService(String username) {
        return new UserDetailsRequestPostProcessor(username);
    }

    /**
     * Establish a security context with the given {@link SecurityContext} and thus be
     * authenticated with {@link SecurityContext#getAuthentication()}.
     */
    public SecurityContextRequestPostProcessor securityContext(SecurityContext securityContext) {
        return new SecurityContextRequestPostProcessor(securityContext);
    }

    private static class CsrfRequestPostProcessor implements RequestPostProcessor {

        private CsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();

        /*
         * (non-Javadoc)
         * @see
         * org.springframework.test.web.servlet.request.RequestPostProcessor
         * #postProcessRequest(org.springframework.mock.web.MockHttpServletRequest)
         */
        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            if (!request.getRequestURI().startsWith("/project_metadata")) {
                CsrfToken token = repository.generateToken(request);
                repository.saveToken(token, request, new MockHttpServletResponse());
                request.setParameter(token.getParameterName(), token.getToken());
            }
            return request;
        }
    }

    /**
     * Support class for {@link RequestPostProcessor}'s that establish a Spring Security
     * context
     */
    private static abstract class SecurityContextRequestPostProcessorSupport {

        private SecurityContextRepository repository = new HttpSessionSecurityContextRepository();

        final void save(Authentication authentication, HttpServletRequest request) {
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            save(securityContext, request);
        }

        final void save(SecurityContext securityContext, HttpServletRequest request) {
            HttpServletResponse response = new MockHttpServletResponse();

            HttpRequestResponseHolder requestResponseHolder = new HttpRequestResponseHolder(request, response);
            repository.loadContext(requestResponseHolder);

            request = requestResponseHolder.getRequest();
            response = requestResponseHolder.getResponse();

            repository.saveContext(securityContext, request, response);
        }
    }

    public final static class SecurityContextRequestPostProcessor
            extends SecurityContextRequestPostProcessorSupport implements RequestPostProcessor {

        private final SecurityContext securityContext;

        private SecurityContextRequestPostProcessor(SecurityContext securityContext) {
            this.securityContext = securityContext;
        }

        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            save(securityContext, request);
            return request;
        }
    }

    public final static class UserRequestPostProcessor
            extends SecurityContextRequestPostProcessorSupport implements RequestPostProcessor {

        private final Object username;

        private String rolePrefix = "ROLE_";

        private Object credentials;

        private List<GrantedAuthority> authorities = new ArrayList<>();

        private UserRequestPostProcessor(Object username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
        }

        /**
         * Sets the prefix to append to each role if the role does not already start with
         * the prefix. If no prefix is desired, an empty String or null can be used.
         */
        public UserRequestPostProcessor rolePrefix(String rolePrefix) {
            this.rolePrefix = rolePrefix;
            return this;
        }

        /**
         * Specify the roles of the user to authenticate as. This method is similar to
         * {@link #authorities(GrantedAuthority...)}, but just not as flexible.
         *
         * @param roles The roles to populate. Note that if the role does not start with
         *        {@link #rolePrefix(String)} it will automatically be prepended. This
         *        means by default {@code roles("ROLE_USER")} and {@code roles("USER")}
         *        are equivalent.
         * @see #authorities(GrantedAuthority...)
         * @see #rolePrefix(String)
         */
        public UserRequestPostProcessor roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
            for (String role : roles) {
                if (rolePrefix == null || role.startsWith(rolePrefix)) {
                    authorities.add(new SimpleGrantedAuthority(role));
                } else {
                    authorities.add(new SimpleGrantedAuthority(rolePrefix + role));
                }
            }
            return this;
        }

        /**
         * Populates the user's {@link GrantedAuthority}'s.
         *
         * @param authorities
         * @see #roles(String...)
         */
        public UserRequestPostProcessor authorities(GrantedAuthority... authorities) {
            this.authorities = Arrays.asList(authorities);
            return this;
        }

        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, credentials, authorities);
            save(authentication, request);
            return request;
        }
    }

    public final static class UserDetailsRequestPostProcessor
            extends SecurityContextRequestPostProcessorSupport implements RequestPostProcessor {

        private final String username;

        private String userDetailsServiceBeanId;

        private UserDetailsRequestPostProcessor(String username) {
            this.username = username;
        }

        /**
         * Use this method to specify the bean id of the {@link UserDetailsService} to use
         * to look up the {@link UserDetails}.
         *
         * <p>
         * By default a lookup of {@link UserDetailsService} is performed by type. This
         * can be problematic if multiple {@link UserDetailsService} beans are declared.
         */
        public UserDetailsRequestPostProcessor userDetailsServiceBeanId(String userDetailsServiceBeanId) {
            this.userDetailsServiceBeanId = userDetailsServiceBeanId;
            return this;
        }

        @Override
        public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
            UsernamePasswordAuthenticationToken authentication = authentication(request.getServletContext());
            save(authentication, request);
            return request;
        }

        private UsernamePasswordAuthenticationToken authentication(ServletContext servletContext) {
            ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            UserDetailsService userDetailsService = userDetailsService(context);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        }

        private UserDetailsService userDetailsService(ApplicationContext context) {
            if (userDetailsServiceBeanId == null) {
                return context.getBean(UserDetailsService.class);
            }
            return context.getBean(userDetailsServiceBeanId, UserDetailsService.class);
        }
    }

    private SecurityRequestPostProcessors() {
    }

}
