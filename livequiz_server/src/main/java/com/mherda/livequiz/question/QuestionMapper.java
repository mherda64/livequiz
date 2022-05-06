package com.mherda.livequiz.question;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.question.dto.QuestionResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {

    public static QuestionResponse toDto(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getContent(),
                question.getAvailableAnswers().stream()
                        .map(Answer::getContent)
                        .toList()
        );
    }

}
