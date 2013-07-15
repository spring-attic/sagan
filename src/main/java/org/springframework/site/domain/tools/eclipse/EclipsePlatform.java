package org.springframework.site.domain.tools.eclipse;

import org.springframework.site.domain.tools.ToolsDownloads;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

public class EclipsePlatform implements ToolsDownloads.ToolsPlatform {
	private final List<EclipsePackage> packages;
	private String name;

	public EclipsePlatform(String name) {
		this.name = capitalize(name);
		this.packages = new ArrayList<EclipsePackage>();
	}

	public String getName() {
		return name;
	}

	public List<EclipsePackage> getPackages() {
		return packages;
	}

	public void addPackage(EclipsePackage p) {
		if (!packages.contains(p)) {
			packages.add(p);
		}
	}
}
