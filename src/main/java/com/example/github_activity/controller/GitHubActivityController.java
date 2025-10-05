package com.example.github_activity.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.github_activity.exception.GitHubApiException;
import com.example.github_activity.model.Activity;
import com.example.github_activity.service.GitHubService;

@Controller
public class GitHubActivityController {
    private final GitHubService gitHubService;
    
    public GitHubActivityController(GitHubService gitHubService){
        this.gitHubService=gitHubService;
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/user/{username}")
    public String userActivity(@PathVariable String username,@RequestParam(required=false) String token,Model model){
        try{
            List<Activity> activities=gitHubService.getUserEvents(username, token);
            model.addAttribute("username",username);
            model.addAttribute("activities",activities);
            model.addAttribute("providedToken",token!=null && !token.isBlank());
            return "user";
        }
        catch(GitHubApiException e){
            model.addAttribute("error",e.getMessage());
            model.addAttribute("username",username);
            return "index";
        }
    }

    @GetMapping("/api/user/{username}/events")
    public List<Activity> apiUserEvents(@PathVariable String username, @RequestParam(required=false) String token) throws GitHubApiException{
        return gitHubService.getUserEvents(username, token);
    }
}
