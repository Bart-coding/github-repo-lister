package com.example.githubrepolister.integration;

import com.example.githubrepolister.config.ApiPaths;
import com.example.githubrepolister.dto.RepoView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void shouldReturnReposForExistingUser() {

        String existingUsername = "octocat";
        String url = UriComponentsBuilder.fromPath("/api/v1")
                .path(ApiPaths.USERS_REPOS)
                .buildAndExpand(existingUsername)
                .toUriString();

        ResponseEntity<List<RepoView>> expectedResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(expectedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<RepoView> repos = expectedResponse.getBody();
        assertThat(repos).isNotNull().isNotEmpty();

        RepoView firstRepo = repos.getFirst();
        assertThat(firstRepo.repositoryName()).isNotBlank();
        assertThat(firstRepo.ownerLogin()).isEqualTo(existingUsername);
        assertThat(firstRepo.branches()).isNotNull().isNotEmpty();
        assertThat(firstRepo.branches().getFirst().name()).isNotBlank();
        assertThat(firstRepo.branches().getFirst().lastCommitSha()).isNotBlank();
    }
}
