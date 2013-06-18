package org.springframework.site.documentation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentationService {
	public List<Project> listProjects() {
		List<Project> projects = new ArrayList<Project>();

        ArrayList<String> supportedVersions = new ArrayList<String>();
        supportedVersions.add("3.2.3.RELEASE");
        projects.add(new Project("Spring Framework",
				"https://github.com/SpringSource/spring-framework",
				"http://static.springsource.org/spring/docs/{version}/spring-framework-reference/html/",
				"http://static.springsource.org/spring/docs/{version}/javadoc-api/", supportedVersions));

        supportedVersions = new ArrayList<String>();
        supportedVersions.add("3.1.4.RELEASE");
		projects.add(new Project("Spring Security",
				"https://github.com/SpringSource/spring-security",
				"http://static.springsource.org/spring-security/site/docs/{version}/reference/springsecurity.html",
				"http://static.springsource.org/spring-security/site/docs/{version}/apidocs/index.html", supportedVersions));

        supportedVersions = new ArrayList<String>();
        supportedVersions.add("1.0.1.RELEASE");
		projects.add(new Project("Spring Mobile",
				"https://github.com/SpringSource/spring-mobile",
				"http://static.springsource.org/spring-mobile/docs/{version}/reference/htmlsingle/",
				"http://static.springsource.org/spring-mobile/docs/{version}/api/", supportedVersions));

        supportedVersions = new ArrayList<String>();
        supportedVersions.add("2.0");
		projects.add(new Project("Spring Web Services",
				"https://github.com/SpringSource/spring-ws",
				"http://static.springsource.org/spring-ws/sites/{version}/reference/html/index.html",
				"http://static.springsource.org/spring-ws/sites/{version}/apidocs/index.html", supportedVersions));

		return projects;
	}
}
