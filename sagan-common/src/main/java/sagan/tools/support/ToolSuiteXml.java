package sagan.tools.support;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import sagan.tools.Release;

import java.util.List;

class ToolSuiteXml {

    @JacksonXmlProperty(localName = "release")
    @JacksonXmlElementWrapper(useWrapping=false)
    private List<Release> releases;

    @JacksonXmlProperty(localName = "other")
    @JacksonXmlElementWrapper(useWrapping=false)
    private List<Release> others;

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
}
