package com.mherda.livequiz.session;

import com.mherda.livequiz.question.QuestionMapper;
import com.mherda.livequiz.session.dto.VotingSessionResponse;
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
                votingSession.getSessionState(),
                QuestionMapper.toDto(votingSession.getQuestion()),
                votingSession.getResult().entrySet().stream()
                        .collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue))
        );
    }
}
