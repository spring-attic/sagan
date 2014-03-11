package sagan.tools.support;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import sagan.tools.Release;

@Root(strict = false)
class ToolSuiteXml {

    @ElementList(name = "release", type = Release.class, inline = true)
    private List<Release> releases;

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
}
