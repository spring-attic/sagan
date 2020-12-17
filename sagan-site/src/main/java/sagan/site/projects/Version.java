package sagan.site.projects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * A {@link Release} version.
 * <p>This class parses and sorts version strings, but supports only {@code '.'} and {@code '-'} separators,
 * as well as a limited number of version schemes.
 * <h2>The previous Spring projects version scheme:
 * <ul>
 *     <li>MAJOR.MINOR.PATCH.IDENTIFIER, e.g. 2.3.0.RELEASE</li>
 *     <li>NAME-IDENTIFIER, e.g. Hopper-BUILD-SNAPSHOT</li>
 *     <li>NAME.IDENTIFIER, e.g. Hoxton.SR1</li>
 * </ul>
 * <p>With this release scheme, sorting works as expected with the exception of {@code "BUILD-SNAPSHOT"}
 * which are considered as older than {@code "RC1"} and {@code "M1"}.
 *
 * <h2>The new Spring projects version scheme:
 * <ul>
 *     <li>MAJOR.MINOR.PATCH for RELEASES, e.g. 2.3.0</li>
 *     <li>MAJOR.MINOR.PATCH-IDENTIFIER for others, e.g. 2.3.0-RC2 or 2.3.0-SNAPSHOT</li>
 * </ul>
 * <p>With this release scheme, sorting works as expected in all cases since
 * it is using {@code "SNAPSHOT"} instead.
 *
 */
public class Version implements Comparable<Version>, Serializable {

	private static Pattern NUMBER = Pattern.compile("\\d+");

	private static final char PART_SEPARATOR = '.';

	private static final char BASE_SEPARATOR = '-';

	private final String source;

	private final String[] parts;

	private Version(String source, List<String> parts) {
		this.source = source;
		this.parts = parts.toArray(new String[0]);
	}

	public static Version of(String rawVersion) {
		rawVersion = rawVersion.trim();
		if (!StringUtils.hasLength(rawVersion)) {
			throw new InvalidVersionException("Version string should not be empty");
		}
		char firstChar = rawVersion.charAt(0);
		if (PART_SEPARATOR == firstChar || BASE_SEPARATOR == firstChar) {
			throw new InvalidVersionException("Version cannot start with a '.' or '-' separator");
		}
		List<String> parts = new ArrayList<>();
		int pos = 0;
		int partPos = 0;
		PARSER_STATE state = PARSER_STATE.PART;
		while (pos < rawVersion.length()) {
			char ch = rawVersion.charAt(pos);
			switch (state) {
				case PART:
					if (ch == PART_SEPARATOR || ch == BASE_SEPARATOR) {
						state = PARSER_STATE.SEPARATOR;
					}
					else if (pos == rawVersion.length() - 1) {
						state = PARSER_STATE.SUFFIX;
					}
					else {
						checkAsciiAlphanum(ch);
						pos++;
					}
					break;
				case SEPARATOR:
					if ((ch == PART_SEPARATOR) ||
							(ch == BASE_SEPARATOR && parts.size() == 0)) {
						state = PARSER_STATE.PART;
					}
					else if (ch == BASE_SEPARATOR) {
						state = PARSER_STATE.SUFFIX;
					}
					parts.add(rawVersion.substring(partPos, pos));
					pos++;
					partPos = pos;
					break;
				case SUFFIX:
					parts.add(rawVersion.substring(partPos));
					pos = rawVersion.length();
					break;
			}
		}
		return new Version(rawVersion, parts);
	}

	enum PARSER_STATE {
		PART,
		SEPARATOR,
		SUFFIX
	}

	private static void checkAsciiAlphanum(char c) {
		if ((c >= 'a' && c <= 'z') ||
				(c >= 'A' && c <= 'Z') ||
				(c >= '0' && c <= '9')) {
			// char is ascii alphanum
		}
		else {
			throw new InvalidVersionException("Character '" + c + "' is not alphanumeric");
		}
	}

	@Override
	public int compareTo(Version other) {
		if (this.equals(other)) {
			return 0;
		}

		String[] theseParts = this.parts;
		String[] otherParts = other.parts;

		int i = 0;
		for (; i < theseParts.length && i < otherParts.length; i++) {
			if (theseParts[i].equals(otherParts[i])) {
				continue;
			}
			boolean isThisPartNumber = isNumber(theseParts[i]);
			boolean isOtherPartNumber = isNumber(otherParts[i]);
			if (isThisPartNumber && !isOtherPartNumber) {
				return 1;
			}
			if (isOtherPartNumber && !isThisPartNumber) {
				return -1;
			}
			if (isThisPartNumber) {
				return Long.valueOf(theseParts[i]).compareTo(Long.valueOf(otherParts[i]));
			}
			// both are strings, we compare them lexicographically
			return theseParts[i].compareTo(otherParts[i]);
		}
		if (i < theseParts.length) {
			return isNumber(theseParts[i]) ? 1 : -1;
		}
		if (i < otherParts.length) {
			return isNumber(otherParts[i]) ? -1 : 1;
		}
		return 0;
	}

	private boolean isNumber(String str) {
		return NUMBER.matcher(str).matches();
	}

	@Override
	public String toString() {
		return this.source;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Version version = (Version) o;
		return source.equals(version.source);
	}

	@Override
	public int hashCode() {
		return Objects.hash(source);
	}

}
