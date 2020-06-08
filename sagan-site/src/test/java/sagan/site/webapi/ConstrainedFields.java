package sagan.site.webapi;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.util.StringUtils;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class ConstrainedFields {

	private final ConstraintDescriptions constraintDescriptions;

	public static ConstrainedFields constraintsOn(Class<?> input) {
		return new ConstrainedFields(input);
	}

	public ConstrainedFields(Class<?> input) {
		this.constraintDescriptions = new ConstraintDescriptions(input);
	}

	public FieldDescriptor withPath(String path) {
		return fieldWithPath(path).attributes(key("constraints").value(StringUtils
				.collectionToDelimitedString(this.constraintDescriptions
						.descriptionsForProperty(path), ". ")));
	}
}
