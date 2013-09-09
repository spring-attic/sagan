
package io.spring.site.web.commingsoon;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ComingSoonController  {

	@RequestMapping("/coming-soon")
	public String commingSoon() {
		return "coming-soon";
	}

}
