package org.springframework.site.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.toolsuite.ToolSuite;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/tools")
public class ToolsController {
	private ToolsService toolsService;

	@Autowired
	public ToolsController(ToolsService toolsService) {
		this.toolsService = toolsService;
	}

	@RequestMapping(value = "/sts/all", method = { GET, HEAD })
	public String allStsDownloads(Model model) throws Exception {
		ToolSuite stsDownloads = toolsService.getStsDownloads();
		model.addAttribute("windows", stsDownloads.getPlatforms().get("windows"));
		model.addAttribute("mac", stsDownloads.getPlatforms().get("mac"));
		model.addAttribute("linux", stsDownloads.getPlatforms().get("linux"));
		return "tools/sts/all";
	}

}
