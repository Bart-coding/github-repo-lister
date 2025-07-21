package com.example.githubrepolister.dto;

public record GithubRepoDto(String name, Owner owner, boolean fork) {
    public record Owner(String login) {}
}
