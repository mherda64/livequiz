package com.mherda.livequiz.session.dto;

import com.mherda.livequiz.question.dto.QuestionResponse;
import com.mherda.livequiz.session.SessionState;

import java.time.LocalDateTime;
import java.util.Map;

public record VotingSessionResponse(
        Long id,
        LocalDateTime startDate,
        SessionState sessionState,
        QuestionResponse question,
        Map<Long, Long> result
) {
}
