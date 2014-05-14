package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer {

    @JsonProperty("is_accepted")
    public boolean accepted;
    public String body;
    public User owner;
}