package org.springframework.site.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.toolsuite.Platform;
import org.springframework.site.domain.tools.toolsuite.ToolSuite;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	@RequestMapping(value = "", method = { GET, HEAD })
	public String index() throws Exception {
		return "tools/index";
	}

	@RequestMapping(value = "/sts", method = { GET, HEAD })
	public String stsIndex(Model model) throws Exception {
		ToolSuite stsDownloads = toolsService.getStsDownloads();
		model.addAttribute("downloadLinks", stsDownloads.getPreferredDownloadLinks());
		model.addAttribute("version", stsDownloads.getPreferredVersion());
		return "tools/sts/index";
	}

	@RequestMapping(value = "/sts/all", method = { GET, HEAD })
	public String allStsDownloads(Model model) throws Exception {
		ToolSuite stsDownloads = toolsService.getStsDownloads();
		buildAllDownloadsModel(model, stsDownloads);
		return "tools/sts/all";
	}

	@RequestMapping(value = "/ggts", method = { GET, HEAD })
	public String ggtsIndex(Model model) throws Exception {
		ToolSuite ggtsDownloads = toolsService.getGgtsDownloads();
		model.addAttribute("downloadLinks", ggtsDownloads.getPreferredDownloadLinks());
		model.addAttribute("version", ggtsDownloads.getPreferredVersion());
		return "tools/ggts/index";
	}

	@RequestMapping(value = "/ggts/all", method = { GET, HEAD })
	 public String allGgtsDownloads(Model model) throws Exception {
		ToolSuite ggtsDownloads = toolsService.getGgtsDownloads();
		buildAllDownloadsModel(model, ggtsDownloads);
		return "tools/ggts/all";
	}

	private void buildAllDownloadsModel(Model model, ToolSuite ggtsDownloads) {
		Map<String,Platform> allPlatforms = ggtsDownloads.getPlatforms();

		List<Platform> platforms = new ArrayList<Platform>();
		platforms.add(allPlatforms.get("windows"));
		platforms.add(allPlatforms.get("mac"));
		platforms.add(allPlatforms.get("linux"));

		model.addAttribute("platforms", platforms);
		model.addAttribute("updateSiteArchives", ggtsDownloads.getArchives());
	}
}
