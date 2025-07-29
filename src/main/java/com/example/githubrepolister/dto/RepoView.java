package com.example.githubrepolister.dto;

import java.util.List;

public record RepoView(
        String repositoryName,
        String ownerLogin,
        List<BranchInfo> branches
) {
}
