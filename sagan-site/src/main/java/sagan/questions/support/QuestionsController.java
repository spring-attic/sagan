package sagan.questions.support;

import sagan.projects.support.ProjectMetadataService;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Navigation(Section.QUESTIONS)
class QuestionsController {

    private final ProjectMetadataService projectMetadata;
    private final StackOverflowClient stackOverflow;

    @Autowired
    public QuestionsController(ProjectMetadataService projectMetadata, StackOverflowClient stackOverflow) {
        this.projectMetadata = projectMetadata;
        this.stackOverflow = stackOverflow;
    }

    @RequestMapping("/questions")
    public String show(Model model) {

        model.addAttribute("questions", stackOverflow.searchForQuestionsTagged("spring"));

        model.addAttribute("projects", projectMetadata.getProjects().stream()
                .filter(project -> !project.getCategory().equals("attic"))
                .filter(project -> !project.getStackOverflowTagList().isEmpty())
                .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                .collect(Collectors.toList()));

        return "questions/index";
    }

}
