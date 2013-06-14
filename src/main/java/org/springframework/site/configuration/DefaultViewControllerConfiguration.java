package org.springframework.site.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class DefaultViewControllerConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private ResourcePatternResolver resourceResolver;

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
				if (((HttpServletRequest) request).getRequestURI().endsWith(".css")) {
					response.setContentType("text/css");
					chain.doFilter(request, response);
				}
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
}
