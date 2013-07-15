package org.springframework.site.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.tools.ToolsDownloads;
import org.springframework.site.domain.tools.ToolsService;
import org.springframework.site.domain.tools.eclipse.EclipseDownloads;
import org.springframework.site.domain.tools.toolsuite.ToolSuiteDownloads;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.site.domain.tools.ToolsDownloads.ToolsPlatform;
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
	public String stsIndex() throws Exception {
		return "tools/sts/index";
	}

	@RequestMapping(value = "/sts/all", method = { GET, HEAD })
	public String allStsDownloads(Model model) throws Exception {
		ToolSuiteDownloads stsDownloads = toolsService.getStsDownloads();
		buildAllDownloadsModel(model, stsDownloads);
		return "tools/sts/all";
	}

	@RequestMapping(value = "/ggts", method = { GET, HEAD })
	public String ggtsIndex() throws Exception {
		return "tools/ggts/index";
	}

	@RequestMapping(value = "/ggts/all", method = { GET, HEAD })
	 public String allGgtsDownloads(Model model) throws Exception {
		ToolSuiteDownloads ggtsDownloads = toolsService.getGgtsDownloads();
		buildAllDownloadsModel(model, ggtsDownloads);
		return "tools/ggts/all";
	}

	@RequestMapping(value = "/eclipse", method = { GET, HEAD })
	public String eclipseIndex(Model model) throws Exception {
		EclipseDownloads eclipseDownloads = toolsService.getEclipseDownloads();
		addPlatformsToModel(model, eclipseDownloads);
		return "tools/eclipse/index";
	}

	private void buildAllDownloadsModel(Model model, ToolSuiteDownloads toolSuite) {
		addPlatformsToModel(model, toolSuite);
		model.addAttribute("updateSiteArchives", toolSuite.getArchives());
	}

	private void addPlatformsToModel(Model model, ToolsDownloads toolsDownloads) {
		Map<String, ? extends ToolsPlatform> allPlatforms = toolsDownloads.getPlatforms();
		List<ToolsPlatform> platforms = new ArrayList<ToolsPlatform>();
		platforms.add(allPlatforms.get("windows"));
		platforms.add(allPlatforms.get("mac"));
		platforms.add(allPlatforms.get("linux"));
		model.addAttribute("platforms", platforms);
	}

}
