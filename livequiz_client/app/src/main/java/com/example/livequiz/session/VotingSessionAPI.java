package com.example.livequiz.session;

import com.example.livequiz.session.dto.VotingSessionDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VotingSessionAPI {

    @GET("sessions/current/healthcheck")
    Call<VotingSessionDTO> getCurrentVotingSessionHealthCheck();

    @GET("sessions/current")
    Call<VotingSessionDTO> getCurrentVotingSession();
}
