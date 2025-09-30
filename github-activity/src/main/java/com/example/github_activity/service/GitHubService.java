package com.example.github_activity.service;
import com.example.github_activity.model.Activity;
import com.example.github_activity.exception.GitHubApiException;
import java.time.Instant;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GitHubService {
    private final ObjectMapper mapper=new ObjectMapper();
    private final HttpClient httpClient=HttpClient.newHttpClient();

    private static class CacheEntry{
        final long timestamp;
        final List<Activity> data;
        CacheEntry(long timestamp,List<Activity> data){
            this.timestamp=timestamp;
            this.data=data;
        }
    }

    private final Map<String,CacheEntry> cache=new ConcurrentHashMap<>();
    private final long ttlMillis=120_000;

    public List<Activity> getUserEvents(String username,String optinalToken) throws GitHubApiException{
        username=username.trim().toLowerCase();
        CacheEntry entry=cache.get(username);
        long now=System.currentTimeMillis();
        if(entry!=null && (now-entry.timestamp)<ttlMillis){
            return entry.data;
        }

        List<Activity> events = fetchFromGithub(username,optinalToken);
        cache.put(username,new CacheEntry(now, events));
        return events;
    }

    private List<Activity> fetchFromGithub(String username,String optinalToken) throws GitHubApiException{
        try{
            String encoded=URLEncoder.encode(username,StandardCharsets.UTF_8);
            String url="https://api.github.com/users/"+encoded+"/events";
            HttpRequest.Builder reqB=HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "github-activity-app");

            String token = optinalToken!=null && !optinalToken.isBlank() ? optinalToken : System.getenv("GITHUB");
            if(token!=null && !token.isBlank()){
                reqB.header("Authorization","Bearer "+token.trim());
            }

            HttpRequest req=reqB.build();
            HttpResponse<String> resp=httpClient.send(req,HttpResponse.BodyHandlers.ofString());
            int status=resp.statusCode();

            if(status==404){
                throw new GitHubApiException("User not found: "+username);
            }
            else if(status==403){
                throw new GitHubApiException("GitHub API returned 403 - you may be rate-limited. Try using a token. ");
            }
            else if(status>=400){
                throw new GitHubApiException("GitHub API error: HTTP "+status);
            }
            String body=resp.body();
            JsonNode root=mapper.readTree(body);
            if(!root.isArray()){
                return Collections.emptyList();
            }

            List<Activity> result=new ArrayList<>();
            for(JsonNode ev:root){
                Activity a=new Activity();
                a.setId(ev.path("id").asText(""));
                String type=ev.path("type").asText("");
                a.setType(type);
                a.setRepoName(ev.path("repo").path("name").asText(""));
                a.setActorLogin(ev.path("actor").path("login").asText(""));
                a.setActorAvatarUrl(ev.path("actor").path("avatar_url").asText(""));
                a.setCreatedAt(parseInstantSafe(ev.path("created_at").asText(null)));
                a.setHtmlUrl(a.getRepoName()!=null && !a.getRepoName().isBlank()?("https://github.com/"+a.getRepoName()):null);

                List<String> details=new ArrayList<>();
                String actionText=type;
                JsonNode payload=ev.path("payload");
            }

        }
        catch{

        }
    }

    private static Instant parseInstantSafe(String s){
        try{
            if(s==null || s.isBlank()){
                return null;
            }
            return Instant.parse(s);
        }
        catch(Exception e){
            return null;
        }
    }
}
