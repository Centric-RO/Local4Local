package nl.centric.innovation.local4localEU.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import nl.centric.innovation.local4localEU.dto.RecoverPasswordDto;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.exception.CustomException.CaptchaException;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.exception.CustomException.PasswordSameException;
import nl.centric.innovation.local4localEU.exception.CustomException.RecoverException;

import java.util.Optional;

public interface UserService {
    void changePassword(String token, String password) throws RecoverException, DtoValidateException, PasswordSameException;
    Optional<User> findByUsername(String email);
    void handlePasswordRecovery(RecoverPasswordDto recoverPasswordDto, HttpServletRequest httpRequest, String locale) throws DtoValidateException, RecoverException, CaptchaException;
    void sendMerchantRegisteredEmail(String merchantName, String language);
}
