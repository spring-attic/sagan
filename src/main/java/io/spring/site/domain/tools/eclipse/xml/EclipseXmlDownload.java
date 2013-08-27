package io.spring.site.domain.tools.eclipse.xml;

import io.spring.site.domain.tools.toolsuite.xml.FileDownload;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class EclipseXmlDownload implements FileDownload {

	@Attribute
	private String os;

	@Attribute(name="version", required = false)
	private String version;

	@Attribute(name="eclipse-version")
	private String eclipseVersion;

	@Attribute
	private String size;

	@Element(name="description")
	private String description;
	@Element
	private String file;
	@Element
	private String md5;
	@Element
	private String sha1;
	@Element
	private String bucket;

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getEclipseVersion() {
		return eclipseVersion;
	}

	public void setEclipseVersion(String eclipseVersion) {
		this.eclipseVersion = eclipseVersion;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
}
