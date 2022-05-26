package com.example.livequiz.session;

import com.example.livequiz.request.Mapper;
import com.example.livequiz.session.dto.VotingSessionDTO;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class VotingSessionService {

    private String baseUrl;
    private Retrofit retrofit;
    private VotingSessionAPI votingSessionAPI;

    public VotingSessionService(String baseUrl) {
        this.baseUrl = baseUrl;

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(Mapper.get()))
                .build();

        votingSessionAPI = retrofit.create(VotingSessionAPI.class);
    }

    public void updateBaseUrl(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(Mapper.get()))
                .build();

        votingSessionAPI = retrofit.create(VotingSessionAPI.class);
    }

    public Call<VotingSessionDTO> getCurrentVotingSession() {
        return votingSessionAPI.getCurrentVotingSession();
    }

    public Call<VotingSessionDTO> getCurrentVotingSessionHealthCheck() {
        return votingSessionAPI.getCurrentVotingSessionHealthCheck();
    }

}
