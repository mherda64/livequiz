package com.mherda.livequiz.admin;

import com.mherda.livequiz.exception.NoOpenSessionException;
import com.mherda.livequiz.session.SessionState;
import com.mherda.livequiz.session.VotingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final VotingSessionService votingSessionService;

    @GetMapping("/admin")
    public String getPanel(Model model) {
        try {
            var votingSession = votingSessionService.getCurrentVotingSession();
            model.addAttribute("votingSession", votingSession);
            model.addAttribute("labels", votingSession.result().keySet());
            model.addAttribute("values", votingSession.result().values());

        } catch (NoOpenSessionException e) {
            System.out.println("NO VOTING SESSION!");
        }


        return "admin";
    }

    @PostMapping("/sessions/{id}/status")
    public String changeStatus(@PathVariable Long id, SessionState sessionState) {
        votingSessionService.changeStatus(id, sessionState);
        return "redirect:/admin";
    }

}
