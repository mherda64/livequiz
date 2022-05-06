package com.mherda.livequiz.session;

import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;

    public VotingSessionResponse getCurrentVotingSession() {
        var currentSession = votingSessionRepository.findAll().stream()
                .filter(session -> session.getSessionState().equals(SessionState.OPEN))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No voting session currently open"));
        return VotingSessionMapper.toDto(currentSession);
    }

}
