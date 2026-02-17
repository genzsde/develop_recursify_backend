package com.recursify.recursify.dto;

import com.recursify.recursify.model.Difficulty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionRequestDto {
    private String title;
    private String description;
    private Integer questionNumber;
    private Difficulty difficulty;
    private String link;
    private String slug;

}
