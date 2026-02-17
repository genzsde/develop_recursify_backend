package com.recursify.recursify.repository;

import com.recursify.recursify.model.SharedQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharedQuestionRepository extends JpaRepository<SharedQuestion, Long> {

    Optional<SharedQuestion> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
