package sagan;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.util.UrlPathHelper;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;
import sagan.support.StaticPagePathFinder;
import sagan.support.nav.Navigation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Site-wide MVC infrastructure configuration. See also {@link SiteApplication} where certain
 * additional web infrastructure is configured.
 */
abstract class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private StaticPagePathFinder staticPagePathFinder;

    @Bean(name = {"uih", "viewRenderingHelper"})
    @Scope("request")
    public ViewRenderingHelper viewRenderingHelper() {
        return new ViewRenderingHelper();
    }

    public abstract AppVersionHelper appVersionHelper();

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public void handleException(ResourceNotFoundException ex) {
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        try {
            for (StaticPagePathFinder.PagePaths paths : staticPagePathFinder.findPaths()) {
                String urlPath = paths.getUrlPath();
                registry.addViewController(urlPath).setViewName("pages" + paths.getFilePath());
                if (!urlPath.isEmpty()) {
                    registry.addViewController(urlPath + "/").setViewName("pages" + paths.getFilePath());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to locate static pages: " + e.getMessage(), e);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                   ModelAndView modelAndView) throws Exception {

                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    Navigation navSection = handlerMethod.getBean().getClass().getAnnotation(Navigation.class);
                    if (navSection != null && modelAndView != null) {
                        modelAndView.addObject("navSection", navSection.value().toString().toLowerCase());
                    }
                }
            }
        });
    }

    @Configuration
    public static class ErrorConfig implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainer factory) {
            factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
            factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500"));
        }

    }

    static class ViewRenderingHelper {

        private final UrlPathHelper urlPathHelper = new UrlPathHelper();

        private HttpServletRequest request;

        @Autowired
        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public String navClass(String active, String current) {
            if (active.equals(current)) {
                return "navbar-link active";
            }
            else {
                return "navbar-link";
            }
        }

        public String blogClass(String active, String current) {
            if (active.equals(current)) {
                return "blog-category active";
            }
            else {
                return "blog-category";
            }
        }

        public String path() {
            return urlPathHelper.getPathWithinApplication(request);
        }
    }

    static class AppVersionHelper {

        private final String version;

        public AppVersionHelper(String version) {
            this.version = version;
        }

        public String version() {
            return version;
        }
    }
}

@Configuration
@ControllerAdvice
@Profile("cloudfoundry")
class CloudFoundryMvcConfig extends MvcConfig {

    @Value("${spring.git.properties:classpath:git.properties}")
    private Resource gitProperties;

    public String getGitCommitId() {
        try {
            if (this.gitProperties.exists()) {
                Properties properties = PropertiesLoaderUtils.loadProperties(this.gitProperties);
                return properties.getProperty("git.commit.id.abbrev");
            }
        } catch (IOException exc) {

        }
        throw new IllegalStateException("Missing git.properties file on classpath");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        VersionResourceResolver versionResourceResolver = new VersionResourceResolver()
                .addFixedVersionStrategy(getGitCommitId(), "/app/**")
                .addContentVersionStrategy("/**");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(versionResourceResolver);

    }

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Bean(name = "saganApp")
    @Override
    public AppVersionHelper appVersionHelper() {
        return new AppVersionHelper(getGitCommitId());
    }
}

@Configuration
@ControllerAdvice
@Profile("standalone")
class StandaloneMvcConfig extends MvcConfig {

    @Bean(name = "saganApp")
    @Override
    public AppVersionHelper appVersionHelper() {
        return new AppVersionHelper("");
    }
}