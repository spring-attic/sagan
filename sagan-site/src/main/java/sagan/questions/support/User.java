package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class User {

    @JsonProperty("user_id")
    public int id;

    @JsonProperty("display_name")
	public String name;

	@JsonProperty("profile_image")
	public String profileImage;

	public String link;

	public Integer reputation;
}
