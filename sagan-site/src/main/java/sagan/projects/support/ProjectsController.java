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
import sagan.support.ResourceNotFoundException;
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
@RequestMapping("/projects")
@Navigation(Section.PROJECTS)
class ProjectsController {

    private ProjectMetadataService projectMetadataService;
    private List<ProjectGuidesRepository> projectGuidesRepositories;

    @Autowired
    public ProjectsController(ProjectMetadataService projectMetadataService,
                             List<ProjectGuidesRepository> projectGuidesRepositories) {
        this.projectMetadataService = projectMetadataService;
        this.projectGuidesRepositories = projectGuidesRepositories;
    }

	@RequestMapping(method = { GET, HEAD })
	public String listProjects(Model model) {
		model.addAttribute("projectMetadata", projectMetadataService);
		return "projects/index";
	}

	@RequestMapping(value = "/{projectName}", method = { GET, HEAD })
    public String showProject(Model model, @PathVariable String projectName) {
		Project project = projectMetadataService.getProject(projectName);
		if (project == null) {
			throw new ResourceNotFoundException("project " + projectName);
		}
        List<Project> projects = this.projectMetadataService.getActiveTopLevelProjects();
        model.addAttribute("selectedProject", project);
        model.addAttribute("projectStackOverflow", stackOverflowUrl(project));
        model.addAttribute("projects", projects);
        model.addAttribute("currentRelease", project.getMostCurrentRelease());
        model.addAttribute("otherReleases", project.getNonMostCurrentReleases());

        List<GuideMetadata> guides = projectGuidesRepositories.stream()
                .flatMap(repo -> repo.findByProject(project).stream())
                .collect(Collectors.toList());
        model.addAttribute("guides", guides);

        return "projects/show";
    }

    private List<Project> getProjectsForSidebar() {
        return projectMetadataService.getProjectsForCategory("active")
                .stream()
                .filter(project -> project.isTopLevelProject())
                .collect(Collectors.toList());
    }

    private String stackOverflowUrl(Project project) {
        return "https://stackoverflow.com/questions/tagged/"
                + Joiner.on("+or+").join(project.getStackOverflowTagList());
    }

}
