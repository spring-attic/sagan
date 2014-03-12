package sagan.tools.support;

import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePlatform;
import sagan.tools.ToolSuiteDownloads;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/tools")
public class ToolsController {
    private ToolsService toolsService;

    @Autowired
    public ToolsController(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String index(Model model) throws Exception {
        ToolSuiteDownloads stsDownloads = toolsService.getStsGaDownloads();
        model.addAttribute("stsDownloadLinks", stsDownloads.getPreferredDownloadLinks());
        model.addAttribute("stsVersion", stsDownloads.getReleaseName());
        ToolSuiteDownloads ggtsDownloads = toolsService.getGgtsGaDownloads();
        model.addAttribute("ggtsDownloadLinks", ggtsDownloads.getPreferredDownloadLinks());
        model.addAttribute("ggtsVersion", ggtsDownloads.getReleaseName());
        return "tools/index";
    }

    @RequestMapping(value = "/sts", method = { GET, HEAD })
    public String stsIndex(Model model) throws Exception {
        ToolSuiteDownloads stsDownloads = toolsService.getStsGaDownloads();
        model.addAttribute("downloadLinks", stsDownloads.getPreferredDownloadLinks());
        model.addAttribute("version", stsDownloads.getReleaseName());
        return "tools/sts/index";
    }

    @RequestMapping(value = "/sts/welcome", method = { GET, HEAD })
    public String stsWelcome(Model model) throws Exception {
        return "tools/sts/welcome";
    }

    @RequestMapping(value = "/sts/all", method = { GET, HEAD })
    public String allStsDownloads(Model model) throws Exception {
        ToolSuiteDownloads stsDownloads = toolsService.getStsGaDownloads();
        ToolSuiteDownloads milestoneDownloads = toolsService.getStsMilestoneDownloads();
        model.addAttribute("gaRelease", stsDownloads);
        model.addAttribute("milestoneRelease", milestoneDownloads);
        model.addAttribute("updateSiteArchives", stsDownloads.getArchives());
        return "tools/sts/all";
    }

    @RequestMapping(value = "/ggts", method = { GET, HEAD })
    public String ggtsIndex(Model model) throws Exception {
        ToolSuiteDownloads ggtsDownloads = toolsService.getGgtsGaDownloads();
        model.addAttribute("downloadLinks", ggtsDownloads.getPreferredDownloadLinks());
        model.addAttribute("version", ggtsDownloads.getReleaseName());
        return "tools/ggts/index";
    }

    @RequestMapping(value = "/ggts/all", method = { GET, HEAD })
    public String allGgtsDownloads(Model model) throws Exception {
        ToolSuiteDownloads gaDownloads = toolsService.getGgtsGaDownloads();
        ToolSuiteDownloads milestoneDownloads = toolsService.getGgtsMilestoneDownloads();
        model.addAttribute("gaRelease", gaDownloads);
        model.addAttribute("milestoneRelease", milestoneDownloads);
        model.addAttribute("updateSiteArchives", gaDownloads.getArchives());
        return "tools/ggts/all";
    }

    @RequestMapping(value = "/eclipse", method = { GET, HEAD })
    public String eclipseIndex(Model model) throws Exception {
        EclipseDownloads eclipseDownloads = toolsService.getEclipseDownloads();
        Map<String, EclipsePlatform> allPlatforms = eclipseDownloads.getPlatforms();
        List<EclipsePlatform> platforms = new ArrayList<>();
        platforms.add(allPlatforms.get("windows"));
        platforms.add(allPlatforms.get("mac"));
        platforms.add(allPlatforms.get("linux"));
        model.addAttribute("platforms", platforms);
        return "tools/eclipse/index";
    }

}
