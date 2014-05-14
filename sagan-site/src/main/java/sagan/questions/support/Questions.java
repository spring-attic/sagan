package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class Questions {

    @JsonProperty
    protected List<Question> items;

    @Override
    public String toString() {
        return "Questions{" +
                "items=" + items +
                '}';
    }
}
