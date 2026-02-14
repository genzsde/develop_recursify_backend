package com.recursify.recursify.dto;

import com.recursify.recursify.model.Difficulty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class QuestionResponseDto {
    private Long id;
    private String title;
    private String description;
    private Integer questionNumber;
    private Difficulty difficulty;
    private LocalDate nextRevisionDate;
    private Integer solveCount;
    private String link;
}
