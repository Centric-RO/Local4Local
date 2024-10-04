package nl.centric.innovation.local4localEU.service.impl;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.entity.LoginAttempt;
import nl.centric.innovation.local4localEU.repository.LoginAttemptRepository;
import nl.centric.innovation.local4localEU.service.interfaces.LoginAttemptService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository attemptRepository;

    private static final int MAX_ATTEMPT = 5;

    @Override
    public Optional<LoginAttempt> findByRemoteAddress(String remoteAddress) {
        return attemptRepository.findByRemoteAddress(remoteAddress);
    }

    @Override
    public LoginAttempt countLoginAttempts(Optional<LoginAttempt> loginAttempt, String remoteAddress, boolean isSuccessful) {

        LoginAttempt loginAttemptResult;

        int counter = isSuccessful ? 0 : 1;

        if (loginAttempt.isEmpty()) {
            loginAttemptResult = loginAttemptBuilder(counter, remoteAddress);
        } else {
            loginAttemptResult = loginAttempt.get();
            if (isSuccessful) {
                loginAttemptResult.setCount(0);
            } else {
                loginAttemptResult.setCount(loginAttemptResult.getCount() + 1);
            }
        }

        return attemptRepository.save(loginAttemptResult);

    }

    @Override
    public boolean isBlocked(LoginAttempt loginAttempt) {
        return loginAttempt.getCount() >= MAX_ATTEMPT;
    }

    public LoginAttempt loginAttemptBuilder(Integer count, String remoteAddress) {
        return LoginAttempt.builder()
                .count(count)
                .remoteAddress(remoteAddress)
                .build();
    }
}