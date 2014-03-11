package sagan.tools;

import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

public class ToolSuitePlatform {
    private String name;
    private List<EclipseVersion> eclipseVersions;

    public ToolSuitePlatform(String name, List<EclipseVersion> eclipseVersions) {
        this.name = capitalize(name);
        this.eclipseVersions = eclipseVersions;
    }

    public String getName() {
        return name;
    }

    public List<EclipseVersion> getEclipseVersions() {
        return eclipseVersions;
    }
}
