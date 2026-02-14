package com.recursify.recursify.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Integer questionNumber;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String link;

    private LocalDate lastSolvedDate;
    private LocalDate nextRevisionDate;
    private Integer solveCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
