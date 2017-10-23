package sagan.guides.support;

import java.util.List;
import java.util.stream.Collectors;

import sagan.guides.GettingStartedGuide;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Controller that handles requests for getting started guide docs at /guides/gs.
 *
 * @see sagan.guides.support.TutorialController
 * @see sagan.guides.support.UnderstandingDocController
 */
@Controller
@Navigation(Section.GUIDES)
@RequestMapping(value = "/guides/gs", method = { GET, HEAD })
class GettingStartedGuideController {

    private final GettingStartedGuides guides;

	private final ProjectMetadataService projectMetadataService;

    @Autowired
    public GettingStartedGuideController(GettingStartedGuides guides, ProjectMetadataService projectMetadataService) {
        this.guides = guides;
        this.projectMetadataService = projectMetadataService;
    }

    @RequestMapping("/{guide}")
    public String viewGuide(@PathVariable String guide, Model model) {
        GettingStartedGuide gsGuide = guides.find(guide);
        List<Project> projects = gsGuide.getProjects()
				.stream().map(name -> projectMetadataService.getProject(name))
				.collect(Collectors.toList());
        model.addAttribute("guide", gsGuide);
		model.addAttribute("projects", projects);
        model.addAttribute("description", "this guide is designed to get you productive as quickly as " +
                "possible and using the latest Spring project releases and techniques as recommended by the Spring team");
        return "guides/gs/guide";
    }

    @RequestMapping("/{guide}/images/{image:[a-zA-Z0-9._-]+}")
    public ResponseEntity<byte[]> loadImage(@PathVariable String guide, @PathVariable String image) {
        return ResponseEntity.ok(guides.loadImage(guides.find(guide), image));
    }
}
