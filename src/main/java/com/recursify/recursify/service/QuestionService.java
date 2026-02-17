package com.recursify.recursify.service;

import com.recursify.recursify.dto.HeatmapDto;
import com.recursify.recursify.dto.QuestionRequestDto;
import com.recursify.recursify.dto.QuestionResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;


import java.util.List;

public interface QuestionService {

    QuestionResponseDto addQuestion(QuestionRequestDto dto, String userEmail);

    List<QuestionResponseDto> getAllQuestions(String userEmail);

    List<QuestionResponseDto> getTodayRevision(String userEmail);

    void deleteQuestion(Long id, String userEmail);

    QuestionResponseDto updateDifficulty(Long id, String difficulty, String userEmail);

    QuestionResponseDto updateQuestion(Long id, QuestionRequestDto dto, String email);

    void markSolved(Long id, String userEmail);
    List<HeatmapDto> getHeatmap(String email);

    List<QuestionResponseDto> getSmartQueue(String email);

    Page<QuestionResponseDto> getPaginated(String email, int page, int size);


}
