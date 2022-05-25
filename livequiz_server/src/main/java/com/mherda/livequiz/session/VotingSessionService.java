package com.mherda.livequiz.session;

import com.mherda.livequiz.session.dto.VotingSessionHealthCheck;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;

    public VotingSessionHealthCheck getCurrentVotingSessionHealthCheck() {
        var session = getCurrentVotingSession();
        return new VotingSessionHealthCheck(session.id(), session.startDate(), session.sessionState());
    }

    public VotingSessionResponse getCurrentVotingSession() {
        var currentSession = votingSessionRepository.findAll().stream()
                .filter(session -> List.of(SessionState.OPEN).contains(session.getSessionState()))
                .findFirst()
                .orElseThrow(() -> new NoOpenSessionException("No voting session currently open"));
        return VotingSessionMapper.toDto(currentSession);
    }

}
