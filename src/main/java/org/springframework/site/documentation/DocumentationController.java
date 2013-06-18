package org.springframework.site.documentation;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/documentation")
public class DocumentationController {

	private DocumentationService service;

	@Autowired
	public DocumentationController(DocumentationService service) {
		this.service = service;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String listProjects(Model model) {
		model.addAttribute("projects", service.listProjects());
		return "documentation/index";
	}

}
