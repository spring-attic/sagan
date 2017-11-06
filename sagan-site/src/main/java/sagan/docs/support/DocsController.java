package sagan.docs.support;

import sagan.projects.support.ProjectMetadataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Controller that handles requests for the /docs documentation page.
 */
@Controller
@RequestMapping("/docs")
class DocsController {

    private ProjectMetadataService projectService;

    @Autowired
    public DocsController(ProjectMetadataService service) {
        this.projectService = service;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listDocumentationTypes() {
        return "docs/index";
    }

    @RequestMapping(value = "/reference", method = { GET, HEAD })
    public String listProjects(Model model) {
        model.addAttribute("activeProjects", projectService.getProjectsForCategory("active"));
        model.addAttribute("atticProjects", projectService.getProjectsForCategory("attic"));
        model.addAttribute("incubatorProjects", projectService.getProjectsForCategory("incubator"));
        return "docs/reference";
    }

}
