package com.mherda.livequiz.question.dto;

import com.mherda.livequiz.answer.dto.AnswerResponse;

import java.util.List;

public record QuestionResponse(
        Long id,
        String content,
        List<AnswerResponse> answers
) {
}
