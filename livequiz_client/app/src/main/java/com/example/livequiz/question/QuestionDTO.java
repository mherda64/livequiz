package com.example.livequiz.question;

import com.example.livequiz.answer.AnswerDTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class QuestionDTO implements Serializable {
    private Long id;
    private String content;
    private List<AnswerDTO> answers;
}
