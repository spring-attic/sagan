package org.springframework.site.web.guides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.guides.GuidesService;
import org.springframework.site.web.NavSection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/guides/tutorials")
@NavSection("guides")
public class TutorialsController {

	private GuidesService service;

	@Autowired
	public TutorialsController(GuidesService service) {
		this.service = service;
	}

	@RequestMapping(value = "/{tutorialId}", method = { GET, HEAD })
	public String viewTutorial(@PathVariable String tutorialId, Model model) {
		model.addAttribute("tutorialId", tutorialId);
		model.addAttribute("tutorial", service.loadTutorial(tutorialId));
		return "guides/tutorial/show";
	}

	@RequestMapping(value = "/{tutorialId}/{pagePath}", method = { GET, HEAD })
	public String viewTutorialPage(@PathVariable String tutorialId, @PathVariable String pagePath, Model model) {
		model.addAttribute("tutorialId", tutorialId);
		model.addAttribute("tutorial", service.loadTutorialPage(tutorialId, pagePath));
		return "guides/tutorial/show";
	}
}
