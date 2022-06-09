package com.mherda.livequiz.session;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.question.QuestionMapper;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VotingSessionMapper {
    public static VotingSessionResponse toDto(VotingSession votingSession) {
        return new VotingSessionResponse(
                votingSession.getId(),
                votingSession.getStartDate(),
                votingSession.getEndDate(),
                (long) votingSession.getVotes().size(),
                votingSession.getSessionState(),
                QuestionMapper.toDto(votingSession.getQuestion()),
                votingSession.getVotes().isEmpty() ?
                        votingSession.getQuestion().getAvailableAnswers().stream()
                                        .map(Answer::getId)
                                                .collect(Collectors.toMap(Function.identity(), e -> 0L)) :
                votingSession.getVotes().stream()
                        .flatMap(vote -> vote.getAnswers().entrySet().stream())
                        .collect(Collectors.groupingBy(
                                entry -> entry.getKey().getId(),
                                Collectors.summingLong(entry -> entry.getValue() ? 1 : 0)))
        );
    }
}
