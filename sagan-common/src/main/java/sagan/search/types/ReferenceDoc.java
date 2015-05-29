package sagan.search.types;

public class ReferenceDoc extends SearchEntry {

    private boolean current;
    private String version;
    private String projectId;

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String getType() {
        return SearchType.REFERENCE_DOC.toString();
    }
}
