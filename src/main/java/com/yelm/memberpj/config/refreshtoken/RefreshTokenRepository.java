package com.yelm.memberpj.config.refreshtoken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String value);

    boolean existsByMemberId(Long memberId);

    Optional<RefreshToken> findByMemberId(Long memberId);
}
