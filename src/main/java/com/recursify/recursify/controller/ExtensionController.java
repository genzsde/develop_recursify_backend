package com.recursify.recursify.controller;

import com.recursify.recursify.dto.QuestionRequestDto;
import com.recursify.recursify.dto.QuestionResponseDto;
import com.recursify.recursify.model.SharedQuestion;
import com.recursify.recursify.service.ExtensionService;
import com.recursify.recursify.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/extension")
@RequiredArgsConstructor
public class ExtensionController {

    private final ExtensionService extensionService;

    @GetMapping("/user-question-exists")
    public boolean userQuestionExists(@RequestParam String slug) {
        return extensionService.userHasQuestion(slug);
    }

    @GetMapping("/shared-question")
    public SharedQuestion getShared(@RequestParam String slug) {
        return extensionService.getShared(slug);
    }

    @PostMapping("/save-shared")
    public void saveShared(@RequestBody SharedQuestion question) {
        extensionService.saveShared(question);
    }

    @PostMapping("/add-to-user")
    public QuestionResponseDto addToUser(@RequestBody QuestionRequestDto dto) {
        return extensionService.addQuestion(dto, SecurityUtil.getCurrentUserEmail());
    }

    @PostMapping("/process")
    public void process(@RequestParam String slug,
                        @RequestParam Long solvedAt,
                        @RequestParam String timezone) {
        extensionService.processSlug(slug, solvedAt, timezone);
    }

}
