package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
class User implements Serializable {

    @JsonProperty("user_id")
    public int id;

    @JsonProperty("display_name")
	public String name;

	@JsonProperty("profile_image")
	public String profileImage;

	public String link;

	public Integer reputation;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", link='" + link + '\'' +
				'}';
	}
}
