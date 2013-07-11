package org.springframework.site.domain.tools.xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict=false)
public class ToolSuiteXml {

	@ElementList(name="release", type = Release.class, inline = true)
	private List<Release> releases;

	public List<Release> getReleases() {
		return releases;
	}

	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}
}
