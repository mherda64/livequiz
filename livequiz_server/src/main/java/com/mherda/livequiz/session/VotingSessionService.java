package com.mherda.livequiz.session;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.answer.AnswerRepository;
import com.mherda.livequiz.exception.NoOpenSessionException;
import com.mherda.livequiz.exception.NoSuchAnswerException;
import com.mherda.livequiz.question.QuestionRepository;
import com.mherda.livequiz.session.dto.VotingSessionHealthCheck;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
import com.mherda.livequiz.vote.Vote;
import com.mherda.livequiz.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final QuestionRepository questionRepository;

    private static String NO_SESSION_OPEN = "Żadna sesja nie jest aktualnie otwarta!";

    public VotingSessionHealthCheck getCurrentVotingSessionHealthCheck() {
        var session = VotingSessionMapper.toDto(getCurrentSession());
        return new VotingSessionHealthCheck(session.id(), session.startDate(), session.endDate(), session.sessionState());
    }

    public VotingSessionResponse changeStatus(Long id, SessionState status) {
        var session = votingSessionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Nie znaleziono sesji z ID: [%d]!", id)));
        session.setSessionState(status);
        switch (status) {
            case OPEN -> session.setStartDate(LocalDateTime.now());
            case FINISHED_RESULTS -> session.setEndDate(LocalDateTime.now());
        }
        votingSessionRepository.save(session);
        return VotingSessionMapper.toDto(session);
    }

    @Transactional
    public VotingSessionResponse addVotes(List<Long> chosenIds) {
        var currentSession = getCurrentSession();
        if (!currentSession.getSessionState().equals(SessionState.OPEN))
            throw new NoOpenSessionException(NO_SESSION_OPEN);

        var availableAnswers = currentSession.getQuestion().getAvailableAnswers();

        chosenIds.stream()
                .filter(id -> availableAnswers.stream()
                        .map(Answer::getId)
                        .noneMatch(answerId -> answerId.equals(id)))
                .findAny()
                .ifPresent(id -> {
                    throw new NoSuchAnswerException(String.format("Brak odpowiedzi o ID: [%d]", id));
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

    public List<VotingSession> getAll() {
        return votingSessionRepository.findAll();
    }

    public VotingSession createNew(Long questionId) {
        var question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Pytanie z ID: [%d] nie istnieje", questionId)));

        var votingSession = VotingSession.builder()
                .sessionState(SessionState.CLOSED)
                .question(question)
                .votes(new ArrayList<>())
                .build();
        votingSessionRepository.save(votingSession);

        return votingSession;
    }

    public VotingSession getCurrentSession() {
        return votingSessionRepository.findAll().stream()
                .filter(session -> List.of(SessionState.CLOSED, SessionState.OPEN, SessionState.FINISHED_RESULTS).contains(session.getSessionState()))
                .findFirst()
                .orElseThrow(() -> new NoOpenSessionException(NO_SESSION_OPEN));
    }

}
