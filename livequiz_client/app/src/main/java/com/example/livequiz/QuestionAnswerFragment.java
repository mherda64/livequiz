package com.example.livequiz;

import static android.content.ContentValues.TAG;

import static com.example.livequiz.Constants.DEST_ADDRESS;
import static com.example.livequiz.Constants.VOTING_SESSION_DTO;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.livequiz.answer.AnswerDTO;
import com.example.livequiz.request.Mapper;
import com.example.livequiz.session.SessionState;
import com.example.livequiz.session.VotingSessionService;
import com.example.livequiz.session.dto.VotingSessionDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.dto.StompMessage;

public class QuestionAnswerFragment extends Fragment {

    private QuestionAnswerFragment questionAnswerFragment;

    private VotingSessionService votingSessionService;
    private QuizApplication quizApplication;

    private String destAddress;
    private VotingSessionDTO votingSessionDTO;

    private Map<Long, Boolean> userAnswers;
    private Disposable socketDisposable;

    private TextView tv_questionId;
    private TextView tv_questionContent;
    private LinearLayout answerLayout;
    private ImageView imageView;
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

        subscribeOnSocket();

        tv_questionId = view.findViewById(R.id.tv_questionId);
        tv_questionContent = view.findViewById(R.id.tv_questionContent);
        answerLayout = view.findViewById(R.id.answerLayout);
        imageView = view.findViewById(R.id.answerImageView);
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
            checkBox.setTextSize(20);
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
        }

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

        btn_voteButton.setOnClickListener(v -> {

            List<Long> chosenIds = userAnswers.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            votingSessionService.sendVote(chosenIds).enqueue(new Callback<VotingSessionDTO>() {
                @Override
                public void onResponse(Call<VotingSessionDTO> call, Response<VotingSessionDTO> response) {
                    quizApplication.addVote(votingSessionDTO.getId());
                    if (response.body() != null)
                        NavHostFragment.findNavController(questionAnswerFragment)
                                .navigate(QuestionAnswerFragmentDirections
                                        .actionQuestionAnswerFragmentToResultsFragment(destAddress, response.body()));
                    if (response.code() >= 400) {
                        votingSessionService.getCurrentVotingSession().enqueue(new Callback<VotingSessionDTO>() {
                            @Override
                            public void onResponse(Call<VotingSessionDTO> call, Response<VotingSessionDTO> response) {
                                NavHostFragment.findNavController(questionAnswerFragment)
                                        .navigate(QuestionAnswerFragmentDirections
                                                .actionQuestionAnswerFragmentToResultsFragment(destAddress, response.body()));
                            }

                            @Override
                            public void onFailure(Call<VotingSessionDTO> call, Throwable t) {
                                NavHostFragment.findNavController(questionAnswerFragment)
                                        .navigate(QuestionAnswerFragmentDirections.actionQuestionAnswerFragmentToJoinFragment());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<VotingSessionDTO> call, Throwable t) {
                    Toast.makeText(getActivity(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });


        return view;
    }

    private void subscribeOnSocket() {
        Flowable<StompMessage> socketFlowable = quizApplication.getStompMessageFlowable();
        socketDisposable = socketFlowable.subscribe(message -> {
            votingSessionDTO = Mapper.get().readValue(message.getPayload(), VotingSessionDTO.class);
            if (SessionState.shouldDisconnect(votingSessionDTO.getSessionState())) {
                new Handler(Looper.getMainLooper()).post(() ->
                        NavHostFragment.findNavController(this)
                                .navigate(QuestionAnswerFragmentDirections.actionQuestionAnswerFragmentToJoinFragment()));

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        socketDisposable.dispose();
    }
}