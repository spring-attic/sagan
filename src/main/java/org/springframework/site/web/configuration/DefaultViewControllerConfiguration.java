package org.springframework.site.web.configuration;

import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.site.domain.StaticPageMapper;
import org.springframework.site.web.ApplicationDialect;
import org.springframework.site.web.NavSection;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DefaultViewControllerConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private StaticPageMapper staticPageMapper;

	private Map<String, MediaType> mimeTypes = new HashMap<String, MediaType>();

	{
		this.mimeTypes.put("css", MediaType.valueOf("text/css"));
		this.mimeTypes.put("js", MediaType.valueOf("text/javascript"));
		this.mimeTypes.put("ico", MediaType.APPLICATION_OCTET_STREAM);
		this.mimeTypes.put("png", MediaType.IMAGE_PNG);
		this.mimeTypes.put("jpg", MediaType.IMAGE_JPEG);
		this.mimeTypes.put("woff", MediaType.valueOf("application/font-woff"));
	}

	@Bean
	public ApplicationDialect applicationDialect() {
		return new ApplicationDialect();
	}

	@Bean
	public DataAttributeDialect dataAttributeDialect() {
		return new DataAttributeDialect();
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		try {
			for(StaticPageMapper.StaticPageMapping mapping : staticPageMapper.staticPagePaths()) {
				registry.addViewController(mapping.getUrlPath()).setViewName("pages" + mapping.getFilePath());
			}

		} catch (IOException e) {
			throw new RuntimeException(
					"Unable to locate static pages: " + e.getMessage(), e);
		}
	}

	// FIXME: would like to use a HandlerInterceptor, but the css is served by a Handler
	// that @EnableWebMvc does not add interceptors to, so we use a Filter instead. We
	// would like this to be fixed in Spring (but open to suggestion) -
	// https://jira.springsource.org/browse/SPR-10655.
	@Bean
	public Filter cssMimeTypeFilter() {
		return new GenericFilterBean() {

			@Override
			public void doFilter(ServletRequest request, ServletResponse response,
					FilterChain chain) throws IOException, ServletException {
				String uri = ((HttpServletRequest) request).getRequestURI();
				String extension = StringUtils.getFilenameExtension(uri);
				if (DefaultViewControllerConfiguration.this.mimeTypes
						.containsKey(extension)) {
					response.setContentType(DefaultViewControllerConfiguration.this.mimeTypes
							.get(extension).toString());
				} else {
					this.logger.warn("No media type found for request: " + uri);
				}
				chain.doFilter(request, response);
			}
		};
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
					if (navSection != null) {
						modelAndView.addObject("navSection", navSection.value());
					}
				}
			}
		});
	}

}
