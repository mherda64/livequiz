package com.mherda.livequiz.session;

import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @GetMapping("/sessions/current")
    public VotingSessionResponse getCurrentVotingSession() {
        try {
            return votingSessionService.getCurrentVotingSession();
        } catch (NoOpenSessionException e) {
            return new VotingSessionResponse(null, null, SessionState.CLOSED, null, null);
        }
    }

}
