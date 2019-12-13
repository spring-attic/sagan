/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sagan.projects;

import java.util.stream.Stream;

/**
 * @author Haytham Mohamed
 **/

public enum ProjectGroup {

	MICROSERVICES("microservices")
	,REACTIVE("reactive")
	,EVENT("event driven")
	,CLOUD("cloud")
	,WEB("webapps")
	,SERVERLESS("serverless")
	,STREAMS("streams")
	,BATCH("batch")
	;

	private String value;

	private ProjectGroup(String value) { this.value = value.toLowerCase(); }

	public String getValue() { return this.value; }

	public static ProjectGroup getGroup(String _value) {
		return Stream.of(ProjectGroup.values())
				.filter(e -> e.getValue().toLowerCase().equals(_value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

}
