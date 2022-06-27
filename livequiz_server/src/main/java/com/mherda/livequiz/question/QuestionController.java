package com.mherda.livequiz.question;

import com.mherda.livequiz.question.dto.QuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @GetMapping(
            value = "/questions/images/{id}",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public @ResponseBody byte[] getImageById(@PathVariable Long id) {
        return questionService.getImage(id);
    }

}
