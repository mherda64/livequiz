package com.mherda.livequiz.session.dto;

import com.mherda.livequiz.session.SessionState;

import java.time.LocalDateTime;

public record VotingSessionHealthCheck (
        Long id,
        LocalDateTime startDate,
        SessionState sessionState
){
}
