package com.mherda.livequiz.session;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.question.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "voting_session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VotingSession_SEQ")
    @SequenceGenerator(name = "VotingSession_SEQ")
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private SessionState sessionState;

    @ManyToOne
    private Question question;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "voting_session_answer_mapping",
        joinColumns = {@JoinColumn(name = "voting_session_id", referencedColumnName = "id")}
    )
    @MapKeyJoinColumn(name = "answer_id")
    private Map<Answer, Long> result;
}