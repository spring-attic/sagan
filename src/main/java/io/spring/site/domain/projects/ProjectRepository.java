package io.spring.site.domain.projects;

public class ProjectRepository {
    private String id;
    private String name;
    private String url;
    private Boolean snapshotsEnabled;

    ProjectRepository(String id, String name, String url, Boolean snapshotsEnabled) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.snapshotsEnabled = snapshotsEnabled;
    }

    static ProjectRepository get(String versionName) {
        if (versionName.contains("RELEASE")) {
            return null;
        }

        if (versionName.contains("SNAPSHOT")) {
            return new ProjectRepository("spring-snapshots", "Spring Snapshots",
                    "http://repo.springsource.org/snapshot", true);
        }

        return new ProjectRepository("spring-milestones", "Spring Milestones",
                "http://repo.springsource.org/milestone", false);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public Boolean getSnapshotsEnabled() {
        return this.snapshotsEnabled;
    }
}
