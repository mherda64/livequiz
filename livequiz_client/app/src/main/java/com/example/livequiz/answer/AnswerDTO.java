package com.example.livequiz.answer;

import java.io.Serializable;

import lombok.Data;

@Data
public class AnswerDTO implements Serializable {
    private Long id;
    private String content;
    private boolean correct;
}
