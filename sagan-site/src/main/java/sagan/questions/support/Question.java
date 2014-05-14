package sagan.questions.support;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    public String title;
    public String body;
    public List<String> tags;

    @JsonProperty("is_answered")
    public boolean isAnswered;

    @JsonProperty("question_id")
    public String id;

    public User owner;
}
