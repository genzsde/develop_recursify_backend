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

import lombok.RequiredArgsConstructor;
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

    /**
     * Delegates to QuestionService (Single Source of Truth)
     */
    @Override
    public QuestionResponseDto addQuestion(QuestionRequestDto dto, String email) {
        return questionService.addQuestion(dto, email);
    }

    /**
     * Main Extension Flow (Idempotent + Optimized)
     */
    @Override
    public void processSlug(String slug) {

        String email = SecurityUtil.getCurrentUserEmail();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();

        // 1️⃣ Skip if already exists
        if (questionRepository.existsBySlugAndUserId(slug, userId)) {
            return;
        }

        // 2️⃣ Get from shared
        SharedQuestion shared = sharedRepo.findBySlug(slug).orElse(null);

        // 3️⃣ Fetch from LeetCode if missing
        if (shared == null) {
            shared = leetCodeGraphQLService.fetchQuestion(slug);
            sharedRepo.save(shared);
        }

        // 4️⃣ Safety check
        if (shared.getQuestionNumber() == null || shared.getQuestionNumber() == 0) {
            throw new RuntimeException("Invalid question number from LeetCode");
        }

        // 5️⃣ Convert → DTO (FIXED: slug added)
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
