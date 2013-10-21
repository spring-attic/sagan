package sagan.guides.web;

import sagan.guides.service.GuidesService;
import sagan.util.web.NavSection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping("/guides/tutorials")
@NavSection("guides")
public class TutorialsController {

    private GuidesService service;

    @Autowired
    public TutorialsController(GuidesService service) {
        this.service = service;
    }

    @RequestMapping(value = "/{tutorialId}", method = { GET, HEAD })
    public String viewTutorial(@PathVariable String tutorialId, Model model) {
        model.addAttribute("tutorialId", tutorialId);
        model.addAttribute("tutorial", service.loadTutorial(tutorialId));
        return "guides/tutorial/show";
    }

    @RequestMapping(value = "/{tutorialId}/{pagePath}", method = { GET, HEAD })
    public String viewTutorialPage(@PathVariable String tutorialId, @PathVariable String pagePath, Model model) {
        model.addAttribute("tutorialId", tutorialId);
        model.addAttribute("tutorial", service.loadTutorialPage(tutorialId, pagePath));
        return "guides/tutorial/show";
    }

    @RequestMapping(value = "/{tutorialId}/images/{name:[a-zA-Z0-9._-]+}", method = { GET, HEAD })
    public ResponseEntity<byte[]> loadImage(@PathVariable String tutorialId, @PathVariable("name") String imageName) {
        byte[] image = this.service.loadTutorialImage(tutorialId, imageName);
        return new ResponseEntity<>(image, HttpStatus.OK);
    }

}
