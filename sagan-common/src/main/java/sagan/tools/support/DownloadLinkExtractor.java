package sagan.tools.support;

import sagan.tools.DownloadLink;
import sagan.tools.FileDownload;

class DownloadLinkExtractor {
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
