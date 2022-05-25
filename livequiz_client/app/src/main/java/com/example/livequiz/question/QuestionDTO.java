package com.example.livequiz.question;

import com.example.livequiz.answer.AnswerDTO;

import java.util.List;

import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String content;
    private List<AnswerDTO> answers;
}
