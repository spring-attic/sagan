package sagan.questions.support;

import java.util.List;

/**
 * API to interact with StackOverflow.
 */
public interface StackOverflowClient {

    /**
     * Returns all {@link Question}s tagged with the given tags.
     *
     * @param tags
     * @return
     */
    List<Question> getQuestionsForTags(String... tags);

    /**
     * Returns the accepted answer for the given {@link Question}.
     *
     * @param question
     * @return
     */
    Answer getAcceptedAnswerFor(Question question);

}