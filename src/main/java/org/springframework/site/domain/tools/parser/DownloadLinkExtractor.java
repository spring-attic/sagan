package org.springframework.site.domain.tools.parser;

import org.springframework.site.domain.tools.toolsuite.DownloadLink;
import org.springframework.site.domain.tools.xml.Download;

public class DownloadLinkExtractor {
	public DownloadLink createDownloadLink(Download download) {
		String url = download.getBucket() + download.getFile();
		String fileType;
		if (download.getFile().endsWith(".tar.gz")) {
			fileType = "tar.gz";
		} else {
			fileType = download.getFile().substring(download.getFile().lastIndexOf(".") + 1);
		}
		return new DownloadLink(url, fileType, download.getSize());
	}
}