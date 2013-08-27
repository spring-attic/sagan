package io.spring.site.web.configuration;

import io.spring.site.domain.StaticPagePathFinder;
import io.spring.site.web.NavSection;
import io.spring.site.web.ViewRenderingHelper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;

@Configuration
public class DefaultViewControllerConfiguration extends WebMvcConfigurerAdapter {

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
