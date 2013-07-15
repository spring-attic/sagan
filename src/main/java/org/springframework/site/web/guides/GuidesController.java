package org.springframework.site.web.guides;

import org.springframework.site.web.NavSection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

@Controller
@RequestMapping("/guides")
@NavSection("guides")
public class GuidesController {

	@RequestMapping(value = "", method = { GET, HEAD })
	public String index() {
		return "guides/index";
	}
}
