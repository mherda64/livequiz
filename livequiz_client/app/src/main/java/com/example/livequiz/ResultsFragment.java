package com.example.livequiz;

import static android.content.ContentValues.TAG;
import static com.example.livequiz.Constants.VOTING_SESSION_DTO;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livequiz.answer.AnswerDTO;
import com.example.livequiz.question.QuestionDTO;
import com.example.livequiz.request.Mapper;
import com.example.livequiz.session.dto.VotingSessionDTO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ResultsFragment extends Fragment {

    private QuizApplication quizApplication;
    private VotingSessionDTO votingSessionDTO;

    private TextView tv_questionId;
    private TextView tv_questionContent;
    private LinearLayout answerLayout;

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

        updateQuestion(votingSessionDTO);

        return view;
    }

    private void updateQuestion(VotingSessionDTO dto) {
        tv_questionId.setText(getString(R.string.questionId) + dto.getId());
        tv_questionContent.setText(getString(R.string.questionContent) + dto.getQuestion().getContent());

        List<AnswerDTO> answers = dto.getQuestion().getAnswers();

        answerLayout.removeAllViewsInLayout();

        for (int i = 0; i < answers.size(); i++) {
            AnswerDTO answer = answers.get(i);
            TextView answerTextView = new TextView(getActivity());
            answerTextView.setText(answer.getContent() + " : " + dto.getResult().get(answer.getId()));

            answerLayout.addView(answerTextView);
        }
    }

    private void subscribeOnSocket() {
        Flowable<StompMessage> socketFlowable = quizApplication.getStompMessageFlowable();
        socketFlowable.subscribe(message -> {
            votingSessionDTO = Mapper.get().readValue(message.getPayload(), VotingSessionDTO.class);
            new Handler(Looper.getMainLooper()).post(() -> updateQuestion(votingSessionDTO));
        });
    }
}