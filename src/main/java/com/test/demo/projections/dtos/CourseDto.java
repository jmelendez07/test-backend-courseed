package com.test.demo.projections.dtos;

import java.io.Serializable;
import java.util.List;

public class CourseDto implements Serializable {
    private String id;
    private String url;
    private String title;
    private String image;
    private String description;
    private Double price;
    private String duration;
    private String modality;
    private CategoryDto category;
    private String type;
    private InstitutionDto institution;
    private UserDto user;
    private List<ContentDto> contents;
    private List<ReviewDto> reviews;
    private List<ReactionDto> reactions;
    private List<ViewDto> views;
    private PredictionDataDto prediction;
    private String predictionAvgConfidence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public InstitutionDto getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionDto institution) {
        this.institution = institution;
    }

    public List<ContentDto> getContents() {
        return contents;
    }

    public void setContents(List<ContentDto> contents) {
        this.contents = contents;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDto> reviews) {
        this.reviews = reviews;
    }

    public List<ReactionDto> getReactions() {
        return reactions;
    }

    public void setReactions(List<ReactionDto> reactions) {
        this.reactions = reactions;
    }

    public List<ViewDto> getViews() {
        return views;
    }

    public void setViews(List<ViewDto> views) {
        this.views = views;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public PredictionDataDto getPrediction() {
        return prediction;
    }

    public void setPrediction(PredictionDataDto prediction) {
        this.prediction = prediction;
    }

    public String getPredictionAvgConfidence() {
        return predictionAvgConfidence;
    }

    public void setPredictionAvgConfidence(String predictionAvgConfidence) {
        this.predictionAvgConfidence = predictionAvgConfidence;
    }
}
