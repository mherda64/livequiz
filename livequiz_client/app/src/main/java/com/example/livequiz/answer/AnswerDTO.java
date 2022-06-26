package com.example.livequiz.answer;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long id;
    private String content;
    private boolean correct;
}
