package com.huyhn.shorter_url_backend.repository;

import com.huyhn.shorter_url_backend.domain.ShortUrl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByCode(String code);

    @Transactional
    @Modifying
    @Query("""
                UPDATE ShortUrl s
                SET s.clicks = s.clicks + :count
                WHERE s.code = :code
            """)
    void increaseClicks(@Param("code") String code,
                        @Param("count") Long count);
}
