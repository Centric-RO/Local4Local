package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.entity.RecoverPassword;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateNotFoundException;
import nl.centric.innovation.local4localEU.exception.CustomException.RecoverException;

import java.util.Optional;
import java.util.UUID;

public interface RecoverPasswordService {
    Optional<RecoverPassword> findRecoverPasswordByToken(String token) throws RecoverException, DtoValidateNotFoundException;

    Integer countAllByUserInLastDay(UUID userId);

    RecoverPassword save(RecoverPassword recoverPassword);
}
