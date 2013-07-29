package org.springframework.redirector;

import java.util.Map;

public class RedirectMappingService {

	private final Map<String, String> redirectMappings;

	public RedirectMappingService(Map<String, String> redirectMappings) {
		this.redirectMappings = redirectMappings;
	}

	public String redirectUrlFor(String path) {
		return this.redirectMappings.get(path.replaceAll("/$", ""));
	}

}
