package sagan.tools;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Spring Tools supported platform
 */
@Entity
@SuppressWarnings("serial")
public class SpringToolsPlatform {

	@Id
	private String id;

	@ElementCollection
	private List<SpringToolsDownload> downloads = new ArrayList<>();

	@SuppressWarnings("unused")
	private SpringToolsPlatform() {
	}

	public SpringToolsPlatform(String id) {
		this.id = id;
	}

	public SpringToolsPlatform(String id, List<SpringToolsDownload> downloads) {
		this.id = id;
		this.downloads = downloads;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<SpringToolsDownload> getDownloads() {
		return downloads;
	}

	public void setDownloads(List<SpringToolsDownload> downloads) {
		this.downloads = downloads;
	}

	@Override
	public String toString() {
		return "SpringToolsPlatform{" +
				"id='" + id + '\'' +
				", downloads=" + downloads +
				'}';
	}
}
