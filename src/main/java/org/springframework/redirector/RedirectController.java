package org.springframework.redirector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RedirectController {

	private static Log logger = LogFactory.getLog(RedirectController.class);
	private final RedirectMappingService redirectMappingService;

	@Autowired
	public RedirectController(RedirectMappingService redirectMappingService) {
		this.redirectMappingService = redirectMappingService;
	}

	@RequestMapping(value = {"/**"})
	public ModelAndView redirector(HttpServletRequest request) {
		String requestedUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		ModelAndView redirectModelAndView = new ModelAndView();
		String redirectedUrl = redirectMappingService.redirectUrlFor(requestedUrl);
		if (redirectedUrl == null) {
			redirectedUrl = "http://sagan.cfapps.io";
			logger.info(String.format("No mapping found for %s, redirecting to default site: %s", requestedUrl, redirectedUrl));
		}

		logger.info(String.format("REDIRECT: %s => %s", requestedUrl, redirectedUrl));
		RedirectView redirectView = new RedirectView(redirectedUrl);
		redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		redirectModelAndView.setView(redirectView);
		return redirectModelAndView;
	}
}
