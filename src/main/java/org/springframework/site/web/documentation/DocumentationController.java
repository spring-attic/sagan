package org.springframework.site.web.documentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

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
		model.addAttribute("projects", service.getProjects());
		return "documentation/index";
	}

}
