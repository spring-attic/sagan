package org.springframework.site.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

public class ViewRenderingHelper {

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
		return this.urlPathHelper.getPathWithinApplication(this.request);
	}

	public String getGhPagesUrl() {
		return projectMetadataService.getGhPagesBaseUrl();
	}

}
