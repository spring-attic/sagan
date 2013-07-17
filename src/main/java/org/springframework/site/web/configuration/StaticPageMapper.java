package org.springframework.site.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaticPageMapper {
	private ResourcePatternResolver resourceResolver;

	public static class StaticPageMapping {
		private String filePath;
		private String urlPath;

		public StaticPageMapping(String filePath, String urlPath) {
			this.filePath = filePath;
			this.urlPath = urlPath;
		}

		public String getFilePath() {
			return filePath;
		}

		public String getUrlPath() {
			return urlPath;
		}
	}

	@Autowired
	public StaticPageMapper(ResourcePatternResolver resourceResolver) {
		this.resourceResolver = resourceResolver;
	}

	List<StaticPageMapping> staticPagePaths() throws IOException {
		Resource baseResource = resourceResolver
				.getResource("classpath:/templates/pages");
		String basePath = baseResource.getURL().getPath();
		Resource[] resources = resourceResolver
				.getResources("classpath:/templates/pages/**/*.html");
		List<StaticPageMapping> paths = new ArrayList<>();
		for (Resource resource : resources) {
			String filePath = relativeFilePath(basePath, resource);
			paths.add(new StaticPageMapping(filePath, buildRequestMapping(filePath)));
		}
		return paths;
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