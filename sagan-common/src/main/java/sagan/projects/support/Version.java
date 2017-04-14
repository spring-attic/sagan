package sagan.projects.support;

import java.util.ArrayList;
import java.util.List;

public class Version {
    private final String source;
    private final String[] parts;
    private final Version baseVersion;

    private Version(String source, List<String> parts, Version baseVersion) {
        this.source = source;
        this.parts = parts.toArray(new String[0]);
        this.baseVersion = baseVersion == null ? this : baseVersion;
    }

    /**
     * Adapted from Gradle's VersionParser
     */
    public static Version build(String raw) {
        if(raw == null)
            return null;

        List<String> parts = new ArrayList<String>();
        boolean digit = false;
        int startPart = 0;
        int pos = 0;
        int endBase = 0;
        int endBaseStr = 0;
        for (; pos < raw.length(); pos++) {
            char ch = raw.charAt(pos);
            if (ch == '.' || ch == '_' || ch == '-' || ch == '+' || ch == ' ') {
                parts.add(raw.substring(startPart, pos));
                startPart = pos + 1;
                digit = false;
                if (ch != '.' && endBaseStr == 0) {
                    endBase = parts.size();
                    endBaseStr = pos;
                }
            } else if (ch >= '0' && ch <= '9') {
                if (!digit && pos > startPart) {
                    if (endBaseStr == 0) {
                        endBase = parts.size() + 1;
                        endBaseStr = pos;
                    }
                    parts.add(raw.substring(startPart, pos));
                    startPart = pos;
                }
                digit = true;
            } else {
                if (digit) {
                    if (endBaseStr == 0) {
                        endBase = parts.size() + 1;
                        endBaseStr = pos;
                    }
                    parts.add(raw.substring(startPart, pos));
                    startPart = pos;
                }
                digit = false;
            }
        }
        if (pos > startPart) {
            parts.add(raw.substring(startPart, pos));
        }
        Version base = null;
        if (endBaseStr > 0) {
            base = new Version(raw.substring(0, endBaseStr), parts.subList(0, endBase), null);
        }
        return new Version(raw, parts, base);
    }

    @Override
    public String toString() {
        return source;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Version other = (Version) obj;
        return source.equals(other.source);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    public boolean isQualified() {
        return baseVersion != this;
    }

    public Version getBaseVersion() {
        return baseVersion;
    }

    public String[] getParts() {
        return parts;
    }
}
