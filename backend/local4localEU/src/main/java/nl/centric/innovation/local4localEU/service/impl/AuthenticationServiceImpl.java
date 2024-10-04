package nl.centric.innovation.local4localEU.service.impl;

import io.hypersistence.utils.common.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.dto.LoginRequestDto;
import nl.centric.innovation.local4localEU.dto.LoginResponseDto;
import nl.centric.innovation.local4localEU.entity.LoginAttempt;
import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.Role;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.CustomException;
import nl.centric.innovation.local4localEU.exception.CustomException.AuthenticationLoginException;
import nl.centric.innovation.local4localEU.exception.CustomException.CaptchaException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.InvalidRoleException;
import nl.centric.innovation.local4localEU.exception.L4LEUException;
import nl.centric.innovation.local4localEU.repository.OtpCodesRepository;
import nl.centric.innovation.local4localEU.service.interfaces.AuthenticationService;
import nl.centric.innovation.local4localEU.service.interfaces.CaptchaService;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;
import nl.centric.innovation.local4localEU.service.interfaces.LoginAttemptService;
import nl.centric.innovation.local4localEU.service.interfaces.OtpCodesService;
import nl.centric.innovation.local4localEU.service.interfaces.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import nl.centric.innovation.local4localEU.authentication.JwtUtil;
import util.SecurityUtils;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static util.ClaimsUtils.setClaims;

@Service
@RequiredArgsConstructor
@PropertySource({"classpath:errorcodes.properties"})
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;

    private final LoginAttemptService loginAttemptService;

    private final CaptchaService captchaService;

    private final RefreshTokenService refreshTokenService;

    private final EmailService emailService;

    private final OtpCodesService otpCodesService;

    @Value("${error.captcha.show}")
    private String errorCaptchaShow;
    @Value("${error.captcha.notCompleted}")
    private String errorCaptchaNotCompleted;

    @Value("${error.credentials.invalid}")
    private String errorCredentialsInvalid;

    @Value("${jwt.refresh.expiration}")
    private String refreshTokenDuration;

    @Value("${jwt.expiration.time}")
    private String jwtExpirationTime;

    @Value("${error.jwt.notFound}")
    private String errorJwtNotFound;

    @Override
    public AuthResponseDto authenticateByRole(LoginRequestDto loginRequest, String language, HttpServletRequest request)
            throws CaptchaException, AuthenticationLoginException, InvalidRoleException {

        String remoteAddress = SecurityUtils.getClientIP(request);

        boolean isRecaptchaBlank = StringUtils.isBlank(loginRequest.reCaptchaResponse());

        Optional<LoginAttempt> loginAttempt = loginAttemptService.findByRemoteAddress(remoteAddress);

        if (shouldShowCaptcha(loginAttempt, isRecaptchaBlank)) {
            loginAttemptService.countLoginAttempts(loginAttempt, remoteAddress, false);
            throw new CaptchaException(errorCaptchaShow);
        }

        if (isCaptchaValid(isRecaptchaBlank, loginRequest.reCaptchaResponse(), remoteAddress)) {
            return performAuthentication(loginRequest, remoteAddress, loginAttempt, language);
        }

        throw new CaptchaException(errorCaptchaNotCompleted);
    }

    @Override
    public LoginResponseDto getTokenInfo(HttpServletRequest httpServletRequest) throws DtoValidateNotFoundException,
            ExpiredJwtException, AuthenticationLoginException {
        String token = jwtUtil.extractTokenFromCookie(httpServletRequest, "jwtToken");

        if (token == null) {
            throw new DtoValidateNotFoundException(errorJwtNotFound);
        }

        String rememberMeToken = jwtUtil.extractTokenFromCookie(httpServletRequest, "refreshToken");
        boolean isRememberMeActive = (rememberMeToken != null);

        jwtUtil.validateToken(token);
        return new LoginResponseDto(jwtUtil.extractRole(token), jwtUtil.extractExpirationDate(token), isRememberMeActive);

    }

    private AuthResponseDto performAuthentication(LoginRequestDto loginRequestDto, String remoteAddress,
                                                  Optional<LoginAttempt> loginAttempt, String language)
            throws CaptchaException, AuthenticationLoginException, InvalidRoleException {

        authenticate(loginAttempt, loginRequestDto, remoteAddress);
        User userDetails = loadUserDetails(loginRequestDto.email());
        validateUserRole(userDetails, loginRequestDto.role());

        loginAttemptService.countLoginAttempts(loginAttempt, remoteAddress, true);

        return generateAuthenticationResponse(userDetails, loginRequestDto.rememberMe(), language);
    }

    private User loadUserDetails(String email) throws AuthenticationLoginException {
        return (User) userDetailsService.loadUserByUsername(email);
    }

    private void validateUserRole(User userDetails, String expectedRole) throws InvalidRoleException {
        boolean isRoleValid = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(expectedRole));

        if (!isRoleValid) {
            throw new InvalidRoleException("Unexpected role");
        }
    }

    private AuthResponseDto generateAuthenticationResponse(User userDetails, boolean rememberMe, String language) {
        HttpHeaders httpHeaders = new HttpHeaders();

        // Modify extraClaims when necessary
        Map<String, Object> extraClaims = setClaims(userDetails);

        String jwtToken = jwtUtil.generateToken(extraClaims, userDetails);

        if (rememberMe) {
            String refreshToken = refreshTokenService.getRefreshToken(userDetails).getToken();
            httpHeaders.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(refreshToken).toString());
            httpHeaders.add(HttpHeaders.SET_COOKIE, createJwtTokenCookie(jwtToken, true).toString());
        } else {
            httpHeaders.add(HttpHeaders.SET_COOKIE, createJwtTokenCookie(jwtToken, false).toString());
        }

        Role role = (Role) extraClaims.get("role");
        Date expirationDate = jwtUtil.extractExpirationDate(jwtToken);
        sendOtpCode(userDetails, language);

        return AuthResponseDto.builder()
                .loginResponseDto(LoginResponseDto.builder()
                        .role(role.getName())
                        .expirationDate(expirationDate)
                        .rememberMe(rememberMe)
                        .build())
                .httpHeaders(httpHeaders)
                .build();
    }

    private void authenticate(Optional<LoginAttempt> loginAttempt, LoginRequestDto loginRequestDto,
                              String remoteAddress) throws CaptchaException, AuthenticationLoginException {
        var username = loginRequestDto.email();
        var password = loginRequestDto.password();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationLoginException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            this.manageBadCredentials(loginRequestDto, remoteAddress, loginAttempt);
        }
    }

    private void sendOtpCode(User userDetails, String language) {
        OtpCodes otpCode = otpCodesService.saveOtpCode(userDetails);
        emailService.sendManagerOtpEmail(language, new String[]{userDetails.getEmail()}, otpCode.getOtpCode());
    }

    private void manageBadCredentials(LoginRequestDto loginRequestDto, String
            remoteAddress, Optional<LoginAttempt> loginAttempt) throws CaptchaException, AuthenticationLoginException {
        LoginAttempt loginAttemptResult = loginAttemptService.countLoginAttempts(loginAttempt, remoteAddress, false);

        if (loginAttemptService.isBlocked(loginAttemptResult)) {
            if (loginRequestDto.reCaptchaResponse() == null || loginRequestDto.reCaptchaResponse().isEmpty()) {
                throw new CaptchaException(errorCaptchaShow);
            }
            throw new CaptchaException(errorCredentialsInvalid);
        }

        throw new AuthenticationLoginException(errorCredentialsInvalid);
    }

    private boolean shouldShowCaptcha(Optional<LoginAttempt> loginAttempt, boolean isRecaptchaBlank) {
        return loginAttempt.isPresent() && loginAttemptService.isBlocked(loginAttempt.get()) && isRecaptchaBlank;
    }

    private boolean isCaptchaValid(boolean isRecaptchaBlank, String reCaptchaResponse, String remoteAddress) {
        return isRecaptchaBlank || captchaService.isResponseValid(reCaptchaResponse, remoteAddress);
    }

    private HttpCookie createJwtTokenCookie(String jwtToken, boolean rememberMe) {
        if (rememberMe) {
            return SecurityUtils.createCookie("jwtToken", jwtToken, refreshTokenDuration);
        }
        return SecurityUtils.createCookie("jwtToken", jwtToken, jwtExpirationTime);
    }

    private HttpCookie createRefreshTokenCookie(String refreshToken) {
        return SecurityUtils.createCookie("refreshToken", refreshToken, refreshTokenDuration);
    }

}
