package sagan.guides.support;

import java.util.List;

import sagan.guides.GuideMetadata;
import sagan.projects.Project;

public interface ProjectGuidesRepository {
    List<GuideMetadata> findByProject(Project project);
}
