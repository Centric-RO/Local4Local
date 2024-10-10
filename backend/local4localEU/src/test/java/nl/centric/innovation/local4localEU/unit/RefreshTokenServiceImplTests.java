package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.authentication.JwtUtil;
import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.dto.LoginResponseDto;
import nl.centric.innovation.local4localEU.entity.RefreshToken;
import nl.centric.innovation.local4localEU.entity.Role;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.RefreshTokenException;
import nl.centric.innovation.local4localEU.repository.RefreshTokenRepository;
import nl.centric.innovation.local4localEU.repository.UserRepository;
import nl.centric.innovation.local4localEU.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static nl.centric.innovation.local4localEU.entity.Role.ROLE_MANAGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTests {
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;


    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDuration", 50000000L);
        ReflectionTestUtils.setField(refreshTokenService, "jwtExpirationTime", "10000");
    }

    @Test
    public void GivenValidToken_WhenFindByToken_ThenExpectRefreshToken() {
        // Given
        String token = "sampleToken";
        RefreshToken expectedToken = new RefreshToken();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(expectedToken));

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedToken, result.get());
    }

    @Test
    public void GivenUser_WhenCreatingRefreshToken_ThenExpectRefreshToken() {
        // Given
        User user = new User(); // Create a user as needed

        when(refreshTokenRepository.save(any())).thenReturn(mock(RefreshToken.class));

        // When
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(user);

        // Then
        assertNotNull(refreshToken);
    }

    @Test
    public void GivenValidRefreshTokenRequest_WhenRefreshToken_ThenExpectSuccess() {
        // Given
        String validRefreshToken = "validRefreshToken";

        // When
        User user = new User();
        user.setRole(Role.builder().build());
        when(refreshTokenService.findByToken(validRefreshToken)).thenReturn(Optional.of(RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build()));
        when(jwtUtil.generateToken(any(), any())).thenReturn("newAccessToken");

        // Then
        AuthResponseDto result = refreshTokenService.refreshToken(validRefreshToken);

        assertNotNull(result);
    }

    @Test
    public void GivenInexistingRefreshToken_WhenRefreshToken_ThenExpectTokenRefreshException() {
        // Given
        String invalidRefreshToken = "invalidRefreshToken";

        // When
        when(refreshTokenService.findByToken(invalidRefreshToken)).thenReturn(Optional.empty());

        // Then
        assertThrows(RefreshTokenException.class, () -> refreshTokenService.refreshToken(invalidRefreshToken));
    }

    @Test
    public void GivenExpiredTokenRequest_WhenRefreshToken_ThenExpectSuccess() {
        // Given
        String validRefreshToken = "validRefreshToken";

        User user = new User();
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().minusSeconds(3600))
                .build();
        // When
        when(refreshTokenService.findByToken(validRefreshToken)).thenReturn(Optional.of(token));
        doNothing().when(refreshTokenRepository).delete(token);
        // Then
        assertThrows(RefreshTokenException.class, () -> refreshTokenService.refreshToken(validRefreshToken));
    }

    @Test
    public void GivenValidToken_WhenDeleteByToken_ThenExpectDeletedUser() {
        // Given
        String validRefreshToken = "validRefreshToken";

        // When
        refreshTokenService.deleteByToken(validRefreshToken);

        // Then
        verify(refreshTokenRepository, times(1)).deleteByToken(validRefreshToken);
    }


    @Test
    public void GivenUserAndRefreshTokenExpired_WhenCreatingRefreshToken_ThenExpectRefreshToken() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());// Create a user as needed
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().minusSeconds(3600))
                .build();

        // When
        when(refreshTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(token));
        doNothing().when(refreshTokenRepository).delete(token);
        when(refreshTokenRepository.save(any())).thenReturn(mock(RefreshToken.class));

        RefreshToken refreshToken = refreshTokenService.getRefreshToken(user);

        // Then
        assertNotNull(refreshToken);
    }

    @Test
    public void GivenUserAndRefreshTokenValid_WhenCreatingRefreshToken_ThenExpectRefreshToken() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());// Create a user as needed
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .build();

        // When
        when(refreshTokenRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(token));

        RefreshToken refreshToken = refreshTokenService.getRefreshToken(user);

        // Then
        assertNotNull(refreshToken);
    }

}
