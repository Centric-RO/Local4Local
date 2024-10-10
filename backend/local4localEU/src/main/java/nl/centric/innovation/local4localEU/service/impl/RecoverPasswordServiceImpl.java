package nl.centric.innovation.local4localEU.service.impl;

import nl.centric.innovation.local4localEU.entity.RecoverPassword;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.RecoverException;
import nl.centric.innovation.local4localEU.repository.RecoverPasswordRepository;
import nl.centric.innovation.local4localEU.service.interfaces.RecoverPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RecoverPasswordServiceImpl implements RecoverPasswordService {

    @Autowired
    private RecoverPasswordRepository recoverPasswordRepository;

    @Value("${error.recovery.expired}")
    private String errorRecoverExpired;

    @Value("${error.entity.notfound}")
    private String errorEntityNotFound;

    private static final int HOUR_LIMIT = 2;

    @Override
    public Optional<RecoverPassword> findRecoverPasswordByToken(String token) throws RecoverException, DtoValidateNotFoundException {
        Optional<RecoverPassword> resetPasswordToken = recoverPasswordRepository.findByTokenAndIsActiveTrue(token);

        if (resetPasswordToken.isEmpty()) {
            throw new DtoValidateNotFoundException(errorEntityNotFound);
        }

        validateRecoverTime(resetPasswordToken.get().getTokenExpirationDate().getTime());

        return resetPasswordToken;
    }

    @Override
    public Integer countAllByUserInLastDay(UUID userId) {
        return recoverPasswordRepository.countRecoveryRequestsInLastDayByUser(userId);
    }

    @Override
    public RecoverPassword save(RecoverPassword recoverPassword) {
        return recoverPasswordRepository.save(recoverPassword);
    }

    private void validateRecoverTime(long tokenExpirationDate) throws RecoverException {
        long timeDifference = new Date().getTime() - tokenExpirationDate;
        long hoursDifference = TimeUnit.MILLISECONDS.toHours(timeDifference);

        if (hoursDifference >= HOUR_LIMIT) {
            throw new RecoverException(errorRecoverExpired);
        }
    }
}

