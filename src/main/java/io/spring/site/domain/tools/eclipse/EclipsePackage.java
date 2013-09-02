package io.spring.site.domain.tools.eclipse;


import io.spring.site.domain.tools.toolsuite.Architecture;

import java.util.List;

public class EclipsePackage {
    private String name;
    private List<Architecture> architectures;

    public EclipsePackage(String name, List<Architecture> architectures) {
        this.name = name;
        this.architectures = architectures;
    }

    public String getName() {
        return name;
    }

    public List<Architecture> getArchitectures() {
        return architectures;
    }
}
