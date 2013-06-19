package org.springframework.site.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DocumentationService {

	private List<Project> projects = new ArrayList<Project>();
	private Map<String, Project> map;

	public void setProjects(List<Project> projects) {
		this.projects = projects;
		for (Project project : projects) {
			map.put(project.getId(), project);
		}
	}

	public List<Project> getProjects() {
		return projects;
	}

	public Project getProject(String id) {
		if (map==null) {
			map = new HashMap<String, Project>();
			setProjects(projects);
		}
		return map.get(id);
	}

}
