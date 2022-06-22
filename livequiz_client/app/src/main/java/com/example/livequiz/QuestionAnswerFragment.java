package com.example.livequiz;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livequiz.answer.AnswerDTO;
import com.example.livequiz.session.VotingSessionService;
import com.example.livequiz.session.dto.VotingSessionDTO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionAnswerFragment extends Fragment {

    private QuestionAnswerFragment questionAnswerFragment;

    private VotingSessionService votingSessionService;
    private QuizApplication quizApplication;

    private static final String DEST_ADDRESS = "destAddress";
    private static final String VOTING_SESSION_DTO = "votingSessionDto";

    private String destAddress;
    private VotingSessionDTO votingSessionDTO;

    private Map<Long, Boolean> userAnswers;

    private TextView tv_questionId;
    private TextView tv_questionContent;
    private LinearLayout answerLayout;
    private Button btn_voteButton;

    public QuestionAnswerFragment() {
    }

    public static QuestionAnswerFragment newInstance(String destAddress) {
        QuestionAnswerFragment fragment = new QuestionAnswerFragment();
        Bundle args = new Bundle();
        args.putString(DEST_ADDRESS, destAddress);
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
        questionAnswerFragment = this;
        quizApplication = (QuizApplication) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_answer, container, false);

        tv_questionId = view.findViewById(R.id.tv_questionId);
        tv_questionContent = view.findViewById(R.id.tv_questionContent);
        answerLayout = view.findViewById(R.id.answerLayout);
        btn_voteButton = view.findViewById(R.id.btn_voteButton);

        votingSessionService = new VotingSessionService(destAddress);

        tv_questionId.setText(getString(R.string.questionId) + votingSessionDTO.getId());
        tv_questionContent.setText(getString(R.string.questionContent) + votingSessionDTO.getQuestion().getContent());

        List<AnswerDTO> answers = votingSessionDTO.getQuestion().getAnswers();
        userAnswers = answers.stream()
                .map(AnswerDTO::getId)
                .collect(Collectors.toMap(Function.identity(), e -> Boolean.FALSE));

        for (int i = 0; i < answers.size(); i++) {
            AnswerDTO answer = answers.get(i);
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setId(answer.getId().intValue());
            checkBox.setText(answer.getContent());
            checkBox.setTag(answer.getId());

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    Log.d(TAG, "Value is checked = " + compoundButton.getTag());
                } else {
                    Log.d(TAG, "Value is unchecked = " + compoundButton.getTag());
                }
                userAnswers.put((Long) compoundButton.getTag(), isChecked);
            });

            answerLayout.addView(checkBox);

            btn_voteButton.setOnClickListener(v -> {

                List<Long> chosenIds = userAnswers.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                votingSessionService.sendVote(chosenIds).enqueue(new Callback<VotingSessionDTO>() {
                    @Override
                    public void onResponse(Call<VotingSessionDTO> call, Response<VotingSessionDTO> response) {
                        Toast.makeText(getActivity(), "SUCCESS: " + response, Toast.LENGTH_LONG).show();
                        quizApplication.addVote(votingSessionDTO.getId());
                        NavHostFragment.findNavController(questionAnswerFragment)
                                .navigate(QuestionAnswerFragmentDirections.actionQuestionAnswerFragmentToResultsFragment());
                    }

                    @Override
                    public void onFailure(Call<VotingSessionDTO> call, Throwable t) {
                        Toast.makeText(getActivity(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });

        }
        return view;
    }
}