package com.example.livequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livequiz.session.SessionState;
import com.example.livequiz.session.VotingSessionDTO;
import com.example.livequiz.session.VotingSessionService;

import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private VotingSessionService votingSessionService;

    private Button btn_updateVotingSession;
    private Button btn_join;
    private TextView tv_sessionState;
    private TextView tv_startDate;
    private EditText et_baseAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_updateVotingSession = findViewById(R.id.btn_updateVotingSession);
        btn_join = findViewById(R.id.btn_join);
        tv_sessionState = findViewById(R.id.tv_sessionState);
        tv_startDate = findViewById(R.id.tv_startDate);
        et_baseAddress = findViewById(R.id.et_baseAddress);

        votingSessionService = new VotingSessionService(et_baseAddress.getText().toString());

        btn_updateVotingSession.setOnClickListener(v -> {

            votingSessionService.updateBaseUrl(et_baseAddress.getText().toString());

            votingSessionService.getCurrentVotingSession().enqueue(new Callback<VotingSessionDTO>() {
                @Override
                public void onResponse(Call<VotingSessionDTO> call, Response<VotingSessionDTO> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "ERROR: " + response.code() + response.message(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    tv_startDate.setText(getString(R.string.startDate));
                    tv_sessionState.setText(getString(R.string.session));
                    btn_join.setEnabled(false);
                }
            });
        });
    }

}