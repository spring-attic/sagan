package sagan.tools;

import java.util.List;

public class EclipsePlatform {
    private final List<EclipseRelease> products;
    private String name;

    public EclipsePlatform(String name, List<EclipseRelease> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public List<EclipseRelease> getReleases() {
        return products;
    }
}
