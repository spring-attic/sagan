package sagan.tools;

import java.util.List;

public class EclipseRelease {
    private final String name;
    private List<EclipsePackage> packages;
    private String eclipseVersion;

    public EclipseRelease(String name, String eclipseVersion, List<EclipsePackage> packages) {
        this.name = name;
        this.eclipseVersion = eclipseVersion;
        this.packages = packages;
    }

    public String getName() {
        return name;
    }

    public String getEclipseVersion() {
        return eclipseVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        EclipseRelease that = (EclipseRelease) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public List<EclipsePackage> getPackages() {
        return packages;
    }
}
