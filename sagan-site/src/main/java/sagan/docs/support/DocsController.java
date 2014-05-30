package sagan.docs.support;

import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.nav.Navigation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles requests for the /docs documentation page.
 */
@Controller
@RequestMapping("/docs")
@Navigation(Section.DOCS)
class DocsController {

    private ProjectMetadataService projectService;

    @Autowired
    public DocsController(ProjectMetadataService service) {
        this.projectService = service;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listProjects(Model model) {
        model.addAttribute("activeProjects", nonAggregatorsForCategory("active"));
        model.addAttribute("atticProjects", nonAggregatorsForCategory("attic"));
        model.addAttribute("incubatorProjects", nonAggregatorsForCategory("incubator"));
        return "docs/index";
    }

    private List<Project> nonAggregatorsForCategory(String category) {
        return projectService.getProjectsForCategory(category).stream()
                .filter(project -> !project.isAggregator())
                .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                .collect(Collectors.toList());
    }
}
