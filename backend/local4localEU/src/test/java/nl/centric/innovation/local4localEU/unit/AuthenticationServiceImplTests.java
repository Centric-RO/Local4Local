package nl.centric.innovation.local4localEU.unit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.dto.LoginRequestDto;
import nl.centric.innovation.local4localEU.dto.LoginResponseDto;
import nl.centric.innovation.local4localEU.entity.LoginAttempt;
import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.RefreshToken;
import nl.centric.innovation.local4localEU.entity.Role;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.CustomException.AuthenticationLoginException;
import nl.centric.innovation.local4localEU.exception.CustomException.CaptchaException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.InvalidRoleException;
import nl.centric.innovation.local4localEU.service.impl.AuthenticationServiceImpl;
import nl.centric.innovation.local4localEU.service.interfaces.CaptchaService;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;
import nl.centric.innovation.local4localEU.service.interfaces.LoginAttemptService;
import nl.centric.innovation.local4localEU.service.interfaces.OtpCodesService;
import nl.centric.innovation.local4localEU.service.interfaces.RefreshTokenService;
import nl.centric.innovation.local4localEU.authentication.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import util.SecurityUtils;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTests {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private CaptchaService captchaService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private EmailService emailService;

    @Mock
    private OtpCodesService otpCodesService;

    private static final String VALID_RESPONSE = "validResponse";
    private static final String INVALID_RESPONSE = "invalidResponse";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "username@example.com";
    private static final String EMPTY_STRING = "";
    private static final String REMOTE_ADDRESS = "0.0.0.1";
    private static final String LANGUAGE = "en";

    @Test
    void GivenValidRequest_WhenAuthenticateByRole_ThenExpectSuccess() throws Exception {
        // Given
        ReflectionTestUtils.setField(authenticationService, "jwtExpirationTime", "3600000");
        ReflectionTestUtils.setField(authenticationService, "refreshTokenDuration", "2592000");

        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(EMPTY_STRING, false);

        Role role = Role.builder().name(Role.ROLE_MANAGER).id(0).build();
        User mockUser = User.builder().email(EMAIL).role(role).build();
        OtpCodes otpCode = OtpCodes.builder()
                .user(mockUser)
                .sessionId(UUID.randomUUID())
                .otpCode(123456)
                .build();

        // When
        when(userDetailsService.loadUserByUsername(loginRequestDto.email()))
                .thenReturn(mockUser);
        when(jwtUtil.generateToken(anyMap(), any(UserDetails.class))).thenReturn("mocked-token");
        when(otpCodesService.saveOtpCode(mockUser)).thenReturn(otpCode);
        doNothing().when(emailService).sendManagerOtpEmail(LANGUAGE, new String[]{mockUser.getEmail()}, otpCode.getOtpCode());
        AuthResponseDto result = authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request);

        // Then
        assertNotNull(result.httpHeaders().get(HttpHeaders.SET_COOKIE));

        boolean hasJwtCookie = result.httpHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("jwtToken"));
        boolean hasRefreshCookie = result.httpHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("refreshToken"));

        Assertions.assertTrue(hasJwtCookie);
    }

    @Test
    void GivenValidRequestWithCaptcha_WhenAuthenticateByRole_ThenExpectSuccess() throws Exception {
        // Given
        ReflectionTestUtils.setField(authenticationService, "jwtExpirationTime", "3600000");
        ReflectionTestUtils.setField(authenticationService, "refreshTokenDuration", "2592000");

        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(VALID_RESPONSE, false);

        Role role = Role.builder().name(Role.ROLE_MANAGER).id(0).build();

        User mockUser = User.builder().email(EMAIL).role(role).build();
        OtpCodes otpCode = OtpCodes.builder()
                .user(mockUser)
                .sessionId(UUID.randomUUID())
                .otpCode(123456)
                .build();

        // When
        when(userDetailsService.loadUserByUsername(loginRequestDto.email()))
                .thenReturn(mockUser);
        when(captchaService.isResponseValid(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(anyMap(), any(UserDetails.class))).thenReturn("mocked-token");
        when(otpCodesService.saveOtpCode(mockUser)).thenReturn(otpCode);
        doNothing().when(emailService).sendManagerOtpEmail(LANGUAGE, new String[]{mockUser.getEmail()}, otpCode.getOtpCode());
        AuthResponseDto result = authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request);

        // Then
        assertNotNull(result.httpHeaders().get(HttpHeaders.SET_COOKIE));

        boolean hasJwtCookie = result.httpHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("jwtToken"));
        boolean hasRefreshCookie = result.httpHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("refreshToken"));

        Assertions.assertTrue(hasJwtCookie);
    }

    @Test
    void GivenNoCaptchaResponse_WhenAuthenticateByRole_ThenExpectCaptchaException() {
        // Given
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(INVALID_RESPONSE, false);

        when(captchaService.isResponseValid(any(), any())).thenReturn(false);

        // When and Then
        assertThrows(CaptchaException.class, () -> authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request));
    }

    @Test
    void GivenInvalidRole_WhenAuthenticateByRole_ThenExpectInvalidRoleException() {
        // Given
        Role expectedRole = Role.builder().name("INVALID").id(0).build();
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(VALID_RESPONSE, false);

        when(captchaService.isResponseValid(any(), any())).thenReturn(true);

        User mockUser = User.builder().email(EMAIL).role(expectedRole).build();
        when(userDetailsService.loadUserByUsername(loginRequestDto.email()))
                .thenReturn(mockUser);

        // When and Then
        assertThrows(InvalidRoleException.class, () -> authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request));
    }

    @Test
    void GivenIsResponseValidAndInvalidCredentials_WhenAuthenticateByRole_ThenExpectAuthenticationLoginException() {
        // Given
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(INVALID_RESPONSE, false);

        when(captchaService.isResponseValid(any(), any())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        // When and Then
        assertThrows(AuthenticationLoginException.class, () -> authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request));
    }

    @Test
    void GivenIsResponseValidAndDisabledAccount_WhenAuthenticateByRole_ThenExpectAuthenticationLoginException() {
        // Given
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(INVALID_RESPONSE, false);

        when(captchaService.isResponseValid(any(), any())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenThrow(DisabledException.class);

        // When and Then
        assertThrows(AuthenticationLoginException.class, () -> authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request));
    }

    @Test
    void GivenIsResponseValidAndTooManyAttempts_WhenAuthenticateByRole_ThenExpectCaptchaException() {
        // Given
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(INVALID_RESPONSE, false);

        when(captchaService.isResponseValid(any(), any())).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        when(loginAttemptService.isBlocked(any())).thenReturn(true);

        // When and Then
        assertThrows(CaptchaException.class, () -> authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request));
    }

    @Test
    void GivenRequestRememberMeTrue_WhenAuthenticateByRole_ThenExpectSuccess() throws Exception {
        // Given
        ReflectionTestUtils.setField(authenticationService, "jwtExpirationTime", "3600000");
        ReflectionTestUtils.setField(authenticationService, "refreshTokenDuration", "2592000");
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(EMPTY_STRING, true);

        Role role = Role.builder().name(Role.ROLE_MANAGER).id(0).build();

        User mockUser = User.builder().email(EMAIL).role(role).build();
        OtpCodes otpCode = OtpCodes.builder()
                .user(mockUser)
                .sessionId(UUID.randomUUID())
                .otpCode(123456)
                .build();

        // When
        when(userDetailsService.loadUserByUsername(loginRequestDto.email()))
                .thenReturn(mockUser);
        when(jwtUtil.generateToken(anyMap(), any(UserDetails.class))).thenReturn("mocked-token");
        when(refreshTokenService.getRefreshToken(any(User.class))).thenReturn(RefreshToken.builder().token("refreshToken").build());
        when(otpCodesService.saveOtpCode(mockUser)).thenReturn(otpCode);
        doNothing().when(emailService).sendManagerOtpEmail(LANGUAGE, new String[]{mockUser.getEmail()}, otpCode.getOtpCode());
        AuthResponseDto result = authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request);

        // Then
        assertNotNull(result.httpHeaders().get(HttpHeaders.SET_COOKIE));

        boolean hasJwtCookie = result.httpHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("jwtToken"));
        boolean hasRefreshCookie = result.httpHeaders().get(HttpHeaders.SET_COOKIE).stream()
                .anyMatch(cookie -> cookie.contains("refreshToken"));

        Assertions.assertTrue(hasJwtCookie);
        Assertions.assertTrue(hasRefreshCookie);
    }

    @Test
    void GivenRecaptchaResponseEmptyAndLoginAttemptsAbove_WhenAuthenticateByRole_ThenExpectCaptchaException() {
        // Given
        LoginRequestDto loginRequestDto = loginRequestDtoBuilder(EMPTY_STRING, false);

        LoginAttempt loginAttempt = LoginAttempt.builder()
                .remoteAddress(REMOTE_ADDRESS)
                .count(5)
                .build();

        when(SecurityUtils.getClientIP(request)).thenReturn(REMOTE_ADDRESS);
        when(loginAttemptService.findByRemoteAddress(REMOTE_ADDRESS)).thenReturn(Optional.of(loginAttempt));
        when(loginAttemptService.isBlocked(loginAttempt)).thenReturn(true);

        // When and Then
        assertThrows(CaptchaException.class, () -> authenticationService.authenticateByRole(loginRequestDto, LANGUAGE, request));
    }

    @Test
    @SneakyThrows
    void GivenValidToken_WhenGetTokenInfo_ThenExpectCorrectResponse() throws DtoValidateNotFoundException {
        // Given
        String token = "mocked-token";
        String expectedRole = Role.ROLE_MANAGER;
        Date expectedExpirationDate = new Date();

        when(jwtUtil.extractTokenFromCookie(request, "jwtToken")).thenReturn(token);
        when(jwtUtil.extractRole(token)).thenReturn(expectedRole);
        when(jwtUtil.extractExpirationDate(token)).thenReturn(expectedExpirationDate);

        // When
        LoginResponseDto result = authenticationService.getTokenInfo(request);

        // Then
        assertNotNull(result);
        assertEquals(expectedRole, result.role());
        assertEquals(expectedExpirationDate, result.expirationDate());
    }

    @Test
    void GivenNoToken_WhenGetTokenInfo_ThenExpectDtoValidateNotFoundException() {
        // Given
        when(jwtUtil.extractTokenFromCookie(request, "jwtToken")).thenReturn(null);

        // When and Then
        assertThrows(DtoValidateNotFoundException.class, () -> authenticationService.getTokenInfo(request));
    }

    private LoginRequestDto loginRequestDtoBuilder(String reCaptchaResponse, Boolean rememberMe) {
        return LoginRequestDto.builder()
                .password(PASSWORD)
                .email(EMAIL)
                .role(Role.ROLE_MANAGER)
                .reCaptchaResponse(reCaptchaResponse)
                .rememberMe(rememberMe)
                .build();
    }
}
