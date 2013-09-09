
package io.spring.site.web.commingsoon;

import org.springframework.boot.actuate.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ComingSoonController implements ErrorController {

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping("/error")
	public String errorRedirect() {
		return "redirect:/coming-soon";
	}

	@RequestMapping("/coming-soon")
	public String commingSoon() {
		return "coming-soon";
	}

}
