package sagan.guides.support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

@Controller
@Navigation(Section.GUIDES)
public class DeployToPwsController {

    @RequestMapping("/guides/deploy-to-pws")
    //@RequestMapping("/pws")

    public String deployToPws() {

        return "guides/pws";
    }

}
