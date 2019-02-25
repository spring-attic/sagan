package sagan.tools.support;

import sagan.tools.SpringToolsPlatformRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for Spring Tools page
 */
@Controller
@RequestMapping("/tools")
public class SpringToolsController {

	private final SpringToolsPlatformRepository repository;

	public SpringToolsController(SpringToolsPlatformRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public String listDownloads(Model model) {
		this.repository.findAll().forEach(platform -> {
			model.addAttribute(platform.getId(), platform);
		});
		return "tools/list";
	}
}
