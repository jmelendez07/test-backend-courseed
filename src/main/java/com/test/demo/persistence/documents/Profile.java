package com.test.demo.persistence.documents;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_profiles")
public class Profile {

    @Id
    private String id;
    private String userId;
    private String knowledgeLevel;
    private int availableHoursTime;
    private String platformPreference;
    private Double budget;
    private String interest;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatformPreference() {
        return platformPreference;
    }

    public void setPlatformPreference(String platformPreference) {
        this.platformPreference = platformPreference;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKnowledgeLevel() {
        return knowledgeLevel;
    }

    public void setKnowledgeLevel(String knowledgeLevel) {
        this.knowledgeLevel = knowledgeLevel;
    }

    public int getAvailableHoursTime() {
        return availableHoursTime;
    }

    public void setAvailableHoursTime(int availableHoursTime) {
        this.availableHoursTime = availableHoursTime;
    }

    public String getPlatformPrefered() {
        return platformPreference;
    }

    public void setPlatformPrefered(String platformPrefered) {
        this.platformPreference = platformPrefered;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
