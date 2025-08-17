package com.example.lss.repo;

import com.example.lss.entity.UrlMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<UrlMapping> findByOwner_Username(String username, Pageable pageable);
    void deleteByShortUrlAndOwner_Username(String shortUrl, String username);
    boolean existsByShortUrlAndOwner_Username(String shortUrl, String username);
    void deleteByOwner_Username(String username);
}
