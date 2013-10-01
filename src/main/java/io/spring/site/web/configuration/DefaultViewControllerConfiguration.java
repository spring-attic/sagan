package io.spring.site.web.configuration;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import io.spring.site.domain.StaticPagePathFinder;
import io.spring.site.web.NavSection;
import io.spring.site.web.ViewRenderingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.FingerprintResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlFilter;
import org.springframework.web.servlet.resource.ResourceUrlGenerator;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
public class DefaultViewControllerConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private StaticPagePathFinder staticPagePathFinder;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/")
                .setResourceResolvers(
                        Arrays.<ResourceResolver>asList(
                                new FingerprintResourceResolver(),
                                new PathResourceResolver()
                        ));

        // order before Spring Boot's ResourceHandler as injected above (see ResourceHandlerRegistry)
        // registry.setOrder(Integer.MAX_VALUE-2);
    }

    @Bean
    public ResourceUrlFilter resourceUrlFilter() {
        return new ResourceUrlFilter();
    }

    @Bean
    public ResourceUrlGenerator resourceUrlGenerator(
            @Qualifier("resourceHandlerMapping") HandlerMapping handlerMapping) {
        ResourceUrlGenerator resourceUrlGenerator = new ResourceUrlGenerator();
        resourceUrlGenerator.setResourceHandlerMappings(Arrays.<SimpleUrlHandlerMapping>asList((SimpleUrlHandlerMapping)handlerMapping));
        return resourceUrlGenerator;
    }

    @Bean(name = { "uih", "viewRenderingHelper" })
    @Scope("request")
    public ViewRenderingHelper viewRenderingHelper() {
        return new ViewRenderingHelper();
    }

    @Bean
    public DataAttributeDialect dataAttributeDialect() {
        return new DataAttributeDialect();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        try {
            for (StaticPagePathFinder.PagePaths paths : this.staticPagePathFinder.findPaths()) {
                String urlPath = paths.getUrlPath();
                registry.addViewController(urlPath).setViewName(
                        "pages" + paths.getFilePath());
                if (!urlPath.isEmpty()) {
                    registry.addViewController(urlPath + "/").setViewName(
                            "pages" + paths.getFilePath());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to locate static pages: " + e.getMessage(), e);
        }
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public void postHandle(HttpServletRequest request,
                    HttpServletResponse response, Object handler,
                    ModelAndView modelAndView) throws Exception {

                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    NavSection navSection = handlerMethod.getBean().getClass()
                            .getAnnotation(NavSection.class);
                    if (navSection != null && modelAndView != null) {
                        modelAndView.addObject("navSection", navSection.value());
                    }
                }
            }
        });
    }

    @Configuration
    public static class ErrorConfiguration implements EmbeddedServletContainerCustomizer {

        @Override
        public void customize(ConfigurableEmbeddedServletContainerFactory factory) {
            factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
            factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500"));
        }

    }

}
