package nl.centric.innovation.local4localEU.repository;

import nl.centric.innovation.local4localEU.entity.LoginAttempt;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LoginAttemptRepository extends CrudRepository<LoginAttempt, Long> {
    Optional<LoginAttempt> findByRemoteAddress(String remoteAddress);
}
