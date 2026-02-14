package com.recursify.recursify.repository;

import com.recursify.recursify.model.Difficulty;
import com.recursify.recursify.model.Question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


import java.time.LocalDate;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("""
    SELECT q.lastSolvedDate, COUNT(q)
    FROM Question q
    WHERE q.user.id = :userId
    AND q.lastSolvedDate IS NOT NULL
    GROUP BY q.lastSolvedDate
    """)
    List<Object[]> getDailySolveCount(Long userId);


    List<Question> findByUserId(Long userId);
    Page<Question> findByUserId(Long userId, Pageable pageable);



    List<Question> findByUserIdAndNextRevisionDateLessThanEqual(Long userId, LocalDate date);
    boolean existsByQuestionNumberAndUserId(Integer questionNumber, Long userId);


    List<Question> findTop5ByUserIdAndDifficultyAndNextRevisionDateLessThanEqual(
        Long userId, Difficulty difficulty, LocalDate date);

}
