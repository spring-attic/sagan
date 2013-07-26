package org.springframework.site.web.guides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.web.NavSection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/guides")
@NavSection("guides")
public class GuidesController {

	private GettingStartedService service;

	@Autowired
	public GuidesController(GettingStartedService service) {
		this.service = service;
	}

	@RequestMapping(value = "", method = { GET, HEAD })
	public String index(Model model) {
		model.addAttribute("guides", service.listGuides());
		return "guides/index";
	}
}
