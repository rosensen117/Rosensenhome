package com.shiguang.lostfound.item;

import java.time.Instant;
import java.time.LocalDate;

public class ItemPost {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String category;
    private LocalDate eventDate;
    private String location;
    private String description;
    private String hiddenFeature;
    private String status;
    private Instant createdAt;
    private String publisherName;
    private boolean publisherVerified;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getHiddenFeature() { return hiddenFeature; }
    public void setHiddenFeature(String hiddenFeature) { this.hiddenFeature = hiddenFeature; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    public boolean isPublisherVerified() { return publisherVerified; }
    public void setPublisherVerified(boolean publisherVerified) { this.publisherVerified = publisherVerified; }
}
