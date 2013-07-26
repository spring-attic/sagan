package org.springframework.site.web.documentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.site.web.NavSection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/documentation")
@NavSection("documentation")
public class DocumentationController {

	private DocumentationService service;

	@Autowired
	public DocumentationController(DocumentationService service) {
		this.service = service;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listProjects(Model model) {
		model.addAttribute("activeProjects", service.getProjectsForCategory("active"));
		model.addAttribute("atticProjects", service.getProjectsForCategory("attic"));
		model.addAttribute("incubatorProjects", service.getProjectsForCategory("incubator"));
		model.addAttribute("otherProjects", service.getProjectsForCategory("other"));
		return "documentation/index";
	}

}
