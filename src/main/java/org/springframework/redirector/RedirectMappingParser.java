package org.springframework.redirector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedirectMappingParser {
	private static Log logger = LogFactory.getLog(RedirectMappingParser.class);


	public Map<String, String> parseMappings(InputStream mappingsYaml) {
		logger.debug("Loading Mappings");
		Map<String, String> redirectMappings = new HashMap<>();

		Map<String, List<Map<String, String>>> data = (Map<String, List<Map<String, String>>>) new Yaml().load(mappingsYaml);
		for (Map<String, String> mapping : data.get("mappings")) {
			String newUrl = mapping.get("new_url");
			redirectMappings.put(buildOldUrl(mapping, newUrl), newUrl);
		}
		logger.debug(String.format("%d Mappings loaded", redirectMappings.size()));
		return redirectMappings;
	}

	private String buildOldUrl(Map<String, String> mapping, String newUrl) {
		String oldUrl = mapping.get("old_url").replaceAll("/$", "");
		URL url;
		try {
			url = new URL(oldUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		logger.debug(String.format("Loaded mapping: %s => %s", oldUrl, newUrl));
		return url.getPath();
	}
}
