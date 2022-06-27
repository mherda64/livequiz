package com.example.livequiz;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class QuizApplication extends Application {

    private StompClient stompClient;
    private QuizApplication quizApplication;

    private final List<Long> alreadyVotedIds = new ArrayList<>();
    private Flowable<StompMessage> stompMessageFlowable;

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

    public void socketConnect(String destAddress) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                parseWebsocketAddress(destAddress) + "liveQuiz-websocket");
        stompClient.connect();

        stompMessageFlowable = stompClient.topic("/topic");
    }

    public Flowable<StompMessage> getStompMessageFlowable() {
        return stompMessageFlowable;
    }

    public void socketDisconnect() {
        if (stompClient != null && stompClient.isConnected())
            stompClient.disconnect();
    }

    private String parseWebsocketAddress(String address) {
        String parsed = address;
        if (parsed.startsWith("http://"))
            parsed = parsed.substring(7);
        if (!parsed.endsWith("/"))
            parsed = parsed + "/";
        return "ws://" + parsed;
    }


}
