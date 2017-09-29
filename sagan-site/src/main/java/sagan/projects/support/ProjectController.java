package sagan.projects.support;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/project")
@Navigation(Section.PROJECTS)
class ProjectController {

    private ProjectMetadataService projectMetadataService;

    @Autowired
    public ProjectController(ProjectMetadataService projectMetadataService) {
        this.projectMetadataService = projectMetadataService;
    }

    @RequestMapping(value = "/{projectName}", method = { GET, HEAD })
    public String showProject(Model model, @PathVariable String projectName) {
        model.addAttribute("project", projectMetadataService.getProject(projectName));
        return "projects/show";
    }

}
