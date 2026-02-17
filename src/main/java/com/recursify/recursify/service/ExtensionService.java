package com.recursify.recursify.service;

import com.recursify.recursify.dto.QuestionRequestDto;
import com.recursify.recursify.dto.QuestionResponseDto;
import com.recursify.recursify.model.SharedQuestion;

public interface ExtensionService {

    boolean userHasQuestion(String slug);

    SharedQuestion getShared(String slug);

    void saveShared(SharedQuestion question);

    QuestionResponseDto addQuestion(QuestionRequestDto dto, String email);

    void processSlug(String slug);
}
