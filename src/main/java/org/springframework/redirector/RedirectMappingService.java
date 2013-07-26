package org.springframework.redirector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.bootstrap.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ConfigurationProperties(path = "${redirectMappings.path:classpath:redirect_mappings.yml}")
public class RedirectMappingService {

	private static Log logger = LogFactory.getLog(RedirectMappingService.class);

	private final List<MappingEntry> mappings = new ArrayList<>();

	private Map<String, String> redirectMappings;

	public List<MappingEntry> getMappings() {
		return mappings;
	}

	public String redirectUrlFor(String path) {
		if (redirectMappings == null) {
			buildMappings();
		}
		return redirectMappings.get(path.replaceAll("/$", ""));
	}

	private void buildMappings() {
		redirectMappings = new HashMap<>();
		logger.debug("Loading Mappings");
		for (MappingEntry mapping : mappings) {
			URL url;
			String oldUrl = mapping.getOldUrl().replaceAll("/$", "");

			try {
				url = new URL(oldUrl);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			logger.debug(String.format("Loaded mapping: %s => %s", oldUrl, mapping.getNewUrl()));
			redirectMappings.put(url.getPath(), mapping.getNewUrl());
		}
		logger.debug(String.format("%d Mappings loaded", redirectMappings.size()));
	}

}
