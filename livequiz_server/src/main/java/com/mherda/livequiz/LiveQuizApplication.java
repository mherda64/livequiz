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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        var first = generateAndPersistQuestion("Recordy w Javie to:", List.of(
                Pair.of("Typy prymitywne", false),
                Pair.of("Klasy", true),
                Pair.of("Typy wyliczeniowe", false),
                Pair.of("Żadne z powyższych", false)
        ));
        var second = generateAndPersistQuestion("Project Valhalla zakłada dodanie do Javy:", List.of(
                Pair.of("Typów prymitywnych deifniowanych przez programistę", true),
                Pair.of("Automatyczne generowanie geterów i seterów", false),
                Pair.of("Kompilację kodu bajtowego Javy do kodu natywnego", false)
        ));
        var third = generateAndPersistQuestion("Project Loom zakłada dodanie do Javy:", List.of(
                Pair.of("Ulepszoną wydajność pracy wątków systemowych", false),
                Pair.of("Wirtualnych wątków", true),
                Pair.of("Nowego API do wykonywania kodu natywnego", false)
        ));

        var votingSession = VotingSession.builder()
                .sessionState(SessionState.FINISHED_CLOSED)
                .startDate(LocalDateTime.now())
                .question(first)
                .votes(new ArrayList<>())
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

    private Question generateAndPersistQuestion(String content, List<Pair<String, Boolean>> answers) {
        var question = generateQuestion(content, answers);
        questionRepository.save(question);
        question.getAvailableAnswers().forEach(answer -> answer.setQuestion(question));
        answerRepository.saveAll(question.getAvailableAnswers());
        return question;
    }

    private Question generateQuestion(String content) {
        var answers = List.of(
                Answer.builder().content("Odpowiedź pierwsza").isCorrect(((int)(Math.random() * 100)) % 2 == 0).build(),
                Answer.builder().content("Odpowiedź druga").isCorrect(((int)(Math.random() * 100)) % 2 == 0).build(),
                Answer.builder().content("Odpowiedź trzecia").isCorrect(((int)(Math.random() * 100)) % 2 == 0).build()
        );

        return Question.builder()
                .content(content)
                .availableAnswers(answers)
                .build();
    }

    private Question generateQuestion(String content, List<Pair<String, Boolean>> answers) {
        var resultAnswers = answers.stream().map(
                a -> Answer.builder().content(a.getFirst()).isCorrect(a.getSecond()).build()
        ).toList();

        return Question.builder()
                .content(content)
                .availableAnswers(resultAnswers)
                .build();
    }
}
