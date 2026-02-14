package com.recursify.recursify.service.impl;
import com.recursify.recursify.dto.HeatmapDto;
import com.recursify.recursify.dto.QuestionRequestDto;
import com.recursify.recursify.dto.QuestionResponseDto;
import com.recursify.recursify.model.*;
import com.recursify.recursify.repository.QuestionRepository;
import com.recursify.recursify.repository.UserRepository;
import com.recursify.recursify.service.QuestionService;
import com.recursify.recursify.util.RevisionUtil;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QuestionResponseDto addQuestion(QuestionRequestDto dto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (questionRepository.existsByQuestionNumberAndUserId(dto.getQuestionNumber(), user.getId())) {
        throw new RuntimeException("Question number already exists. Please use a different question number.");
        }

        LocalDate today = LocalDate.now();

        Question question = Question.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .questionNumber(dto.getQuestionNumber())
                .difficulty(dto.getDifficulty())
                .link(dto.getLink())
                .solveCount(1)                     
                .lastSolvedDate(today)             
                .nextRevisionDate(
                        RevisionUtil.calculateNextRevision(dto.getDifficulty())
                )                                  
                .user(user)
                .build();

        Question saved = questionRepository.save(question);

        
        if (user.getLastSolvedDate() != null &&
            user.getLastSolvedDate().equals(today.minusDays(1))) {

            user.setStreakCount(user.getStreakCount() + 1);
        } else if (user.getLastSolvedDate() == null ||
                !user.getLastSolvedDate().equals(today)) {

            user.setStreakCount(1);
        }

        user.setLastSolvedDate(today);
        userRepository.save(user);

        return mapToDto(saved);
    }


    @Override
    public List<QuestionResponseDto> getAllQuestions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return questionRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDto> getTodayRevision(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> dueQuestions =
                questionRepository.findByUserIdAndNextRevisionDateLessThanEqual(
                        user.getId(), LocalDate.now());

        return dueQuestions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markSolved(Long id, String email) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getUser().getEmail().equals(email))
            throw new RuntimeException("Unauthorized");

        question.setSolveCount(question.getSolveCount() + 1);
        question.setLastSolvedDate(LocalDate.now());
        question.setNextRevisionDate(
                RevisionUtil.calculateNextRevision(question.getDifficulty()));

        questionRepository.save(question);

        User user = question.getUser();
        LocalDate today = LocalDate.now();

        if (user.getLastSolvedDate() != null &&
            user.getLastSolvedDate().equals(today.minusDays(1))) {

            user.setStreakCount(user.getStreakCount() + 1);
        } else if (user.getLastSolvedDate() == null ||
                   !user.getLastSolvedDate().equals(today)) {

            user.setStreakCount(1);
        }

        user.setLastSolvedDate(today);
        userRepository.save(user);
    }

    @Override
    public QuestionResponseDto updateDifficulty(Long id, String difficulty, String email) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getUser().getEmail().equals(email))
            throw new RuntimeException("Unauthorized");

        Difficulty newDiff = Difficulty.valueOf(difficulty.toUpperCase());

        question.setDifficulty(newDiff);
        question.setNextRevisionDate(RevisionUtil.calculateNextRevision(newDiff));

        Question saved = questionRepository.save(question);

        return mapToDto(saved);
    }

    @Override
    public void deleteQuestion(Long id, String email) {

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        if (!question.getUser().getEmail().equals(email))
            throw new RuntimeException("Unauthorized");

        questionRepository.delete(question);
    }

    @Override
    public List<HeatmapDto> getHeatmap(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return questionRepository.getDailySolveCount(user.getId())
                .stream()
                .map(obj -> new HeatmapDto(
                        (LocalDate) obj[0],
                        ((Long) obj[1]).intValue()))
                .toList();
    }

    @Override
    public List<QuestionResponseDto> getSmartQueue(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        List<Question> result = new ArrayList<>();

        result.addAll(questionRepository
                .findTop5ByUserIdAndDifficultyAndNextRevisionDateLessThanEqual(
                        user.getId(), Difficulty.HARD, today));

        result.addAll(questionRepository
                .findTop5ByUserIdAndDifficultyAndNextRevisionDateLessThanEqual(
                        user.getId(), Difficulty.MEDIUM, today));

        result.addAll(questionRepository
                .findTop5ByUserIdAndDifficultyAndNextRevisionDateLessThanEqual(
                        user.getId(), Difficulty.EASY, today));

        return result.stream().map(this::mapToDto).toList();
    }

    @Override
    public Page<QuestionResponseDto> getPaginated(String email, int page, int size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        return questionRepository.findByUserId(user.getId(), pageable)
                .map(this::mapToDto);
    }

    private QuestionResponseDto mapToDto(Question q) {
        return QuestionResponseDto.builder()
                .id(q.getId())
                .title(q.getTitle())
                .description(q.getDescription())
                .questionNumber(q.getQuestionNumber())
                .difficulty(q.getDifficulty())
                .nextRevisionDate(q.getNextRevisionDate())
                .solveCount(q.getSolveCount())
                .link(q.getLink())
                .build();
    }
}
