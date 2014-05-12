package sagan.questions.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QuestionsController {

    private ProjectMetadataService service;

    @Autowired
    public QuestionsController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping("/questions")
    public String show(Model model) {

        model.addAttribute("projects", projectsWithStackOverflowTags());
        return "questions/index";
    }

    private List<Project> projectsWithStackOverflowTags() {
        return service.getProjects().stream()
                .filter(project -> !project.getCategory().equals("attic"))
                .filter(project -> !project.getStackOverflowTagList().isEmpty())
                .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                .collect(Collectors.toList());
    }
}
