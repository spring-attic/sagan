package sagan.projects;

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

    public static ProjectRepository get(String versionName) {
        if (versionName.contains("RELEASE")) {
            return null;
        }

        if (versionName.contains("SNAPSHOT")) {
            return new ProjectRepository("spring-snapshots", "Spring Snapshots", "http://repo.spring.io/snapshot", true);
        }

        return new ProjectRepository("spring-milestones", "Spring Milestones", "http://repo.spring.io/milestone", false);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getSnapshotsEnabled() {
        return snapshotsEnabled;
    }
}
