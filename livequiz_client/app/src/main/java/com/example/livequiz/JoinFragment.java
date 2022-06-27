package com.example.livequiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.livequiz.session.SessionState;
import com.example.livequiz.session.dto.VotingSessionDTO;
import com.example.livequiz.session.VotingSessionService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinFragment extends Fragment {

    private static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private VotingSessionService votingSessionService;
    private QuizApplication quizApplication;

    private VotingSessionDTO votingSession;

    private Button btn_updateVotingSession;
    private Button btn_join;
    private TextView tv_sessionState;
    private TextView tv_startDate;
    private TextView tv_endDate;
    private EditText et_baseAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizApplication = (QuizApplication) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join, container, false);

        btn_updateVotingSession = view.findViewById(R.id.btn_updateVotingSession);
        btn_join = view.findViewById(R.id.btn_join);
        tv_sessionState = view.findViewById(R.id.tv_sessionState);
        tv_startDate = view.findViewById(R.id.tv_startDate);
        tv_endDate = view.findViewById(R.id.tv_endDate);
        et_baseAddress = view.findViewById(R.id.et_baseAddress);
        btn_join.setEnabled(false);

        quizApplication.socketDisconnect();
        votingSessionService = new VotingSessionService(et_baseAddress.getText().toString());

        btn_updateVotingSession.setOnClickListener(v -> {

            if (!et_baseAddress.getText().toString().matches(URL_REGEX)) {
                Toast.makeText(getActivity(), "ERROR: Invalid URL", Toast.LENGTH_LONG).show();
                tv_startDate.setText(getString(R.string.startDate));
                tv_sessionState.setText(getString(R.string.session));
                btn_join.setEnabled(false);
                return;
            }

            votingSessionService.updateBaseUrl(et_baseAddress.getText().toString());

            votingSessionService.getCurrentVotingSession().enqueue(new Callback<VotingSessionDTO>() {
                @Override
                public void onResponse(Call<VotingSessionDTO> call, Response<VotingSessionDTO> response) {
                    if (!response.isSuccessful()) {
                        try {
                            Toast.makeText(getActivity(), "ERROR: " + response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "ERROR: " + response.code() + response.message(), Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                    votingSession = response.body();
                    if (votingSession != null) {
                        if (votingSession.getStartDate() != null) {
                            tv_startDate.setText(getString(R.string.startDate) + votingSession.getStartDate()
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                        }
                        if (votingSession.getEndDate() != null) {
                            tv_endDate.setText(getString(R.string.endDate) + votingSession.getStartDate()
                                    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                        }
                        if (votingSession.getSessionState() != null) {
                            tv_sessionState.setText(getString(R.string.session) + votingSession.getSessionState().toString());
                        }
                        btn_join.setEnabled(votingSession.getSessionState() != null &&
                                SessionState.canConnect(votingSession.getSessionState()));
                    }
                }

                @Override
                public void onFailure(Call<VotingSessionDTO> call, Throwable t) {
                    Toast.makeText(getActivity(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    tv_startDate.setText(getString(R.string.startDate));
                    tv_sessionState.setText(getString(R.string.session));
                    btn_join.setEnabled(false);
                }
            });
        });

        btn_join.setOnClickListener(v -> {
            if (btn_join.isEnabled()) {
                quizApplication.socketConnect(et_baseAddress.getText().toString());

                NavController navController = NavHostFragment.findNavController(this);
                if (quizApplication.hasAlreadyVoted(votingSession.getId()) ||
                        votingSession.getSessionState().equals(SessionState.FINISHED_RESULTS)) {
                    navController.navigate(JoinFragmentDirections
                            .actionJoinFragmentToResultsFragment(String.valueOf(et_baseAddress.getText()), votingSession));
                } else {
                    JoinFragmentDirections.ActionJoinFragmentToQuestionAnswerFragment action =
                            JoinFragmentDirections.actionJoinFragmentToQuestionAnswerFragment(
                                    String.valueOf(et_baseAddress.getText()),
                                    votingSession
                            );
                    navController.navigate(action);
                }
            }
        });

        return view;
    }
}
