package com.example.livequiz.session;

import com.example.livequiz.request.Mapper;
import com.example.livequiz.session.dto.VotingSessionDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class VotingSessionService {

    private Retrofit retrofit;
    private VotingSessionAPI votingSessionAPI;

    public VotingSessionService(String baseUrl) {

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

    public Call<ResponseBody> getImage(Long id) {
        return votingSessionAPI.getImage(id);
    }

    public Call<VotingSessionDTO> sendVote(List<Long> chosenIds) {
        return votingSessionAPI.sendVote(chosenIds);
    }

}
