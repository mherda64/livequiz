package com.example.livequiz;

import android.app.Application;
import android.content.Context;

import com.example.livequiz.request.Mapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class QuizApplication extends Application {

    private StompClient stompClient;
    private QuizApplication quizApplication;
    private String destAddress;

    private List<Long> alreadyVotedIds = new ArrayList<>();
    private Flowable<StompMessage> stompMessageFlowable;

    public QuizApplication getInstance() {
        return quizApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        quizApplication = this;
        loadVotedIds();
    }

    public boolean hasAlreadyVoted(Long id) {
        return alreadyVotedIds.contains(id);
    }

    public void addVote(Long id) {
        alreadyVotedIds.add(id);

        try {
            FileOutputStream fOut = openFileOutput("votedIds.txt", Context.MODE_PRIVATE);
            fOut.write(Mapper.get().writeValueAsString(alreadyVotedIds).getBytes());
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadVotedIds() {
        try {
            FileInputStream fIn = openFileInput("votedIds.txt");
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader bf = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            isr.close();
            alreadyVotedIds = Mapper.get().readValue(sb.toString(), new TypeReference<List<Long>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }
}
