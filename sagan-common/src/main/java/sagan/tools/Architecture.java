package sagan.tools;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Architecture {
    private static final Pattern NAME_PATTERN = Pattern.compile("\\(([a-zA-Z]+)");
    private String name;
    private List<DownloadLink> downloadLinks;

    public Architecture(String name, List<DownloadLink> downloadLinks) {
        this.name = name;
        this.downloadLinks = downloadLinks;
    }

    public String getName() {
        return name;
    }

    public List<DownloadLink> getDownloadLinks() {
        return downloadLinks;
    }

    public String getDisplayName() {
        Matcher matcher = NAME_PATTERN.matcher(name);
        String arch = "WIN";
        if (matcher.find()) {
            arch = matcher.group(1);
        }
        return String.format("%s, %sBIT", arch.toUpperCase(), downloadLinks.get(0).getArchitecture());
    }
}
