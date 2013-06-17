package org.springframework.site.projects;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectsService {
	public List<Project> listGuides() {
		List<Project> projects = new ArrayList<Project>();
		projects.add(new Project("Spring Framework", null));
		projects.add(new Project("Spring Security", null));
		projects.add(new Project("Spring Mobile", null));
		return projects;
	}
}
