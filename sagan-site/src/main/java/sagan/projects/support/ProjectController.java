package sagan.projects.support;

import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.guides.GuideMetadata;
import sagan.guides.support.ProjectGuidesRepository;
import sagan.projects.Project;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

import java.util.List;
import java.util.stream.Collectors;

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
    private List<ProjectGuidesRepository> projectGuidesRepositories;

    @Autowired
    public ProjectController(ProjectMetadataService projectMetadataService,
                             List<ProjectGuidesRepository> projectGuidesRepositories) {
        this.projectMetadataService = projectMetadataService;
        this.projectGuidesRepositories = projectGuidesRepositories;
    }

    @RequestMapping(value = "/{projectName}", method = { GET, HEAD })
    public String showProject(Model model, @PathVariable String projectName) {
        Project project = projectMetadataService.getProject(projectName);
        model.addAttribute("project", project);
        model.addAttribute("projectStackOverflow", stackOverflowUrl(project));
        model.addAttribute("projects", projectMetadataService.getProjectsForCategory("active"));

        List<GuideMetadata> guides = projectGuidesRepositories.stream()
				.flatMap(repo -> repo.findByProject(project).stream())
                .collect(Collectors.toList());

        model.addAttribute("guides", guides);

        return "projects/show";
    }

    private String stackOverflowUrl(Project project) {
        return "https://stackoverflow.com/questions/tagged/"
                +  Joiner.on("+or+").join(project.getStackOverflowTagList());
    }

}
