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
		this.rel = rel;
		this.url = url;
	}

	public String getRel() {
		return rel;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "GithubLinkHeader{'" + this.rel + "' => '" + this.url + "'}";
	}

	/**
	 * Parse a link header returned by Github into a {@link Map} or {@link GithubLinkHeader}.
	 * 
	 * @param header
	 * @return
	 */
	public static Map<String, GithubLinkHeader> of(String header) {
		Map<String, GithubLinkHeader> links = new TreeMap<>();

		for (String link : header.split(",")) {
			String[] parts = link.split(";");
			String url = unwrap(parts[0].trim());

			String[] relParts = parts[1].split("=");
			String rel = unwrap(relParts[1].trim());

			links.put(rel, new GithubLinkHeader(rel, url));
		}

		return links;
	}

	/**
	 * Get the string enclosed by some other character, e.g. {@literal <Foo>} returns {@literal Foo} and {@literal "Bar"} returns {@literal Bar}
	 *
	 * @param token
	 * @return
	 */
	private static String unwrap(String token) {
		return token.substring(1, token.length()-1);
	}
}
