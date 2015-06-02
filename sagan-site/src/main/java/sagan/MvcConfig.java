package sagan;

import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;
import sagan.support.StaticPagePathFinder;
import sagan.support.nav.Navigation;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.filter.CharacterEncodingFilter;
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

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Site-wide MVC infrastructure configuration. See also {@link SiteConfig} where certain
 * additional web infrastructure is configured.
 */
@Configuration
@ControllerAdvice
class MvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private StaticPagePathFinder staticPagePathFinder;

    @Bean(name = { "uih", "viewRenderingHelper" })
    @Scope("request")
    public ViewRenderingHelper viewRenderingHelper() {
        return new ViewRenderingHelper();
    }

    @Bean
    public DataAttributeDialect dataAttributeDialect() {
        return new DataAttributeDialect();
    }

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

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

	@Bean
	public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
		return new ResourceUrlEncodingFilter();
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
        private ProjectMetadataService projectMetadataService;

        @Autowired
        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public String navClass(String active, String current) {
            if (active.equals(current)) {
                return "navbar-link active";
            } else {
                return "navbar-link";
            }
        }

        public String blogClass(String active, String current) {
            if (active.equals(current)) {
                return "blog-category active";
            } else {
                return "blog-category";
            }
        }

        public String path() {
            return urlPathHelper.getPathWithinApplication(request);
        }
    }
}

@Configuration
@Profile(SaganProfiles.STANDALONE)
class LocalResourceHandlingConfig extends WebMvcConfigurerAdapter {

    @Value("${SAGAN_HOME:}")
    private String saganPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!this.saganPath.isEmpty()) {
			VersionResourceResolver versionResolver = new VersionResourceResolver()
					.addFixedVersionStrategy("dev", "/**/*.js", "/**/*.map")
					.addContentVersionStrategy("/**");

            registry.addResourceHandler("/**")
                    .addResourceLocations("file:///" + this.saganPath + "/sagan-client/src/")
                    .setCachePeriod(0)
					.resourceChain(false)
					.addResolver(versionResolver);
        }
    }
}

@Configuration
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudResourceHandlingConfig extends WebMvcConfigurerAdapter {

	@Value("${info.git.commit.id:}")
	private String gitCommitId;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		VersionResourceResolver versionResolver = new VersionResourceResolver()
				.addFixedVersionStrategy(gitCommitId, "/**/*.js", "/**/*.map")
				.addContentVersionStrategy("/**");

		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/")
				.setCachePeriod(3600 * 24 * 30)
				.resourceChain(true)
				.addResolver(versionResolver);

	}
}
