package com.example.livequiz.session;

import com.example.livequiz.session.dto.VotingSessionDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface VotingSessionAPI {

    @GET("sessions/current/healthcheck")
    Call<VotingSessionDTO> getCurrentVotingSessionHealthCheck();

    @GET("sessions/current")
    Call<VotingSessionDTO> getCurrentVotingSession();

    @POST("sessions/current/vote")
    Call<VotingSessionDTO> sendVote(@Body List<Long> chosenIds);
}
