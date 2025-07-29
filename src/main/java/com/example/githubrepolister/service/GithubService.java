package com.example.githubrepolister.service;

import com.example.githubrepolister.dto.BranchInfo;
import com.example.githubrepolister.dto.GithubBranch;
import com.example.githubrepolister.dto.GithubRepo;
import com.example.githubrepolister.dto.RepoView;
import com.example.githubrepolister.exception.UserNotFoundException;
import com.example.githubrepolister.utils.GithubApiConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final RestTemplate restTemplate;
    private final String githubApiBaseUrl;
    private final Executor taskExecutor;

    public GithubService(
            RestTemplate restTemplate,
            @Value("${github.api.base-url}") String githubApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.githubApiBaseUrl = githubApiBaseUrl;
        this.taskExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public List<RepoView> listUserRepos(String username) {

        try {
            ResponseEntity<List<GithubRepo>> response = restTemplate.exchange(
                    buildGithubApiUrl(GithubApiConstants.USERS_REPOS_SOURCES, username),
                    HttpMethod.GET,
                    createJsonAcceptHttpEntity(),
                    new ParameterizedTypeReference<>() {
                    }
            );

            List<GithubRepo> repos = response.getBody();

            if (repos == null) {
                return List.of();
            }

            List<CompletableFuture<RepoView>> futures = repos.stream()
                    .filter(repo -> !repo.fork())
                    .map(repo -> CompletableFuture.supplyAsync(
                                    () -> listRepoBranches(repo.owner().login(), repo.name()),
                                    taskExecutor
                            ).thenApply(branches -> new RepoView(repo.name(), repo.owner().login(), branches))
                    ).toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());


        } catch (HttpClientErrorException.NotFound ex) {
            throw new UserNotFoundException(username);
        }
    }

    private List<BranchInfo> listRepoBranches(String ownerLogin, String repoName) {

        try {
            ResponseEntity<List<GithubBranch>> response = restTemplate.exchange(
                    buildGithubApiUrl(GithubApiConstants.REPOS_BRANCHES, ownerLogin, repoName),
                    HttpMethod.GET,
                    createJsonAcceptHttpEntity(),
                    new ParameterizedTypeReference<>() {
                    }
            );

            List<GithubBranch> branches = response.getBody();

            if (branches == null) {
                return List.of();
            }

            return branches.stream()
                    .map(branch -> new BranchInfo(branch.name(), branch.commit().sha()))
                    .collect(Collectors.toList());

        } catch (RestClientException ex) {
            return List.of();
        }
    }

    private String buildGithubApiUrl(String path, Object... uriVariables) {
        return UriComponentsBuilder.fromUriString(githubApiBaseUrl)
                .path(path)
                .buildAndExpand(uriVariables)
                .toUriString();
    }

    private HttpEntity<?> createJsonAcceptHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept((List.of(MediaType.APPLICATION_JSON)));
        return new HttpEntity<>(headers);
    }
}
