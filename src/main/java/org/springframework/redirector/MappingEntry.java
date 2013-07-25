package org.springframework.redirector;

public class MappingEntry {

	private String oldUrl;
	private String newUrl;

	public String getOldUrl() {
		return oldUrl;
	}

	public void setOldUrl(String oldUrl) {
		this.oldUrl = oldUrl;
	}

	public String getNewUrl() {
		return newUrl;
	}

	public void setNewUrl(String newUrl) {
		this.newUrl = newUrl;
	}
}
