package com.recursify.recursify.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"questionNumber", "user_id"})
)
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
