package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.entity.RefreshToken;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.RefreshTokenException;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken getRefreshToken(User user);

    AuthResponseDto refreshToken(String requestRefreshToken) throws RefreshTokenException;

    void deleteByToken(String token);
}
