package com.example.github_activity.model;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Activity {
    private String id;
    private String type;
    private String repoName;
    private String actorLogin;
    private String actorAvatarUrl;
    private String actionText;
    private List<String> details;
    private Instant createdAt;
    private String htmlUrl;
    
    public Activity(){}

    public String getRelativeTimeString(){
        if(createdAt==null){
            return "";
        }
        DateTimeFormatter f=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Kolkata"));
        return f.format(createdAt);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getRepoName() {
        return repoName;
    }
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
    public String getActorLogin() {
        return actorLogin;
    }
    public void setActorLogin(String actorLogin) {
        this.actorLogin = actorLogin;
    }
    public String getActorAvatarUrl() {
        return actorAvatarUrl;
    }
    public void setActorAvatarUrl(String actorAvatarUrl) {
        this.actorAvatarUrl = actorAvatarUrl;
    }
    public String getActionText() {
        return actionText;
    }
    public void setActionText(String actionText) {
        this.actionText = actionText;
    }
    public List<String> getDetails() {
        return details;
    }
    public void setDetails(List<String> details) {
        this.details = details;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public String getHtmlUrl() {
        return htmlUrl;
    }
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }
}
