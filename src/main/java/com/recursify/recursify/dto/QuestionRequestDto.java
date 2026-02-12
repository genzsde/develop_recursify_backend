package com.recursify.recursify.dto;

import com.recursify.recursify.model.Difficulty;
import lombok.Data;

@Data
public class QuestionRequestDto {
    private String title;
    private String description;
    private Integer questionNumber;
    private Difficulty difficulty;
}
