package sagan.site.tools;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Spring Tools download information for a given platform and operating system
 */
@Embeddable
@SuppressWarnings("serial")
public class SpringToolsDownload {

	@Column(nullable = false)
	private String downloadUrl;

	@Column(nullable = false)
	private String variant;

	@Column(nullable = false)
	private String label;

	@Column(nullable = false)
	private String version;

	public SpringToolsDownload() {
		
	}

	public SpringToolsDownload(String downloadUrl, String variant, String label) {
		this.downloadUrl = downloadUrl;
		this.variant = variant;
		this.label = label;
	}

	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getVariant() {
		return this.variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "SpringToolsDownload{" +
				"downloadUrl='" + downloadUrl + '\'' +
				", variant='" + variant + '\'' +
				", label='" + label + '\'' +
				", version='" + version + '\'' +
				'}';
	}
}
