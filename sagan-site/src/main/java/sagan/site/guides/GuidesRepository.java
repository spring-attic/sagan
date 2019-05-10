package sagan.site.guides;

import java.util.Arrays;
import java.util.Optional;

import sagan.projects.Project;

public interface GuidesRepository<T extends Guide> {

	GuideHeader[] findAll();

	Optional<GuideHeader> findGuideHeaderByName(String name);

	Optional<T> findByName(String name);

	default GuideHeader[] findByProject(Project project) {
		return Arrays.stream(findAll())
				.filter(guide -> guide.getProjects().contains(project.getId()))
				.toArray(GuideHeader[]::new);
	}

}
