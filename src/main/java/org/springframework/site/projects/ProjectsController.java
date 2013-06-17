package org.springframework.site.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/projects")
public class ProjectsController {

	private ProjectsService service;

	@Autowired
	public ProjectsController(ProjectsService service) {
		this.service = service;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listProjects(Model model) {
		List<Project> projects = service.listGuides();
		model.addAttribute("someprojects", projects);
		return "projects/index";
	}

}
