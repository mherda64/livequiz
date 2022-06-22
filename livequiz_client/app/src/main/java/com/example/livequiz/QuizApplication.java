package com.example.livequiz;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class QuizApplication extends Application {

    private QuizApplication quizApplication;

    private List<Long> alreadyVotedIds = new ArrayList<>();

    public QuizApplication getInstance() {
        return quizApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        quizApplication = this;
    }

    public boolean hasAlreadyVoted(Long id) {
        return alreadyVotedIds.contains(id);
    }

    public void addVote(Long id) {
        alreadyVotedIds.add(id);
    }

}
