package com.recursify.recursify.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "shared_questions")
public class SharedQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slug;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;


    private Integer questionNumber;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
}
