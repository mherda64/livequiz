package com.mherda.livequiz.admin;

import com.mherda.livequiz.exception.NoOpenSessionException;
import com.mherda.livequiz.exception.SessionAlreadyOpenedException;
import com.mherda.livequiz.question.Question;
import com.mherda.livequiz.question.QuestionService;
import com.mherda.livequiz.session.SessionState;
import com.mherda.livequiz.session.VotingSessionMapper;
import com.mherda.livequiz.session.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final QuestionService questionService;
    private final VotingSessionService votingSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    private static String NO_SESSION_OPEN = "Żadna sesja nie jest aktualnie otwarta!";

    @GetMapping("/admin")
    public String getPanel(Model model) {
        var questions = questionService.getAll();
        model.addAttribute("questions", questions);

        try {
            var votingSession = VotingSessionMapper.toDto(votingSessionService.getCurrentSession());
            model.addAttribute("votingSession", votingSession);
            model.addAttribute("labels", votingSession.result().keySet());
            model.addAttribute("values", votingSession.result().values());
        } catch (NoOpenSessionException e) {
            System.out.println(NO_SESSION_OPEN);
        }

        return "admin";
    }

    @PostMapping("/sessions/{id}/status")
    public String changeStatus(@PathVariable Long id, SessionState sessionState) {
        var response = votingSessionService.changeStatus(id, sessionState);
        messagingTemplate.convertAndSend("/topic", response);
        return "redirect:/admin";
    }

    @PostMapping("/sessions")
    public String createVotingSession(Long questionId) {
        if (votingSessionService.getAll().stream()
                .anyMatch(session ->
                        List.of(SessionState.OPEN, SessionState.FINISHED_RESULTS)
                                .contains(session.getSessionState()))) {
            throw new SessionAlreadyOpenedException("Sesja została już otwarta!");
        }

        votingSessionService.createNew(questionId);

        return "redirect:/admin";
    }

}
