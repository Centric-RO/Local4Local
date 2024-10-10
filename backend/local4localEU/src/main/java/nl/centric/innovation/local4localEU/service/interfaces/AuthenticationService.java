package nl.centric.innovation.local4localEU.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import nl.centric.innovation.local4localEU.dto.LoginRequestDto;
import nl.centric.innovation.local4localEU.dto.LoginResponseDto;
import nl.centric.innovation.local4localEU.exception.CustomException.AuthenticationLoginException;
import nl.centric.innovation.local4localEU.exception.CustomException.CaptchaException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.InvalidRoleException;
import nl.centric.innovation.local4localEU.exception.L4LEUException;
import org.springframework.http.HttpHeaders;

public interface AuthenticationService {
    HttpHeaders authenticateByRole(LoginRequestDto loginRequest, String language, HttpServletRequest request)
            throws L4LEUException, CaptchaException, AuthenticationLoginException, InvalidRoleException;

    LoginResponseDto getTokenInfo(HttpServletRequest httpServletRequest) throws DtoValidateNotFoundException, AuthenticationLoginException;
}
