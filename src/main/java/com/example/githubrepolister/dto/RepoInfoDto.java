package com.example.githubrepolister.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RepoInfoDto(
        @JsonProperty("repositoryName")
        String name,
        String ownerLogin,
        List<BranchInfoDto> branches
) {}
