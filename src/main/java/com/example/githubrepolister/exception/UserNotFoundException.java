package com.example.githubrepolister.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public static final String MESSAGE_FORMAT = "GitHub user '%s' not found.";

    public UserNotFoundException(String username) {
        super(String.format(MESSAGE_FORMAT, username));
    }
}
