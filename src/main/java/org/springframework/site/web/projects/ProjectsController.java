package org.springframework.site.web.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.projects.ProjectMetadataService;
import org.springframework.site.web.NavSection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/projects")
@NavSection("projects")
public class ProjectsController {

	private ProjectMetadataService service;

	@Autowired
	public ProjectsController(ProjectMetadataService service) {
		this.service = service;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listProjects(Model model) {
		return "projects/index";
	}

}
