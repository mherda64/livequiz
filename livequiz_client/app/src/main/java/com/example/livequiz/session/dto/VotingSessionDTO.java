package com.example.livequiz.session.dto;

import androidx.annotation.Nullable;

import com.example.livequiz.question.QuestionDTO;
import com.example.livequiz.session.SessionState;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class VotingSessionDTO implements Serializable {
    private Long id;
    @Nullable
    private LocalDateTime startDate;
    @Nullable
    private LocalDateTime endDate;
    private Long voteCount;
    private SessionState sessionState;
    private QuestionDTO question;
    private Map<Long, Long> result;
}
