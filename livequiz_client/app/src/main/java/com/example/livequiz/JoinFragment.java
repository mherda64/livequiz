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
import com.example.livequiz.session.VotingSessionDTO;
import com.example.livequiz.session.VotingSessionService;

import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinFragment extends Fragment {

    private VotingSessionService votingSessionService;

    private Button btn_updateVotingSession;
    private Button btn_join;
    private TextView tv_sessionState;
    private TextView tv_startDate;
    private EditText et_baseAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_join, container, false);

        btn_updateVotingSession = view.findViewById(R.id.btn_updateVotingSession);
        btn_join = view.findViewById(R.id.btn_join);
        tv_sessionState = view.findViewById(R.id.tv_sessionState);
        tv_startDate = view.findViewById(R.id.tv_startDate);
        et_baseAddress = view.findViewById(R.id.et_baseAddress);

        btn_join.setEnabled(false);

        votingSessionService = new VotingSessionService(et_baseAddress.getText().toString());

        btn_updateVotingSession.setOnClickListener(v -> {

            votingSessionService.updateBaseUrl(et_baseAddress.getText().toString());

            votingSessionService.getCurrentVotingSession().enqueue(new Callback<VotingSessionDTO>() {
                @Override
                public void onResponse(Call<VotingSessionDTO> call, Response<VotingSessionDTO> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getActivity(), "ERROR: " + response.code() + response.message(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    VotingSessionDTO dto = response.body();
                    if (dto != null) {
                        if (dto.getStartDate() != null) {
                            tv_startDate.setText(getString(R.string.startDate) + dto.getStartDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                        }
                        if (dto.getSessionState() != null) {
                            tv_sessionState.setText(getString(R.string.session) + dto.getSessionState().toString());
                        }
                        btn_join.setEnabled(dto.getSessionState() == null || dto.getSessionState().equals(SessionState.OPEN));
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
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.action_joinFragment_to_questionAnswerFragment);
            }
        });

        return view;
    }
}
