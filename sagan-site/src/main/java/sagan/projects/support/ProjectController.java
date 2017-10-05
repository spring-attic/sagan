package sagan.projects.support;

import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.projects.Project;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

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
        Project project = projectMetadataService.getProject(projectName);
        model.addAttribute("project", project);
        model.addAttribute("projectStackOverflow", stackOverflowUrl(project));
        model.addAttribute("projects", projectMetadataService.getProjectsForCategory("active"));
        return "projects/show";
    }

    private String stackOverflowUrl(Project project) {
        return "https://stackoverflow.com/questions/tagged/"
                +  Joiner.on("+or+").join(project.getStackOverflowTagList());
    }

}
