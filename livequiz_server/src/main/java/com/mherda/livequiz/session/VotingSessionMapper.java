package com.mherda.livequiz.session;

import com.mherda.livequiz.question.QuestionMapper;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
import com.mherda.livequiz.vote.Vote;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
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
                votingSession.getVotes().stream()
                        .flatMap(vote -> vote.getAnswers().entrySet().stream())
                        .collect(Collectors.groupingBy(
                                entry -> entry.getKey().getId(),
                                Collectors.summingLong(entry -> entry.getValue() ? 1 : 0)))
        );
    }
}
