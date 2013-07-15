package org.springframework.site.web.projects;

import org.springframework.site.web.NavSection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/projects")
@NavSection("projects")
public class ProjectsController {

	@RequestMapping(value = "", method = { GET, HEAD })
	public String index() {
		return "projects/index";
	}
}
