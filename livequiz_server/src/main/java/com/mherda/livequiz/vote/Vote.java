package com.mherda.livequiz.vote;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.session.VotingSession;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

@Entity
@Table(name = "vote")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Vote_SEQ")
    @SequenceGenerator(name = "Vote_SEQ")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    private VotingSession votingSession;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "voting_session_answer_mapping",
            joinColumns = {@JoinColumn(name = "vote_id", referencedColumnName = "id")}
    )
    @MapKeyJoinColumn(name = "answer_id")
    private Map<Answer, Boolean> answers;

}
