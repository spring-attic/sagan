package sagan.tools.toolsuite.parser;

import sagan.tools.toolsuite.DownloadLink;
import sagan.tools.toolsuite.xml.FileDownload;

public class DownloadLinkExtractor {
    public DownloadLink createDownloadLink(FileDownload download) {
        String url = download.getBucket() + download.getFile();
        String fileType;
        if (download.getFile().endsWith(".tar.gz")) {
            fileType = "tar.gz";
        } else {
            fileType = download.getFile().substring(download.getFile().lastIndexOf(".") + 1);
        }
        String architecture = download.getDescription().indexOf("64bit") != -1 ? "64" : "32";
        return new DownloadLink(url, fileType, download.getSize(), download.getOs(), architecture);
    }
}
