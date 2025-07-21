package com.example.githubrepolister.service;

import com.example.githubrepolister.dto.*;
import com.example.githubrepolister.exception.UserNotFoundException;
import com.example.githubrepolister.utils.GithubApiConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final RestTemplate restTemplate;
    private final String githubApiBaseUrl;

    public GithubService(
        RestTemplate restTemplate,
        @Value("${github.api.base-url}") String githubApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.githubApiBaseUrl = githubApiBaseUrl;
    }

    public List<RepoInfoDto> listUserRepos(String username) {

        String reposUrl = githubApiBaseUrl + String.format(GithubApiConstants.USERS_REPOS_SOURCES, username);

        try {
            ResponseEntity<List<GithubRepoDto>> response = restTemplate.exchange(
                    reposUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
                return response.getBody().stream()
                        .filter(repoDto -> !repoDto.fork())
                        .map(repoDto -> {
                            List<BranchInfoDto> branches = listRepoBranches(repoDto.owner().login(), repoDto.name());
                            return new RepoInfoDto(repoDto.name(), repoDto.owner().login(), branches);
                        })
                        .collect(Collectors.toList());
            } else {
                return List.of();
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException(username);
        }
    }

    private List<BranchInfoDto> listRepoBranches(String ownerLogin, String repoName) {

        String branchesUrl = githubApiBaseUrl + String.format(GithubApiConstants.REPOS_BRANCHES, ownerLogin, repoName);

        ResponseEntity<List<GithubBranchDto>> response = restTemplate.exchange(
                branchesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
            return response.getBody().stream()
                    .map(branchDto -> new BranchInfoDto(branchDto.name(), branchDto.commit().sha()))
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }
}
