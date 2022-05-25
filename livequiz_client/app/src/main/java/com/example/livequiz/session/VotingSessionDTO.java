package com.example.livequiz.session;

import com.example.livequiz.question.QuestionDTO;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class VotingSessionDTO {
    private Long id;
    private LocalDateTime startDate;
    private SessionState sessionState;
    private QuestionDTO question;
    private Map<Long, Long> result;
}
