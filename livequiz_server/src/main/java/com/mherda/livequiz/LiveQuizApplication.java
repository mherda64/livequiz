package com.mherda.livequiz;

import com.mherda.livequiz.answer.Answer;
import com.mherda.livequiz.answer.AnswerRepository;
import com.mherda.livequiz.question.Question;
import com.mherda.livequiz.question.QuestionRepository;
import com.mherda.livequiz.session.SessionState;
import com.mherda.livequiz.session.VotingSession;
import com.mherda.livequiz.session.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class LiveQuizApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiveQuizApplication.class, args);
    }

}

@Component
@RequiredArgsConstructor
class MyRunner implements CommandLineRunner {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final VotingSessionRepository votingSessionRepository;

    @Override
    public void run(String... args) throws Exception {
        var first = generateAndPersistQuestion("Pytanie pierwsze");
        generateAndPersistQuestion("Pytanie drugie");
        generateAndPersistQuestion("Pytanie trzecie");

        var votingSession = VotingSession.builder()
                .sessionState(SessionState.OPEN)
                .startDate(LocalDateTime.now())
                .question(first)
                .result(first.getAvailableAnswers().stream().collect(Collectors.toMap(Function.identity(), item -> 0L)))
                .build();
        votingSessionRepository.save(votingSession);
    }

    private Question generateAndPersistQuestion(String content) {
        var question = generateQuestion(content);
        questionRepository.save(question);
        question.getAvailableAnswers().forEach(answer -> answer.setQuestion(question));
        answerRepository.saveAll(question.getAvailableAnswers());
        return question;
    }

    private Question generateQuestion(String content) {
        var answers = List.of(
                Answer.builder().content("Odpowiedź pierwsza").isCorrect(true).build(),
                Answer.builder().content("Odpowiedź druga").isCorrect(false).build(),
                Answer.builder().content("Odpowiedź trzecia").isCorrect(false).build()
        );

        return Question.builder()
                .content(content)
                .availableAnswers(answers)
                .build();
    }
}
