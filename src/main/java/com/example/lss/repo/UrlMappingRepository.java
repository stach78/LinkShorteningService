package com.example.lss.repo;

import com.example.lss.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortUrl(String shortUrl);
    boolean existsByShortUrl(String shortUrl);

    @Modifying
    @Query("update UrlMapping u set u.clickCount = u.clickCount + 1, u.lastAccessed = ?2 where u.id = ?1")
    int incrementClickCount(long id, Instant accessTime);
}
