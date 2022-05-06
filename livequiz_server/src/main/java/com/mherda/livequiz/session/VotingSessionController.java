package com.mherda.livequiz.session;

import com.mherda.livequiz.session.dto.VotingSessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VotingSessionController {

    private final SimpMessagingTemplate messagingTemplate;
    private final VotingSessionService votingSessionService;

    @MessageMapping("/votingSession/current")
    @SendTo("/topic/votingSession")
    public VotingSessionResponse getCurrentVotingSession() {
        return votingSessionService.getCurrentVotingSession();
    }

}
