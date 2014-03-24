package sagan.projects;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProjectRepository {
    private static final ProjectRepository SNAPSHOT = new ProjectRepository("spring-snapshots", "Spring Snapshots",
            "http://repo.spring.io/snapshot", true);
    private static final ProjectRepository MILESTONE = new ProjectRepository("spring-milestones", "Spring Milestones",
            "http://repo.spring.io/milestone", false);

    @Id
    private String id;
    private String name;
    private String url;
    private Boolean snapshotsEnabled;

    @SuppressWarnings("unused")
    private ProjectRepository() {
    }

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
            return SNAPSHOT;
        }

        return MILESTONE;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSnapshotsEnabled(Boolean snapshotsEnabled) {
        this.snapshotsEnabled = snapshotsEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof ProjectRepository))
            return false;

        ProjectRepository that = (ProjectRepository) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProjectRepository{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", snapshotsEnabled=" + snapshotsEnabled +
                '}';
    }
}
