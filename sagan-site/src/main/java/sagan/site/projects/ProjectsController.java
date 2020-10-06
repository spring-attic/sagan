package sagan.site.projects;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sagan.site.guides.GettingStartedGuides;
import sagan.site.guides.Topicals;
import sagan.site.guides.Tutorials;
import sagan.site.support.ResourceNotFoundException;
import sagan.site.support.nav.Navigation;
import sagan.site.support.nav.Section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that handles requests for the projects overview page at /projects.
 */
@Controller
@RequestMapping("/projects")
@Navigation(Section.PROJECTS)
class ProjectsController {

    private ProjectMetadataService projectMetadataService;
    private GettingStartedGuides gsGuides;
    private Tutorials tutorials;
    private Topicals topicals;

    @Autowired
    public ProjectsController(ProjectMetadataService projectMetadataService,
                              GettingStartedGuides gsGuides, Tutorials tutorials, Topicals topicals) {
        this.projectMetadataService = projectMetadataService;
        this.gsGuides = gsGuides;
        this.tutorials = tutorials;
        this.topicals = topicals;
    }

	@GetMapping
	public String listProjects(Model model) {
		List<Project> projects = this.projectMetadataService.fetchTopLevelProjectsWithGroups();
		model.addAttribute("groups", this.projectMetadataService.getAllGroups());
		model.addAttribute("featured",
				projects.stream().filter(p -> p.getDisplay().isFeatured()).collect(Collectors.toList()));
		projects
				.stream()
				.collect(Collectors.groupingBy(Project::getStatus))
				.forEach((status, proj) -> {
					model.addAttribute(status.name(), proj);
				});
		return "projects/index";
	}

	@GetMapping("/{projectName}")
    public String showProject(Model model, @PathVariable String projectName) {
		Project project = projectMetadataService.fetchFullProject(projectName);
		if (project == null) {
			throw new ResourceNotFoundException("project " + projectName);
		}
        List<Project> projects = this.projectMetadataService.fetchActiveProjectsTree();

		model.addAttribute("now", LocalDate.now());
        model.addAttribute("project", project);
        model.addAttribute("projectStackOverflow", stackOverflowUrl(project));
        model.addAttribute("projects", projects);
        model.addAttribute("currentRelease", project.getCurrentRelease());
        model.addAttribute("otherReleases", project.getNonCurrentReleases());

        model.addAttribute("guides", Arrays.asList(gsGuides.findByProject(project)));
        model.addAttribute("topicals", Arrays.asList(topicals.findByProject(project)));
        model.addAttribute("tutorials", Arrays.asList(tutorials.findByProject(project)));

        return "projects/show";
    }

    private String stackOverflowUrl(Project project) {
		String tags = project.getStackOverflowTags() != null ? project.getStackOverflowTags() : "spring";
		return "https://stackoverflow.com/questions/tagged/"
                + String.join("+or+", tags.split(","));
    }

}
