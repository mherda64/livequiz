package com.mherda.livequiz.session;

import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;

    public VotingSessionResponse getCurrentVotingSession() {
        var currentSession = votingSessionRepository.findAll().stream()
                .filter(session -> List.of(SessionState.OPEN, SessionState.FINISHED_RESULTS).contains(session.getSessionState()))
                .findFirst()
                .orElseThrow(() -> new NoOpenSessionException("No voting session currently open"));
        return VotingSessionMapper.toDto(currentSession);
    }

}
