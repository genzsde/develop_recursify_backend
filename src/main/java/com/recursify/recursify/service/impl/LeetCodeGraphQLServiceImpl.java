package com.recursify.recursify.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recursify.recursify.model.Difficulty;
import com.recursify.recursify.model.SharedQuestion;
import com.recursify.recursify.service.LeetCodeGraphQLService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeetCodeGraphQLServiceImpl implements LeetCodeGraphQLService {

    private final RestTemplate restTemplate;     
    private final ObjectMapper objectMapper;   

    @Override
    public SharedQuestion fetchQuestion(String slug) {
        try {
            String url = "https://leetcode.com/graphql";

            String query = """
                query getQuestion($titleSlug: String!) {
                question(titleSlug: $titleSlug) {
                    title
                    questionId
                    difficulty
                    content
                }
                }
            """;

            Map<String, Object> bodyMap = Map.of(
                    "query", query,
                    "variables", Map.of("titleSlug", slug)
            );

            String body = objectMapper.writeValueAsString(bodyMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(MediaType.parseMediaTypes("application/json"));
            headers.set("User-Agent", "Mozilla/5.0");
            headers.set("Referer", "https://leetcode.com/problems/" + slug + "/");


            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());

            // If LeetCode returns error
            if (root.has("errors")) {
                throw new RuntimeException("LeetCode GraphQL error: " + root.get("errors").toString());
            }

            JsonNode q = root.path("data").path("question");

            if (q.isMissingNode() || q.isNull()) {
                throw new RuntimeException("Question not found on LeetCode");
            }

            return SharedQuestion.builder()
                    .slug(slug)
                    .title(q.get("title").asText())
                    .description(q.get("content").asText())
                    .questionNumber(q.get("questionId").asInt())
                    .difficulty(Difficulty.valueOf(q.get("difficulty").asText().toUpperCase()))
                    .build();

        } catch (Exception e) {
            e.printStackTrace(); 
            throw new RuntimeException("Failed to fetch from LeetCode GraphQL", e);
        }
    }
        
}
