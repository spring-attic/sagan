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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author Haytham Mohamed
 **/

@Converter(autoApply = true)
public class ProjectGroupConverter implements AttributeConverter<Set<ProjectGroup>, String> {

	@Override
	public String convertToDatabaseColumn(Set<ProjectGroup> group) {
		if (group == null || group.isEmpty()) {
			return null;
		}
		return group.stream()
				.map(e -> e.getValue().toLowerCase())
				.collect(Collectors.joining(","));
	}

	@Override
	public Set<ProjectGroup> convertToEntityAttribute(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return Arrays.stream(value.split(","))
				.map(s -> s.toLowerCase())
				.map(s -> ProjectGroup.getGroup(s))
				.collect(Collectors.toSet());
	}
}
