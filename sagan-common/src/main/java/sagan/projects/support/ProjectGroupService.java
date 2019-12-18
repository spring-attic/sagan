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

import java.util.List;

import sagan.projects.ProjectGroup;

import org.springframework.stereotype.Service;

/**
 * @author Haytham Mohamed
 **/

@Service
public class ProjectGroupService {

	private final ProjectGroupRepository repository;

	public ProjectGroupService(ProjectGroupRepository repository) {
		this.repository = repository;
	}

	public List<ProjectGroup> getAll() {
		return repository.findAll();
	}

	public ProjectGroup getById(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("group name should not be null");
		}
		return repository.findOne(id);
	}

	public ProjectGroup updateById(Long id, ProjectGroup group) {
		if (group == null) {
			throw new IllegalArgumentException("group argument should not be null");
		}
		ProjectGroup grp = this.getById(id);
		if (grp == null) {
			throw new RuntimeException("group with id " + id + " is not found");
		}
		grp.setName(group.getName());
		return repository.save(grp);
	}

	public ProjectGroup deleteById(Long id) {
		ProjectGroup group = this.getById(id);
		if (group == null) {
			throw new RuntimeException("No group with id " + id + " found to delete");
		}
		repository.delete(id);
		return group;
	}

	public List<ProjectGroup> getByNameIgnoreCase(String name) {
		if (name == null) {
			throw new IllegalArgumentException("group name should not be null");
		}
		List<ProjectGroup> group = repository.findByNameIgnoreCase(name);
		return group;
	}

	public ProjectGroup add(ProjectGroup group) {
		if (group == null) {
			throw new IllegalArgumentException("group argument should not be null");
		}
		List<ProjectGroup> grp = repository.findByNameIgnoreCase(group.getName());
		if (grp != null && grp.size() > 0) {
			throw new RuntimeException("A group with name " + group.getName() + " already exists");
		}
		return repository.save(group);
	}

}
