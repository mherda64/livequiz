package com.mherda.livequiz.session;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.answer.AnswerRepository;
import com.mherda.livequiz.exception.NoOpenSessionException;
import com.mherda.livequiz.exception.NoSuchAnswerException;
import com.mherda.livequiz.session.dto.VotingSessionHealthCheck;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
import com.mherda.livequiz.vote.Vote;
import com.mherda.livequiz.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VoteRepository voteRepository;
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
        var availableAnswers = currentSession.getQuestion().getAvailableAnswers();

        chosenIds.stream()
                .filter(id -> availableAnswers.stream()
                        .map(Answer::getId)
                        .noneMatch(answerId -> answerId.equals(id)))
                .findAny()
                .ifPresent(id -> {
                    throw new NoSuchAnswerException(String.format("No answer with id:[%d]", id));
                });

        var vote = Vote.builder()
                .votingSession(currentSession)
                .answers(availableAnswers.stream()
                        .collect(
                                Collectors.toMap(Function.identity(), answer -> chosenIds.contains(answer.getId()))
                        ))
                .build();
        currentSession.getVotes().add(vote);

        voteRepository.save(vote);
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
