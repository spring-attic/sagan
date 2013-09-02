package io.spring.site.web.docs;

import io.spring.site.domain.projects.ProjectMetadataService;
import io.spring.site.web.NavSection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/docs")
@NavSection("docs")
public class DocsController {

    private ProjectMetadataService service;

    @Autowired
    public DocsController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listProjects(Model model) {
        model.addAttribute("activeProjects", service.getProjectsForCategory("active"));
        model.addAttribute("atticProjects", service.getProjectsForCategory("attic"));
        model.addAttribute("incubatorProjects", service.getProjectsForCategory("incubator"));
        model.addAttribute("otherProjects", service.getProjectsForCategory("other"));
        return "docs/index";
    }

}
