package com.recursify.recursify.service;

import com.recursify.recursify.model.SharedQuestion;

public interface LeetCodeGraphQLService {
    SharedQuestion fetchQuestion(String slug);
}
