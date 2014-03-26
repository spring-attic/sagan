package sagan.tools;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class Release {

    @Attribute
    private String name;

	@Attribute(required = false)
	private String whatsnew;

    @ElementList(name = "download", type = Download.class, inline = true)
    private List<Download> downloads;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getWhatsnew() {
		return whatsnew;
	}

	public void setWhatsnew(String whatsnew) {
		this.whatsnew = whatsnew;
	}

	public List<Download> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<Download> downloads) {
        this.downloads = downloads;
    }
}
