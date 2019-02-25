package sagan.tools;

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

	public SpringToolsDownload() {
		
	}

	public SpringToolsDownload(String downloadUrl, String variant, String label) {
		this.downloadUrl = downloadUrl;
		this.variant = variant;
		this.label = label;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "SpringToolsDownload{" +
				"downloadUrl='" + downloadUrl + '\'' +
				", variant='" + variant + '\'' +
				'}';
	}
}
