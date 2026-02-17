package com.recursify.recursify.controller;

import com.recursify.recursify.dto.*;
import com.recursify.recursify.service.QuestionService;
import com.recursify.recursify.util.SecurityUtil;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // ADD QUESTION
    @PostMapping
    public QuestionResponseDto add(@RequestBody QuestionRequestDto dto) {
        return questionService.addQuestion(dto, SecurityUtil.getCurrentUserEmail());
    }

    // DASHBOARD
    @GetMapping
    public List<QuestionResponseDto> getAll() {
        return questionService.getAllQuestions(SecurityUtil.getCurrentUserEmail());
    }

    // TODAY REVISION
    @GetMapping("/today")
    public List<QuestionResponseDto> today() {
        return questionService.getTodayRevision(SecurityUtil.getCurrentUserEmail());
    }

    // MARK SOLVED
    @PostMapping("/{id}/solve")
    public ResponseEntity<Void> solve(@PathVariable Long id) {
        questionService.markSolved(id, SecurityUtil.getCurrentUserEmail());
        return ResponseEntity.ok().build();
    }


    // UPDATE DIFFICULTY
    @PutMapping("/{id}/difficulty")
    public QuestionResponseDto updateDifficulty(
            @PathVariable Long id,
            @RequestParam String level) {
        return questionService.updateDifficulty(
                id, level, SecurityUtil.getCurrentUserEmail());
    }

    // // UPDATE QUESTION
     @PutMapping("/update/{id}")
    public QuestionResponseDto updateQuestion(
            @PathVariable Long id,
            @RequestBody QuestionRequestDto dto) {
        return questionService.updateQuestion(id, dto, SecurityUtil.getCurrentUserEmail());
    }


    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        questionService.deleteQuestion(id, SecurityUtil.getCurrentUserEmail());
        return "Deleted";
    }

    @GetMapping("/heatmap")
    public List<HeatmapDto> getHeatmap(Authentication auth) {
        return questionService.getHeatmap(auth.getName());
    }

    @GetMapping("/smart")
    public List<QuestionResponseDto> getSmartQueue(Authentication auth) {
        return questionService.getSmartQueue(auth.getName());
    }

    @GetMapping("/paged")
    public Page<QuestionResponseDto> getPaged(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return (Page<QuestionResponseDto>) questionService.getPaginated(auth.getName(), page, size);
    }



}
