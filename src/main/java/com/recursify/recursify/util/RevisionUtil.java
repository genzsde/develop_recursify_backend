package com.recursify.recursify.util;

import com.recursify.recursify.model.Difficulty;

import java.time.LocalDate;

public class RevisionUtil {

    public static LocalDate calculateNextRevision(Difficulty difficulty) {

        LocalDate today = LocalDate.now();

        return switch (difficulty) {
            case HARD -> today.plusDays(2);
            case MEDIUM -> today.plusDays(3);
            case EASY -> today.plusDays(5);
        };
    }
}
