package io.spring.site.web.understanding;

import io.spring.site.domain.understanding.UnderstandingGuide;
import io.spring.site.domain.understanding.UnderstandingGuidesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
public class UnderstandingGuidesController {

	private final UnderstandingGuidesService understandingGuidesService;

	@Autowired
	public UnderstandingGuidesController(UnderstandingGuidesService understandingGuidesService) {
		this.understandingGuidesService = understandingGuidesService;
	}

	@RequestMapping(value = "/understanding/{subject}", method = {GET, HEAD})
	public String understandingASubject(@PathVariable String subject, Model model) {
		UnderstandingGuide guide = understandingGuidesService.getGuideForSubject(subject);
		model.addAttribute("guide", guide);
		return "understanding/show";
	}
}
