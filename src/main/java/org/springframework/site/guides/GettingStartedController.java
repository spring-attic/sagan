package org.springframework.site.guides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/guides/gs")
public class GettingStartedController {

	private GettingStartedService service;

	@Autowired
	public GettingStartedController(GettingStartedService service) {
		this.service = service;
	}

	@RequestMapping(value = "/{guideSlug}", method = { GET, HEAD })
	public String viewGuide(@PathVariable("guideSlug") String guideSlug, Model model) {
		model.addAttribute("guideSlug", guideSlug);
		model.addAttribute("guide", service.loadGuide(guideSlug));
		return "guides/gs/guide";
	}

}
