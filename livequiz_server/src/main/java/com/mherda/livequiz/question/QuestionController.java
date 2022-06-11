package com.mherda.livequiz.question;

import com.mherda.livequiz.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/questions")
    public List<QuestionResponse> getAllRestQuestions() {
        var allQuestions = questionService.getAll().stream()
                .map(QuestionMapper::toDto).toList();
        log.info("Requested all questions, returning {}", allQuestions);
        return allQuestions;
    }

}
