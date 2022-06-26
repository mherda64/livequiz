package com.mherda.livequiz.answer;

import com.mherda.livequiz.answer.dto.AnswerResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerMapper {
    public static AnswerResponse toDto(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getContent(),
                answer.isCorrect()
        );
    }
}
