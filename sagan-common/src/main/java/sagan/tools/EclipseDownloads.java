package sagan.tools;

import java.util.Map;

public class EclipseDownloads {
    private final Map<String, EclipsePlatform> platforms;

    public EclipseDownloads(Map<String, EclipsePlatform> platforms) {
        this.platforms = platforms;
    }

    public Map<String, EclipsePlatform> getPlatforms() {
        return platforms;
    }
}
