package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class User {

    @JsonProperty("user_id")
    private int id;

    @JsonProperty("display_name")
    private String name;
}
