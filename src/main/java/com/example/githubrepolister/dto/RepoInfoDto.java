package com.example.githubrepolister.dto;

import java.util.List;

public record RepoInfoDto(String repositoryName, String ownerLogin, List<BranchInfoDto> branches) {}
