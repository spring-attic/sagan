package org.springframework.site.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;

@Configuration
public class DefaultViewControllerConfiguration extends WebMvcConfigurerAdapter{

	@Autowired
	private ResourcePatternResolver resourceResolver;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		try {
			Resource baseResource = resourceResolver.getResource("classpath:/templates/pages");
			String basePath = baseResource.getURL().getPath();
			Resource[] resources = resourceResolver.getResources("classpath:/templates/pages/**/*.html");
			for (Resource resource : resources) {
				String filePath = relativeFilePath(basePath, resource);
				registry.addViewController(buildRequestMapping(filePath))
						.setViewName("pages" + filePath);
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Unable to locate static pages: " + e.getMessage(), e);
		}
	}

	private String relativeFilePath(String basePath, Resource resource) throws IOException {
		return resource.getURL().getPath().substring(basePath.length()).replace(".html", "");
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
