package nl.centric.innovation.local4localEU.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import nl.centric.innovation.local4localEU.dto.AuthResponseDto;
import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.CustomException.AuthenticationLoginException;

public interface OtpCodesService {
    OtpCodes checkForOtpCode(User user, String sessionId);

    AuthResponseDto validateOtp(HttpServletRequest request, Integer otpCode) throws AuthenticationLoginException;
}
