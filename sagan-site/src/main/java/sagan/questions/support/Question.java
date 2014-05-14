package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


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