package com.mherda.livequiz.question;

import com.mherda.livequiz.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class QuestionRestController {

    private final QuestionRepository questionRepository;

    @GetMapping("/questions")
    public List<QuestionResponse> getAllRestQuestions() {
        var allQuestions = questionRepository.findAll().stream()
                .map(QuestionMapper::toDto).toList();
        log.info("REST Requested all questions, returning {}", allQuestions);
        return allQuestions;
    }

}
