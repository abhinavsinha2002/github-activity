package com.example.github_activity.exception;

public class GitHubApiException extends Exception {
    public GitHubApiException(String message){
        super(message);
    }

    public GitHubApiException(String message,Throwable cause){
        super(message,cause);
    }
}
