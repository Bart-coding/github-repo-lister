package com.example.githubrepolister.dto;

public record GithubBranch(String name, Commit commit) {
    public record Commit(String sha) {
    }
}
