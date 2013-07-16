package org.springframework.site.domain.tools.eclipse;

import org.springframework.site.domain.tools.ToolsDownloads;

import java.util.List;

public class EclipsePlatform implements ToolsDownloads.ToolsPlatform {
	private final List<EclipseRelease> products;
	private String name;

	public EclipsePlatform(String name, List<EclipseRelease> products) {
		this.name = name;
		this.products = products;
	}

	public String getName() {
		return name;
	}

	public List<EclipseRelease> getReleases() {
		return products;
	}
}
