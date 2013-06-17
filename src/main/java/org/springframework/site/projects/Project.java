package org.springframework.site.projects;

import java.net.URL;

public class Project {

	private String name;
	private URL url;

	public Project(String name, URL url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public URL getUrl() {
		return url;
	}
}
