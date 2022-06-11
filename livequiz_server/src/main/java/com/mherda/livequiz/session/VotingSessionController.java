package com.mherda.livequiz.session;

import com.mherda.livequiz.exception.NoOpenSessionException;
import com.mherda.livequiz.session.dto.VotingSessionHealthCheck;

import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VotingSessionController {

    private final SimpMessagingTemplate messagingTemplate;
    private final VotingSessionService votingSessionService;

    @GetMapping("/sessions/current/healthcheck")
    public VotingSessionHealthCheck getCurrentVotingSessionHealthCheck() {
        try {
            return votingSessionService.getCurrentVotingSessionHealthCheck();
        } catch (NoOpenSessionException e) {
            return new VotingSessionHealthCheck(null, null, null, SessionState.CLOSED);
        }
    }

    @GetMapping("/sessions/current")
    public VotingSessionResponse getCurrentVotingSession() {
        return VotingSessionMapper.toDto(votingSessionService.getCurrentVotingSession());
    }

    @PostMapping("/sessions/current/vote")
    @MessageMapping("/topic")
    public VotingSessionResponse addVotes(@RequestBody List<Long> chosenIds) {
        var response = votingSessionService.addVotes(chosenIds);
        messagingTemplate.convertAndSend("/topic", response);
        return response;
    }

}
