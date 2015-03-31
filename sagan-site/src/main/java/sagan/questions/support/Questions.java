package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class Questions implements Serializable {

    @JsonProperty
    protected List<Question> items;
}
