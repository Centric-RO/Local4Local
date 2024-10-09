package nl.centric.innovation.local4localEU.unit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import nl.centric.innovation.local4localEU.authentication.JwtUtil;
import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.RefreshToken;
import nl.centric.innovation.local4localEU.entity.Role;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.CustomException.AuthenticationLoginException;
import nl.centric.innovation.local4localEU.repository.OtpCodesRepository;
import nl.centric.innovation.local4localEU.service.impl.OtpCodesServiceImpl;
import nl.centric.innovation.local4localEU.service.interfaces.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class OtpCodesServiceImplTests {
    @Mock
    private OtpCodesRepository otpCodesRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private OtpCodesServiceImpl otpCodesServiceImpl;

    private static final String EMAIL = "username@example.com";


    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(otpCodesServiceImpl, "otpExpirationTime", "900");
        ReflectionTestUtils.setField(otpCodesServiceImpl, "jwtExpirationTime", "14400");
        ReflectionTestUtils.setField(otpCodesServiceImpl, "refreshTokenDuration", "2592000");
    }

    @Test
    void GivenValidUser_WhenSaveOtpCode_ThenOtpCodeIsGeneratedAndSaved() {
        // Given
        User user = new User();
        OtpCodes otpCode = OtpCodes.builder()
                .otpCode(123456)
                .sessionId(UUID.randomUUID())
                .user(user)
                .build();

        doNothing().when(otpCodesRepository).deleteAllByCreatedDateBefore(any(LocalDateTime.class));
        when(otpCodesRepository.save(any(OtpCodes.class))).thenReturn(otpCode);

        // When
        OtpCodes savedOtpCode = otpCodesServiceImpl.checkForOtpCode(user, null);

        // Then
        assertNotNull(savedOtpCode);
        assertEquals(user, savedOtpCode.getUser());
        assertEquals(6, String.valueOf(savedOtpCode.getOtpCode()).length());
        verify(otpCodesRepository, times(1)).save(any(OtpCodes.class));
    }

    @Test
    @SneakyThrows
    void GivenValidOtp_WhenValidateOtp_ThenReturnAuthenticationResponse() {
        // Given
        UUID sessionId = UUID.randomUUID();
        Integer otpCode = 123456;
        Role role = Role.builder().name(Role.ROLE_MANAGER).id(0).build();
        User mockUser = User.builder().email(EMAIL).role(role).build();
        OtpCodes otpCodes = OtpCodes.builder()
                .otpCode(otpCode)
                .sessionId(sessionId)
                .user(mockUser)
                .build();

        otpCodes.setCreatedDate(LocalDateTime.now());

        when(jwtUtil.extractTokenFromCookie(request, "sessionId")).thenReturn(sessionId.toString());
        when(jwtUtil.extractTokenFromCookie(request, "rememberMe")).thenReturn("false");
        doNothing().when(otpCodesRepository).deleteAllByCreatedDateBefore(any(LocalDateTime.class));
        when(jwtUtil.generateToken(anyMap(), any(UserDetails.class))).thenReturn("mocked-token");
        when(otpCodesRepository.findBySessionIdAndOtpCode(sessionId, otpCode)).thenReturn(Optional.of(otpCodes));

        // When
        AuthResponseDto response = otpCodesServiceImpl.validateOtp(request, otpCode);

        // Then
        assertNotNull(response);
        verify(otpCodesRepository, times(1)).findBySessionIdAndOtpCode(sessionId, otpCode);
    }

    @Test
    void GivenExpiredOtp_WhenValidateOtp_ThenThrowException() {
        // Given
        UUID sessionId = UUID.randomUUID();
        Integer otpCode = 123456;
        User user = new User();
        OtpCodes otpCodes = OtpCodes.builder()
                .otpCode(otpCode)
                .sessionId(sessionId)
                .user(user)
                .build();

        otpCodes.setCreatedDate(LocalDateTime.now().minusMinutes(16));

        when(jwtUtil.extractTokenFromCookie(request, "sessionId")).thenReturn(sessionId.toString());
        when(jwtUtil.extractTokenFromCookie(request, "rememberMe")).thenReturn("false");
        doNothing().when(otpCodesRepository).deleteAllByCreatedDateBefore(any(LocalDateTime.class));
        when(otpCodesRepository.findBySessionIdAndOtpCode(sessionId, otpCode)).thenReturn(Optional.of(otpCodes));

        // When / Then
        assertThrows(AuthenticationLoginException.class, () ->
                otpCodesServiceImpl.validateOtp(request, otpCode)
        );
        verify(otpCodesRepository, times(1)).delete(otpCodes);
    }

    @Test
    void GivenNoOtpFound_WhenValidateOtp_ThenThrowException() {
        // Given
        UUID sessionId = UUID.randomUUID();
        Integer otpCode = 123456;

        when(jwtUtil.extractTokenFromCookie(request, "sessionId")).thenReturn(sessionId.toString());
        when(jwtUtil.extractTokenFromCookie(request, "rememberMe")).thenReturn("false");
        doNothing().when(otpCodesRepository).deleteAllByCreatedDateBefore(any(LocalDateTime.class));
        when(otpCodesRepository.findBySessionIdAndOtpCode(sessionId, otpCode)).thenReturn(Optional.empty());

        // When / Then

        assertThrows(AuthenticationLoginException.class, () ->
                otpCodesServiceImpl.validateOtp(request, otpCode)
        );

        verify(otpCodesRepository, times(1)).findBySessionIdAndOtpCode(sessionId, otpCode);
    }

    @Test
    void GivenExistingOtp_WhenCheckForOtpCode_ThenReturnSameOtp() {
        // Given
        User user = new User();
        UUID sessionId = UUID.randomUUID();
        OtpCodes otpCode = OtpCodes.builder()
                .otpCode(123456)
                .sessionId(sessionId)
                .user(user)
                .build();

        otpCode.setCreatedDate(LocalDateTime.now().minusMinutes(5));

        doNothing().when(otpCodesRepository).deleteAllByCreatedDateBefore(any(LocalDateTime.class));
        when(otpCodesRepository.findBySessionId(any(UUID.class))).thenReturn(Optional.of(otpCode));

        // When
        OtpCodes returnedOtpCode = otpCodesServiceImpl.checkForOtpCode(user, sessionId.toString());

        // Then
        assertNotNull(returnedOtpCode);
        assertEquals(otpCode, returnedOtpCode);
        verify(otpCodesRepository, times(0)).save(any(OtpCodes.class));
    }

    @Test
    @SneakyThrows
    void GivenValidOtp_WhenValidateOtpWithRememberMeTrue_ThenReturnAuthResponseWithRefreshToken() {
        // Given
        UUID sessionId = UUID.randomUUID();
        Integer otpCode = 123456;
        Role role = Role.builder().name(Role.ROLE_MANAGER).id(0).build();
        User mockUser = User.builder().email(EMAIL).role(role).build();
        OtpCodes otpCodes = OtpCodes.builder()
                .otpCode(otpCode)
                .sessionId(sessionId)
                .user(mockUser)
                .build();


        otpCodes.setCreatedDate(LocalDateTime.now());

        when(jwtUtil.extractTokenFromCookie(request, "sessionId")).thenReturn(sessionId.toString());
        when(jwtUtil.extractTokenFromCookie(request, "rememberMe")).thenReturn("true");

        doNothing().when(otpCodesRepository).deleteAllByCreatedDateBefore(any(LocalDateTime.class));
        when(jwtUtil.generateToken(anyMap(), any(UserDetails.class))).thenReturn("mocked-jwt-token");
        when(refreshTokenService.getRefreshToken(any(User.class))).thenReturn(RefreshToken.builder().token("refreshToken").build());
        when(otpCodesRepository.findBySessionIdAndOtpCode(sessionId, otpCode)).thenReturn(Optional.of(otpCodes));

        // When
        AuthResponseDto response = otpCodesServiceImpl.validateOtp(request, otpCode);

        // Then
        assertNotNull(response);
        assertNotNull(response.loginResponseDto());
        assertTrue(response.loginResponseDto().rememberMe());
        verify(otpCodesRepository, times(1)).findBySessionIdAndOtpCode(sessionId, otpCode);

        HttpHeaders headers = response.httpHeaders();
        assertNotNull(headers.get(HttpHeaders.SET_COOKIE));
        assertTrue(Objects.requireNonNull(headers.get(HttpHeaders.SET_COOKIE)).stream().anyMatch(cookie -> cookie.contains("jwtToken")));
        assertTrue(Objects.requireNonNull(headers.get(HttpHeaders.SET_COOKIE)).stream().anyMatch(cookie -> cookie.contains("refreshToken")));
    }

}
