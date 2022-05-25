package com.example.livequiz.session;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VotingSessionAPI {

    @GET("sessions/current")
    Call<VotingSessionDTO> getCurrentVotingSession();

}
