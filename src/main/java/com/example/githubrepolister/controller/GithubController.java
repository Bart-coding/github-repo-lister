package com.example.githubrepolister.controller;

import com.example.githubrepolister.config.ApiPaths;
import com.example.githubrepolister.dto.RepoView;
import com.example.githubrepolister.service.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.USERS)
public class GithubController {

    private final GithubService githubService;

    public GithubController (GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}" + ApiPaths.REPOS_SUFFIX)
    public ResponseEntity<List<RepoView>> getUserRepos(@PathVariable String username) {
        List<RepoView> repos = githubService.listUserRepos((username));
        return ResponseEntity.ok(repos);
    }
}
