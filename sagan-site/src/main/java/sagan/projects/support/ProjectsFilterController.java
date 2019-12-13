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
package sagan.projects.support;

import java.util.Collection;

import sagan.projects.Project;
import sagan.support.JsonPController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * @author Haytham Mohamed
 **/

@JsonPController
@RequestMapping("/projects_filter")
public class ProjectsFilterController {

	private final ProjectMetadataService projectMetadataService;

	public ProjectsFilterController(ProjectMetadataService service) {
		this.projectMetadataService = service;
	}

	@RequestMapping(value="/group/{tag}", method = { GET, HEAD })
	public Collection<Project> byGroup(@PathVariable("tag") String tag) {
		Collection<Project> projects = projectMetadataService.getProjectsInGroup(tag);
		projects.stream().forEach(System.out::println);
		return projects;
	}
}
