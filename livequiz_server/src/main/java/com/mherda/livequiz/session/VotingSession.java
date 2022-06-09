package com.mherda.livequiz.session;

import com.mherda.livequiz.question.Question;
import com.mherda.livequiz.vote.Vote;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany
    private List<Vote> votes;

}