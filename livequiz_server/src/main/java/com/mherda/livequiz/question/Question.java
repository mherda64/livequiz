package com.mherda.livequiz.question;

import com.mherda.livequiz.answer.Answer;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "question")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Question_SEQ")
    @SequenceGenerator(name = "Question_SEQ")
    @Column(nullable = false)
    private Long id;

    @Lob
    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] picture;

    @Column(nullable = false)
    private String content;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "question")
    private List<Answer> availableAnswers;

}