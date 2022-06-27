package com.example.livequiz;

import static com.example.livequiz.Constants.DEST_ADDRESS;
import static com.example.livequiz.Constants.VOTING_SESSION_DTO;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livequiz.answer.AnswerDTO;
import com.example.livequiz.request.Mapper;
import com.example.livequiz.session.SessionState;
import com.example.livequiz.session.VotingSessionService;
import com.example.livequiz.session.dto.VotingSessionDTO;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ResultsFragment extends Fragment {

    private QuizApplication quizApplication;
    private VotingSessionService votingSessionService;
    private VotingSessionDTO votingSessionDTO;
    private String destAddress;

    private TextView tv_questionId;
    private TextView tv_questionContent;
    private LinearLayout answerLayout;
    private ImageView imageView;
    private Disposable socketDisposable;
    private PieChart pieChart;
    private List<Integer> colors = Arrays.asList(
            Color.parseColor("#6768e1"),
            Color.parseColor("#e065e1"),
            Color.parseColor("#e3e26a"),
            Color.parseColor("#65dd64"),
            Color.parseColor("#f26379")
    );


    public ResultsFragment() {
    }

    public static ResultsFragment newInstance() {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destAddress = getArguments().getString(DEST_ADDRESS);
            votingSessionDTO = (VotingSessionDTO) getArguments().get(VOTING_SESSION_DTO);
        }
        quizApplication = (QuizApplication) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        subscribeOnSocket();

        tv_questionId = view.findViewById(R.id.tv_questionIdResult);
        tv_questionContent = view.findViewById(R.id.tv_questionContentResult);
        answerLayout = view.findViewById(R.id.answerLayoutResult);
        imageView = view.findViewById(R.id.resultsImageView);
        pieChart = view.findViewById(R.id.chart);
        pieChart.animateXY(1000, 1000);

        votingSessionService = new VotingSessionService(destAddress);
        votingSessionService.getImage(votingSessionDTO.getQuestion().getId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        byte[] imageBytes = response.body().bytes();
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "ERROR: Could not decode image", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        updateQuestion(votingSessionDTO);

        return view;
    }

    private void updateQuestion(VotingSessionDTO dto) {
        tv_questionId.setText(getString(R.string.questionId) + dto.getId());
        tv_questionContent.setText(getString(R.string.questionContent) + dto.getQuestion().getContent());

        List<AnswerDTO> answers = dto.getQuestion().getAnswers();
        List<PieEntry> entrySet = new ArrayList<>();

        answerLayout.removeAllViewsInLayout();

        for (int i = 0; i < answers.size(); i++) {
            AnswerDTO answer = answers.get(i);
            TextView answerTextView = new TextView(getActivity());
            String answerText = answer.getContent() + " : " + dto.getResult().get(answer.getId());

            if (dto.getSessionState().equals(SessionState.FINISHED_RESULTS))
                answerText = answerText + " " +
                        (answer.isCorrect() ? getString(R.string.correct) : getString(R.string.incorrect));
            answerTextView.setText(answerText);
            answerTextView.setTextSize(20);

            Long result = dto.getResult().get(answer.getId());
            if (result > 0)
                entrySet.add(new PieEntry(result, answer.getContent()));

            answerLayout.addView(answerTextView);
        }

        PieDataSet pieDataSet = new PieDataSet(entrySet, getString(R.string.questionContent));
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieChart.setData(pieData);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    private void subscribeOnSocket() {
        Flowable<StompMessage> socketFlowable = quizApplication.getStompMessageFlowable();
        socketDisposable = socketFlowable.subscribe(message -> {
            votingSessionDTO = Mapper.get().readValue(message.getPayload(), VotingSessionDTO.class);
            if (SessionState.shouldDisconnect(votingSessionDTO.getSessionState())) {
                new Handler(Looper.getMainLooper()).post(() ->
                        NavHostFragment.findNavController(this)
                                .navigate(ResultsFragmentDirections.actionResultsFragmentToJoinFragment()));
                return;
            }

            new Handler(Looper.getMainLooper()).post(() -> updateQuestion(votingSessionDTO));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socketDisposable.dispose();
    }
}