package sagan.guides.web;

import sagan.guides.service.GuidesService;
import sagan.util.web.NavSection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/guides")
@NavSection("guides")
public class GuidesController {

    private GuidesService service;

    @Autowired
    public GuidesController(GuidesService service) {
        this.service = service;
    }

    @RequestMapping(value = "", method = { GET, HEAD })
    public String index(Model model) {
        model.addAttribute("guides", service.listGettingStartedGuidesWithoutContent());
        model.addAttribute("tutorials", service.listTutorialsWithoutContent());
        return "guides/index";
    }
}
