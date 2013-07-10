package org.springframework.site.web.configuration;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DefaultViewControllerConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private ResourcePatternResolver resourceResolver;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private ThymeleafViewResolver resolver;

	private Map<String, MediaType> mimeTypes = new HashMap<String, MediaType>();

	{
		mimeTypes.put("css", MediaType.valueOf("text/css"));
		mimeTypes.put("js", MediaType.valueOf("text/javascript"));
		mimeTypes.put("ico", MediaType.APPLICATION_OCTET_STREAM);
		mimeTypes.put("png", MediaType.IMAGE_PNG);
		mimeTypes.put("jpg", MediaType.IMAGE_JPEG);
		mimeTypes.put("woff", MediaType.valueOf("application/font-woff"));
	}

	@PostConstruct
	public void configureThymeleafSecurity() {
		templateEngine.addDialect(new SpringSecurityDialect());
		templateEngine.addDialect(new LayoutDialect());
	}

	@PostConstruct
	public void addUtf8EncodingToThymeleaf() {
		resolver.setCharacterEncoding("UTF-8");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		try {
			Resource baseResource = resourceResolver
					.getResource("classpath:/templates/pages");
			String basePath = baseResource.getURL().getPath();
			Resource[] resources = resourceResolver
					.getResources("classpath:/templates/pages/**/*.html");
			for (Resource resource : resources) {
				String filePath = relativeFilePath(basePath, resource);
				registry.addViewController(buildRequestMapping(filePath)).setViewName(
						"pages" + filePath);
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
				if (mimeTypes.containsKey(extension)) {
					response.setContentType(mimeTypes.get(extension).toString());
				} else {
					logger.warn("No media type found for request: " + uri);
				}
				chain.doFilter(request, response);
			}
		};
	}

	private String relativeFilePath(String basePath, Resource resource)
			throws IOException {
		return resource.getURL().getPath().substring(basePath.length())
				.replace(".html", "");
	}

	private String buildRequestMapping(String filePath) {
		String requestMapping = filePath;
		if (requestMapping.endsWith("/index")) {
			requestMapping = requestMapping.replace("/index", "");
			if (requestMapping.equals("")) {
				requestMapping = "/";
			}
		}
		return requestMapping;
	}

	@Bean
	public OpenEntityManagerInViewInterceptor interceptor() {
		return new OpenEntityManagerInViewInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addWebRequestInterceptor(interceptor());
	}

	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
		return new HiddenHttpMethodFilter();
	}

}
