package com.mherda.livequiz.session;

import com.mherda.livequiz.session.dto.VotingSessionHealthCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    @GetMapping("/sessions/current/healthcheck")
    public VotingSessionHealthCheck getCurrentVotingSession() {
        try {
            return votingSessionService.getCurrentVotingSessionHealthCheck();
        } catch (NoOpenSessionException e) {
            return new VotingSessionHealthCheck(null, null, SessionState.CLOSED);
        }
    }

}
