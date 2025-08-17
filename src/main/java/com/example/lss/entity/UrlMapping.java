package com.example.lss.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserAccount owner;

    public UserAccount getOwner() {
        return owner;
    }
    public void setOwner(UserAccount owner) {
        this.owner = owner;
    }

    @Column(name="short_url", length=32, nullable=false, unique=true)
    private String shortUrl;

    @Column(name="original_url", nullable = false,  length = 2048)
    private String originalUrl;

    @Column(name="create_date", nullable = false)
    private Instant createDate;

    @Column(name="last_accessed")
    private Instant lastAccessed;

    @Column(name="is_custom", nullable = false)
    private boolean custom;

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    @Column(name="click_count", nullable = false)
    private int clickCount;

    @PrePersist
    void onCreate() {
        if (createDate == null) {
            createDate = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public Instant getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Instant lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
