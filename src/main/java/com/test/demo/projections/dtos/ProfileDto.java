package com.test.demo.projections.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ProfileDto implements Serializable {
    private String id;
    private String knowledgeLevel;
    private int availableHoursTime;
    private String platformPrefered;
    private Double budget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String interest;

    public ProfileDto() {}

    public ProfileDto(String id, String knowledgeLevel, int availableHoursTime, String platformPrefered, Double budget, String interest, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.knowledgeLevel = knowledgeLevel;
        this.availableHoursTime = availableHoursTime;
        this.platformPrefered = platformPrefered;
        this.budget = budget;
        this.interest = interest;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
        return platformPrefered;
    }

    public void setPlatformPrefered(String platformPrefered) {
        this.platformPrefered = platformPrefered;
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

    public String getId() {
        return id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public void setId(String id) {
        this.id = id;
    }

}
