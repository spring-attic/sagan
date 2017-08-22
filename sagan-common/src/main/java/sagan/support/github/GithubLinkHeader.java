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

import java.util.Map;
import java.util.TreeMap;

/**
 * Value object to represent a link returned from a Github request.
 * 
 * @author Greg Turnquist
 */
public class GithubLinkHeader {

	private final String rel;
	private final String url;

	public static GithubLinkHeader NONE = new GithubLinkHeader("", "");

	/**
	 * Use the static helpers to construct new {@link GithubLinkHeader}s instead of the constructor call.
	 * 
	 * @param rel
	 * @param url
	 */
	private GithubLinkHeader(String rel, String url) {
		this.rel = rel.trim();
		this.url = url.trim();
	}

	public String getRel() {
		return rel;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "GithubLinkHeader{'" + rel + "' => '" + url + "'}";
	}

	/**
	 * Parses a link header returned by Github into a {@link Map} of {@link GithubLinkHeader}.
	 * 
	 * @param header
	 * @return
	 */
	public static Map<String, GithubLinkHeader> of(String header) {
		Map<String, GithubLinkHeader> links = new TreeMap<>();

		for (String link : header.split(",")) {
			String[] parts = link.split(";");
			String url = parts[0].trim().substring(1, parts[0].trim().length() - 1);

			String[] relParts = parts[1].split("=");
			String rel = relParts[1].substring(1, relParts[1].length() - 1);

			GithubLinkHeader linkHeader = new GithubLinkHeader(rel, url);

			links.put(linkHeader.getRel(), linkHeader);
		}
		return links;
	}
}
