package com.mherda.livequiz.question;

import com.mherda.livequiz.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionRepository questionRepository;

    @MessageMapping("/questions")
    @SendTo("/topic/questions")
    public List<QuestionResponse> getAllRestQuestions() {
        var allQuestions = questionRepository.findAll().stream()
                .map(QuestionMapper::toDto).toList();
        log.info("Requested all questions, returning {}", allQuestions);
        return allQuestions;
    }

}
