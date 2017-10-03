/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sagan.support.github;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Greg Turnquist
 */
public class PaginationUtils {

	private static final Logger log = LoggerFactory.getLogger(PaginationUtils.class);

	static public <T> List<T> readResults(Function<String, ResponseEntity<String>> githubRequest, String requestUrl, ObjectMapper objectMapper, Class<T> clazz) {

		Assert.isTrue(requestUrl.contains("&page=") || requestUrl.contains("?page="),
			"Navigating all the pages requires a page param or Github won't return a 'Link' header -> suggest ?page=1 or &page=1");
		List<T> results = new ArrayList<>();

		String url = requestUrl;

		while (!url.equals("")) {

			log.debug("Fetching " + url);

			// Make a github request, but also grab any headers
			ResponseEntity<String> pageOfJson = githubRequest.apply(url);

			// Deserialize the JSON into a {@literal List<T>}, then add to the final collection
			try {
				JavaType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);

				List<T> values = objectMapper.readValue(pageOfJson.getBody(), collectionType);

				results.addAll(values);
			} catch (IOException e) {
				log.error(e.getMessage());
				throw new RuntimeException(e);
			}

			List<String> linkHeader = pageOfJson.getHeaders().getOrDefault("Link", new ArrayList<>());

			if (linkHeader.isEmpty()) {
				url = ""; // No more links to navigate
			} else {
				Map<String, GithubLinkHeader> links = GithubLinkHeader.of(linkHeader.get(0));

				log.trace("Links? " + links);

				url = links.getOrDefault("next", GithubLinkHeader.NONE).getUrl();

				if (log.isTraceEnabled()) {
					if (url.equals("")) {
						log.trace("That looks like the last page.");
					} else {
						log.trace("Appears to be another page.");
					}
				}
			}
		}

		return results;
	}

}
