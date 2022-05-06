package com.mherda.livequiz.answer;

import com.mherda.livequiz.question.Question;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Answer_SEQ")
    @SequenceGenerator(name = "Answer_SEQ")
    @Column(name = "id", nullable = false)
    private Long id;

    private String content;

    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}