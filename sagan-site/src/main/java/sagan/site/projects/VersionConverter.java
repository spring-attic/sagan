package sagan.site.projects;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA {@link AttributeConverter} for converting {@link Version} to/from its {@code String} representation.
 * @see Release
 */
@Converter(autoApply = true)
public class VersionConverter implements AttributeConverter<Version, String> {

	@Override
	public String convertToDatabaseColumn(Version version) {
		return version.toString();
	}

	@Override
	public Version convertToEntityAttribute(String rawVersion) {
		return Version.of(rawVersion);
	}
}
