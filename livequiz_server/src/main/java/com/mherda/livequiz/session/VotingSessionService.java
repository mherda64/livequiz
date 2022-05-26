package com.mherda.livequiz.session;

import com.mherda.livequiz.answer.AnswerRepository;
import com.mherda.livequiz.session.dto.VotingSessionHealthCheck;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final AnswerRepository answerRepository;

    public VotingSessionHealthCheck getCurrentVotingSessionHealthCheck() {
        var session = getCurrentVotingSession();
        return new VotingSessionHealthCheck(session.id(), session.startDate(), session.endDate(), session.sessionState());
    }

    public VotingSessionResponse getCurrentVotingSession() {
        var currentSession = getCurrentSession();
        return VotingSessionMapper.toDto(currentSession);
    }

    @Transactional
    public VotingSessionResponse addVotes(List<Long> chosenIds) {
        var currentSession = getCurrentSession();
        var result = currentSession.getResult();
        chosenIds.stream()
                .map(id -> result.keySet().stream()
                        .filter(a -> a.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(String.format("No answer with id:[%d]", id)))
                ).forEach(answer -> {
                    var value = result.get(answer);
                    result.put(answer, value + 1);
                });
        votingSessionRepository.save(currentSession);
        return VotingSessionMapper.toDto(currentSession);
    }

    private VotingSession getCurrentSession() {
        return votingSessionRepository.findAll().stream()
                .filter(session -> List.of(SessionState.OPEN, SessionState.FINISHED_RESULTS).contains(session.getSessionState()))
                .findFirst()
                .orElseThrow(() -> new NoOpenSessionException("No voting session currently open"));
    }

}
