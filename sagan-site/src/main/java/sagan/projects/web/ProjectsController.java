package sagan.projects.web;

import sagan.projects.service.ProjectMetadataService;
import sagan.support.web.NavSection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/projects")
@NavSection("projects")
public class ProjectsController {

    private ProjectMetadataService service;

    @Autowired
    public ProjectsController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String listProjects(Model model) {
        model.addAttribute("projectMetadata", service);
        return "projects/index";
    }

}
