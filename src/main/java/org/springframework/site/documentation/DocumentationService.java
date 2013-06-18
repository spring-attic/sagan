package org.springframework.site.documentation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentationService {
	public List<Project> listProjects() {
		List<Project> projects = new ArrayList<Project>();

		projects.add(new Project("Spring Framework",
				"https://github.com/SpringSource/spring-framework",
				"http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/",
				"http://static.springsource.org/spring/docs/3.2.x/javadoc-api/"));

		projects.add(new Project("Spring Security",
				"https://github.com/SpringSource/spring-security",
				"http://static.springsource.org/spring-security/site/docs/3.2.x/reference/springsecurity.html",
				"http://static.springsource.org/spring-security/site/docs/3.2.x/apidocs/index.html"));

		projects.add(new Project("Spring Mobile",
				"https://github.com/SpringSource/spring-mobile",
				"http://static.springsource.org/spring-mobile/docs/current/reference/htmlsingle/",
				"http://static.springsource.org/spring-mobile/docs/current/api/"));

		return projects;
	}
}
