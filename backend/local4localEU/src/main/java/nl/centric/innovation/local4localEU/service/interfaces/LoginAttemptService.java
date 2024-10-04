package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.entity.LoginAttempt;

import java.util.Optional;

public interface LoginAttemptService {
    LoginAttempt countLoginAttempts(Optional<LoginAttempt> loginAttempt, String remoteAddress, boolean isSuccessful);
    Optional<LoginAttempt> findByRemoteAddress(String remoteAddress);

    boolean isBlocked(LoginAttempt loginAttempt);
}
