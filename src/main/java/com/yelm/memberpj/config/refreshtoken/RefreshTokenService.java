package com.yelm.memberpj.config.refreshtoken;

import com.yelm.memberpj.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken findtok(String val) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByValue(val);
        RefreshToken RT = optionalRefreshToken.get();
        return RT;
    }

    public void deleteRefreshToken(String refreshToken) {
        RefreshToken refreshToken1 = findtok(refreshToken);
        refreshTokenRepository.delete(refreshToken1);
    }

    @Transactional
    public RefreshToken findRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByValue(refreshToken)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REFRESH_NOT_FOUND));

        return token;
    }

    public Long findMemberIdByTokString(String token) {
        return findRefreshToken(token).getMemberId();
    }

}
