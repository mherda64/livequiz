package com.example.livequiz.session;

import com.example.livequiz.session.dto.VotingSessionDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VotingSessionAPI {

    @GET("sessions/current")
    Call<VotingSessionDTO> getCurrentVotingSession();

    @GET("questions/images/{id}")
    Call<ResponseBody> getImage(@Path("id") Long id);

    @POST("sessions/current/vote")
    Call<VotingSessionDTO> sendVote(@Body List<Long> chosenIds);
}
