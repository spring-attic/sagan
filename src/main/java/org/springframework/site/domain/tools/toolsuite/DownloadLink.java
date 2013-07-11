package org.springframework.site.domain.tools.toolsuite;

public class DownloadLink {
	private String url;
	private String fileType;
	private String fileSize;

	public DownloadLink(String url, String fileType, String fileSize) {
		this.url = url;
		this.fileType = fileType;
		this.fileSize = fileSize;
	}

	public String getUrl() {
		return url;
	}

	public String getFileType() {
		return fileType;
	}

	public String getFileSize() {
		return fileSize;
	}
}
