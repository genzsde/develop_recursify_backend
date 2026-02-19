package com.recursify.recursify.service.impl;

import com.recursify.recursify.dto.QuestionRequestDto;
import com.recursify.recursify.dto.QuestionResponseDto;
import com.recursify.recursify.model.SharedQuestion;
import com.recursify.recursify.repository.QuestionRepository;
import com.recursify.recursify.repository.SharedQuestionRepository;
import com.recursify.recursify.repository.UserRepository;
import com.recursify.recursify.service.ExtensionService;
import com.recursify.recursify.service.LeetCodeGraphQLService;
import com.recursify.recursify.service.QuestionService;
import com.recursify.recursify.util.SecurityUtil;
import com.recursify.recursify.model.Question;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExtensionServiceImpl implements ExtensionService {

    private final QuestionRepository questionRepository;
    private final SharedQuestionRepository sharedRepo;
    private final UserRepository userRepository;
    private final LeetCodeGraphQLService leetCodeGraphQLService;
    private final QuestionService questionService;

    @Override
    public boolean userHasQuestion(String slug) {
        String email = SecurityUtil.getCurrentUserEmail();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        return questionRepository.existsBySlugAndUserId(slug, userId);
    }

    @Override
    public SharedQuestion getShared(String slug) {
        return sharedRepo.findBySlug(slug).orElse(null);
    }

    @Override
    public void saveShared(SharedQuestion q) {
        if (!sharedRepo.existsBySlug(q.getSlug())) {
            sharedRepo.save(q);
        }
    }


    @Override
    public QuestionResponseDto addQuestion(QuestionRequestDto dto, String email) {
        return questionService.addQuestion(dto, email);
    }

 
    @Override
    public void processSlug(String slug, Long solvedAt, String timezone) {

        LocalDate solvedDate = Instant.ofEpochMilli(solvedAt)
        .atZone(ZoneId.of(timezone))
        .toLocalDate();

        String email = SecurityUtil.getCurrentUserEmail();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();

       
        Optional<Question> existingOpt =
                questionRepository.findBySlugAndUserId(slug, userId);

        if (existingOpt.isPresent()) {

            Question q = existingOpt.get();

            if (q.getNextRevisionDate() != null &&
                q.getNextRevisionDate().equals(solvedDate)) {

                questionService.markSolved(q.getId(), email);
            }

            return;
        }

        SharedQuestion shared = sharedRepo.findBySlug(slug).orElse(null);

        if (shared == null) {
            shared = leetCodeGraphQLService.fetchQuestion(slug);
            sharedRepo.save(shared);
        }

       
        if (shared.getQuestionNumber() == null || shared.getQuestionNumber() == 0) {
            throw new RuntimeException("Invalid question number from LeetCode");
        }


        QuestionRequestDto dto = QuestionRequestDto.builder()
                .slug(slug)
                .title(shared.getTitle())
                .description(shared.getDescription())
                .questionNumber(shared.getQuestionNumber())
                .difficulty(shared.getDifficulty())
                .link("https://leetcode.com/problems/" + slug)
                .build();

        questionService.addQuestion(dto, email);
    }

}
