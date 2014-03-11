package sagan.docs.support;

import sagan.projects.Project;
import sagan.projects.service.ProjectMetadataService;
import sagan.support.web.NavSection;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/docs")
@NavSection("docs")
class DocsController {

    private ProjectMetadataService service;

    @Autowired
    public DocsController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listProjects(Model model) {
        model.addAttribute("activeProjects", nonAggregatorsForCategory("active"));
        model.addAttribute("atticProjects", nonAggregatorsForCategory("attic"));
        model.addAttribute("incubatorProjects", nonAggregatorsForCategory("incubator"));
        return "docs/index";
    }

    private List<Project> nonAggregatorsForCategory(String category) {
        List<Project> nonAggregators = new ArrayList<>();
        for (Project project : service.getProjectsForCategory(category)) {
            if (!project.isAggregator()) {
                nonAggregators.add(project);
            }
        }
        return nonAggregators;
    }
}
