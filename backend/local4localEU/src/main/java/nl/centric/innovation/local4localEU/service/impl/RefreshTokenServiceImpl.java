package nl.centric.innovation.local4localEU.service.impl;

import io.hypersistence.utils.common.StringUtils;
import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.dto.LoginResponseDto;
import nl.centric.innovation.local4localEU.entity.RefreshToken;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.RefreshTokenException;
import nl.centric.innovation.local4localEU.repository.RefreshTokenRepository;
import nl.centric.innovation.local4localEU.service.interfaces.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nl.centric.innovation.local4localEU.authentication.JwtUtil;
import util.SecurityUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static util.ClaimsUtils.setClaims;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDuration;

    @Value("${error.refresh.expired}")
    private String errorRefreshTokenExpired;

    @Value("${error.entity.notfound}")
    private String errorEntityNotFound;

    @Value("${error.general.entityValidate}")
    private String errorEntityValidate;

    @Value("${jwt.expiration.time}")
    private String jwtExpirationTime;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken getRefreshToken(User user) {

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUserId(user.getId());

        if (refreshToken.isEmpty()) {
            return createRefreshToken(user);
        }

        try {
            verifyExpiration(refreshToken.get());
            return refreshToken.get();
        } catch (RefreshTokenException tokenRefreshException) {
            return createRefreshToken(user);
        }

    }

    @Override
    public AuthResponseDto refreshToken(String requestRefreshToken) throws RefreshTokenException {
        if (StringUtils.isBlank(requestRefreshToken)) {
            throw new RefreshTokenException(requestRefreshToken, errorEntityValidate);
        }
        Optional<RefreshToken> optionalToken = findByToken(requestRefreshToken);

        if (optionalToken.isEmpty()) {
            throw new RefreshTokenException(requestRefreshToken, errorEntityNotFound);
        }

        RefreshToken refreshToken = optionalToken.get();
        verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String jwtToken = jwtUtil.generateToken(setClaims(user), user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, createJwtTokenCookie(jwtToken).toString());

        return AuthResponseDto.builder()
                .loginResponseDto(LoginResponseDto.builder()
                        .role(user.getRole().getName())
                        .expirationDate(jwtUtil.extractExpirationDate(jwtToken))
                        .rememberMe(true)
                        .build())
                .httpHeaders(httpHeaders)
                .build();
    }

    @Transactional
    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    private void verifyExpiration(RefreshToken token) throws RefreshTokenException {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), errorRefreshTokenExpired);
        }
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenDuration * 1000))
                .token(UUID.randomUUID().toString())
                .build();

        newRefreshToken = refreshTokenRepository.save(newRefreshToken);
        return newRefreshToken;
    }

    private HttpCookie createJwtTokenCookie(String jwtToken) {
        return SecurityUtils.createCookie("jwtToken", jwtToken, jwtExpirationTime);
    }

}
