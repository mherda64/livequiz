package com.mherda.livequiz.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getAll() {
        return questionRepository.findAll();
    }

    public byte[] getImage(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Brak zdjęcia o ID: [%d]", id)))
                .getPicture();
    }
}
