# GitHub Repo Lister

A simple and efficient RESTful API built with Spring Boot to list a user's public GitHub repositories.

---

## Project Description

This application provides a single endpoint that retrieves all public repositories (excluding forks) for a given GitHub username. For each repository, it returns a list of its branches along with the last commit SHA for each branch. The project emphasizes clean code, performance through asynchronous processing, and resilience to errors from the external GitHub API.

---

## Table of Contents

* [Project Description](#project-description)
* [Architecture](#architecture)
* [Technologies](#technologies)
* [Getting Started](#getting-started)
    * [Prerequisites](#prerequisites)
    * [Configuration](#configuration)
    * [Installation & Running](#installation--running)
* [Testing](#testing)
* [API Documentation](#api-documentation)
    * [Endpoint](#endpoint)
    * [Path Parameters](#path-parameters)
    * [Headers](#headers)
    * [Example Responses](#example-responses)
* [Key Implementation Aspects](#key-implementation-aspects)
* [Acknowledgements](#acknowledgements)
* [License](#license)

---

## Architecture

The application's logic follows a simple data flow:

`API Client ──> GitHub Repo Lister API ──> GitHub REST API`

---

## Technologies

* **Java 21**
* **Spring Boot 3.5.4**
* **Maven**
* **Lombok**
* **JUnit 5**
* **AssertJ**
* **RestTemplate** (for GitHub API communication)
* **CompletableFuture** (for asynchronous branch data fetching)

---

## Getting Started

### Prerequisites

* JDK 21 or later
* Apache Maven
* GitHub API Token (optional, but highly recommended)

### Configuration

### GitHub API Rate Limiting

To avoid issues with the public GitHub API, it's highly recommended to use a **Personal Access Token**. Without a token, the API applies a strict rate limit of 60 requests per hour for unauthenticated requests, which can cause the application to fail for users with many repositories. Using a token increases the limit significantly to 5,000 requests per hour. For more details on these limits, consult the official [GitHub documentation](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api).

1.  Generate a new token in your GitHub account under `Settings > Developer settings > Personal access tokens`. The `public_repo` scope is sufficient.
2.  Set the token as an environment variable named `GITHUB_API_TOKEN`:

    **Linux/macOS:**
    ```bash
    export GITHUB_API_TOKEN="your_token_here"
    ```

    **Windows (PowerShell):**
    ```powershell
    $env:GITHUB_API_TOKEN="your_token_here"
    ```
    The application will automatically detect and use the token if the variable is set.

### Installation & Running

1.  Clone the repository:
    ```bash
    git clone [YOUR_REPOSITORY_LINK]
    cd [YOUR_PROJECT_DIRECTORY_NAME]
    ```
2.  Build the project using Maven:
    ```bash
    mvn clean install
    ```
3.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
    The application will start on the default port `8080`.

---

## Testing

To run the unit and integration tests, execute the following command:

```bash
mvn test
````

The project includes a single integration test, as per the business requirements, which verifies the primary use case, also known as the *happy path*. Note that the test does not use mocks. It is an integration test that verifies the real API client's behavior and the service layer's data processing.

-----

## API Documentation

### Endpoint

Retrieves a list of repositories for a given user.

`GET /api/v1/users/{username}/repos`

### Path Parameters

| Parameter  | Type   | Required | Description            |
| :--------- | :----- | :------- | :--------------------- |
| `username` | string | Yes      | The GitHub username.   |

### Headers

| Header  | Value           | Required | Description                                |
| :------ | :-------------- | :------- | :----------------------------------------- |
| `Accept` | `application/json` | Yes      | Ensures the response is returned in JSON format. The application strictly enforces this header.|

### Example Responses

#### Success (200 OK)

A successful request returns a list of repositories and their branches.

**Request:** `GET /api/v1/users/octocat/repos`

**Response:**

```json
[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2"
      }
    ]
  },
  {
    "repositoryName": "Spoon-Knife",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "f1e2d3c4b5a6f1e2d3c4b5a6f1e2d3c4b5a6f1e2"
      },
      {
        "name": "test-branch",
        "lastCommitSha": "c9b8a7d6e5f4c9b8a7d6e5f4c9b8a7d6e5f4c9b8"
      }
    ]
  }
]
```

#### Error: User Not Found (404 Not Found)

**Request:** `GET /api/v1/users/a-non-existent-user-xyz-123/repos`

**Response:**

```json
{
  "status": 404,
  "message": "GitHub user 'a-non-existent-user-xyz-123' not found."
}
```

## Key Implementation Aspects

  * **Dedicated Error Handling:** The `UserNotFoundException` is caught by a dedicated `GlobalExceptionHandler` to provide a consistent `ErrorResponse` object. This centralized logic ensures clarity and a uniform format for "User not found" error.
  * **API Versioning:** To fulfill the business requirement for GitHub API `v3`, the application uses the `X-GitHub-Api-Version: 2022-11-28` header. This approach, recommended by GitHub, ensures long-term stability and resilience to potential future API changes.
  * **Resilience to External API Failures:** The original requirements only specified handling the "User Not Found" error. However, the implementation was extended to ensure service stability. If fetching branches for a single repository fails (e.g., due to a network error or a temporary GitHub API issue), the entire process is not interrupted. Instead, an empty list of branches is returned for that specific repository.
  * **Asynchronous Data Fetching (CompletableFuture):** Branch details for each repository are fetched in parallel. This approach prevents the application from being blocked or slowed down when fetching data for a large number of a user's repositories.
  * **Secure URL Construction (UriComponentsBuilder):** All external API URLs are built using `UriComponentsBuilder` instead of manual string formatting. This ensures proper parameter encoding, protects against potential errors, and aligns with Spring Framework best practices.
  * **Explicit Content Negotiation:** The application strictly enforces the response content type by validating the `Accept` header. This guarantees predictable response formats and prevents miscommunication between the client and the server.

-----

## Acknowledgements

The project was created with the assistance of the large language model Google Gemini, which served as a tool for generating code and documentation. The final structure and quality of the project are the result of diligent verification and user input.

-----

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.
