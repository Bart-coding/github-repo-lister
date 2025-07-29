package com.example.githubrepolister.dto;

public record GithubRepo(String name, Owner owner, boolean fork) {
    public record Owner(String login) {
    }
}
