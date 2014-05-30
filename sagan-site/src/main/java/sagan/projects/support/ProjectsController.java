package sagan.projects.support;

import sagan.support.nav.Navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles requests for the projects overview page at /projects.
 */
@Controller
@RequestMapping("/projects")
@Navigation(Section.PROJECTS)
class ProjectsController {

    private ProjectMetadataService projectMetadataService;

    @Autowired
    public ProjectsController(ProjectMetadataService projectMetadataService) {
        this.projectMetadataService = projectMetadataService;
    }

    @RequestMapping(method = { GET, HEAD })
    public String listProjects(Model model) {
        model.addAttribute("projectMetadata", projectMetadataService);
        return "projects/index";
    }

}
