package com.example.livequiz.session.dto;

import androidx.annotation.Nullable;

import com.example.livequiz.question.QuestionDTO;
import com.example.livequiz.session.SessionState;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class VotingSessionDTO {
    private Long id;
    private LocalDateTime startDate;
    @Nullable
    private LocalDateTime endDate;
    private SessionState sessionState;
    private QuestionDTO question;
    private Map<Long, Long> result;
}
