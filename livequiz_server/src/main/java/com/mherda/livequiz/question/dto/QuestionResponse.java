package com.mherda.livequiz.question.dto;

import java.util.List;

public record QuestionResponse(
        Long id,
        String content,
        List<String> answers
) {
}
