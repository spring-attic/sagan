package org.springframework.site.domain.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.bootstrap.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(path = "${documentation.path:classpath:documentation.yml}")
public class DocumentationService {

	private List<Project> projects = new ArrayList<Project>();
	private Map<String, Project> map;

	public void setProjects(List<Project> projects) {
		this.projects = projects;
		for (Project project : projects) {
			this.map.put(project.getId(), project);
		}
	}

	public List<Project> getProjects() {
		return this.projects;
	}

	public Project getProject(String id) {
		if (this.map == null) {
			this.map = new HashMap<String, Project>();
			setProjects(this.projects);
		}
		return this.map.get(id);
	}

}
