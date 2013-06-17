package org.springframework.site.projects;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Nick Street
 * Date: 6/17/13
 * Time: 12:25 PM
 */

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
