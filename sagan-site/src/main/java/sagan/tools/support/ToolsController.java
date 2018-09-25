package sagan.tools.support;

import sagan.tools.EclipseDownloads;
import sagan.tools.EclipsePlatform;
import sagan.tools.ToolSuiteDownloads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.tools.UpdateSiteArchive;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles requests for Spring Tool Suite (STS)
 *
 * Note that unlike other sections of the site, there is no "Admin" counterpart for this
 * controller. Rather, metadata about tools releases and download link locations is
 * managed in XML files stored at dist.springsource.com. See
 * {@link sagan.tools.support.ToolsService} for details.
 */
@Controller
@RequestMapping("/tools3")
class ToolsController {

    private ToolsService toolsService;

    @Autowired
    public ToolsController(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    @RequestMapping(method = { GET, HEAD })
    public String index(Model model) throws Exception {
        ToolSuiteDownloads stsDownloads = toolsService.getStsGaDownloads();
        model.addAttribute("stsDownloadLinks", stsDownloads.getPreferredDownloadLinks());
        model.addAttribute("stsVersion", stsDownloads.getReleaseName());
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

    @RequestMapping(value = "/sts/legacy", method = { GET, HEAD })
    public String legacyStsDownloads(Model model) throws Exception {
        Collection<ToolSuiteDownloads> stsDownloads = toolsService.getStsLegacyDownloads();
        model.addAttribute("legacyReleases", stsDownloads);
        return "tools/sts/legacy";
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
