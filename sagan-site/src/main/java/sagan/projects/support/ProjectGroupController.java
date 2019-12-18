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

import sagan.projects.ProjectGroup;
import sagan.support.JsonPController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Haytham Mohamed
 **/

@JsonPController
@RequestMapping("/project_groups")
public class ProjectGroupController {

	private final ProjectGroupService service;

	public ProjectGroupController(ProjectGroupService service) {
		this.service = service;
	}

	// List all groups
	@RequestMapping(method = { GET, HEAD })
	public Collection<ProjectGroup> all() {
		return service.getAll();
	}

	// add a group
	@RequestMapping(method = { POST })
	public ProjectGroup add(@RequestBody ProjectGroup group) {
		return service.add(group);
	}

	// get group by id
	@RequestMapping(value = "/{id}", method = { GET, HEAD })
	public ProjectGroup byId(@PathVariable("id") Long id) {
		return service.getById(id);
	}

	// delete group by id
	@RequestMapping(value = "/{id}", method = DELETE)
	public ProjectGroup deleteById(@PathVariable("id") Long id) {
		return service.deleteById(id);
	}

	// update group by id
	@RequestMapping(value = "/{id}", method = PUT)
	public ProjectGroup updateById(@PathVariable("id") Long id,
			@RequestBody ProjectGroup group) {
		return service.updateById(id, group);
	}

	// search group match an ignored case name
	@RequestMapping(value = "/names/{name}", method = { GET, HEAD })
	public Collection<ProjectGroup> byName(@PathVariable("name") String name) {
		return service.getByNameIgnoreCase(name);
	}

}
